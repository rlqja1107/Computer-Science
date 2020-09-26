package assignment01;

public class Lecture {
	private String day;
	private int time;
	private String lectureName;
	private String professor;
	private String roomNumber;
	private int Enrolled;
	private int maxEnrolled;
	
	
	public Lecture()
	{
		lectureName="----";
		professor="None";
		roomNumber="None";
		
	}
	
	public Lecture(String day, int time, String lectureName, String professor,
			String roomNumber, int maxEnrolled)
	{
		this.day=day;
		this.time=time;
		this.lectureName=lectureName;
		this.professor=professor;
		this.roomNumber=roomNumber;
		this.maxEnrolled=maxEnrolled;
		this.Enrolled=0;
		
	}
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day=day;
	}

	public int getTime() {
		return time;
		}

	public void setTime(int time) {
		this.time=time;
	}

	public String getLectureName() {
		return lectureName;
	}

	public void setLectureName(String lectureName) {
		this.lectureName=lectureName;
	}

	public String getProfessor() {
		return professor;
	}

	public void setProfessor(String professor) {
		this.professor=professor;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber=roomNumber;
	}
	
	public int getMaxEnrolled() {
		return maxEnrolled;
	}
	
	public void setMaxEnrolled(int maxEnrolled) {
	    this.maxEnrolled=maxEnrolled;
	}
	public int getEnrolled() {
		return Enrolled;
	}
	public void incEnrolled() {
		Enrolled++;
	}
	public void decEnrolled() {
		Enrolled--;
	}
	
	public boolean equals(Lecture s) {
		if(s==null)return false;
		else {
			return this.getLectureName()==s.getLectureName()&&
					this.getProfessor()==s.getProfessor()&&
					this.getRoomNumber()==s.getRoomNumber();
		}
	}

	public boolean checkLecture() {
		if(this.getLectureName().equals("----")) {
			return true;
		}
		else {
			return false;
		}
			
	}
	
	
}