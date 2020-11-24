

import java.io.*;

public class ClientMain {
	public static String Server="127.0.0.1";
	public static int commendPort=2020;
	public static int dataPort=2121;
	public static String path;
	public static void main(String []args) {
		if(args.length==3) {
			Server=args[0];
			commendPort=Integer.parseInt(args[1]);
			dataPort=Integer.parseInt(args[2]);
		}
		path=System.getProperty("user.dir");
		outer:
			while(true) {
				try {
//					System.out.println("================================================");
//					System.out.println("Enter the Quit if you want to quit this program.");
//					System.out.println("================================================");
					BufferedReader inFromUser=new BufferedReader(new InputStreamReader(System.in));
					String sentence=inFromUser.readLine();
					String split[]=sentence.split(" ",2);
					switch(split[0].toUpperCase()) {
					case "QUIT":
						System.out.println("Client is Closed.");
						break outer;
					case "GET":case "CD":case "LIST":
						SocketClient commendSocket=new SocketClient();
						commendSocket.openCommendChannel(Server, commendPort, sentence,false);

						break;
					case "PUT":
						if(isCanPut(split[1])) {
							SocketClient put_commend_socket=new SocketClient();
							put_commend_socket.openCommendChannel(Server, commendPort, sentence,true);
						}
						else {
							System.out.println("There is no file");
						}
						break;
					case "DROP": case "BITERROR":case "TIMEOUT":
						addErrorList(split);
						break;
					default:
						System.out.println("Please input other method");
						break;
					}
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}
	}
	public static void addErrorList(String split[]) {
		if(split.length>=2) {
			String errorCount[]=split[1].split(",");
			for(int i=0;i<errorCount.length;i++) {
				if(split[0].toUpperCase().equals("DROP")) 
					SocketClient.dropList.add(Integer.parseInt(errorCount[i].substring(1)));			
				else if(split[0].toUpperCase().equals("TIMEOUT"))
					SocketClient.timeoutList.add(Integer.parseInt(errorCount[i].substring(1)));			
				else	
					SocketClient.bitErrorList.add(Integer.parseInt(errorCount[i].substring(1)));			
			}
		}
		else
			System.out.println("Please Insert One more Packet number");
	}
	public static boolean isCanPut(String filename) {

		File temp=new File(path+"\\"+filename);
		if(temp.exists()&&temp.isFile())
			return true;
		else return false;
	}
}

