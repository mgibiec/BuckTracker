package pl.gibiec.bucktracker;

public class Expenditure {

	
	public Expenditure(String category, String name, String price, String date) {
		this.category = category;
		this.name = name;
		this.price = price;
		this.date = date;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	private String category;
	private String name;
	private String price;
	private String date;
}
