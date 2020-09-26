package assignment03;

public class Candidate implements Comparable {

	private String name;
	private int numVotes = 0;
	private static  Vote[] votes;

	public Candidate(String name,int maxVotes) {
		this.name=name;
		votes=new Vote[maxVotes];
	}

	public String toString() {
		String str="-----------Candidate-----------\n";
		return str+"Name: "+this.name+"\n"+"Total Votes: "+this.numVotes;
	}

	public int toRegionString(int regionNum) {
		int count=0;
		for(int i=0;i<numVotes;i++) {
			if(votes[i].regionNum==regionNum)
				count++;
		}
		return count;
	}
	
	//Need to synchronized for preventing playing method together
	//Use anonymouse Object to Call inner class method
	public synchronized void  callAddVotes(int regionNum) {
		new Vote(regionNum).addVote(regionNum);
	}

	@Override
	public int compareTo(Object obj) {
		if(obj==null)throw new NullPointerException();
		else if(obj.getClass()!=this.getClass())throw new ClassCastException();
		else {
			Candidate temp=(Candidate)obj;
			if(this.numVotes>temp.numVotes)return 1;
			else if(this.numVotes<temp.numVotes)return -1;
			else return 0;
		}
	}

	private class Vote {

		private int regionNum;

		public Vote(int regionNum) {
			this.regionNum=regionNum;
		}

		public synchronized void addVote(int regionNum){
			votes[numVotes++]=new Vote(regionNum);
		
		}
	}
}
