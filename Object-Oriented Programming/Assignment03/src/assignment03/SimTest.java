package assignment03;

import java.io.IOException;
//조교님 PPT설명 중에는 후보자가 받은 투표 수를 기반으로 오름차순으로 하라고 하셨는데, 출력 결과는 내림차순으로되어있습니다
//출력결과보다는 명시 사항이 더 중요하다고 생각하여 오름차순으로 구현했습니다.
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