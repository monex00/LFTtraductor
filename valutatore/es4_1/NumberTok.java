//Ispirare alla qualle work , Toker attributo per costanti numeriche
//lessema può essere considerata non come stringa ma come intero
public class NumberTok extends Token {
	// ... completare ...
	public String number = "";
    public NumberTok(int tag, String num) { super(tag); number=num; }
    public String toString() { return "<" + tag + ", " + number + ">"; }
}
