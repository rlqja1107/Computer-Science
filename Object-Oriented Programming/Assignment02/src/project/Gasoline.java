package project;

public class Gasoline extends Oil {
	private int price;
	private double VAT;
	Gasoline(int price, String company){
		super(company,"gasoline");
		this.price=price;
		VAT=Math.ceil(Math.random()*30) /100;
	}
	public int getOilePrice() {
		return price;
	}
	public String toString() {
		return super.toString()+"\n"+"supply price : "+ price;
	}
	@Override
	public double getOilPrice() {
		return price+price*VAT;
	}
}
