package assignment;

public class EmptyException extends Exception {
	EmptyException(){
		super("List is empty");
	}
	EmptyException(String str){
		super(str);
	}
}
