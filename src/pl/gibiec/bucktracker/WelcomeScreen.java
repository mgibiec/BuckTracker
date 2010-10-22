package pl.gibiec.bucktracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeScreen extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); 
        
        speakButton = (Button) findViewById(R.id.buttonSpeak);
        speakButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
//				startVoiceRecognitionActivity();
				Intent intent = new Intent(WelcomeScreen.this, ConfirmInput.class);
	            startActivity(intent);
			}
		});
        chartsButton = (Button) findViewById(R.id.buttonCharts);
        chartsButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(WelcomeScreen.this, ChartsActivity.class);
	            startActivity(intent);				
			}
		});
    }
    
    
    private Button speakButton;
    private Button chartsButton;
}