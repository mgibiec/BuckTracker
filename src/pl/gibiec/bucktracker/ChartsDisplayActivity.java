package pl.gibiec.bucktracker;

import pl.gibiec.bucktracker.db.DbHelper;
import pl.gibiec.bucktracker.db.DbManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class ChartsDisplayActivity extends Activity {
	private DbHelper dbHelper;
	ProgressDialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charts);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading...");
		progressDialog.show();
		dbHelper = DbManager.getDbHelper(getApplicationContext());
		
		webView = (WebView) findViewById(R.id.webkt);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(new MyWebChromeClient());
//		webView.loadUrl("file:///android_asset/basic.html");
//		webView.loadUrl("file:///android_asset/graph-types.html");
		data = readChartDataFromDb();
		webView.loadUrl("file:///android_asset/chart.html");
		webView.addJavascriptInterface(new ChartDataLoader(), "dataProvider");
		Log.i("mgibiec", "JavaInretface loaded");
		
	}
	@Override
	protected void onDestroy() {
		dbHelper.close();
		super.onDestroy();
	}
	private WebView webView;
	private String data;
	private static StringBuilder colorBuilder = new StringBuilder();
	private String colorIntToHexRgb(int color){
		colorBuilder.delete(0, colorBuilder.capacity()-1);		
		String red = Integer.toHexString(Color.red(color));
		String green = Integer.toHexString(Color.green(color));
		String blue = Integer.toHexString(Color.blue(color));
		colorBuilder.append("#")
					.append(red.length() < 2 ? "0"+red : red)
				    .append(green.length() < 2 ? "0"+green : green)
					.append(blue.length() < 2 ? "0"+blue : blue);
		
		return colorBuilder.toString();
	}
	private String readChartDataFromDb(){
		Cursor c = dbHelper.getGroupedByCategory("1990-09-09 00:00:00.000", "2990-09-09 00:00:00.000");
		if(c.getCount() < 1){			
			progressDialog.dismiss();
			Toast.makeText(this, "There is no data to generate a report!", Toast.LENGTH_SHORT).show();
			return "";
		}
		StringBuilder sb = new StringBuilder();
		while(c.moveToNext()){
			String temp = c.getString(2);
			Log.i("mgibiec2", temp);
			String color = colorIntToHexRgb(c.getInt(2));
			sb.append(c.getString(0) + "_" + c.getDouble(1) + "_" + color + ";");
		}
		if(sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1); // deleting the last semicolon
		c.close();
		return sb.toString();
	}
	final class ChartDataLoader{
		public void indicateLoadingEnded(){
			progressDialog.dismiss();
		}
		public String getStringData(){
//			String data =  "Lunch_280;Groceries_120;Rent_600;Other_45";
			return data;
		}
		public String getChartTitle(){
			return "Expenses report";
		}
		public String getAxisXLabel(){
			return "";
		}
		public String getAxisYLabel(){
			return "";
		}
		public int getChartHeight(){
			return 300;
		}
		public int getChartWidth(){
			return 310;
		}
	}
	final class MyWebChromeClient extends WebChromeClient{
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result){
			Log.d("mgibiec", message);
			result.confirm();
			return true;
		}
	}
}
