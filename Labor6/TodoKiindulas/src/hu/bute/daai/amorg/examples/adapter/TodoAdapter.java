package hu.bute.daai.amorg.examples.adapter;

import hu.bute.daai.amorg.examples.R;
import hu.bute.daai.amorg.examples.data.Todo;
import hu.bute.daai.amorg.examples.datastorage.TodoDbLoader;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TodoAdapter extends CursorAdapter {

	public TodoAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View row = inflater.inflate(R.layout.todorow, null);
		bindView(row, context, cursor);
		return row;
	}
	
	// UI elemek feltöltése
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// referencia a UI elemekre
		TextView titleTV = (TextView) view.findViewById(R.id.textViewTitle);
		TextView dueDateTV = (TextView) view.findViewById(R.id.textViewDueDate);
		ImageView priorityIV = (ImageView) view.findViewById(R.id.imageViewPriority);
		
		// Todo példányosítás Cursorból
		Todo todo = TodoDbLoader.getTodoByCursor(cursor);
		
		// UI elemek
		titleTV.setText(todo.getTitle());
		dueDateTV.setText(todo.getDueDate());
		switch (todo.getPriority()) {
			case HIGH:
				priorityIV.setImageResource(R.drawable.high);
				break;
			case MEDIUM:
				priorityIV.setImageResource(R.drawable.medium);
				break;
			case LOW:
				priorityIV.setImageResource(R.drawable.low);
				break;
		}
		
	}


	@Override
	public Todo getItem(int position) {
		getCursor().moveToPosition(position);
		
		return TodoDbLoader.getTodoByCursor(getCursor());
	}
    
}