package pl.gibiec.bucktracker;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class FilterExpensesActivity extends TabActivity implements TabContentFactory{

	Handler handler;
	TextView result, webBody, errors;
	EditText inputBox;
	DatePicker from, to;
	String[] categoriesArray;
	RadioGroup group;
	private String selectCategoryFilter; 
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		selectCategoryFilter = "All Categories";
		//Set View, instantiate Tabs
		setContentView(R.layout.filter_expenses_activity);

		categoriesArray = getIntent().getStringArrayExtra("Categories");
		categoriesArray[categoriesArray.length-1] = selectCategoryFilter;
		TabHost tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("tab_test1")
				.setIndicator("Filter category")
				.setContent(this));
		tabHost.addTab(tabHost.newTabSpec("tab_test2")
				.setIndicator("Filter timespan")
				.setContent(R.id.tab2_contents));	
		tabHost.setCurrentTab(1);
		Button closeButton = (Button) findViewById(R.id.btnSelectFilters);
		closeButton.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				closeActivity();
			}
		});
		RadioButton rb = (RadioButton) group.getChildAt(group.getChildCount()-1);
		rb.toggle();
	}
	
	private void closeActivity(){
		from = (DatePicker) findViewById(R.id.datePickerFrom);
		to = (DatePicker) findViewById(R.id.datePickerTo);
		
//		Toast.makeText(this, selectCategoryFilter, Toast.LENGTH_LONG).show();
		Intent data = new Intent();
		data.putExtra("from", getSqliteDateFormat(from.getYear(), from.getMonth()+1, from.getDayOfMonth()));
		data.putExtra("to", getSqliteDateFormat(to.getYear(), to.getMonth()+1, to.getDayOfMonth()));
		data.putExtra("category", selectCategoryFilter);
		setResult(RESULT_OK, data);
		finish();
	}
	private String getSqliteDateFormat(int year, int month, int day){
		StringBuilder sb = new StringBuilder();
		sb.append(year).append("-")
		  .append(month > 9 ? month : "0"+month).append("-")
		  .append(day > 9 ? day : "0"+day);
		
		return sb.toString();
	}
	public View createTabContent(String tag) {
				
		group = (RadioGroup) findViewById(R.id.radioGroup);
		for(String cat : categoriesArray){
			RadioButton btn = new RadioButton(this);
			btn.setText(cat);
			btn.setChecked(false);
			btn.setOnClickListener(radioListener);
			group.addView(btn);	
		}
		
		return findViewById(R.id.tab1_contents);
	}
	private View.OnClickListener radioListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			RadioButton r = (RadioButton) v;
			selectCategoryFilter = r.getText().toString();
			
		}
	};
}