package assignment;

public class WordDefinitionPair {
	private String word;
	private String definition;
	WordDefinitionPair(String word,String definition){
		this.word=word;
		this.definition=definition;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word=word;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String def) {
		this.definition=def;
	}
}
