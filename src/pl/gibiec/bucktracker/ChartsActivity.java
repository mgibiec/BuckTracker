package pl.gibiec.bucktracker;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ChartsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charts);
		
		webView = (WebView) findViewById(R.id.webkt);
		webView.getSettings().setJavaScriptEnabled(true);
//		webView.loadUrl("file:///android_asset/basic.html");
		webView.loadUrl("file:///android_asset/graph-types.html");
	}
	private WebView webView;
}
