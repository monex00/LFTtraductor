import java.io.*; 

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    private String tempId = "";
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    /* Metodo che controllo la validità del numero */
    private boolean validNumber(String number){
        number = number.replaceAll(" ", "");
        //Controllo casi come : 12a, numeri che contengono lettere al loro interno
        for(char x : number.toCharArray()){
            if(!Character.isDigit(x)){
                System.out.println("Erroneous identifier sintax");
                return false;
            }
        }
        //Controllo casi come: 0034, numeri che iniziano con 0 ma hanno cifre diverse da 0 in seguito
        if(number.length()>=2 && number.toCharArray()[0] == '0'){
            if( number.toCharArray()[1] != '0'){
                System.out.println("Erroneous number sintax");
                return false;
            }
        }
       return true; 
    }

    public Token lexical_scan(BufferedReader br) {
        //ignora tabulazioni e spazi a caso
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        //gestire i casi di toker senza attributi
        //// ... gestire i casi di (, ), {, }, +, -, *, /, ; ... //
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':
                peek = ' ';
                return Token.div;            
            case ';':
                peek = ' ';
                return Token.semicolon;
	
            //gestire i casi  in cui ci sono due operatori 
            // ... gestire i casi di ||, <, >, <=, >=, ==, <>, = ... //
            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            case '<':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.le;
                }else if(peek == '>'){
                    peek = ' ';
                    return Word.ne;
                } else {
                   return Word.lt;
                }
            case '>':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }
            case '=':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.eq;
                }else if(peek == '>'){
                    peek = ' ';
                    return Word.ge;
                }else{
                    return Token.assign;
                }

            case (char)-1:
                return new Token(Tag.EOF);

            default:
                //definisco una variabile stringa per prendere tutto quello che devo leggere
                // ... gestire il caso degli identificatori e delle parole chiave    
                if (Character.isLetter(peek)) {
                    tempId = Character.toString(peek);
                    readch(br);
                    while(Character.isLetter(peek) || Character.isDigit(peek) ){
                        tempId += Character.toString(peek);
                        readch(br);
                    }

                    switch(tempId){
                        case "cond":
                            return Word.cond;
                        case "while":
                            return Word.whiletok;
                        case "when":
                            return Word.when;
                        case "then":
                            return Word.then;
                        case "else":
                            return  Word.elsetok;
                        case "do":
                            return Word.dotok;
                        case "seq":
                            return Word.seq;
                        case "print":
                            return Word.print;
                        case "read":
                            return Word.read; 
                        default:
                            return new Word(Tag.ID, tempId);       
                    }
                }else if (Character.isDigit(peek)) {
                //controllo che i numeri rispettino l'espressione regolare data 
                // ... gestire il caso dei numeri ...
                    tempId = Character.toString(peek);
                    readch(br);
                    while(Character.isLetter(peek) || Character.isDigit(peek) ){
                        tempId += Character.toString(peek);
                        readch(br);
                    }
                    if(validNumber(tempId)){
                        return new NumberTok(Tag.NUM, tempId);
                    }
                    peek = ' ';
                    return null;
                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
        }
    }
}
