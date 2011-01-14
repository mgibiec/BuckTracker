package pl.gibiec.bucktracker;

import pl.gibiec.bucktracker.utils.ColorPickerDialog;
import pl.gibiec.bucktracker.utils.ColorPickerDialog.OnColorChangedListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SelectColorActivity extends Activity {
	private ColorPickerDialog dialog;
	private int categoryColor;
	private Button customColorButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_color);
		customColorButton = (Button) findViewById(R.id.buttonCustomColor);
		dialog = new ColorPickerDialog(this, new OnColorChangedListener() {
			
			public void colorChanged(int color) {
				Log.i("mgibiec3", "new color: " + color);
				categoryColor = color;			
				customColorButton.setBackgroundColor(categoryColor);
				customColorButton.setTextColor(categoryColor);
				getColor(customColorButton);
			}
		}, Color.GRAY);
		
	}
	public void getColor(View v){
		categoryColor = ((Button) v).getCurrentTextColor();
		Intent intent = new Intent();
		intent.putExtra("color", categoryColor);
		setResult(RESULT_OK, intent);
		finish();
	}
	public void getCustomColor(View v){
		dialog.show();
	}
}
