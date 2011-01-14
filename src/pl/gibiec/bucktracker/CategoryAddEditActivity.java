package pl.gibiec.bucktracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CategoryAddEditActivity extends Activity {

	
	private EditText catName;
	Button changeColor;
	private int categoryColor;
	private boolean edit;
	private int editId;
	private final static int SELECT_NEW_COLOR = 1000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_add_edit);
		catName = (EditText) findViewById(R.id.categoryEditAddName);
		changeColor = (Button) findViewById(R.id.categoryColorPicker);
		
		setupData(getIntent().getExtras());		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == SELECT_NEW_COLOR){
			categoryColor = data.getExtras().getInt("color");
			changeColor.setBackgroundColor(categoryColor);
		}else
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void setupData(Bundle extras){
		if(extras != null){
			edit = extras.getBoolean("edit", false);
			editId = extras.getInt("id");
			categoryColor = extras.getInt("color", Color.GRAY);
			catName.setText(extras.getString("name"));
		}else{
			categoryColor = Color.GRAY;
		}changeColor.setBackgroundColor(categoryColor);
	}

	public void pickColor(View v){
		Log.i("mgibiec3", "Button pressed");
//		dialog.show();
		startActivityForResult(new Intent(CategoryAddEditActivity.this, SelectColorActivity.class), SELECT_NEW_COLOR);
	}
	public void saveCategory(View v){
		Intent intent = new Intent();
		if(edit) intent.putExtra("id", editId);
		intent.putExtra("name", catName.getText().toString());
		intent.putExtra("color", categoryColor);
		setResult(RESULT_OK, intent);		
		finish();
	}	
}
