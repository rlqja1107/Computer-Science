

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class ServerMain {
	public static int commendPort=2020;
	public static int dataPort=2121;
	public static String curPath=System.getProperty("user.dir");
	public static String message(int num,String path) {
		switch(num) {
		case 502:
			return num+" Failed for Unknown reason";
		case 501:
			return num+" Failed Directory name is invalid";
		case 200: return num+ " Moved to "+path;
		case 201:{
			File temp=new File(path);
			int count= temp.listFiles().length;
			return num+" Comprising "+count+" entries";	
		}
		case 401:
			return num+" Failed No such file exists";
		case 203:
			return num+" Ready to receive";
		case 300:
			return num+" Containing "+path+"bytes in total";
		default :
			return "not";
		}
	}
	public static void sendToUser(String sentence,String source,DataOutputStream output) {
		System.out.println("Response :"+sentence);
		try {
			output.writeBytes(sentence+"\n"+source);
		}
		catch(Exception e) {
		}
	}
	public static void function(String first,String second,DataOutputStream output,DataInputStream input) throws Exception{
		ChangeDirectory cd=new ChangeDirectory();
		DataSocket dataSocket=new DataSocket();
		String absolPath=cd.getabsolutePath(second);
		switch(first.toUpperCase()) {
		case "CD":
			if(cd.changePath(absolPath)) 
				sendToUser(message(200,absolPath),"",output);		
			else 
				sendToUser(message(501,absolPath),"",output);		
			break;
		case "LIST":
			File file=new File(absolPath);
			if(cd.isDir(file)) {
				String response=cd.listFile(file);
				String responseMessage=message(201,absolPath);

				sendToUser(responseMessage,"",output);

				output.writeBytes(response);
			}
			else 
				sendToUser(message(501,absolPath),"",output);
			break;
		case "GET":
			File curFile=new File(absolPath);
			if(curFile.exists()&&curFile.isFile()) {
				System.out.println("Response: 300"+ " Containing "+ curFile.length()+"bytes in total");
				output.writeBytes(message(300,Long.toString(curFile.length())));
				dataSocket.filename=absolPath;
				dataSocket.length=0L;
				dataSocket.start();
			}
			else {
				sendToUser(message(401,absolPath),"",output);
			}
			break;
		case "PUT":
			long length=input.readLong();
			System.out.println("Request :"+length);
			sendToUser(message(203,null),"",output);
			dataSocket.filename=second;
			dataSocket.length=length;
			dataSocket.start();
			break;
		case "QUIT":
			break;
		default:
			break;
		}
	}
	public static String convertToString(DataInputStream inFromServer)throws Exception {
		byte []data=new byte[1000];
		int length=inFromServer.read(data);
		data=Arrays.copyOf(data, length);
		return new String(data);
	}
	public static void main(String[] args) {
		//when you put two variable, there would be commend port and data port.
		//when you put one variable, the would be commend port
		if(args.length==2) {
			commendPort=Integer.parseInt(args[0]);
			dataPort=Integer.parseInt(args[1]);
		}
		else if(args.length==1) {
			commendPort=Integer.parseInt(args[0]);
		}
		try {
			ServerSocket welcomeSocket=new ServerSocket(commendPort);
			System.out.println("Server Open");
			curPath=System.getProperty("user.dir");
			while(true) {
				Socket commendSocket=welcomeSocket.accept();
				DataOutputStream outToClient=new DataOutputStream(commendSocket.getOutputStream());
				DataInputStream inFromClient=new DataInputStream(commendSocket.getInputStream());
				String inputCommend=convertToString(inFromClient);

				System.out.println("Request: "+inputCommend);
				String list[]=inputCommend.split(" ",2);
				String first=list[0];
				String second="";
				if(list.length==1) 	
					second=" ";
				else second=list[1];
			
				
				function(first,second,outToClient,inFromClient);
				inFromClient.close();
				outToClient.close();
				commendSocket.close();
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
