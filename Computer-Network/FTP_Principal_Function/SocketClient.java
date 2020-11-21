

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Arrays;

public class SocketClient {

	public void showFileList(String list,int length) {
		String []fileList=list.split(",",2*length);
		for(int i=0;i<fileList.length;i++) {
			if(i%2==0)
				System.out.print(fileList[i]+",");
			else if(i!=fileList.length-1)
				System.out.println(fileList[i]);
			else System.out.println(fileList[i]);
		}
	}
	public void openCommendChannel(String server,int port,String sentence,boolean putCommend)throws Exception {
		Socket clientCommendSocket=new Socket(server,port);

		DataOutputStream outToServer=new DataOutputStream(clientCommendSocket.getOutputStream());
		BufferedReader inFromServer=new BufferedReader(new InputStreamReader(clientCommendSocket.getInputStream()));
		outToServer.write(sentence.getBytes());
		if(putCommend) {
			String absolutePath=ClientMain.path+"\\"+sentence.split(" ",2)[1];
			File temp=new File(absolutePath);
			outToServer.writeLong(temp.length());		
		}
		String inputData=inFromServer.readLine();

		errorOrNot(inputData,sentence,inFromServer,outToServer);
		inFromServer.close();
		outToServer.close();
		clientCommendSocket.close();
	}
	public void errorOrNot(String sentence,String filename,BufferedReader inFromServer,DataOutputStream output)throws Exception {
		String split[]=sentence.split(" ");

		switch(Integer.parseInt(split[0])) {
		case 401:
			System.out.println("Failed Such file does not exist!");
			break;
		case 200:
			System.out.println(sentence.split(" ",4)[3]);
			break;
		case 201:
			String response=sentence.split(" ",4)[2];
			String list=inFromServer.readLine();
			showFileList(list,Integer.parseInt(response));
			break;
		case 501:
			System.out.println(sentence.substring(4));
			break;
		case 203:
			String name=filename.split(" ",2)[1];
			File outputFile=new File(ClientMain.path+"\\"+name);			
			System.out.println(outputFile.getName()+" transferred / "+outputFile.length()+" bytes");
			openDataChannel((int)outputFile.length(),name,true);
			break;

		case 300:
			int fileIndex=filename.lastIndexOf("\\");
			String fileName;
			if(fileIndex==-1)
				fileName=filename.split(" ")[1];
			else {
				String fileList[]=filename.split("\\\\");
				fileName=fileList[fileList.length-1];
			}
			int lastIndex=sentence.lastIndexOf("bytes");
			int size=Integer.parseInt(sentence.substring(15,lastIndex));
			System.out.println("Received "+fileName+" /  "+size+"bytes");
			openDataChannel(size,fileName,false);
			break;
		default:
			System.out.println("There is other case");
			break;
		}
	}
	public byte[] converIntToByte(int num,int size) {
		if(size==1)
			return new byte[] {
					(byte)num
		};
		else
			return new byte[] {
					(byte)(num >> 8),
					(byte)num };
	}
	public void sendToServer(int size,String filename,Socket clientDataSocket,DataOutputStream outToServer,DataInputStream inFromServer)throws Exception {
		File sendFile=new File(filename);
		int share=size/1000;
		int remainder=(int)size%1000;
		byte []allData=Files.readAllBytes(sendFile.toPath());
		byte []data=new byte[1005];
		byte sequenceNumber=0;
		data[0]=sequenceNumber;
		byte []checkSum;
		byte []chunkSize;
		byte []dataChunk;
		byte []ack=new byte[3];
		for(int i=0;i<share+1;i++) {
			if(i!=share) {				
				checkSum=converIntToByte(0x0000,2);
				System.arraycopy(checkSum, 0, data, 1, 2);
				chunkSize=converIntToByte(1000,2);
				System.arraycopy(chunkSize,0, data, 3, 2);
				dataChunk=Arrays.copyOfRange(allData,i*1000, i*1000+1000);
				System.arraycopy(dataChunk, 0, data, 5, dataChunk.length);
				outToServer.write(data);
			}
			else {
				checkSum=converIntToByte(0x0000,2);
				System.arraycopy(checkSum, 0, data, 1, 2);
				chunkSize=converIntToByte(remainder,2);
				System.arraycopy(chunkSize,0, data, 3, 2);
				dataChunk=Arrays.copyOfRange(allData,i*1000, i*1000+remainder);
				System.arraycopy(dataChunk, 0, data, 5, dataChunk.length);
				outToServer.write(data);
			}
			inFromServer.read(ack);
			System.out.print("#");
		}
		System.out.print("    Completed...");
		System.out.println();
		allData=null;
		data=null;
	}
	public void getFromServer(int size,DataInputStream inFromServer,String filename,DataOutputStream outToClient)throws Exception {
		byte []data=new byte[1];
		int share=size/1000;
		byte []totalData=new byte[size];
		int count=0;
		byte []ack=new byte[3];
		byte []seq=new byte[] {
				(byte)1
		};
		byte []checkSum=new byte[] {
				(byte)(0xFFFF>>8),
				(byte)0xFFFF
		};
		System.arraycopy(seq, 0, ack, 0, 1);
		System.arraycopy(checkSum, 0, ack, 1, 2);
		while(count<=share) {
			data=new byte[1005];
			inFromServer.read(data);
			int chunkSize=((data[3]&0xFF)<<8) | ((data[4]&0xFF)<<0);
			System.arraycopy(data,5,totalData,count*1000, chunkSize);
			count++;
			System.out.print("# ");
			outToClient.write(ack);
		}
		System.out.println("             Completed...");
		File newFile=new File(ClientMain.path+"\\"+filename);
		FileOutputStream outFile=new FileOutputStream(newFile);
		outFile.write(totalData);
		outFile.close();
	}
	//sendOrGet is true if Put Method
	//sedOrGet is false if Get Method 
	public void openDataChannel(int size,String filename,boolean sendOrGet)throws Exception {
		Socket clientDataSocket=new Socket(ClientMain.Server,ClientMain.dataPort);
		DataOutputStream outToServer=new DataOutputStream(clientDataSocket.getOutputStream());
		DataInputStream inFromServer=new DataInputStream(clientDataSocket.getInputStream());

		if(!sendOrGet) 		
			getFromServer(size,inFromServer,filename,outToServer);
		else 
			sendToServer(size,filename,clientDataSocket,outToServer,inFromServer);

		outToServer.close();
		inFromServer.close();
		clientDataSocket.close();
	}

}
