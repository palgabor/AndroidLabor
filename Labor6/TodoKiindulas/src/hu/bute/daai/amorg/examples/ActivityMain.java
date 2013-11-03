package hu.bute.daai.amorg.examples;

import hu.bute.daai.amorg.examples.data.Todo;
import hu.bute.daai.amorg.examples.fragment.TodoDetailsFragment;
import hu.bute.daai.amorg.examples.fragment.TodoListFragment.ITodoListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public class ActivityMain extends FragmentActivity implements ITodoListFragment {

	private ViewGroup fragmentContainer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		fragmentContainer = (ViewGroup) findViewById(R.id.FragmentContainer);
	}

	// ITodoListFragment
	public void onTodoSelected(Todo selectedTodo) {
		if (fragmentContainer != null) {
			FragmentManager fm = getSupportFragmentManager();

			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.FragmentContainer, TodoDetailsFragment
					.newInstance(selectedTodo));
			ft.commit();
		} else {
			Intent i = new Intent(this, ActivityTodoDetails.class);
			i.putExtra(TodoDetailsFragment.KEY_TODO,
					selectedTodo);
			startActivity(i);
		}
	}
}