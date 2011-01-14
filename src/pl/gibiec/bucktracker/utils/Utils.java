package pl.gibiec.bucktracker.utils;

import java.util.ArrayList;
import java.util.List;


public class Utils {
	private final static String[] months = { "Jan", "Feb", "Mar", "Apr", "May",
			"Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	private final static List<String> monthsList = new ArrayList<String>();
	static{
		for(String m : months)
			monthsList.add(m);
	}
	
	public static String getDateDisplayStyle(String dateDbStyle) {
		dateDbStyle = dateDbStyle.substring(0, dateDbStyle.indexOf(" ")); // get rid of the time part
		String[] parts = dateDbStyle.split("-"); // Split year, month and day
		// format as December 6, 2010
		return months[Integer.parseInt(parts[1]) - 1] + " " + parts[2] + ", "
				+ parts[0];
	}

	public static String getDateDbStyle(String dateDisplayStyle) {
		String temp[] = dateDisplayStyle.split(" ");
		int day = Integer.parseInt(temp[1]);
		int month = monthsList.indexOf(temp[0].substring(0, temp[0].length()-1));
		int year = Integer.parseInt(temp[2]);
		return getDateDbStyle(year, month, day);
	}
	public static String getDateDisplayStyle(int year, int month, int day){
		return months[month] + ", " + day + " " + year;
	}
	public static String getDateDbStyle(int year, int month, int day){
		++month;
		String dayString = (day > 9 ? "" + day : "0"+ day);
		String monthString = (month > 9 ? "" + month : "0"+ month);
		String dateString = year + "-" + monthString + "-" + dayString +
							" 00:00:00.000";
		return dateString;
	}
	public static int[] getDateCalendarStyle(String dateDisplayStyle){
		int[] date = new int[3];
		String temp[] = dateDisplayStyle.split(" ");
		
		int day = Integer.parseInt(temp[1].substring(0, temp[1].length()-1));
		int month = monthsList.indexOf(temp[0]);
		int year = Integer.parseInt(temp[2]);
		date[0] = year;
		date[1] = month;
		date[2] = day;
		return date;
		
	}
}