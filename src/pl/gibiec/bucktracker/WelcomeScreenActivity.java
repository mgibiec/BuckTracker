package pl.gibiec.bucktracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WelcomeScreenActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
    } 
    public void addNewExpense(View v){
    	Intent intent = new Intent(WelcomeScreenActivity.this, AllExpensesListActivity.class);
		intent.putExtra("AddNew", true);
		startActivity(intent);
    }
    public void showCharts(View v){    	
		startActivity(new Intent(WelcomeScreenActivity.this, ChartsDisplayActivity.class));
    }
    public void showAllExpenses(View v){
		startActivity(new Intent(WelcomeScreenActivity.this, AllExpensesListActivity.class));
    }
    public void manageCategories(View v){
    	startActivity(new Intent(WelcomeScreenActivity.this, AllCategoriesListActivity.class));
    }
}