package assignment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Program{
	public static void main(String args[]) {
	int choose;
	boolean flag=true;
	DictionaryArray dict=new DictionaryArray();
	Scanner scan=new Scanner(System.in);
	Scanner scan2=new Scanner(System.in);
	String word;
	String definition;
	while(flag) {
		System.out.println("============Main Menu=============");
		System.out.println("(1) Add a word to dictionary");
		System.out.println("(2) Show definition of word");
		System.out.println("(3) Show word list");
		System.out.println("(4) Remove word");
		System.out.println("(5) print all content");
		System.out.println("(6) Exit program");
		System.out.println("==================================");
		System.out.print("choose a menu : ");

		choose=scan.nextInt();
	switch(choose) {
	case 1: 
		System.out.println("word :");
		word=scan2.nextLine();
		System.out.println("definition : ");		
		definition=scan2.nextLine();
		try {
		dict.insertEntry(word, definition);
		}
		catch(AlreadyExistsInException e) {
			System.out.println(e.getMessage());
		}
		break;
	case 2:
		System.out.print("word for searching : ");
		word=scan.next();
		try {
			dict.getDefinition(word);
		}
		catch(EmptyException e) {
			System.out.println(e.getMessage());
		}
		catch(NotInDicException e) {
			System.out.println(e.getMessage());
		}
		break;
	case 3:
		try {
			dict.printWrods();
		}
		catch(EmptyException e) {
			System.out.println(e.getMessage());
		}
		break;
	case 4:
		System.out.print("word to remove : ");
		word=scan.next();
		try {
			dict.removeWord(word);
		}
		catch(EmptyException e) {
			System.out.println(e.getMessage());
		}
		catch(NotInDicException e) {
			System.out.println(e.getMessage());
		}
		break;
	case 5:
		try {
			dict.printAll();
		}
		catch(EmptyException e) {
			System.out.println(e.getMessage());
		}
		break;
	case 6:
		System.out.println();
		System.out.println("Enter a file name :");
		word=scan.next();
		try {
			int i=1;
		PrintWriter write=new PrintWriter(new FileOutputStream(word));
		for(WordDefinitionPair temp: dict.getDictList()) {
			write.println(i+". word : "+temp.getWord());
			write.println("\t definition : "+temp.getDefinition());
			i++;
		}
		System.out.println("saved as"+word);
		write.close();
		}
		catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("exit program");
		flag=false;
		break;
	default :
		System.out.println("Insert 1 ~ 6 ");
		break;
	}
	
	
	}
}}
