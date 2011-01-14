package pl.gibiec.bucktracker.db;

import android.content.Context;

public class DbManager {
	private static DbHelper dbHelper;
	
	public static DbHelper getDbHelper(Context context){
		if(dbHelper == null)
			dbHelper = new DbHelper(context.getApplicationContext());
		
//		while(dbHelper.)
		return dbHelper;
	}
}
