package assignment01;

import java.util.Calendar;
import java.util.Scanner;

public class TimeTableApp {
	public static void main(String[] args) {
		Lecture lecture[] = new Lecture[10];
		lecture[0] = new Lecture("MONDAY", 1, "OOP", "mr.park", "ITBT808", 20);
		lecture[1] = new Lecture("TUESDAY", 2, "security", "mr.park", "ITBT816", 20);
		lecture[2] = new Lecture("WEDNESDAY", 1, "Datastructure", "mr.park", "ITBT808", 25);
		lecture[3] = new Lecture("THURSDAY", 6, "Network", "mr.yang", "ITBT503", 20);
		lecture[4] = new Lecture("FRIDAY", 9, "computer architecture", "mr.han", "ITBT507", 20);
		lecture[5] = new Lecture("TUESDAY", 5, "Digital logic", "mr.kim", "ITBT401", 15);
		lecture[6] = new Lecture("WEDNESDAY", 2, "OS", "mr.yoon", "ITBT606", 15);
		lecture[7] = new Lecture("THURSDAY", 3, "C-language", "mr.choi", "ITBT503", 20);
		lecture[8] = new Lecture("FRIDAY", 6, "Python", "mr.han", "ITBT504", 15);
		lecture[9] = new Lecture("TUESDAY", 9, "Ski-Board", "mr.kang", "ITBT404", 5);

		Scanner keyboard = new Scanner(System.in);
		String[] weeks = { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };
		Calendar cal = Calendar.getInstance();
		System.out.println("학생 수를 입력하세요");
		int student_num=keyboard.nextInt();
		Student []all_student=new Student[student_num];
		String name;
		int id;
		for(int i=0;i<student_num;i++) {
			System.out.println((i+1)+" 학생의 이름을 입력하세요");
			name=keyboard.next();
			System.out.println((i+1)+" 학생의 학번을 입력하세요");
			id=keyboard.nextInt();
			all_student[i]=new Student(name,id);
		}



		int enter;
		int choose;
		int id1, id2;
		String str,lec;
		boolean flag=true;
		do {
			System.out.println();
			System.out.println("학생의 시간표를 입력하려면 1번을 누르세요");
			System.out.println("특정 과목을 수강하는 학생 수를 출력하려면 2번을 누르세요");
			System.out.println("학생들의 시간표를 비교하려면 3번을 누르세요");
			System.out.println("학생의 시간표를 지우려면 4번을 누르세요");
			System.out.println("시간표를 출력하려면 5번을 누르세요");
			System.out.println("해당 학생의 특정 날짜 시간표를 출력하려면 6번을 누르세요");
			System.out.println("프로그램을 종료하려면 7번을 누르세요");
			enter=keyboard.nextInt();
			switch (enter) {
			case 1:
				System.out.println("시간표를 입력할 학생의 학번을 입력하세요");
				id=keyboard.nextInt();
				System.out.println("다음 중 추가할 시간표의 번호를 입력하세요");
				System.out.println();
				showClass();
				System.out.println();
				choose=keyboard.nextInt();
				if(lecture[choose-1].getEnrolled()>=lecture[choose-1].getMaxEnrolled()) {
					System.out.println("강의를 추가할 수 없는니다. : 최대 수강 인원 초과");
				}
				else if(find_student(id,all_student)==null){
					System.out.println("해당하는 학번이 없습니다.");
				}
				else {
					find_student(id,all_student).setScheduleInformation(lecture[choose-1]);
					System.out.println("시간이 정상적으로 추가되었습니다.");
				}
				break;

			case 2:
				System.out.println("과목의 이름을 입력하세요");
				lec=keyboard.next();
				for(int i=0;i<lecture.length;i++) {
					if(lecture[i].getLectureName().equals(lec)) {
						System.out.println(lec+" 과목을 수강하는 학생의 수는 "+lecture[i].getEnrolled());
					}
				}

				break;

			case 3:
				System.out.println("시간표를 비교할 첫번째 학생의 학번을 입력하세요");
				id1=keyboard.nextInt();
				System.out.println("두번째 학생의 학번을 입력하세요");
				id2=keyboard.nextInt();
				if(find_student(id1,all_student)==null||find_student(id2,all_student)==null) {
					System.out.println("해당 학번이 존재하지 않습니다.");
				}
				else if(!find_student(id1,all_student).equals(find_student(id2,all_student))){
					System.out.println("두 학생의 시간표는 다릅니다.");
				}
				else 
					System.out.println("두 학생의 시간표는 같습니다.");
				break;

			case 4:
				System.out.println("시간표를 삭제할 학생의 학번을 입력하세요");
				id1=keyboard.nextInt();
				
				if(find_student(id1,all_student)==null) {
					System.out.println("해당하는 학번이 존재하지않습니다.");
					break;}
				
				System.out.println("지울 시간표의 요일을 입력하세요");
				
				str=keyboard.next().toUpperCase();
				
				System.out.println("지울 시간표의 시간을 입력하세요");
				
				choose=keyboard.nextInt();
				
				find_student(id1,all_student).deleteSchedule(str, choose-1);
				
				System.out.println(find_student(id1,all_student).showTimetable());
				break;

			case 5:
				System.out.println("시간표를 출력할 학생의 학번을 입력하세요");
				id1=keyboard.nextInt();
				if(find_student(id1,all_student)==null) {
					System.out.println("해당하는 학번이 존재하지않습니다.");
					break;
				}
				System.out.println(find_student(id1,all_student).showTimetable());
				break;

			case 6:
				System.out.println("출력할 학생의 학번을 입력하세요");
				id1=keyboard.nextInt();

				if(find_student(id1,all_student)==null) {
					System.out.println("해당하는 학번이 존재하지않습니다.");
					break;}

				cal=find_student(id1, all_student).setInputDate();

				if(weeks[cal.get(Calendar.DAY_OF_WEEK)-1].equals("SUNDAY")||
						weeks[cal.get(Calendar.DAY_OF_WEEK)-1].equals("SATURDAY"))
				{	
					System.out.println("토요일 또는 일요일의 시간표는 존재하지 않습니다.");
				}
				else {
					System.out.println(find_student(id1,all_student).oneDaySchedule(weeks[cal.get(Calendar.DAY_OF_WEEK)-1]));
				}
				break;
			case 7:
				flag=false;
				break;

			default:
				System.out.println("Try again");
				break;
			}

		} while (flag);
	}

	//For finding the Student object as using id
	private static Student find_student(int id,Student [] stu) {
		for(int i=0;i<stu.length;i++) {
			if(stu[i].getStudentCode()==id)
				return stu[i];
		}
		return null;
	}
	private static void showClass() {
		System.out.println();
		System.out.println("(1) OOP - 월요일 - 1교시");
		System.out.println("(2) security - 화요일 - 2교시");
		System.out.println("(3) Datastructure - 수요일 - 1교시");
		System.out.println("(4) Network - 목요일 - 6교시");
		System.out.println("(5) computer architecture - 금요일 - 8교시");
		System.out.println("(6) Digital design - 화요일 -5교시");
		System.out.println("(7) OS - 수요일 - 2교시");
		System.out.println("(8) C-language - 목요일 - 3교시");
		System.out.println("(9) Python - 금요일 - 6교시");
		System.out.println("(10) Ski-board - 화요일 - 9교시");
	}
}
