package project;


public class FullArrayException extends Exception {
	FullArrayException(){
		super("list is full!"+"\n"+"Try to expand...."+"\n"+"How much will it expand");
	}
	FullArrayException(String str){
		super(str);
	}
}
