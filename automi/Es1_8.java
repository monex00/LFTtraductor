public class Es1_8 {
    public static boolean scan(String s){
        int state = 0;
        int i = 0;
        while(state >= 0 && i < s.length()){
            final char ch = s.charAt(i++);
            switch(state){
                case 0:
                    if(ch == 'S')
                        state = 1;
                    else
                        state = 2;
                    break;
                case 1:
                    if(ch == 'i')
                        state = 3;
                    else
                        state = 4;
                    break;
                case 2:
                    if(ch == 'i')
                        state = 4;
                    else
                        state = -1;
                    break;
                case 3:
                    if(ch == 'm')
                        state = 5;
                    else
                        state = 6;
                    break;
                case 4:
                    if(ch == 'm')
                        state = 6;
                    else
                        state = -1;
                    break;
                case 5:
                    state = 7;
                    break;
                case 6:
                    if(ch == 'o')
                        state = 7;
                    else
                        state = -1;
                    break;
                case 7:
                    state = 7;
                    break;
            }
        }
        return state == 7;
    }

    public static void main(String[] args) {
        System.out.println(scan(args[0]) ? "OK" : "NOPE"); 
    }
    
}
