package assignment;

import java.util.ArrayList;

public class DictionaryArray implements Dictionary{
	private ArrayList<WordDefinitionPair> DictList;
	DictionaryArray(){	
		DictList=new ArrayList<WordDefinitionPair>();
	}
	public ArrayList<WordDefinitionPair> getDictList(){
		ArrayList<WordDefinitionPair> temp=new ArrayList<WordDefinitionPair>();
		DictList.forEach(tem->{
			temp.add(tem);
		});
		return temp;
	}
	public boolean isEmpty() {
		return DictList.isEmpty();
	}
	public void insertEntry(String word, String definition) throws AlreadyExistsInException{
		for(WordDefinitionPair tem:DictList) {
			if(tem.getWord().equals(word))throw new AlreadyExistsInException();
		}
		DictList.add(new WordDefinitionPair(word,definition));
	
	}
	@Override
	public void getDefinition(String word) throws EmptyException, NotInDicException {
		if(DictList.isEmpty()) throw new EmptyException();
		for(WordDefinitionPair tem: DictList) {
			if(tem.getWord().equals(word)) {
				System.out.println(tem.getDefinition());
				return;
			}
		}
		throw new NotInDicException();
		
	}
	@Override
	public void removeWord(String word) throws EmptyException, NotInDicException {
		if(DictList.isEmpty())throw new EmptyException();
		for(WordDefinitionPair tem: DictList) {
			if(tem.getWord().equals(word)) {
				DictList.remove(tem);
				return;
			}
		}
		throw new NotInDicException();
	}
	@Override
	public void printWrods() throws EmptyException {
		int i=1;
		if(DictList.isEmpty())throw new EmptyException();
		for(WordDefinitionPair temp:DictList) {
			System.out.println(i+". "+temp.getWord());
			i++;
		}
	}
	@Override
	public void printAll() throws EmptyException {
		if(DictList.isEmpty())throw new EmptyException();
		for(WordDefinitionPair temp:DictList) {
			System.out.println("=====================");
			System.out.println("Word : "+temp.getWord());
			System.out.println("Definition : "+temp.getDefinition());
		}
		
	}
	
	
	
}	
