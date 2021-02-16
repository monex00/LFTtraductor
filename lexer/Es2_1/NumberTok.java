public class NumberTok extends Token {
    public String number = "";
	public NumberTok(int tag, String number){
        super(tag);
        this.number = number;
    }
    public String toString(){ return "<" + tag + ", " + number + ">";}
}