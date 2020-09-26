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
		System.out.println("�л� ���� �Է��ϼ���");
		int student_num=keyboard.nextInt();
		Student []all_student=new Student[student_num];
		String name;
		int id;
		for(int i=0;i<student_num;i++) {
			System.out.println((i+1)+" �л��� �̸��� �Է��ϼ���");
			name=keyboard.next();
			System.out.println((i+1)+" �л��� �й��� �Է��ϼ���");
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
			System.out.println("�л��� �ð�ǥ�� �Է��Ϸ��� 1���� ��������");
			System.out.println("Ư�� ������ �����ϴ� �л� ���� ����Ϸ��� 2���� ��������");
			System.out.println("�л����� �ð�ǥ�� ���Ϸ��� 3���� ��������");
			System.out.println("�л��� �ð�ǥ�� ������� 4���� ��������");
			System.out.println("�ð�ǥ�� ����Ϸ��� 5���� ��������");
			System.out.println("�ش� �л��� Ư�� ��¥ �ð�ǥ�� ����Ϸ��� 6���� ��������");
			System.out.println("���α׷��� �����Ϸ��� 7���� ��������");
			enter=keyboard.nextInt();
			switch (enter) {
			case 1:
				System.out.println("�ð�ǥ�� �Է��� �л��� �й��� �Է��ϼ���");
				id=keyboard.nextInt();
				System.out.println("���� �� �߰��� �ð�ǥ�� ��ȣ�� �Է��ϼ���");
				System.out.println();
				showClass();
				System.out.println();
				choose=keyboard.nextInt();
				if(lecture[choose-1].getEnrolled()>=lecture[choose-1].getMaxEnrolled()) {
					System.out.println("���Ǹ� �߰��� �� ���´ϴ�. : �ִ� ���� �ο� �ʰ�");
				}
				else if(find_student(id,all_student)==null){
					System.out.println("�ش��ϴ� �й��� �����ϴ�.");
				}
				else {
					find_student(id,all_student).setScheduleInformation(lecture[choose-1]);
					System.out.println("�ð��� ���������� �߰��Ǿ����ϴ�.");
				}
				break;

			case 2:
				System.out.println("������ �̸��� �Է��ϼ���");
				lec=keyboard.next();
				for(int i=0;i<lecture.length;i++) {
					if(lecture[i].getLectureName().equals(lec)) {
						System.out.println(lec+" ������ �����ϴ� �л��� ���� "+lecture[i].getEnrolled());
					}
				}

				break;

			case 3:
				System.out.println("�ð�ǥ�� ���� ù��° �л��� �й��� �Է��ϼ���");
				id1=keyboard.nextInt();
				System.out.println("�ι�° �л��� �й��� �Է��ϼ���");
				id2=keyboard.nextInt();
				if(find_student(id1,all_student)==null||find_student(id2,all_student)==null) {
					System.out.println("�ش� �й��� �������� �ʽ��ϴ�.");
				}
				else if(!find_student(id1,all_student).equals(find_student(id2,all_student))){
					System.out.println("�� �л��� �ð�ǥ�� �ٸ��ϴ�.");
				}
				else 
					System.out.println("�� �л��� �ð�ǥ�� �����ϴ�.");
				break;

			case 4:
				System.out.println("�ð�ǥ�� ������ �л��� �й��� �Է��ϼ���");
				id1=keyboard.nextInt();
				
				if(find_student(id1,all_student)==null) {
					System.out.println("�ش��ϴ� �й��� ���������ʽ��ϴ�.");
					break;}
				
				System.out.println("���� �ð�ǥ�� ������ �Է��ϼ���");
				
				str=keyboard.next().toUpperCase();
				
				System.out.println("���� �ð�ǥ�� �ð��� �Է��ϼ���");
				
				choose=keyboard.nextInt();
				
				find_student(id1,all_student).deleteSchedule(str, choose-1);
				
				System.out.println(find_student(id1,all_student).showTimetable());
				break;

			case 5:
				System.out.println("�ð�ǥ�� ����� �л��� �й��� �Է��ϼ���");
				id1=keyboard.nextInt();
				if(find_student(id1,all_student)==null) {
					System.out.println("�ش��ϴ� �й��� ���������ʽ��ϴ�.");
					break;
				}
				System.out.println(find_student(id1,all_student).showTimetable());
				break;

			case 6:
				System.out.println("����� �л��� �й��� �Է��ϼ���");
				id1=keyboard.nextInt();

				if(find_student(id1,all_student)==null) {
					System.out.println("�ش��ϴ� �й��� ���������ʽ��ϴ�.");
					break;}

				cal=find_student(id1, all_student).setInputDate();

				if(weeks[cal.get(Calendar.DAY_OF_WEEK)-1].equals("SUNDAY")||
						weeks[cal.get(Calendar.DAY_OF_WEEK)-1].equals("SATURDAY"))
				{	
					System.out.println("����� �Ǵ� �Ͽ����� �ð�ǥ�� �������� �ʽ��ϴ�.");
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
		System.out.println("(1) OOP - ������ - 1����");
		System.out.println("(2) security - ȭ���� - 2����");
		System.out.println("(3) Datastructure - ������ - 1����");
		System.out.println("(4) Network - ����� - 6����");
		System.out.println("(5) computer architecture - �ݿ��� - 8����");
		System.out.println("(6) Digital design - ȭ���� -5����");
		System.out.println("(7) OS - ������ - 2����");
		System.out.println("(8) C-language - ����� - 3����");
		System.out.println("(9) Python - �ݿ��� - 6����");
		System.out.println("(10) Ski-board - ȭ���� - 9����");
	}
}
