package assignment;

public interface Dictionary {
	boolean isEmpty();
	void insertEntry(String word, String definition) throws AlreadyExistsInException;
	void getDefinition(String word) throws EmptyException, NotInDicException;
	void removeWord(String word) throws EmptyException, NotInDicException;
	void printWrods() throws EmptyException;
	void printAll() throws EmptyException;
}
