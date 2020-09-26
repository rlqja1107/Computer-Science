package project;

import java.util.Scanner;

public class PriceListApp {
	public static int numberOfCompany=0;
	public static void main(String args[]) {
		Scanner scan=new Scanner(System.in);
		PriceList list=new PriceList();
		int number;
		int view;
		boolean flag=true;
		do {
			showMenu();
			System.out.print("choose a menu : ");
			number=scan.nextInt();
			switch(number){
			case 1:
				if((addOil(0,list,scan)==0)) {
					System.out.println();
						addOil(1,list,scan);
				}
				break;
			case 2:
				System.out.print("region number to view : ");
				view=scan.nextInt();
				if(view>list.getVertical())
					System.out.println("out of index");
				else
				System.out.println(list.getRegionInfo(view-1));
				
				break;
			case 3:
				System.out.println("\t\t Gasoline\t Diesel");
				System.out.println("==============================================");
				list.printList();
				
				
				break;
			case 4:
				flag=false;
				System.out.println("Exit application");
				break;
			default:
				System.out.println("Insert 1~4");
				break;
		}
		}while(flag);
		
		
	}
	public static int addOil(int oil,PriceList list,Scanner scan) {
		
		try {
			if(numberOfCompany>=list.getVertical())
				throw new FullArrayException();
			System.out.print("company : ");
			String company=scan.next();
			System.out.print("supply price : ");
			int price=scan.nextInt();
			if(oil==0) {
			list.setPriceList(numberOfCompany, 0,new Gasoline(price,company) );
			System.out.println("gasoline added");
			}
			else {
				list.setPriceList(numberOfCompany, 1, new Diesel(price,company));
				System.out.println("diesel added");
				numberOfCompany++;
			}
			return 0;
		}
		catch(FullArrayException e) {
			System.out.println(e.getMessage());
			int num= scan.nextInt();
			list.extendList(num);
			System.out.println("list is extended");
			System.out.println();
			addOil(0,list,scan);
			System.out.println();
			addOil(1,list,scan);
			return -1;
		}
	}
	public static void showMenu() {
		System.out.println();
		System.out.println("======== Main Menu ========");
		System.out.println("(1) Add a oil to price list\n"+
				"(2) View Region Information\n"+
				"(3) Print the entire price list\n"+
				"(4) Exit the program");
		System.out.println("===========================");
	}
}
