package hu.bute.daai.amorg.androidlabor4.fragment;

import hu.bute.daai.amorg.androidlabor4.R;
import hu.bute.daai.amorg.androidlabor4.data.Todo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TodoDetailsFragment extends Fragment {

	public static final String TAG = "TodoDetailsFragment";

	public static final String KEY_TODO = "todo";

	private TextView todoDescription;

	private static Todo selectedTodo;

	public static TodoDetailsFragment newInstance(Todo todo) {
		TodoDetailsFragment result = new TodoDetailsFragment();

		Bundle args = new Bundle();
		args.putParcelable(KEY_TODO, todo);
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
				selectedTodo = getArguments().getParcelable(KEY_TODO);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_todo_details, container,
				false);

		todoDescription = (TextView) root.findViewById(R.id.todoDescription);
		todoDescription.setText(Html.fromHtml(
				"<h3>"+selectedTodo.getTitle()+"</h3>"
				+selectedTodo.getDescription()
				));

		return root;
	}

}
