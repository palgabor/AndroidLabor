package hu.bute.daai.amorg.examples.fragment;

import hu.bute.daai.amorg.examples.R;
import hu.bute.daai.amorg.examples.Todo;
import hu.bute.daai.amorg.examples.Todo.Priority;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TodoDetailsFragment extends Fragment {

	public static final String TAG = "TodoDetailsFragment";

	public static final String KEY_TODO_DESCRIPTION = "todoDesc";

	private TextView todoDescription;

	private static Todo selectedTodo;

	public static TodoDetailsFragment newInstance(String todoDesc) {
		TodoDetailsFragment result = new TodoDetailsFragment();

		Bundle args = new Bundle();
		args.putString(KEY_TODO_DESCRIPTION, todoDesc);
		result.setArguments(args);

		return result;
	}
	
	public static TodoDetailsFragment newInstance(Bundle args) {
		TodoDetailsFragment result = new TodoDetailsFragment();
		
		result.setArguments(args);
		
		return result;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			if (getArguments() != null) {
				selectedTodo = new Todo("cim", Priority.LOW, "1987.23.12",
						getArguments().getString(KEY_TODO_DESCRIPTION));
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_todo_details, container,
				false);

		todoDescription = (TextView) root.findViewById(R.id.todoDescription);
		todoDescription.setText(selectedTodo.getDescription());

		return root;
	}

}