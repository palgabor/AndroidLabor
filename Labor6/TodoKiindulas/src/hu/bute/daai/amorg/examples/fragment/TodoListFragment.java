package hu.bute.daai.amorg.examples.fragment;

import hu.bute.daai.amorg.examples.R;
import hu.bute.daai.amorg.examples.adapter.TodoAdapter;
import hu.bute.daai.amorg.examples.application.TodoApplication;
import hu.bute.daai.amorg.examples.data.Todo;
import hu.bute.daai.amorg.examples.datastorage.DbConstants;
import hu.bute.daai.amorg.examples.datastorage.TodoDbLoader;
import hu.bute.daai.amorg.examples.fragment.TodoCreateFragment.ITodoCreateFragment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
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
	private LocalBroadcastManager lbm;

	// Listener
	private ITodoListFragment listener;

	// DBloader
	private TodoDbLoader dbLoader;
	private GetAllTask getAllTask;

	private BroadcastReceiver updateDbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshList();
		}
	};

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

		lbm = LocalBroadcastManager.getInstance(getActivity());
		dbLoader = TodoApplication.getTodoDbLoader();
	}

	@Override
	public void onStart() {
		super.onStart();

		registerForContextMenu(getListView());
	}

	@Override
	public void onResume() {
		super.onResume();

		// Kódból regisztraljuk az adatbazis modosulasara figyelmezteto
		// Receiver-t
		IntentFilter filter = new IntentFilter(
				DbConstants.ACTION_DATABASE_CHANGED);
		lbm.registerReceiver(updateDbReceiver, filter);

		// Frissitjuk a lista tartalmat, ha visszater a user
		refreshList();
	}

	@Override
	public void onPause() {
		super.onPause();

		lbm.unregisterReceiver(updateDbReceiver);
		if (getAllTask != null) {
			getAllTask.cancel(false);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (adapter != null && adapter.getCursor() != null) {
			adapter.getCursor().close();
		}
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
			Cursor cursor = ((CursorAdapter)getListAdapter()).getCursor();
			cursor.moveToPosition(info.position);
			int rowId = cursor.getInt(cursor.getColumnIndex(DbConstants.Todo.KEY_ROWID));
			dbLoader.deleteTodo(rowId);
			refreshList();
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
		
		if(item.getItemId() == R.id.itemDeleteAllTodo){
			dbLoader.deleteAllTodo();
			refreshList();
		}

		return super.onOptionsItemSelected(item);
	}

	public interface ITodoListFragment {
		public void onTodoSelected(Todo selecedTodo);
	}

	// ITodoCreateFragment
	public void onTodoCreated(Todo newTodo) {
		dbLoader.createTodo(newTodo);
		refreshList();
	}

	private class GetAllTask extends AsyncTask<Void, Void, Cursor> {
		private static final String TAG = "GetAllTask";

		@Override
		protected Cursor doInBackground(Void... params) {
			try {
				Cursor result = dbLoader.fetchAll();
				if (!isCancelled()) {
					return result;
				} else {
					Log.d(TAG, "Cancelled, closing cursor");
					if (result != null) {
						result.close();
					}
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);
			Log.d(TAG, "Fetch completed, displaying cursor results!");
			try {
				if (adapter == null) {
					adapter = new TodoAdapter(getActivity()
							.getApplicationContext(), result);
					setListAdapter(adapter);
				} else {
					adapter.changeCursor(result);
				}
				getAllTask = null;
			} catch (Exception e) {
			}
		}
	}

	private void refreshList() {
		if (getAllTask != null) {
			getAllTask.cancel(false);
		}
		getAllTask = new GetAllTask();
		getAllTask.execute();
	}

}
