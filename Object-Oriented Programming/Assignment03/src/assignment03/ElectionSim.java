package assignment03;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class ElectionSim {

	private String outputFile;
	private int population;
	private Candidate[] candidates;
	private Region[] regions;
	public ElectionSim(String inputFile, String outputFile) throws IOException {
		this.outputFile=outputFile;
		Scanner scan=null;
		try {
			scan= new Scanner(new FileInputStream(inputFile));
		}
		catch(FileNotFoundException e) {
			System.err.print(e.getMessage());
		}
		scan.next();
		population=scan.nextInt();
		scan.next();
		candidates=new Candidate[scan.nextInt()];
		scan.nextLine();
		for(int i=0;i<candidates.length;i++) {
			candidates[i]=new Candidate(scan.nextLine(),population);
		}
		scan.next();
		regions=new Region[scan.nextInt()];
		scan.nextLine();
		for(int i=0; i<regions.length;i++) {
			regions[i]=new Region(scan.next(),scan.nextInt(),scan.nextInt(),candidates);
		}
		scan.close();

	}

	public void saveData(){
		PrintWriter writer=null;
		try {
			writer=new PrintWriter(new FileOutputStream(outputFile));
		}
		catch(IOException e) {System.out.println(e.getMessage());
		System.exit(0);
		}
		Arrays.sort(candidates);
		for(int i=0; i<candidates.length;i++) {
			writer.println(candidates[i].toString());
			for(int k=0;k<regions.length;k++) {
				writer.println(regions[k].getRegionName()+": "+candidates[i].toRegionString(regions[k].getRegionNum()));
			}
		
		}

		writer.close();

	}

	public void runSimulation() throws InterruptedException {
		int i;
		
		for( i=0;i<regions.length;i++) {
			regions[i].start();
		}
		for( i=0;i<regions.length;i++) {
			regions[i].join();
		}
		saveData();


	}
}
