

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Arrays;

public class DataSocket extends Thread{
	public String filename;
	public long length;
	DataSocket(){
	}
	public DataSocket(String filename,long length) {
		this.filename=filename;
		this.length=length;
	}
	public static byte[] converIntToByte(int num,int size) {
		if(size==1)
			return new byte[] {
					(byte)num
		};
		else
			return new byte[] {
					(byte)(num >> 8),
					(byte)num };
	}
	public static void sendToClient(File file,DataOutputStream outToClient,DataInputStream inFromClient)throws Exception {

		long size=file.length();
		int share=(int)size/1000;
		byte []allData=Files.readAllBytes(file.toPath());
		int remainder=(int)size%1000;
		byte []data=new byte[1005];
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
				outToClient.write(data);
			}
			else {
				checkSum=converIntToByte(0x0000,2);
				System.arraycopy(checkSum, 0, data, 1, 2);
				chunkSize=converIntToByte(remainder,2);
				System.arraycopy(chunkSize,0, data, 3, 2);
				dataChunk=Arrays.copyOfRange(allData,i*1000, i*1000+remainder);
				System.arraycopy(dataChunk, 0, data, 5, dataChunk.length);
				outToClient.write(data);
			}
			inFromClient.read(ack);

		}
		allData=null;
		data=null;
	}
	public static void getFromClient(int size,DataInputStream inFromClient,String filename,DataOutputStream outToClient)throws Exception {
		byte []data=new byte[1];
		int share=(int)size/1000;
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
			inFromClient.read(data);
			int chunkSize=((data[3]&0xFF)<<8) | ((data[4]&0xFF)<<0);
			System.arraycopy(data,5,totalData,count*1000, chunkSize);		
			count++;
			outToClient.write(ack);
		}

		File newFile=new File(ServerMain.curPath+"\\"+filename);
		FileOutputStream outFile=new FileOutputStream(newFile);
		outFile.write(totalData);
		outFile.close();
	}
	public void run(){
		try {
			ServerSocket dataSocket=new ServerSocket(ServerMain.dataPort);
			Socket connectionSocket=null;
			BufferedReader inFromClient=null;
			DataOutputStream outToClient=null;
			DataInputStream inFromCli;
			while(true) {
				connectionSocket=dataSocket.accept();
				inFromClient=new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				outToClient=new DataOutputStream(connectionSocket.getOutputStream());
				inFromCli=new DataInputStream(connectionSocket.getInputStream());
				if(length==0) {
					sendToClient(new File(filename),outToClient,inFromCli);		
				}
				else {
					getFromClient((int)length,inFromCli,filename,outToClient);

				}
				inFromCli.close();
				outToClient.close();
				inFromClient.close();
				connectionSocket.close();
				dataSocket.close();

			}
		}
		catch(Exception e) {
		}
	}


}
