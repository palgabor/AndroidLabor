package hu.bute.daai.amorg.labor.xg5ort.adapter;

import hu.bute.daai.amorg.labor.xg5ort.fragment.FriendsListFragment;
import hu.bute.daai.amorg.labor.xg5ort.fragment.FriendsMapFragment;
import hu.bute.daai.amorg.labor.xg5ort.fragment.LocationDashboardFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FriendsFragmentPagerAdapter extends FragmentPagerAdapter {

	private LocationDashboardFragment locationDashboardFragment;
	private FriendsListFragment friendsListFragment;
	private FriendsMapFragment friendsMapFragment;
	
	public FriendsFragmentPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		locationDashboardFragment = new LocationDashboardFragment();
		friendsListFragment = new FriendsListFragment();
		friendsMapFragment = new FriendsMapFragment();
	}

	@Override
	public Fragment getItem(int pos) {
		switch (pos) {
		case 0:
			return locationDashboardFragment;
		case 1:
			return friendsListFragment;
		case 2:
			return friendsMapFragment;
		default:
			return null;
		}
	}
	
	@Override
	public CharSequence getPageTitle(int pos) {
		switch (pos) {
			case 0:
				return LocationDashboardFragment.TITLE;
			case 1:
				return FriendsListFragment.TITLE;
			case 2:
				return FriendsMapFragment.TITLE;
			default:
				return null;
		}
	}

	@Override
	public int getCount() {
		return 3;
	}
	
	public FriendsListFragment getFriendsListFragment() {
		return friendsListFragment;
	}
}