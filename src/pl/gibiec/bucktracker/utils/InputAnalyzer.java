package pl.gibiec.bucktracker.utils;

import java.util.Date;
import java.util.List;


import android.util.Log;

public class InputAnalyzer {
	public static Expenditure analyze(List<String> inputs){
		String name = "", category="", price="", price2="";
		for(String input : inputs){
			
			try{	
				String[] temp = input.split(" at");
				category = temp[0];
				String[] temp2 = temp[1].split(" for");
				name = temp2[0];
				price = temp2[1];
				price2 = "" + (Double.parseDouble(price)/100);
				return new Expenditure(category, name, price2, new Date().toGMTString());
			}catch(ArrayIndexOutOfBoundsException e){
				Log.e("mgibiec", e.toString() + "\t" + input);
			}catch(Exception e){
				Log.e("mgibiec", "ALERT! THIS SHOULD BE INVESTIGATED!" + e.toString());
			}			
		}
		return new Expenditure(category, name, price2, new Date().toGMTString());
	}
}
