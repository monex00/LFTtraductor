/*
GUIDE:
<prog> -> <statlist>EOF = {'=', print, read, cond, while, '{'}
<statlist> -> <stat><statlistp> = {'=', print, read, cond, while, '{'}
<statlistp> -> ;<stat><statlistp> = {;}
<statlistp> -> eps = {'EOF', '}'}
<stat> -> =ID<expr> = {'='}
<stat> -> print(exprlist>) = {print}
<stat> -> read(ID) = {read}
<stat> -> cond<whenlist>else<stat> = {cond}
<stat> -> while(<bexpr>)<stat> = {while}
<stat> -> {<statlist>} = {'{'}
<whenlist> -> <whenitem><whenlistp> = {when}
<whenlistp> -> <whenitem><whenlistp> = {when}
<whenlisp> -> eps = {else}
<whenitem> -> when(<bexpr>)do<stat> = {when}
<bexpr> -> RELOP<expr><expr> = {RELOP}
<expr> -> +(<exprlist>) = {+}
<expr> -> *(<exprlist>) = {*}
<expr> -> -<expr><expr> = {-}
<expr> -> /<expr><expr> = {/}
<expr> -> NUM = NUM
<expr> -> ID = ID
<exprlist> -> <expr><exprlistp> = {'+', '-', '*', '/', NUM, ID}
<exprlistp> -> <expr><exprlistp> = {'+', '-', '*', '/', NUM, ID}
<exprlistp> -> eps = {')'}
*/

import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move(); // leggo il primo campo token
    }

    // usa l'output del lexer; si muove token per token stampa i token
    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    // circostanziare l'errore
    void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    // verifica che non sono alla fine del file e che non ci siano errori di
    // sintassi
    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF)
                move();
        } else
            error("syntax error");
    }

    public void prog() {
        // controlla che il token faccia parte dell'insieme guida della produzione
        // <start> ::= <expr>EOF
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.COND
                || look.tag == Tag.WHILE || look.tag == '{') {
            statlist();
            match(Tag.EOF);
        } else {
            error("Syntax error prog");
        }
    }

    private void statlist() {
        if (look.tag == '=' || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.COND
                || look.tag == Tag.WHILE || look.tag == '{') {
            stat();
            statlistp();
        } else {
            error("Syntax error statlist");
        }
    }

    private void statlistp() {
        switch (look.tag) {
            case ';':
                match(';');
                stat();
                statlistp();
                break;
            case '}':
            case Tag.EOF: // E'->ℇ
                break;
            default:
                error("Syntax error statlistp");
        }
    }

    private void stat() {
        switch (look.tag) {
            case '=':
                match('=');
                match(Tag.ID);
                expr();
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist();
                match(')');
                break;
            case Tag.READ:
                match(Tag.READ);
                match('(');
                match(Tag.ID);
                match(')');
                break;
            case Tag.COND:
                match(Tag.COND);
                whenlist();
                match(Tag.ELSE);
                stat();
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                match('(');
                bexpr();
                match(')');
                stat();
                break;
            case '{':
                match('{');
                statlist();
                match('}');
                break;
            default:
                error("Syntax error stat");
        }
    }

    private void whenlist() {
        if (look.tag == Tag.WHEN) {
            whenitem();
            whenlistp();
        } else {
            error("Syntax error whenlist");
        }
    }

    private void whenlistp() {
        switch (look.tag) {
            case Tag.WHEN:
                whenitem();
                whenlistp();
                break;
            case Tag.ELSE:
                break;
            default:
                error("Syntax error whenlistp");
        }
    }

    private void whenitem() {
        if (look.tag == Tag.WHEN) {
            match(Tag.WHEN);
            match('(');
            bexpr();
            match(')');
            match(Tag.DO);
            stat();
        } else {
            error("Syntax error whenitem");
        }
    }

    private void bexpr() {
        if (look.tag == Tag.RELOP) {
            match(Tag.RELOP);
            expr();
            expr();
        } else {
            error("Syntax error bexpr");
        }
    }

    private void expr() {
        switch (look.tag) {
            case '+':
                match('+');
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
                match('-');
                expr();
                expr();
                break;
            case '*':
                match('*');
                match('(');
                exprlist();
                match(')');
                break;
            case '/':
                match('/');
                expr();
                expr();
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            case Tag.ID:
                match(Tag.ID);
                break;
            default:
                error("Syntax error expr");
        }
    }

    private void exprlist() {
        if (look.tag == '+' || look.tag == '-' || look.tag == '*' || look.tag == '/' || look.tag == Tag.NUM
                || look.tag == Tag.ID) {
            expr();
            exprlistp();
        } else {
            error("Syntax error explist");
        }
    }

    private void exprlistp() {
        if (look.tag == '+' || look.tag == '-' || look.tag == '*' || look.tag == '/' || look.tag == Tag.NUM
                || look.tag == Tag.ID) {
            expr();
            exprlistp();
        } else if (look.tag != ')') {
            error("Syntax error explist");
        }
    }

}