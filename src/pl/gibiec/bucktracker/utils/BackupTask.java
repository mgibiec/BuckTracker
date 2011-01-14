package pl.gibiec.bucktracker.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import pl.gibiec.bucktracker.db.DbHelper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class BackupTask extends AsyncTask<String, Integer, Intent>{
	private Context context;
	private DbHelper dbHelper;
	public BackupTask(Context context, DbHelper dbHelper){
		this.context = context;
		this.dbHelper = dbHelper;
	}
	@Override
	protected Intent doInBackground(String... params) {
		
		String state = Environment.getExternalStorageState();

		if (!Environment.MEDIA_MOUNTED.equals(state)) {
		    Toast.makeText(context, "Sorry, SD card is not ready to do backup", Toast.LENGTH_LONG).show();
			return null;    
		}
		String rootDirectory = Environment.getExternalStorageDirectory().toString();						
		String fileName = params[0];
		fileName = "BT_BackUp.csv";
		
		File myFile = new File(rootDirectory, fileName);
		
		Cursor c = dbHelper.getAllExpensesFromView();
		StringBuilder sb = new StringBuilder();
		while(c.moveToNext()){
			sb.append(c.getString(1) + ";")
			  .append(c.getString(2) + ";")
			  .append(c.getString(3) + ";")
			  .append(c.getString(4) + ";")
			  .append(c.getString(5) + ";")
			  .append("\n");
		}
		c.close();
		
		OutputStream fos;
		try {
			fos = new FileOutputStream(myFile);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (FileNotFoundException e) {				
			e.printStackTrace();
		} catch (IOException e) {				
			e.printStackTrace();
		}
		
		String emailSubject = "Buck Tracker expenses backup";
		String emailBody = "Hello," +
				"\n\nPlease find enclosed a backup of your expenses stored in Buck Tracker Android App." +
				"\n\nThanks for using Buck Tracker!" +
				"\n\nAndroMarek";
		Uri uri = Uri.fromFile(myFile);
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");  
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"Your Email"});  
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);  
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(emailIntent);
//		Uri.parse("file:///" + fileName)
//		deleteFile(fileName);
		return emailIntent;
	}
	
	@Override
	protected void onPostExecute(Intent result) {
		super.onPostExecute(result);
	}
	
}
