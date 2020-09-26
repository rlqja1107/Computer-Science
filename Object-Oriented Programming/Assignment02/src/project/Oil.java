package project;

public abstract class Oil {
	private String company;
	private String oilType;
	private double consumerPrice;
	Oil(String company, String oilType){
		this.company=company;
		this.oilType=oilType;
		consumerPrice=0;
	}
	public double getconsumerPrice() {
		return consumerPrice;
	}
	public void setconsumerPrice(double price) {
		this.consumerPrice=price;
	}
	public String toString() {
		return "company : "+company+"\n"+"oil type : "+oilType;
	}
	public abstract double getOilPrice();
	
	public void setOilPrice(double price) {
		this.consumerPrice=price;
	}
}
