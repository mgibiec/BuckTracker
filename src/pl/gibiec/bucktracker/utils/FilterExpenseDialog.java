package pl.gibiec.bucktracker.utils;

import pl.gibiec.bucktracker.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class FilterExpenseDialog extends Dialog implements TabContentFactory {

	private final Context context;
	public FilterExpenseDialog(Context context) {
		super(context);
		this.context = context;
		setContentView(R.layout.filter_expenses_dialog);
		
		TabHost tabHost = (TabHost) findViewById(R.id.tabhostDialog);
		TabSpec tab1 = tabHost.newTabSpec("tab_test1")
							  .setIndicator("Tab 1")
							  .setContent(this);
		
		tabHost.addTab(tab1);
//		tabHost.addTab(tabHost.newTabSpec("tab_test1")
//				.setIndicator("Filter category"));
//				.setContent(R.id.tab1_contents_dialog));
//		tabHost.addTab(tabHost.newTabSpec("tab_test2")
//				.setIndicator("Filter timespan"));
//				.setContent(R.id.tab2_contents_dialog));
//		
//		tabHost.setCurrentTab(0);	
	}
	public View createTabContent(String tag) {
		final TextView tv = new TextView(FilterExpenseDialog.this.context);
		tv.setText("Content fot tab with tag: " + tag);
		return tv;
	}

}
