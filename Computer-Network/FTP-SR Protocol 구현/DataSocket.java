

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class DataSocket extends Thread{
	public String filename;
	public long length;
	public static  byte rcvBase;
	public static int count;
	static int saveCount;
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
		count=0;
		byte []ack=new byte[3];
		byte []checkSum=new byte[] {
				(byte)(0x0000>>8),
				(byte)0x0000>>0
		};
		//At first, receive the sequence number
		inFromClient.read(ack);
		rcvBase=ack[0];
		inFromClient.read(ack);
		byte delaySize=ack[0];
		ArrayList<byte[]> buffer=new ArrayList<byte[]>();

		byte checkAck[]=new byte[16];
		byte ackSequence;
		saveCount=0;
		while(count<=share+delaySize) {
			data=new byte[1005];
			inFromClient.read(data);
			//int chunkSize=((data[3]&0xFF)<<8) | ((data[4]&0xFF)<<0);
			ackSequence=(byte)((data[0]&0xFF)<<0);
			//bit error check
			if((((data[1]&0xFF)<<8)|((data[2]&0xFF)<<0))==0x0000) {
				byte rangeValue= checkRcvSequence(ackSequence,checkAck,outToClient,buffer,totalData,data);
				//in - order case
				if(rangeValue==2) {
					ack[0]=ackSequence;
					System.arraycopy(checkSum, 0, ack, 1, 2);
					System.out.print(ack[0]+"ack ");
					outToClient.write(ack);
					count++;
				}
				//out - order case
				else if(rangeValue==3) {
					//Put in Buffer
					byte[] tempBuffer=new byte[1005];
					System.arraycopy(data, 0, tempBuffer, 0, 1005);
					buffer.add(tempBuffer);
					ack[0]=ackSequence;
					System.out.print(ack[0]+"ack ");
					System.arraycopy(checkSum, 0, ack, 1, 2);
					outToClient.write(ack);
					count++;
				}
			}
		}	
		System.out.println();
		System.out.println("finish");
		File newFile=new File(ServerMain.curPath+"\\"+filename);
		FileOutputStream outFile=new FileOutputStream(newFile);
		buffer.clear();
		outFile.write(totalData);
		outFile.close();
	}
	
	public static void bufferToUpperLayer(ArrayList<byte[]>buffer,byte[]totalData,byte curseq) {
		
		curseq=(curseq==15)?0:(byte)(curseq+1);
		while(buffer.size()>0) {
			byte seq=buffer.get(0)[0];
			if(curseq==seq) { 
				System.arraycopy(buffer.get(0), 5, totalData, saveCount*1000, ((buffer.get(0)[3]&0xFF)<<8)|((buffer.get(0)[4]&0xFF)<<0));
				saveCount++;
				curseq=(curseq==15)?0:(byte)(curseq+1);
				//plus one at total number of data; 
				buffer.remove(0);
			}
			else break;
		}
	}
	public static byte checkRcvSequence(byte ackseq,byte []checkAck,DataOutputStream output,ArrayList<byte[]>buffer,byte[]totalData,byte[]data)throws Exception {
		if(ackseq==rcvBase) {
			byte i=rcvBase==15?0:(byte)(rcvBase+1);
			while(true) {
				if(checkAck[i]==0) 
					break;
				checkAck[i]=0;
				i=i==15?0:(byte)(i+1);
			}
			System.arraycopy(data, 5, totalData, saveCount*1000, ((data[3]&0xFF)<<8)|((data[4]&0xFF)<<0));
			saveCount++;
			//count++;
			//have to check buffer whether it is empty or not
			if(buffer.size()!=0) 
				bufferToUpperLayer(buffer,totalData,ackseq);
			rcvBase=i;
			return 2;
		}
		else {
			if(rcvBase<=11){
				//In proper range
				if(ackseq>rcvBase&&rcvBase+5>ackseq) {
					checkAck[ackseq]=1;
					return 3;
				}
				else {
					output.write(makeAckPacket(ackseq,0x0000));
					count++;
					System.out.println(ackseq+" resend");
					return 1;
				}
			}
			else {
				if((ackseq<4&&rcvBase>ackseq+11)||(ackseq>rcvBase&&rcvBase+5>ackseq)) {
					checkAck[ackseq]=1;
					return 3;
				}
				else {
					output.write(makeAckPacket(ackseq,0x0000));
					count++;
					System.out.println(ackseq+" resend");
					return 1;
				}
			}
		}
	}
	public static byte[] makeAckPacket(byte seq,int checkSum) {
		byte []ack=new byte[3];
		ack[0]=seq;
		System.arraycopy(converIntToByte(checkSum,2), 0, ack, 1, 2);
		return ack;
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
				break;
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}


}
