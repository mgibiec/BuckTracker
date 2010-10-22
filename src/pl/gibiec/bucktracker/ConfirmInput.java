package pl.gibiec.bucktracker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ConfirmInput extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_input);
		
		dbHelper = new DbExpenseHelper(this);
		
		category = (EditText) findViewById(R.id.category);
		name = (EditText) findViewById(R.id.name);
		price = (EditText) findViewById(R.id.price);
		date = (EditText) findViewById(R.id.date);
		
		Button buttonSpeak = (Button) findViewById(R.id.buttonSpeak);
		
		ListView allExpenses = (ListView) findViewById(R.id.listViewAllExpenses);
		
		cursor = dbHelper.getAll();
		startManagingCursor(cursor);
		adapter = new ExpenseAdapter(this, cursor);
		allExpenses.setAdapter(adapter);
		
		buttonSpeak.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				 startVoiceRecognitionActivity();
			}
		});
		
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		dbHelper.close();
	}
//	private void refreshCursor(){
//		try{
//		cursor = 
//			dbHelper
//			.getReadableDatabase()
//			.rawQuery("SELECT name FROM Expenses ORDER BY name", null);
//		}catch(NullPointerException e){
//			Log.i("mgibiec", "Database not yet created?");
//		}
//	}
	private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            Log.i("mgibiec", matches.get(0));
            StringBuilder sb = new StringBuilder();
            for(String s : matches)
            	sb.append(s + "\n");
            
            Expenditure e = InputAnalyzer.analyze(matches.get(0));
            
            category.setText(e.getCategory());
    		name.setText(e.getName());
    		price.setText(e.getPrice());
    		date.setText(e.getDate());
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	public void saveButtonClick(View v){
		dbHelper.insert(category.getText().toString(), name.getText().toString(), price.getText().toString(), date.getText().toString());
		cursor.requery();
		category.setText("");
		name.setText("");
		price.setText("");
		date.setText("");
		
	}
	
	private DbExpenseHelper dbHelper;
	ListAdapter adapter;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	Cursor cursor;
	EditText category;
	EditText name;
	EditText price;
	EditText date;

	class ExpenseAdapter extends CursorAdapter{
	
		public ExpenseAdapter(Context context, Cursor c) {
			super(context, c);
		}
	
		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			ExpenseHolder holder = (ExpenseHolder) row.getTag();
			holder.populateForm(cursor, dbHelper);
		}
	
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.row, parent, false);
			ExpenseHolder holder = new ExpenseHolder(row);
			
			row.setTag(holder);
			return row;
		}
	}
	class ExpenseHolder {
		
		private TextView category = null;
		private TextView name = null;
		private TextView price = null;
		private View row = null;
		
		public ExpenseHolder(View row){
			this.row = row;
			category= (TextView) this.row.findViewById(R.id.categoryInRow);
			name = (TextView) this.row.findViewById(R.id.nameInRow);
			price = (TextView) this.row.findViewById(R.id.priceInRow);
		}
		void populateForm(Cursor c, DbExpenseHelper helper){
			category.setText(helper.getCategory(c));
			name.setText(helper.getName(c));
			price.setText(helper.getPrice(c));
			
		}
	}
}