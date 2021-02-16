public class Es1_4 {
    public static boolean scan(String s){
        int state = 0;
        int i = 0;
        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);
            switch(state){
                case 0:
                    if(ch == ' ')
                        state = 0;
                    else if(Character.isDigit(ch) && isPari(ch))
                        state = 1;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 2;
                    else if(Character.isLetter(ch))
                        state = 9;
                    else 
                        state = -1;
                    break;

                case 1:
                    if(ch == ' ')
                        state = 3;
                    else if(Character.isDigit(ch) && isPari(ch))
                        state = 1;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 2;
                    else if((ch>='L' && ch<='Z') || (ch>='a' && ch<='z'))
                        state = 9;
                    else if(ch>='A' && ch<='K')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 2:
                    if(ch == ' ')
                        state = 4;
                    else if(Character.isDigit(ch) && isPari(ch))
                        state = 1;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 2;
                    else if((ch>='A' && ch<='K') || (ch>='a' && ch<='z'))
                        state = 9;
                    else if(ch>='L' && ch<='Z')
                        state = 6;
                    else
                        state = -1;
                    break;

                case 3:
                    if(ch == ' ')
                        state = 3;
                    else if(ch>='A' && ch<='K')
                        state = 5;
                    else if(ch>='0' && ch<='9')
                        state = 9;
                    else 
                        state = -1;
                    break;

                case 4:
                    if(ch == ' ')
                        state = 4;
                    else if(ch>='L' && ch<='Z')
                        state = 6;
                    else if(ch>='0' && ch<='9')
                        state = 9;
                    else 
                        state = -1;
                    break;

                case 5:
                    if(ch == ' ')
                        state = 7;
                    else if(ch>='a' && ch<='z')
                        state = 5;
                    else if(ch>='0' && ch<='9')
                        state = 9;
                    else 
                        state = -1;
                    break;

                case 6:
                    if(ch == ' ')
                        state = 8;
                    else if(ch>='a' && ch<='z')
                        state = 6;
                    else if(ch>='0' && ch<='9')
                        state = 9;
                    else 
                        state = -1;
                    break;
                
                case 7:
                    if(ch == ' ')
                        state = 7;
                    else if((ch>='0' && ch<='9') || (ch>= 'a' && ch <= 'z'))
                        state = 9;
                    else if(ch>='A' && ch<='K')
                        state = 5;
                    else 
                        state = -1;
                    break;

                case 8:
                    if(ch == ' ')
                        state = 8;
                    else if((ch>='0' && ch<='9') || (ch>= 'a' && ch <= 'z'))
                        state = 9;
                    else if(ch>='L' && ch<='Z')
                        state = 5;
                    else 
                        state = -1;
                    break;
                case 9:
                        if(ch == ' ' || Character.isDigit(ch) || Character.isLetter(ch))
                            state = 9;

            }
        }
        return state == 5 || state == 6 || state == 7 || state == 8;
    }

    private static boolean isPari(char ch){
        return (ch - '0') % 2 == 0;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE"); 
    }
}
