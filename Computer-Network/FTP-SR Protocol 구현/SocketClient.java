

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class SocketClient {
	public static ArrayList<Integer> dropList=new ArrayList<Integer>();
	public static ArrayList<Integer> timeoutList=new ArrayList<Integer>();
	public static ArrayList<Integer> bitErrorList=new ArrayList<Integer>();
	public ArrayList<TimeOutData> delaySendData;
	public byte [][] saveData;
	public static byte [] ackCheck;
	public int count;
	public byte send_base;
	public byte sendCount;
	public long timeWatch[];
	public byte inWindowSend;
	int howmany=0;
	byte nextseqnum;
	int realAckNum;
	ArrayList<Byte> timeOutSeq;
	byte delayPacketInWindow;
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
		//for knowing the sequence number 
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
	public byte[] makeAckPacket(byte seq,int checkSum) {
		byte []ack=new byte[3];
		ack[0]=seq;
		System.arraycopy(converIntToByte(checkSum,2), 0, ack, 1, 2);
		return ack;
	}

	
	public void sendToServer(int size,String filename,Socket clientDataSocket,DataOutputStream outToServer,DataInputStream inFromServer)throws Exception {
		File sendFile=new File(filename);
		int share=size/1000;
		int remainder=(int)size%1000;
		byte []allData=Files.readAllBytes(sendFile.toPath());
		byte []data=null;
		nextseqnum=(byte)Math.round(Math.random()*15);
		outToServer.write(makeAckPacket(nextseqnum,0x0000));
		outToServer.write(makeAckPacket((byte)timeoutList.size(),0x0000));
		byte []checkSum;
		byte []chunkSize=null;
		byte []dataChunk=null;
		delaySendData=new ArrayList<TimeOutData>();
		//Role for sending to Server to not exceed the window size
		sendCount=0;
		saveData=new byte[16][1005];
		ackCheck=new byte[16];
		timeWatch=new long[16];
		send_base=nextseqnum;
		//Real amount of Data sent
		count=0;
		realAckNum=0;
		//the count number of sending Data
		howmany=0;
		timeOutSeq=new ArrayList<Byte>();
		byte []ack=new byte[3];
		delayPacketInWindow=1;
		inWindowSend=0;
		int delaySendSize=timeoutList.size();
		while(realAckNum<=share+delaySendSize) {
			//do not send when drop
			data=new byte[1005];
			checkSum=new byte[] {
					(byte)(0x0000>>8),
					(byte)(0x0000>>0)
			};
			sendData(data,checkSum,chunkSize,dataChunk,allData,remainder,share,outToServer);
			timeCheck(outToServer);

			if(timeOutSeq.size()!=0&&inWindowSend==0) {
				if(timeOutSeq.contains(send_base)&&delayPacketInWindow<=1) 
					continue;

			}
			if(!InWindow(nextseqnum)||howmany-1==share) {
				for(int i=0;i<inWindowSend;i++) {
					inFromServer.read(ack);
					denoteAck(ack,outToServer,data,checkSum,chunkSize,dataChunk,allData,remainder,share);
				}
			}
		}
		System.out.println();
		dropList.clear();
		bitErrorList.clear();
		timeoutList.clear();
		delaySendData.clear();
		timeOutSeq.clear();
		allData=null;
		data=null;
		dataChunk=null;
		chunkSize=null;
		checkSum=null;
	}
	public void sendData(byte[]data,byte[]checkSum,byte[] chunkSize,
			byte []dataChunk,byte[]allData,int remainder,int share,DataOutputStream outToServer)throws Exception {
		if(sendCount>=5)
			return;
		if(dropList.contains(howmany+1)) {
			int index=dropList.indexOf(howmany+1);
			dropList.remove(index);
			data[0]=nextseqnum;
			packetMake(checkSum,chunkSize,dataChunk,data,allData,howmany,remainder,share);
			System.arraycopy(data, 0, saveData[nextseqnum], 0, 1005);
			howmany++;
			sendCount++;
			delayPacketInWindow++;
			timeOutSeq.add(nextseqnum);
			System.out.print(nextseqnum+" ");
			timeWatch[nextseqnum]=System.currentTimeMillis();
			nextseqnum=nextseqnum==15?0:(byte)(nextseqnum+1);
		}
		else if(timeoutList.contains(howmany+1)) {
			int index=timeoutList.indexOf(howmany+1);
			timeoutList.remove(index);
			data[0]=nextseqnum;
			packetMake(checkSum,chunkSize,dataChunk,data,allData,howmany,remainder,share);
			System.arraycopy(data, 0, saveData[nextseqnum], 0, 1005);
			TimeOutData temp=new TimeOutData(nextseqnum,new byte[1005]);
			System.arraycopy(data, 0, temp.data, 0, 1005);
			delaySendData.add(temp);
			timeOutSeq.add(nextseqnum);
			howmany++;
			sendCount++;
			System.out.print(nextseqnum+" ");
			timeWatch[nextseqnum]=System.currentTimeMillis();
			nextseqnum=(nextseqnum)==15?0:(byte)(nextseqnum+1);
		}
		else {
			//In Window
			if(howmany<=share) {
				data[0]=nextseqnum;

				packetMake(checkSum,chunkSize,dataChunk,data,allData,howmany,remainder,share);
				if(bitErrorList.contains(howmany+1)) { 
					data[1]=(byte)(0xFFFF>>8);
					data[2]=(byte)(0xFFFF>>0);
					System.arraycopy(data, 0, saveData[nextseqnum], 0, 1005);
					bitErrorList.remove(bitErrorList.indexOf(howmany+1));
					timeOutSeq.add(nextseqnum);
					inWindowSend--;
				}
				//Make Packet
				outToServer.write(data);
				howmany++;
				sendCount++;
				inWindowSend++;
				System.out.print(nextseqnum+" ");
				timeWatch[nextseqnum]=System.currentTimeMillis();
				nextseqnum=(nextseqnum)==15?0:(byte)(nextseqnum+1);
			}
		}
	}
	public void timeCheck(DataOutputStream outToServer)throws Exception {
		if(delaySendData.size()!=0) {
			
			for(int i=0;i<delaySendData.size();i++) {
				if(System.currentTimeMillis()-delaySendData.get(i).startTime>=2000) {
					outToServer.write(delaySendData.get(i).data);
					System.out.println();
					System.out.println(delaySendData.get(i).sequence+" resend");
					inWindowSend++;
					delaySendData.remove(i);
					break;
				}
			}
		}
		for(byte i=0;i<16;i++) {
			if(timeWatch[i]!=0&&System.currentTimeMillis()-timeWatch[i]>=1000) {
				if(timeOutSeq.indexOf(i)!=0)
					break;
				saveData[i][1]=(byte)(0x0000>>8);
				saveData[i][2]=(byte)(0x0000>>0);
				outToServer.write(saveData[i]);
				System.out.println();
				System.out.println(saveData[i][0]+" time out & retransmitted");
				inWindowSend++;
				delayPacketInWindow--;
				timeWatch[i]=System.currentTimeMillis();
				timeOutSeq.remove(timeOutSeq.indexOf(i));
				saveData[i]=new byte[1005];

			}
		}
	}

	public void denoteAck(byte []ack,DataOutputStream output,byte[]data,byte[]checkSum,byte[]chunkSize,byte[]dataChunk,
			byte[]allData,int remainder,int share)throws Exception {
		byte seq=ack[0];
		System.out.print(seq+"acked ");
		timeWatch[seq]=0;
		inWindowSend--;
		realAckNum++;
		if(send_base==seq) {
			byte i=seq==15?0:(byte)(seq+1);
			while(true) {
				sendCount--;
				if(howmany<=share) {
					sendData(data,checkSum,chunkSize,dataChunk,allData,remainder,share,output);
				}
				if(ackCheck[i]==0) 
					break;
				ackCheck[i]=0;
				//Put plus equation in here
				i=(i)==15?0:(byte)(i+1);
			}
			send_base=i;
		}
		else if(InWindow(seq)) {
			ackCheck[seq]=1;
		}
	}
	public void packetMake(byte []checkSum,byte[] chunkSize,byte[] dataChunk,byte[] data,byte[]allData,int howmany,int remainder,int share) {
		System.arraycopy(checkSum, 0, data, 1, 2);
		if(howmany<share) {
			chunkSize=converIntToByte(1000,2);

			dataChunk=Arrays.copyOfRange(allData,howmany*1000, howmany*1000+1000);
		}
		else { 
			chunkSize=converIntToByte(remainder,2);
			dataChunk=Arrays.copyOfRange(allData,howmany*1000, howmany*1000+remainder);
		}
		System.arraycopy(chunkSize,0, data, 3, 2);
		System.arraycopy(dataChunk, 0, data, 5, dataChunk.length);
	}

	public boolean InWindow(int nextseqnum) {

		if(send_base<=11) {
			if(send_base+5>nextseqnum)
				return true;
			else return false;
		}
		else {
			if((nextseqnum<4&&send_base>nextseqnum+11)||(nextseqnum>send_base&&send_base+5>nextseqnum))
				return true;
			else return false;
		}
	}

}
