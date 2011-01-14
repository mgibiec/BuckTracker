package pl.gibiec.bucktracker.db;

import android.database.Cursor;

public class ExpenseViewHelper {
	private ExpenseViewHelper(){
		// private constructor - static class
	}
	public static String getCategory(Cursor c){
		return c.getString(1);
	}
	public static String getName(Cursor c){
		return c.getString(2);
	}
	public static String getPrice(Cursor c){
		return c.getString(3);
	}
	public static String getDate(Cursor c){
		return c.getString(4);
	}
	public static String getColor(Cursor c){
		return c.getString(5);
	}
}
