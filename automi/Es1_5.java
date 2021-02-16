public class Es1_5 {
    public static boolean scan(String s){
        int state = 0;
        int i = 0;
        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);
            switch(state){
                case 0:
                    if(ch>='L' && ch<='Z')
                        state = 1;
                    else if(ch >='A' && ch<='K')
                        state = 2;
                    else if((ch >= 'a' && ch <= 'z') || Character.isDigit(ch))
                            state = 7;
                    else 
                        state = -1;
                    break;

                case 1:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 3;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 4;
                    else if(ch>='A' && ch<='Z')
                        state = 7;
                    else if(ch>='a' && ch<='z')
                        state = 1;
                    else
                        state = -1;
                    break;

                case 2:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 5;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 6;
                    else if((ch>='A' && ch<='Z'))
                        state = 7;
                    else if(ch>='a' && ch<='z')
                        state = 2;
                    else
                        state = -1;
                    break;

                case 3:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 3;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 4;
                    else if(Character.isLetter(ch))
                        state = 7;
                    else 
                        state = -1;
                    break;

                case 4:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 3;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 4;
                    else if(Character.isLetter(ch))
                        state = 7;
                    else 
                        state = -1;
                    break;

                case 5:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 5;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 6;
                    else if(Character.isLetter(ch))
                        state = 7;
                    else 
                        state = -1;
                    break;
                case 6:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 5;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 6;
                    else if(Character.isLetter(ch))
                        state = 7;
                    else 
                        state = -1;
                    break;

                case 7:
                    if(Character.isLetter(ch) || Character.isDigit(ch))
                        state = 7;
                    else 
                        state = -1;
                    break;
            }
        }
        return state == 4 || state == 5;
    }

    private static boolean isPari(char ch){
        return (ch - '0') % 2 == 0;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE"); 
    }
}
