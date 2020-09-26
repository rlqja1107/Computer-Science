package assignment;

public class NotInDicException extends Exception {
	NotInDicException(){
		super("¡°this word not exist in dictionary");
	}
	NotInDicException(String str){
		super(str);
	}
}
