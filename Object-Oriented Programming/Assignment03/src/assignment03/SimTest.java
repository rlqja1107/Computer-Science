package assignment03;

import java.io.IOException;
//������ PPT���� �߿��� �ĺ��ڰ� ���� ��ǥ ���� ������� ������������ �϶�� �ϼ̴µ�, ��� ����� �����������εǾ��ֽ��ϴ�
//��°�����ٴ� ��� ������ �� �߿��ϴٰ� �����Ͽ� ������������ �����߽��ϴ�.
public class SimTest {
	
	private static final String INPUTFILE = "resource\\inputfile.txt";
	private static final String OUTPUTFILE = "resource\\outputfile.txt";

	public static void main(String[] args) {
		try {
			ElectionSim eSim = new ElectionSim(INPUTFILE, OUTPUTFILE);
			eSim.runSimulation();
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}