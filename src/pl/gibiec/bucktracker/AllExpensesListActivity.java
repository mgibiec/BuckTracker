package pl.gibiec.bucktracker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pl.gibiec.bucktracker.db.DbHelper;
import pl.gibiec.bucktracker.db.DbManager;
import pl.gibiec.bucktracker.utils.BackupTask;
import pl.gibiec.bucktracker.utils.ExpenseAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AllExpensesListActivity extends Activity{
	private static final int ADD_EXP_CODE = 1000;
	private static final int EDIT_EXP_CODE = 1001;
	private static final int FILTER_EXP_CODE = 1002;
	private static final int CONTEXT_MENU_DELETE = 1000;
	private static final int CONTEXT_MENU_EDIT = 1001;
	private DbHelper dbHelper;
	ListAdapter adapter;
	ListView allExpenses;
	Cursor cursor;
	Map<String, Integer> categories;
	EditText fileName;
	Button buttonBackup;
//	FilterExpenseDialog dialog;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_expenses);
//		dialog = new FilterExpenseDialog(this);
		allExpenses = (ListView) findViewById(R.id.lvAllExpenses);
		registerForContextMenu(allExpenses);
		dbHelper = DbManager.getDbHelper(getApplicationContext());
		cursor = dbHelper.getAllExpensesFromView();
		startManagingCursor(cursor);
		adapter = new ExpenseAdapter(this, cursor);
		allExpenses.setAdapter(adapter);
		
		if(categories == null)
			refreshCategories();
		if(getIntent().getBooleanExtra("AddNew", false)){
			startExpenseAddActivity();
		}
	}
	@Override
	protected void onResume() {
		if(categories == null)
			refreshCategories();
		super.onResume();
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		dbHelper.close();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		getMenuInflater().inflate(R.menu.all_expenses_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.backupExpenses:
			setupBackupDialog().show();
			return true;
		case R.id.addNewExpenseMenuItem:
			startExpenseAddActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
    protected void onActivityResult(int requestCode, int ResultCode, Intent data){
    	if(requestCode == ADD_EXP_CODE && ResultCode == RESULT_OK){
    		Bundle extras = data.getExtras();
    		if(!categories.containsKey(extras.getString("category"))){
    			int newId = dbHelper.insertCategory(extras.getString("category").trim(), extras.getString("color").trim());
    			Log.i("AndroMarek", "new category added: " + newId);
    			refreshCategories();
    			Toast.makeText(this, "New category was added!", Toast.LENGTH_SHORT).show();
    		}
    		String catId = "" + categories.get(extras.getString("category"));
    		if(extras.getBoolean("edit", false))		
    			dbHelper.updateExpense(extras.getString("id"),
    					catId, 
						extras.getString("name"),
						extras.getString("price"),
						extras.getString("date"));
    		else
    			dbHelper.insertExpense(catId, 
						extras.getString("name"),
						extras.getString("price"),
						extras.getString("date"));   		
    		cursor.requery();
//    		Toast.makeText(this, "New expense was added!", Toast.LENGTH_SHORT).show();
    		if(extras.getBoolean("addNew", false)){
				startExpenseAddActivity();  			
    		}
    	}else if(requestCode == FILTER_EXP_CODE && ResultCode == RESULT_OK){
    		Bundle extras = data.getExtras();
    		String from = extras.getString("from");
    		String to = extras.getString("to");
    		String category = extras.getString("category");
    		
    		filterExpensesToDisplay(from, to, category);
    	}
    }
		
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		 super.onCreateContextMenu(menu, v, menuInfo);
		 menu.setHeaderTitle("Extra actions");
		 menu.add(0, CONTEXT_MENU_DELETE, 0, "Delete entry");
		 menu.add(0, CONTEXT_MENU_EDIT, 0, "Edit entry");
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case CONTEXT_MENU_DELETE:{
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
				long expenseId = info.id;
				dbHelper.deleteExpense(String.valueOf(expenseId));
				cursor.requery();
				break;
			}
			case CONTEXT_MENU_EDIT:{
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
				startExpenseEditActivity(info);
				break;
			}
		}
		return super.onContextItemSelected(item);
	}
	private void filterExpensesToDisplay(String from, String to, String category){
		stopManagingCursor(cursor);
		cursor = DbManager.getDbHelper(getApplicationContext()).getAllExpensesFromView(from, to, category);
		startManagingCursor(cursor);
		adapter = new ExpenseAdapter(getApplicationContext(), cursor);
		allExpenses.setAdapter(adapter);
		cursor.requery();
		Log.i("AndroMarek", "selectCategoriesToDisplay");
	}
	public void setExpensesFilters(View view){		
		Intent intent = new Intent(AllExpensesListActivity.this, FilterExpensesActivity.class);
		intent.putExtra("Categories", categories.keySet().toArray(new String[categories.keySet().size()+1]));
		startActivityForResult(intent, FILTER_EXP_CODE);
		Log.i("AndroMarek", "Filtering activity started");
	}
	public void clearExpensesFilters(View view){
		stopManagingCursor(cursor);
		cursor = dbHelper.getAllExpensesFromView();
		startManagingCursor(cursor);
		adapter = new ExpenseAdapter(getApplicationContext(), cursor);
		allExpenses.setAdapter(adapter);
		cursor.requery();
		Log.i("AndroMarek", "Filters cleared");
	}
	private void refreshCategories(){
		categories = new HashMap<String, Integer>();
		Cursor c = dbHelper.getAllCagegories();
		while(c.moveToNext())
			categories.put(c.getString(1), c.getInt(0));
		c.close();
	}
	private Dialog setupBackupDialog(){
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.backup_dialog);
		
		fileName = (EditText) dialog.findViewById(R.id.backupFileName);
		buttonBackup = (Button) dialog.findViewById(R.id.buttonBackup);
		buttonBackup.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Log.i("AndroMarek", "Performing backup");
				String[] params = {fileName.getText().toString()};
				new BackupTask(getApplicationContext(), dbHelper).execute(params);
				dialog.dismiss();
			}});
		fileName.setText(("BuckTracker_" + new Date().toString() + ".csv").replace(' ', '_'));
		return dialog;
	}
	private void startExpenseAddActivity(){
		Intent intent2 = new Intent(AllExpensesListActivity.this, ExpenseAddEditActivity.class);
		intent2.putExtra("Categories", categories.keySet().toArray(new String[categories.keySet().size()+1]));
		startActivityForResult(intent2, ADD_EXP_CODE);  
	}
	private void startExpenseEditActivity(AdapterView.AdapterContextMenuInfo info){
		long expenseId = info.id;
		
		String category = ((TextView) info.targetView.findViewById(R.id.categoryInRow)).getText().toString();
		String name = ((TextView) info.targetView.findViewById(R.id.nameInRow)).getText().toString();
		String price = ((TextView) info.targetView.findViewById(R.id.priceInRow)).getText().toString().substring(1);
		String date = ((TextView) info.targetView.findViewById(R.id.dateInRow)).getText().toString();
		
		Intent intent = new Intent(AllExpensesListActivity.this, ExpenseAddEditActivity.class);
		intent.putExtra("edit", true);
		intent.putExtra("id", "" + expenseId);
		intent.putExtra("price", price);
		intent.putExtra("category", category);
		intent.putExtra("name", name);
		intent.putExtra("date", date);
		intent.putExtra("Categories", categories.keySet().toArray(new String[categories.keySet().size()+1]));
		
		startActivityForResult(intent, ADD_EXP_CODE);
	}
}
