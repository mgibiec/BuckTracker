package pl.gibiec.bucktracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	
	private final static String DB_NAME = "BuckTracker.db";
	private final static int SCHEMA_VERSION = 13;

	DbHelper(Context context){
		super(context, DB_NAME, null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE Expenses (_id INTEGER PRIMARY KEY AUTOINCREMENT, category VARCHAR(10), name VARCHAR(20), price REAL, date DATETIME);");
		db.execSQL("CREATE TABLE Categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(20), color VARCHAR(10));");
		
		String sqlSelect = "SELECT Expenses._id AS _id, Categories.name AS category, Expenses.name AS name, " +
				                  "Expenses.price AS price, Expenses.date AS date, Categories.color as color from Expenses, Categories " + 
		   "where Expenses.category=Categories._id";
		db.execSQL("CREATE VIEW ExpensesView AS " + sqlSelect);
		db.execSQL("INSERT INTO Categories (name, color) values ('Other', '-6697933');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < 13){
			db.execSQL("DROP VIEW ExpensesView");
			String sqlSelect = "SELECT Expenses._id AS _id, Categories.name AS category, Expenses.name AS name, " +
            "Expenses.price AS price, Expenses.date AS date, Categories.color as color from Expenses, Categories " + 
            "where Expenses.category=Categories._id";
			db.execSQL("CREATE VIEW ExpensesView AS " + sqlSelect);
		}
		if(oldVersion == 11){
			String sqlSelect = "SELECT Expenses._id, Categories.name AS category, Expenses.name AS name, Expenses.price, Expenses.date, Categories.color from Expenses, Categories " + 
			   "where Expenses.category=Categories._id";
			db.execSQL("DROP VIEW ExpensesView");
			db.execSQL("CREATE VIEW ExpensesView AS " + sqlSelect);
			Log.i("mgibiec3", "View ReCreated");
		}else if(oldVersion == 10){
		
			//color stored as a string in a form: #00aaff
			db.execSQL("ALTER TABLE Categories ADD COLUMN color TEXT NOT NULL DEFAULT '#00CC00';"); 
		}else if(oldVersion == 5){
			db.execSQL("ALTER TABLE Expenses2 RENAME TO Expenses");
			
//			db.execSQL("Drop Table Expenses");r
		}else if(oldVersion == 9){
			String sqlSelect = "SELECT Expenses._id, Categories.name AS category, Expenses.name AS name, Expenses.price, Expenses.date from Expenses, Categories " + 
							   "where Expenses.category=Categories._id";
			db.execSQL("DROP VIEW ExpensesView");
			db.execSQL("CREATE VIEW ExpensesView AS " + sqlSelect);
			Log.i("mgibiec3", "View ReCreated");
		}else if(oldVersion == 4){
		
			
//		db.execSQL("DROP TABLE Expenses");
		//onCreate(db);
		String[] columns = { "category" };
		Cursor c = db.query(true, "Expenses", columns, null, null, null, null, null, null);
		db.execSQL("CREATE TABLE Categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(20));");
		ContentValues categoryValues = new ContentValues();
		while(c.moveToNext()){
			categoryValues.put("name", c.getString(0));
			Log.i("mgibiec3", c.getString(0));			
			db.insert("Categories", "eRRoR", categoryValues);
		}
		
		c = db.rawQuery("Select * from Categories", null);
		Cursor c2 = db.rawQuery("SELECT * FROM Expenses ORDER BY date desc", null);
		db.execSQL("CREATE TABLE Expenses2 (_id INTEGER PRIMARY KEY AUTOINCREMENT, category INTEGER, name VARCHAR(20), price REAL, date DATETIME);");
		categoryValues.clear();
		
		while(c.moveToNext()){
			String a = c.getString(1);
			String b = c.getString(0);
			categoryValues.put(a, b);
		}
		while(c2.moveToNext()){			
			
			ContentValues values = new ContentValues(4);
			String cat = ExpenseViewHelper.getCategory(c2);
			String category = categoryValues.getAsInteger(cat).toString();
			String name = ExpenseViewHelper.getName(c2); 
			String price = ExpenseViewHelper.getPrice(c2);
			String date = ExpenseViewHelper.getDate(c2);
			values.put("category", category);
			values.put("name", name);
			values.put("price", price);
			values.put("date", date);
			
			db.insert("Expenses2", "Test", values);
			
		}
		db.execSQL("Drop Table Expenses");
		db.execSQL("ALTER TABLE Expenses2 RENAME TO Expenses");
		}
	}
	
	public int insertExpense(String category, String name, String price, String date){
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
		return (int) sqlCode;
	}
	public void deleteExpense(String id){
		String[] args={id};	    
		long sqlCode = getWritableDatabase().delete("Expenses", "_ID=?", args);
		if(sqlCode > 0)
			Log.i("mgibiec", "Spending deleted. ID= " + id);
		else
			Log.i("mgibiec", "Delete failed. " + id);
	}
	public void updateExpense(String id, String category, String name, String price, String date){
		ContentValues values = new ContentValues(4);
		values.put("category", category);
		values.put("name", name);
		values.put("price", price);
		values.put("date", date);
		String[] args={id};
		long sqlCode = getWritableDatabase().update("Expenses", values, " _ID=?", args);
		if(sqlCode > 0)
			Log.i("mgibiec", sqlCode + " rows affected. Spending updated: " + id);
		else
			Log.i("mgibiec", "Update failed. " + id);
	}
	public int getCategoryId(String categoryName){
		String[] args = {categoryName};
		Cursor c= getReadableDatabase().rawQuery("Select _id from Categories where name like ?" , args);
		c.moveToFirst();
		return c.getInt(0);
	}
	public Cursor getAllCagegories(){
		return getReadableDatabase().rawQuery("SELECT * FROM CATEGORIES", null);
	}
	public Cursor getAllExpenses(){
		return getReadableDatabase().rawQuery("SELECT * FROM Expenses ORDER BY date desc", null);
	}
	public Cursor getAllExpensesFromView(){
		return getReadableDatabase().rawQuery("SELECT * FROM ExpensesView ORDER BY date desc", null);
	}
	public Cursor getAllExpensesFromView(String from, String to, String category){
		String[] columns = {"_id", "category", "name", "price", "date", "color"};
		String selection = "date > ? and date < ? and category like ?";
		String[] selectionArgs = {from, to, category};
		String groupBy = null;
		String having = null;
		String orderBy = "date desc";
		return getReadableDatabase().query("ExpensesView", columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	public Cursor getGroupedByCategory(String from, String to){
		String[] columns = {"category", "sum(price), color"};
		String selection = "date > ? and date < ?";
		String[] selectionArgs = {from, to};
		String groupBy = "category, color";
		String having = null;
		String orderBy = "category";
		return getReadableDatabase().query("ExpensesView", columns, selection, selectionArgs, groupBy, having, orderBy);
	}
//	public Cursor getSpendingStatistics(){
//		return getReadableDatabase().rawQuery("SELECT count(price), sum(price), avg(price) from Expenses", null);
//	}

	public int insertCategory(String categoryName, String categoryColor) {
		
		ContentValues values = new ContentValues(4);
		values.put("name", categoryName);
		values.put("color", categoryColor);
		
		long sqlCode = getWritableDatabase().insert("Categories", "Test", values);
		if(sqlCode != -1)
			Log.i("mgibiec", "Category saved. ID= " + sqlCode);
		else
			Log.i("mgibiec", "FAILED");
		return (int) sqlCode;
	}
	public void updateCategory(String id, String categoryName, String categoryColor){
		ContentValues values = new ContentValues(4);
		values.put("name", categoryName);
		values.put("color", categoryColor);
		
		String[] args={id};
		long sqlCode = getWritableDatabase().update("Categories", values, " _ID=?", args);
		if(sqlCode > 0)
			Log.i("mgibiec", sqlCode + " rows affected. Category updated: " + id);
		else
			Log.i("mgibiec", "Update failed. " + id);
	}
	public int deleteCategory(String id){
		String[] args={id};	    
		long sqlCode = getWritableDatabase().delete("Categories", "_ID=?", args);
		
		int numOfExpensesDeleted = getWritableDatabase().delete("Expenses", "category=?", args);
		if(sqlCode > 0)
			Log.i("mgibiec", "Spending deleted. ID= " + id);
		else
			Log.i("mgibiec", "Delete failed. " + id);
		return numOfExpensesDeleted;
	}

}
