package project;

public class Diesel extends Oil {
	private int price;
	private double VAT;
	private double envTax;
	Diesel(int price, String company){
		super(company,"diesel");
		this.price=price;
		VAT=Math.ceil(Math.random()*30)/100;
		envTax=500;
	}
	public int getOilprice() {
		return price;
	}
	public String toString() {
		return super.toString()+"\n"+"supply price : "+price;
	}
	@Override
	public double getOilPrice() {
		return price+envTax+price*VAT;
	}
	
}
