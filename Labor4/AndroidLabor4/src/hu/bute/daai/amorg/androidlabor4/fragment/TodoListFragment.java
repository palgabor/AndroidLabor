package hu.bute.daai.amorg.androidlabor4.fragment;

import hu.bute.daai.amorg.androidlabor4.adapter.TodoAdapter;
import hu.bute.daai.amorg.androidlabor4.data.Todo;
import hu.bute.daai.amorg.androidlabor4.data.Todo.Priority;
import hu.bute.daai.amorg.androidlabor4.fragment.TodoCreateFragment.ITodoCreateFragment;

import java.util.ArrayList;

import hu.bute.daai.amorg.androidlabor4.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class TodoListFragment extends ListFragment implements
		ITodoCreateFragment {

	// Log tag
	public static final String TAG = "TodoListFragment";

	// State
	private TodoAdapter adapter;

	// Listener
	private ITodoListFragment listener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			listener = (ITodoListFragment) activity;
		} catch (ClassCastException ce) {
			Log.e(TAG, "Parent Activity does not implement listener interface!");
		} catch (Exception e) {
			Log.e(TAG, "Unexpected exception!");
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onStart() {
		super.onStart();

		// Adapter letrehozasa esfeltoltese nehany elemmel
		ArrayList<Todo> todos = new ArrayList<Todo>();
		todos.add(new Todo("title1", Priority.LOW, "2011. 09. 26.",
				"description1"));
		todos.add(new Todo("title2", Priority.MEDIUM, "2011. 09. 27.",
				"description2"));
		todos.add(new Todo("title3", Priority.HIGH, "2011. 09. 28.",
				"description3"));
		adapter = new TodoAdapter(getActivity(), todos);
		setListAdapter(adapter);
		
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.equals(getListView())) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(((Todo) getListAdapter().getItem(info.position))
					.getTitle());
			String[] menuItems = getResources()
					.getStringArray(R.array.todomenu);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		if (menuItemIndex == 0) {
			((TodoAdapter) getListAdapter()).deleteRow((Todo) getListAdapter()
					.getItem(info.position));
			((TodoAdapter) getListAdapter()).notifyDataSetChanged();
		}
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Todo selectedTodo = (Todo) getListAdapter().getItem(position);

		if (listener != null) {
			listener.onTodoSelected(selectedTodo);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.listmenu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.itemCreateTodo) {
			TodoCreateFragment createFragment = new TodoCreateFragment();
			createFragment.setTargetFragment(this, 0);

			FragmentManager fm = getFragmentManager();
			createFragment.show(fm, TodoCreateFragment.TAG);
		}

		return super.onOptionsItemSelected(item);
	}

	public interface ITodoListFragment {
		public void onTodoSelected(Todo selecedTodo);
	}

	// ITodoCreateFragment
	public void onTodoCreated(Todo newTodo) {
		adapter.addItem(newTodo);
		adapter.notifyDataSetChanged();
	}
}
