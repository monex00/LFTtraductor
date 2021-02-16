/*
    guida(START-> <expr>EOF) = {(, NUM}
    guida(E-><term><expr>) = {(, NUM}
    guida(E'->+<term><exprp>) = {+} 
    guida(E'->-<term><exprp>) = {-}
    guida(E'->eps) = follow(E') = {$, )}
    guida(T-><fact><termp>) = {(, NUM}
    guida(T'->*<fact><termp>) = {*}
    guida(T'->/<fact><termp>) = {/}
    guida(T' -> eps) = follow(T') = {$,),+,-}
    guida(F -> (<expr>)) = {(}
    guida(F->NUM) = {NUM}
*/

import java.io.*;

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();      //leggo il primo campo token
    }
    //usa l'output del lexer; si muove token per token stampa i token
    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }
    //circostanziare l'errore
    void error(String s) {
	   throw new Error("near line " + lex.line + ": " + s);
    }
    //verifica che non sono alla fine del file e che non ci siano errori di sintassi
    void match(int t) {
    	if (look.tag == t) {
    	    if (look.tag != Tag.EOF) move();
    	} else error("syntax error");
    }

    public void start() {
        int expr_val;
        //controlla che il token faccia parte dell'insieme guida della produzione <start> ::= <expr>EOF 
        if(look.tag == '(' || look.tag == Tag.NUM){
            expr_val = expr();
            match(Tag.EOF);
            System.out.println(expr_val);
        }else{
            error("Syntax error start");
        }
    }

    private int expr() {
        int term_val, exprp_val = 0;
        //insieme guida
	   if(look.tag == '(' || look.tag == Tag.NUM){
            term_val = term();
            exprp_val = exprp(term_val);
       }else{
            error("Syntax error expr");
       }
       return exprp_val;
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val = 0;
    	switch (look.tag) {
        	case '+':
        	    match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;  
        	case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;
            case ')':
            case Tag.EOF:   //E'->ℇ
                exprp_val = exprp_i;
                break;
            default :
                error("Syntax error exprp");
        }
        return exprp_val;
    }

    private int term() {
        int fact_val, termp_val = 0;
        if(look.tag == '(' || look.tag == Tag.NUM){
            fact_val = fact();
            termp_val = termp(fact_val);
        }else{
            error("Syntax error term");
        }
        return termp_val;
    }

    private int termp(int termp_i) {
        int fact_val, termp_val = 0;
        switch(look.tag){
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;
            case ')':
            case '+':
            case '-':
            case Tag.EOF:
                termp_val = termp_i;
                break;
            default:
                error("Syntax error termp");
        }
        return termp_val;
    }

    private int fact() {
        int fact_val = 0;
        switch(look.tag){
            case '(':
                match('(');
                fact_val = expr();
                match(')');
                break;
            case Tag.NUM: 
                NumberTok num = (NumberTok)look;
                fact_val = Integer.parseInt(num.number);
                match(Tag.NUM);
                break;
            default:
                error("Syntax error fact");
        }
        return fact_val;
    }

}
