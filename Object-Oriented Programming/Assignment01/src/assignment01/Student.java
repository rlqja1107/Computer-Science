package assignment01;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;


public class Student {
	private String studentName;
	private int studentCode;
	Lecture timeTable[][] = new Lecture[10][5];

	public enum DAYS {
		MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY
	}

	public Student() {
		for(int i=0;i<timeTable.length;i++) {
			for(int j=0; j<timeTable[i].length;j++) {
				 timeTable[i][j]=new Lecture();
			}
		}
		
	}

	public Student(String studentName, int studentCode) {
		this();
		this.studentName=studentName;
		this.studentCode=studentCode;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName=studentName;
	}

	public int getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(int studentCode) {
		this.studentCode=studentCode;
	}

	public void setScheduleInformation(Lecture lecture) {
		int day=DAYS.valueOf(lecture.getDay()).ordinal();
		if(lecture.getEnrolled()<lecture.getMaxEnrolled()&&timeTable[lecture.getTime()][day].checkLecture()) {
			timeTable[lecture.getTime()-1][day]=lecture;
			lecture.incEnrolled();
		} 	
	}
	public String showTimetable() {
		String str=String.format("%2s", "");
		DAYS array[]=DAYS.values();
		for(int i=0;i<array.length;i++) {
			str+=String.format("%20s",array[i].toString());
		}
		str+="\n";
		for(int i=0;i<10;i++) {
			str+=String.format("%2s",(i+1));
			for(int j=0;j<array.length;j++) {
				str+=String.format("%20s", timeTable[i][array[j].ordinal()].getLectureName());
			}
			str+="\n";
		}
		return str;
	}

	public boolean equals(Student stu) {
		if(stu==null)return false;
		else {
			for(int i=0;i<5;i++) {
				for(int j=0;j<10;j++) {
					if(!this.timeTable[j][i].equals(stu.timeTable[j][i])) {
						return false;
					}								
					}
				}
			}
		return true;
		
	}
	
	public void deleteSchedule(String day, int period)
	{
		int del_day=DAYS.valueOf(day).ordinal();
		timeTable[period][del_day].decEnrolled();
		timeTable[period][del_day]=new Lecture();
	}
	
	public Calendar setInputDate()
	{System.out.println("Enter the date");
	Scanner scan=new Scanner(System.in);
	String date=scan.next();
	Calendar cal=Calendar.getInstance();
	cal.set(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(4,6))-1,Integer.parseInt(date.substring(6,8)));
	return cal;

	}
	
	public String oneDaySchedule(String day) {
		String str="\n";
		DAYS e_day=DAYS.valueOf(day);
		str+=e_day+"\n";
		for(int i=0;i<10;i++)
			str+=timeTable[i][e_day.ordinal()].getLectureName()+"\n";
		return str;
	}

}
