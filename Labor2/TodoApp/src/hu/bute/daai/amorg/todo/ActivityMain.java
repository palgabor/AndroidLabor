package hu.bute.daai.amorg.todo;

import hu.bute.daai.amorg.todo.adapters.TodoAdapter;
import hu.bute.daai.amorg.todo.data.DataPreferences;
import hu.bute.daai.amorg.todo.data.Todo;
import hu.bute.daai.amorg.todo.data.Todo.Priority;
import hu.bute.daai.amorg.todoapp.R;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ActivityMain extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ArrayList<Todo> todos = new ArrayList<Todo>();
        todos.add(new Todo("title1", Priority.LOW, "2011. 09. 26.", "description1"));
        todos.add(new Todo("title2", Priority.MEDIUM, "2011. 09. 27.", "description2"));
        todos.add(new Todo("title3", Priority.HIGH, "2011. 09. 28.", "description3"));
        TodoAdapter todoAdapter = new TodoAdapter(
                getApplicationContext(), todos);
        setListAdapter(todoAdapter);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView parent, View v, int position, long id) {
        		Todo selectedTodo = (Todo)getListAdapter().getItem(position);
        		Toast.makeText(ActivityMain.this, selectedTodo.getDescription(), Toast.LENGTH_LONG).show();
        	}
		});
        registerForContextMenu(getListView());
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	if (v.equals(getListView())) {
    		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
    		menu.setHeaderTitle(((Todo)getListAdapter().getItem(info.position)).getTitle());
    		String[] menuItems = getResources().getStringArray(R.array.todomenu);
    		for (int i = 0; i<menuItems.length; i++) {
    			menu.add(Menu.NONE, i, i, menuItems[i]);
    		}
    	}
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    	int menuItemIndex = item.getItemId();
    	if (menuItemIndex==0)
    	{
    		((TodoAdapter)getListAdapter()).deleteRow((Todo)getListAdapter().getItem(info.position));
    		((TodoAdapter)getListAdapter()).notifyDataSetChanged();
    	}
    	return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	if (item.getItemId() == R.id.itemCreateTodo)
    	{
    		Intent myIntent = new Intent();
    		myIntent.setClassName("hu.bute.daai.amorg.todoapp",
    		    "hu.bute.daai.amorg.todo.ActivityCreateTodo");
    		startActivity(myIntent);
    	}

    	return super.onOptionsItemSelected(item);
    }
    

	@Override
	protected void onResume() {
		super.onResume();
		
		// NAGYON RONDA MEGOLDÁS
		// Intent elõadáson megmutatjuk a helyeset!
		if (DataPreferences.todoToCreate != null)
		{
		((TodoAdapter)getListAdapter()).addItem(DataPreferences.todoToCreate);
			DataPreferences.todoToCreate = null;
			((TodoAdapter)getListAdapter()).notifyDataSetChanged();
		}
	}
}