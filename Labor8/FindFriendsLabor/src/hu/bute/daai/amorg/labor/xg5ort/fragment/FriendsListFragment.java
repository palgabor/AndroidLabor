package hu.bute.daai.amorg.labor.xg5ort.fragment;

import hu.bute.daai.amorg.labor.xg5ort.MyLocationManager;
import hu.bute.daai.amorg.labor.xg5ort.R;
import hu.bute.daai.amorg.labor.xg5ort.adapter.LocationFriendsAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendsListFragment extends ListFragment {

	public static final String TAG = "FriendsListFragment";
	public static final String TITLE = "Személyek";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.friendslist, container, false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new LocationFriendsAdapter(getActivity().getApplicationContext()));
	}

	@Override
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter(MyLocationManager.BR_LOCATION_INFO));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
			Location myLocation = intent.getParcelableExtra(MyLocationManager.KEY_LOCATION);
			((LocationFriendsAdapter)getListAdapter()).setMyPosition(myLocation);
        }
    };
}