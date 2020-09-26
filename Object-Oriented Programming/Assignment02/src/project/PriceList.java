package project;

import java.util.Arrays;

public class PriceList {
	private Oil[][] pricelist;
	private int vertical;
	PriceList(){
		vertical=2;
		pricelist=new Oil[vertical][vertical];
	}
	public int getVertical() {
		return vertical;
	}
	public void setPriceList(int i,int j,Oil oil)throws FullArrayException {
		pricelist[i][j]=oil;
		pricelist[i][j].setOilPrice(0);
	}
	public void extendList(int amount) {
		this.vertical=this.vertical+amount;
		Oil [][]temp=new Oil[vertical][2];
		for(int i=0;i<pricelist.length;i++ ) {
			temp[i][0]=pricelist[i][0];
			temp[i][1]=pricelist[i][1];
		}
		this.pricelist=temp;
	}
	public String getRegionInfo(int i) {
		return pricelist[i][0].toString()+"\n\n"+pricelist[i][1].toString()+"\n";
	}
	public void printList() {
		for(int i=0;i<PriceListApp.numberOfCompany;i++) {
			System.out.println("Region #"+(i+1)+"\t"+pricelist[i][0].getOilPrice()+"\t\t"+pricelist[i][1].getOilPrice());
			System.out.println();
		}
	}
}
