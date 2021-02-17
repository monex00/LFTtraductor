import java.io.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() { 
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) { 
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error");
    }

    public void prog() {        
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.COND
        || look.tag == Tag.WHILE || look.tag == '{') {
            int lnext_prog = code.newLabel();
            statlist(lnext_prog);
            code.emitLabel(lnext_prog);
            match(Tag.EOF);
            try {
                code.toJasmin();
            }
            catch(java.io.IOException e) {
                System.out.println("IO error\n");
            };
        } else {
            error("Syntax error prog");
        }
    }

    private void statlist(int lnext_statlist) {
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.COND
                || look.tag == Tag.WHILE || look.tag == '{') {
            int lnext_stat = code.newLabel();
            stat(lnext_stat);
            code.emitLabel(lnext_stat);
            statlistp(lnext_statlist);
        } else {
            error("Syntax error statlist");
        }
    }

    private void statlistp(int lnext_statlistp) {
        switch (look.tag) {
            case ';':
                match(';');
                int lnext_stat = code.newLabel();
                stat(lnext_stat);
                code.emitLabel(lnext_stat);
                statlistp(lnext_statlistp);
                break;
            case '}':
            case Tag.EOF: // E'->
                code.emit(OpCode.GOto, lnext_statlistp);
                break;
            default:
                error("Syntax error statlistp");
        }
    }

    public void stat(int lnext_stat ) {
        switch (look.tag) {
            case '=':
                match('=');
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }
                    match(Tag.ID);
                    expr(false);
                    code.emit(OpCode.istore, id_addr);
                    code.emit(OpCode.GOto, lnext_stat);
                }else{
                    error("Error identifier expected" + look);
                }
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist(-1, true);
                match(')');
                code.emit(OpCode.GOto, lnext_stat);
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
                if (look.tag==Tag.ID) {
                    int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1) {
                        id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    match(')');
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,id_addr);
                    code.emit(OpCode.GOto, lnext_stat);
		            // ... completare ...  
                }else
                    error("Error in grammar (stat) after read( with " + look);
                break;
            case Tag.COND:
                match(Tag.COND);
                int bwhenlist_false = code.newLabel();
                whenlist(lnext_stat, bwhenlist_false);
                code.emitLabel(bwhenlist_false);
                match(Tag.ELSE);
                stat(lnext_stat);
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                int lnext_stat1 = code.newLabel();
                int bexpr_true = code.newLabel();
                code.emitLabel(lnext_stat1);
                bexpr(bexpr_true, lnext_stat);
                match(')');
                code.emitLabel(bexpr_true);
                stat(lnext_stat1);
                break;
            case '{':
                match('{');
                statlist(lnext_stat);
                match('}');
                break;
            default:
                error("Syntax error stat");
        }
     }

     private void whenlist(int lnext_whenlist, int bwhenlist_false) {
        if (look.tag == Tag.WHEN) {
            int lnext_whenitem = code.newLabel();
            whenitem(lnext_whenlist, lnext_whenitem);
            code.emitLabel(lnext_whenitem);
            whenlistp(lnext_whenlist, bwhenlist_false);
        } else {
            error("Syntax error whenlist");
        }
    }

    private void whenlistp(int lnext_whenlistp, int bwhenlistp_false) {
        switch (look.tag) {
            case Tag.WHEN:
                int lnext_whenitemp = code.newLabel();
                whenitem(lnext_whenlistp, lnext_whenitemp);
                code.emitLabel(lnext_whenitemp);
                whenlistp(lnext_whenlistp, bwhenlistp_false);
                break;
            case Tag.ELSE:
                code.emit(OpCode.GOto, bwhenlistp_false);
                break;
            default:
                error("Syntax error whenlistp");
        }   
    }

    private void whenitem(int lnext_whenitem, int bwhenitem_false) {
        if (look.tag == Tag.WHEN) {
            match(Tag.WHEN);
            match('(');
            int bexpr_true = code.newLabel();
            int bexpr_false = bwhenitem_false;
            bexpr(bexpr_true, bexpr_false);
            code.emitLabel(bexpr_true);
            match(')');
            match(Tag.DO);
            stat(lnext_whenitem);
        } else {
            error("Syntax error whenitem");
        }
    }

    private void bexpr(int bexpr_true, int bexpr_false) {
        if (look.tag == Tag.RELOP) {
            Word b = (Word)look;
            match(Tag.RELOP);
            switch(b.lexeme){
                case "<":
                    expr(false);
                    expr(false);
                    code.emit(OpCode.if_icmplt, bexpr_true);
                    code.emit(OpCode.GOto, bexpr_false);
                    break;
                case ">":
                    expr(false);
                    expr(false);
                    code.emit(OpCode.if_icmpgt,bexpr_true);
                    code.emit(OpCode.GOto, bexpr_false);
                    break;
                case "==":
                    expr(false);
                    expr(false);
                    code.emit(OpCode.if_icmpeq,bexpr_true);
                    code.emit(OpCode.GOto, bexpr_false);
                    break;
                case "<=":
                    expr(false);
                    expr(false);
                    code.emit(OpCode.if_icmple,bexpr_true);
                    code.emit(OpCode.GOto, bexpr_false);
                    break;
                case "<>":
                    expr(false);
                    expr(false);
                    code.emit(OpCode.if_icmpne,bexpr_true);
                    code.emit(OpCode.GOto, bexpr_false);
                    break;
                case ">=":
                    expr(false);
                    expr(false);
                    code.emit(OpCode.if_icmpge,bexpr_true);
                    code.emit(OpCode.GOto, bexpr_false);
                    break;                    
            }
        } else {
            error("Syntax error bexpr");
        }
    }

    private void expr(boolean stampa) {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist(0, false);
                match(')');
                if(stampa)
                    code.emit(OpCode.invokestatic, 1);
                break;
            case '-':
                match('-');
                expr(false);
                expr(false);
                code.emit(OpCode.isub);
                if(stampa)
                    code.emit(OpCode.invokestatic, 1);
                break;
            case '*':
                match('*');
                match('(');
                exprlist(1, false);
                match(')');
                if(stampa)
                    code.emit(OpCode.invokestatic, 1);
                break;
            case '/':
                match('/');
                expr(false);
                expr(false);
                code.emit(OpCode.idiv);
                if(stampa)
                    code.emit(OpCode.invokestatic, 1);
                break;
            case Tag.NUM:
                NumberTok b = (NumberTok) look;
                code.emit(OpCode.ldc, Integer.parseInt(b.number));
                if(stampa)
                    code.emit(OpCode.invokestatic, 1);
                match(Tag.NUM);
                break;
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if(id_addr!=-1) {
                    match(Tag.ID);
                    code.emit(OpCode.iload, id_addr);
                    if(stampa)
                        code.emit(OpCode.invokestatic, 1);
                }else{
                    error("Identifier used but never created" + look);
                }
                break;
            default:
                error("Syntax error expr");
        }
    }

    private void exprlist(int op, boolean stampa) {
        if (look.tag == '+' || look.tag == '-' || look.tag == '*' || look.tag == '/' || look.tag == Tag.NUM
                || look.tag == Tag.ID) {
            
            expr(stampa);
            exprlistp(op, stampa);

        } else {
            error("Syntax error explist");
        }
    }

    private void exprlistp(int op, boolean stampa) {
        if (look.tag == '+' || look.tag == '-' || look.tag == '*' || look.tag == '/' || look.tag == Tag.NUM
                || look.tag == Tag.ID) {
            expr(stampa);
            exprlistp(op, stampa);
            if(op != -1){
                if(op == 0){ //somma
                    code.emit(OpCode.iadd);
                }
                else{//moltiplicazione
                    code.emit(OpCode.imul);
                }
            }
        } else if (look.tag != ')') {
            error("Syntax error explist");
        }
    }
}

