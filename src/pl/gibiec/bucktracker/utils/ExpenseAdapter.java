package pl.gibiec.bucktracker.utils;

import pl.gibiec.bucktracker.R;
import pl.gibiec.bucktracker.db.ExpenseViewHelper;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ExpenseAdapter extends CursorAdapter{
	
	public ExpenseAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		ExpenseHolder holder = (ExpenseHolder) row.getTag();
		holder.populateForm(cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.row, parent, false);
		ExpenseHolder holder = new ExpenseHolder(row);
		row.setTag(holder);
		return row;
	}
}
class ExpenseHolder {
	private TextView category = null;
	private TextView name = null;
	private TextView price = null;
	private TextView date = null;		
	
	public ExpenseHolder(View row){
		category= (TextView) row.findViewById(R.id.categoryInRow);
		name = (TextView) row.findViewById(R.id.nameInRow);
		price = (TextView) row.findViewById(R.id.priceInRow);
		date = (TextView) row.findViewById(R.id.dateInRow);		
	}
	void populateForm(Cursor c){			
		category.setText(ExpenseViewHelper.getCategory(c));
		name.setText(ExpenseViewHelper.getName(c));
		price.setText("$" + ExpenseViewHelper.getPrice(c));
		price.setBackgroundResource(R.drawable.custom_shape_list);
//		price.setBackgroundColor(c.getInt(5));	
		price.setTextColor(c.getInt(5));	
		date.setText(Utils.getDateDisplayStyle((ExpenseViewHelper.getDate(c))));		
	}
}