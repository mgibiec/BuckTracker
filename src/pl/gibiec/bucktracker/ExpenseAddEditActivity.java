package pl.gibiec.bucktracker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pl.gibiec.bucktracker.utils.Expenditure;
import pl.gibiec.bucktracker.utils.InputAnalyzer;
import pl.gibiec.bucktracker.utils.Utils;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ExpenseAddEditActivity extends Activity{

	 
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_input);
		
		category = (Spinner) findViewById(R.id.category);
		name = (EditText) findViewById(R.id.name);
		price = (EditText) findViewById(R.id.price);
		date = (TextView) findViewById(R.id.date);
		setupData(getIntent().getExtras());
		
		btnChangeDate = (Button) findViewById(R.id.buttonEditDate);
		btnChangeDate.setOnClickListener(new OnClickListener(){		
			public void onClick(View v) {
				showDialog(DATE_DIALOG);
			}
		});
		buttonSpeak = (Button) findViewById(R.id.buttonSpeak);
		buttonSpeak.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 startVoiceRecognitionActivity();
			}
		});
		
		buttonCategorySpeak = (ImageButton) findViewById(R.id.buttonCategorySpeak);
		buttonCategorySpeak.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startVoiceRecognitionActivityForField(CATEGORY_TEXT_EDIT);
			}
		});
		buttonNameSpeak = (ImageButton) findViewById(R.id.buttonNameSpeak);
		buttonNameSpeak.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startVoiceRecognitionActivityForField(NAME_TEXT_EDIT);
			}
		});
		buttonPriceSpeak = (ImageButton) findViewById(R.id.buttonPriceSpeak);
		buttonPriceSpeak.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				startVoiceRecognitionActivityForField(PRICE_TEXT_EDIT);
			}
		});
		category.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(((String)category.getSelectedItem()).equals(ADD_NEW_CATEGORY_STRING)){
					addNewCategory();
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
	}
	private int getCatIdFromAdapter(String category){
		int pos = 0;
		for(int i= 0; i < categoriesArray.length; ++i)
			if(categoriesArray[i].equals(category)){
				pos= i;
				break;
			}		
		return pos;
	}
	private void setupData(Bundle extras){
		categoriesArray = extras.getStringArray("Categories");
		categoriesArray[categoriesArray.length-1] = ADD_NEW_CATEGORY_STRING;

		refreshSpinner();
		edit = (extras == null ? false : extras.getBoolean("edit", false));
		if(edit){
			editId = extras.getString("id");		
			category.setSelection(getCatIdFromAdapter(extras.getString("category")));
			name.setText(extras.getString("name"));
			price.setText(extras.getString("price"));
			int[] dateNumbers = Utils.getDateCalendarStyle(extras.getString("date"));
			currentYear = dateNumbers[0];
			currentMonth = dateNumbers[1];
			currentDay = dateNumbers[2];
		}else{
			Calendar c = Calendar.getInstance(); 			
			currentYear = c.get(Calendar.YEAR);
			currentMonth = c.get(Calendar.MONTH);
			currentDay = c.get(Calendar.DAY_OF_MONTH);
			
		}
//		Log.i("mgibiec", "month: " + currentMonth);
		date.setText(Utils.getDateDisplayStyle(currentYear, currentMonth, currentDay));
	}
	
	@Override
	protected Dialog onCreateDialog(int id){
		switch(id){
			case DATE_DIALOG:{
				return new DatePickerDialog(this, dateUpdater, currentYear, currentMonth, currentDay);
			}
		}
		return null;
	}
	
	private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Example: \"Lunch at Chipotle for 8.62\"");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
	private void startVoiceRecognitionActivityForField(int fieldCode){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Example: \"Lunch at Chipotle for 8.62\"");
        startActivityForResult(intent, fieldCode);
	}
	
	private OnDateSetListener dateUpdater = new OnDateSetListener() {
		
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			currentMonth = monthOfYear;
			currentDay = dayOfMonth;
			currentYear = year;
			
			date.setText(Utils.getDateDisplayStyle(currentYear, currentMonth, currentDay));
		}
	};

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            Log.i("mgibiec", "Found: " + matches.size() + " " + matches.get(0));
            for(String s : matches){
            	Log.i("mgibiec2", s);
            }
            Expenditure e = InputAnalyzer.analyze(matches);
//            if(e.getCategory().equals(ADD_NEW_CATEGORY))
//            	addNewCategory(e.getCategory());
            category.setSelection(getCatIdFromAdapter(e.getCategory()), true);
    		name.setText(e.getName());
    		price.setText(e.getPrice());
        }else if(requestCode == CATEGORY_TEXT_EDIT && resultCode == RESULT_OK){
        	category.setSelection(getCatIdFromAdapter(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0)), true);
//        	category.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }else if(requestCode == NAME_TEXT_EDIT && resultCode == RESULT_OK){
        	name.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }else if(requestCode == PRICE_TEXT_EDIT && resultCode == RESULT_OK){
        	price.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }else if(requestCode == ADD_NEW_CATEGORY && resultCode == RESULT_OK){        	
        	Bundle extras = data.getExtras();
        	String newCategory = extras.getString("name");
        	newCategoryColor = "" + extras.getInt("color");
        	refreshCategories(newCategory);        	
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
	private String addNewCategory(){
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//		final EditText input = new EditText(this);
//		alert.setTitle("Add new Category").setMessage("Message").setView(input);
//		final StringBuilder newCategory= new StringBuilder(); 
//		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {		
//			public void onClick(DialogInterface dialog, int which) {
//				refreshCategories(newCategory.append(input.getText().toString()).toString());
//			}
//		});
//		alert.show();
		startActivityForResult(new Intent(ExpenseAddEditActivity.this, CategoryAddEditActivity.class), ADD_NEW_CATEGORY);
		
		return "";//newCategory.toString();
	}
	private void refreshCategories(String cat){

		categoriesArray[categoriesArray.length-1] = cat;
		refreshSpinner();
		category.setSelection(categoriesArray.length-1, true);
	}
	public void saveButton2Click(View v){
		saveExpenseAndFinishActivity(true);
	}
    public void saveButtonClick(View v){
		saveExpenseAndFinishActivity(false);
	}
    private void saveExpenseAndFinishActivity(boolean startNew){
    	if(!validateInput()){
    		Toast.makeText(this, "Values you entered are not correct!", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	Intent newExpense = getIntent();
//		newExpense.putExtra("category", category.getText().toString().trim());
		newExpense.putExtra("category", category.getItemAtPosition(category.getSelectedItemPosition()).toString().trim());
		newExpense.putExtra("color", newCategoryColor);
		newExpense.putExtra("name", name.getText().toString().trim());
		newExpense.putExtra("price", price.getText().toString().trim());	
		newExpense.putExtra("date", Utils.getDateDbStyle(currentYear, currentMonth, currentDay));
		
//		category.setText("");
		name.setText("");
		price.setText("");
//		date.setText("");
		
		if(edit){
			newExpense.putExtra("edit", true);
			newExpense.putExtra("id", editId);
		}
		if(startNew){
			newExpense.putExtra("addNew", true);
		}
		setResult(RESULT_OK, newExpense);
		finish();
    }
	
    public void times10(View v){
    	try{
    		double currentPrice = Double.parseDouble(price.getText().toString());
    		price.setText(decimalFormat.format(currentPrice * 10) + "");    		
    	}catch(NumberFormatException e){
    		Toast.makeText(getApplicationContext(), price.getText() + " is not a valid value", Toast.LENGTH_SHORT).show();
    	}
    }
    public void div10(View v){
    	try{
    		double currentPrice = Double.parseDouble(price.getText().toString());
    		price.setText(decimalFormat.format(currentPrice / 10) + "");    		
    	}catch(NumberFormatException e){
    		Toast.makeText(getApplicationContext(), price.getText() + " is not a valid value", Toast.LENGTH_SHORT).show();
    	}
    }
    private void refreshSpinner(){
    	categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoriesArray);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category.setAdapter(categoryAdapter);
    }
    private boolean validateInput(){
    	String n = name.getText().toString();
    	String c = category.getItemAtPosition(category.getSelectedItemPosition()).toString().trim();
    	String p = price.getText().toString();
    	if(n.equals("null") || n.length() < 3 ||
		   c.equals("null") || c.length() < 3 ||
		   p.equals("null") || p.length() < 1)
    		return false;
    	return true;
    }
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	
	NumberFormat decimalFormat = new DecimalFormat("##0.00");
	Spinner category;
	EditText name;
	EditText price;
	TextView date;
	Button btnChangeDate;
	Button buttonSpeak;
	Button times10, div10;
	ImageButton buttonCategorySpeak, buttonNameSpeak, buttonPriceSpeak;
	private int currentYear, currentMonth, currentDay;
	private static final int CATEGORY_TEXT_EDIT = 1000;
	private static final int NAME_TEXT_EDIT = 1001;
	private static final int PRICE_TEXT_EDIT = 1002;
	private static final int ADD_NEW_CATEGORY = 2000;
	String[] categoriesArray;
	private static final String ADD_NEW_CATEGORY_STRING= "[Add new]";
	private ArrayAdapter<String> categoryAdapter;
	private boolean edit;
	private String editId;
	private static final int DATE_DIALOG = 0;
	private String newCategoryColor;
}