package pl.gibiec.bucktracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbExpenseHelper extends SQLiteOpenHelper {
	
	private final static String DB_NAME = "BuckTracker.db";
	private final static int SCHEMA_VERSION = 2;

	public DbExpenseHelper(Context context){
		super(context, DB_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE Expenses (_id INTEGER PRIMARY KEY AUTOINCREMENT, category VARCHAR(10), name VARCHAR(20), price REAL, date DATETIME);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE Expenses");
		onCreate(db);
	}
	public void insert(String category, String name, String price, String date){
		ContentValues values = new ContentValues(4);
		values.put("category", category);
		values.put("name", name);
		values.put("price", price);
		values.put("date", date);
		
		long sqlCode = getWritableDatabase().insert("Expenses", "Test", values);
		if(sqlCode != -1)
			Log.i("mgibiec", "Spending saved. ID= " + sqlCode);
		else
			Log.i("mgibiec", "FAILED");
	}
	public Cursor getAll(){
		return getReadableDatabase().rawQuery("SELECT * FROM Expenses ORDER BY name", null);
	}
	public String getCategory(Cursor c){
		return c.getString(1);
	}
	public String getName(Cursor c){
		return c.getString(2);
	}
	public String getPrice(Cursor c){
		return c.getString(3);
	}
	public String getDate(Cursor c){
		return c.getString(4);
	}

}
