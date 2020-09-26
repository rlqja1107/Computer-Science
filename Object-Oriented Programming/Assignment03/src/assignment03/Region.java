package assignment03;



import javax.imageio.IIOException;

public class Region extends Thread{

	private String regionName;			// The name of the region
	private int regionNum;			// The number of the region
	private int population;			// The population of the region
	private Candidate[] candidates;	// An array of the candidates of the election

	public Region(String regionName,int regionNum,int population,Candidate [] can) {
		this.regionName=regionName;
		this.regionNum=regionNum;
		this.population=population;
		candidates=can;
	}

	public String getRegionName() {
		return this.regionName;
	}

	public int getRegionNum() {
		return regionNum;
	}

	public void generateVotes() {
		int random=(int)Math.floor(Math.random()*candidates.length);
		candidates[random].callAddVotes(regionNum);
	}

	public void run(){
		for(int i=0; i<population;i++) {
			generateVotes();
		}
	}
}
