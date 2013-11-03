package hu.bute.daai.amorg.examples;

import hu.bute.daai.amorg.examples.fragment.TodoDetailsFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ActivityTodoDetails extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_details);

		if (savedInstanceState == null && getIntent().getExtras() != null) {
			Bundle args = new Bundle(getIntent().getExtras());
			TodoDetailsFragment detailsFragment = TodoDetailsFragment
					.newInstance(args);
			
			// Add details fragment
			FragmentManager fm = getSupportFragmentManager();
			
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(R.id.FragmentContainer, detailsFragment);
			ft.commit();
		}
	}
}