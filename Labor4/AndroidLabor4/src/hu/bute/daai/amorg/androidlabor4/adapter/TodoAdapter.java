package hu.bute.daai.amorg.androidlabor4.adapter;

import hu.bute.daai.amorg.androidlabor4.data.Todo;

import java.util.ArrayList;
import java.util.List;

import hu.bute.daai.amorg.androidlabor4.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TodoAdapter extends BaseAdapter {

    private final List<Todo> todos;

    public TodoAdapter(final Context context, final ArrayList<Todo> aTodos) {
    	todos = aTodos;
    }
    
    public void addItem(Todo aTodo)
    {
    	todos.add(aTodo);
    }

    public int getCount() {
        return todos.size();
    }

    public Object getItem(int position) {
        return todos.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * Sor megjelenenítésének beállítása
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        final Todo todo = todos.get(position);

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
    		Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.todorow, null);

        ImageView imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewPriority);
        switch (todo.getPriority()) {
			case LOW:
				imageViewIcon.setImageResource(R.drawable.low);
				break;
			case MEDIUM:
				imageViewIcon.setImageResource(R.drawable.medium);
				break;
			case HIGH:
				imageViewIcon.setImageResource(R.drawable.high);
				break;
			default:
				imageViewIcon.setImageResource(R.drawable.high);
				break;
		}
        
        TextView textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        textViewTitle.setText(todo.getTitle());
        
        TextView textViewDueDate = (TextView) itemView.findViewById(R.id.textViewDueDate);
        textViewDueDate.setText(todo.getDueDate());
        
        return itemView;
    }

    /**
     * Egye elem törlése
     */
    public void deleteRow(Todo aTodo) {
        if(todos.contains(aTodo)) {
        	todos.remove(aTodo);
        }
    }
}
