package pl.gibiec.bucktracker.utils;

public class Expenditure {

	
	public Expenditure(String category, String name, String price, String date) {
		this.category = convertToSpaceUpperCase(category);
		this.name = convertToSpaceUpperCase(name);
		this.price = convertToSpaceUpperCase(price);
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
	private String convertToSpaceUpperCase(String str){
		char[] array = str.trim().toCharArray();
		if(array.length < 1) return str;
		array[0] = switchToUpper(array[0]);
		for(int i= 1; i < array.length; ++i)
			if(array[i] == ' ')
				array[i+1]= switchToUpper(array[i+1]);
		return new String(array);
	}
	private char switchToUpper(char c){
		if(c >= 97 && c <=122)
			return (char) (c-32);
		return c;
	}
	private String category;
	private String name;
	private String price;
	private String date;
}
