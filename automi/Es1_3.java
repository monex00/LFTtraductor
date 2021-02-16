public class Es1_3 {
    public static boolean scan(String s){
        int state = 0;
        int i = 0;
        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);
            switch(state){
                case 0:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 1;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 2;
                    else if(Character.isLetter(ch))
                            state = 5;
                    else 
                        state = -1;
                    break;

                case 1:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 1;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 2;
                    else if((ch>='L' && ch<='Z') || (ch>='a' && ch<='z'))
                        state = 5;
                    else if(ch>='A' && ch<='K')
                        state = 3;
                    else
                        state = -1;
                    break;

                case 2:
                    if(Character.isDigit(ch) && isPari(ch))
                        state = 1;
                    else if(Character.isDigit(ch) && !isPari(ch))
                        state = 2;
                    else if((ch>='A' && ch<='K') || (ch>='a' && ch<='z'))
                        state = 5;
                    else if(ch>='L' && ch<='Z')
                        state = 4;
                    else
                        state = -1;
                    break;

                case 3:
                    if(Character.isLetter(ch))
                        state = 3;
                    else if(ch>='0' && ch<='9')
                        state = 5;
                    else 
                        state = -1;
                    break;

                case 4:
                    if(Character.isLetter(ch))
                        state = 4;
                    else if(ch>='0' && ch<='9')
                        state = 5;
                    else 
                        state = -1;
                    break;

                case 5:
                    if(Character.isLetter(ch) || (ch>='0' && ch<='9'))
                        state = 5;
                    else 
                        state = -1;
                    break;
            }
        }
        return state == 3 || state == 4;
    }

    private static boolean isPari(char ch){
        return (ch - '0') % 2 == 0;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE"); 
    }
}
