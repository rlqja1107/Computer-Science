
public class TimeOutData{
	byte sequence;
	byte []data;
	long startTime;
	public TimeOutData(byte sequence,byte[]data) {
		this.sequence=sequence;
		this.data=data;
		setStartTime(System.currentTimeMillis());
	}
	public void setStartTime(long time) {
		this.startTime=time;
	}

}
