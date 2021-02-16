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

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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
        //controlla che il token faccia parte dell'insieme guida della produzione <start> ::= <expr>EOF 
        if(look.tag == '(' || look.tag == Tag.NUM){
            expr();
            match(Tag.EOF);
        }else{
            error("Syntax error start");
        }
    }

    private void expr() {
        //insieme guida
	   if(look.tag == '(' || look.tag == Tag.NUM){
            term();
            exprp();
       }else{
            error("Syntax error expr");
       }
    }

    private void exprp() {
    	switch (look.tag) {
        	case '+':
        	    match('+');
                term();
                exprp();
                break;  
        	case '-':
                match('-');
                term();
                exprp();
                break;
            case ')':
            case Tag.EOF:   //E'->ℇ
                break;
            default :
                error("Syntax error exprp");
        }
    }

    private void term() {
        if(look.tag == '(' || look.tag == Tag.NUM){
            fact();
            termp();
        }else{
            error("Syntax error term");
        }
    }

    private void termp() {
        switch(look.tag){
            case '*':
                match('*');
                fact();
                termp();
                break;
            case '/':
                match('/');
                fact();
                termp();
                break;
            case ')':
            case '+':
            case '-':
            case Tag.EOF:
                break;
            default:
                error("Syntax error termp");
        }
    }

    private void fact() {
        switch(look.tag){
            case '(':
                match('(');
                expr();
                match(')');
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("Syntax error fact");
        }
    }		
}