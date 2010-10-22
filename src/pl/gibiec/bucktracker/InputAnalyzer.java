package pl.gibiec.bucktracker;

import java.util.Date;

public class InputAnalyzer {
	public static Expenditure analyze(String input){
		String[] temp = input.split(" ");
		return new Expenditure(temp[0], temp[1], temp[2], new Date().toGMTString());
	}
}
