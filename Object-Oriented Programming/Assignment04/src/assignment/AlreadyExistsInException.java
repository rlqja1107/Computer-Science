package assignment;

public class AlreadyExistsInException extends Exception {
	AlreadyExistsInException(){
		super("this word already exist in dictionary");
	}
	AlreadyExistsInException(String str){
		super(str);
	}

}
