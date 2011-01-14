package pl.gibiec.bucktracker;

import pl.gibiec.bucktracker.db.DbHelper;
import pl.gibiec.bucktracker.db.DbManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AllCategoriesListActivity extends Activity {

	private Cursor c;
	private static final int CONTEXT_MENU_DELETE = 1000;
	private static final int CONTEXT_MENU_EDIT = 1001;
	private static final int ADD_CATEGORY = 2000;
	private static final int EDIT_CATEGORY = 2001;
	DbHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_categories);
		dbHelper = DbManager.getDbHelper(getApplicationContext());

		c = dbHelper.getAllCagegories();
		startManagingCursor(c);
		ListView allCategories = (ListView) findViewById(R.id.lvAllCategories);
		ListAdapter adapter = new CategoryAdapter(getApplicationContext(), c);
		allCategories.setAdapter(adapter);
		registerForContextMenu(allCategories);
		allCategories.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View itemView,
					int arg2, long categoryId) {
				String categoryName = ((TextView) itemView
						.findViewById(R.id.categoryName)).getText().toString();
				int color = Integer
						.parseInt(((TextView) itemView
								.findViewById(R.id.categoryColor)).getText()
								.toString());
				editCategory(categoryId, categoryName, color);				
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Extra actions");
		menu.add(0, CONTEXT_MENU_DELETE, 0, "Delete entry");
		menu.add(0, CONTEXT_MENU_EDIT, 1, "Edit entry");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONTEXT_MENU_DELETE: {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			final long categoryId = info.id;
			deleteCategory(categoryId);
			break;
		}
		case CONTEXT_MENU_EDIT:{
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
			final long categoryId = info.id;
			String categoryName = ((TextView) info.targetView.findViewById(R.id.categoryName)).getText().toString();
			int color = Integer.parseInt(((TextView) info.targetView.findViewById(R.id.categoryColor)).getText().toString());
			editCategory(categoryId, categoryName, color);
		}
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.all_categories_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addNewCategory:
			startActivityForResult(new Intent(AllCategoriesListActivity.this,
					CategoryAddEditActivity.class), ADD_CATEGORY);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		c.close();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK && requestCode == ADD_CATEGORY) {
			Bundle extras = data.getExtras();
			String newColor = extras.getInt("color") + "";
			String newName = extras.getString("name").trim();
			dbHelper.insertCategory(newName, newColor);
			c.requery();
		} else if (resultCode == RESULT_OK && requestCode == EDIT_CATEGORY) {
			Bundle extras = data.getExtras();
			String id = extras.getInt("id") + "";
			String color = extras.getInt("color") + "";
			String name = extras.getString("name").trim();
			dbHelper.updateCategory(id, name, color);
			c.requery();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void editCategory(long categoryId, String categoryName, int color){
		Log.i("AndroMarek", "You clicked " + categoryName
				+ " and categoryId is " + categoryId);
		Intent intent = new Intent(AllCategoriesListActivity.this,
				CategoryAddEditActivity.class);
		intent.putExtra("edit", true);
		intent.putExtra("id", (int) categoryId);
		intent.putExtra("name", categoryName);
		intent.putExtra("color", color);
		startActivityForResult(intent, EDIT_CATEGORY);
	}
	private void deleteCategory(long categoryId){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Removing this category will delete all expenses associated with it."
						+ "\nAre you sure you want to do it?")
				.setCancelable(false).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								int deletedExpensesCount = dbHelper.deleteCategory(String.valueOf(id));
								c.requery();
								Toast.makeText(getApplicationContext(), deletedExpensesCount
												+ " expenses were removed",
										Toast.LENGTH_LONG).show();
							}
						}).setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								dialog.cancel();
							}
						});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();

	}

	class CategoryAdapter extends CursorAdapter {

		public CategoryAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			CategoryHolder holder = (CategoryHolder) view.getTag();
			holder.populateForm(cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row_category, parent, false);
			CategoryHolder holder = new CategoryHolder(row);
			row.setTag(holder);
			return row;
		}
	}

	class CategoryHolder {
		private TextView categoryName = null;
		private TextView categoryColor = null;

		CategoryHolder(View row) {
			this.categoryName = (TextView) row.findViewById(R.id.categoryName);
			this.categoryColor = (TextView) row
					.findViewById(R.id.categoryColor);
		}

		void populateForm(Cursor c) {
			categoryName.setText(c.getString(1));
			String colorString = c.getString(2);
			int colorInt = Color.BLACK;
			try {
				colorInt = Integer.parseInt(colorString);
			} catch (NumberFormatException e) {
			}
			categoryColor.setText(colorInt + "");
			categoryColor.setBackgroundColor(colorInt);
			categoryColor.setTextColor(colorInt);

		}

	}
}