package hu.bute.daai.amorg.labor.xg5ort;

import java.util.ArrayList;

public class LocationFriendsManager {

	private static LocationFriendsManager instance = null;
	
	public static LocationFriendsManager getInstance() {
		if (instance == null) {
			instance = new LocationFriendsManager();
		}
		return instance;
	}
	
	private ArrayList<LocationFriend> locationFriends;
	
	//ide private constructor kellene, hogy singleton legyen az osztaly
	public LocationFriendsManager() {
		locationFriends = new ArrayList<LocationFriend>();
	}
	
	public LocationFriend getLocationFriend(int aIndex) {
		return locationFriends.get(aIndex);
	}
	
	public int getLocationFriendsNum() {
		return locationFriends.size();
	}
	
	public void addFriend(LocationFriend locFriend) {
		locationFriends.add(locFriend);
	}
	
	public ArrayList<LocationFriend> getLocationFriends() {
		return locationFriends;
	}

	public void clear() {
		if (locationFriends != null) {
			locationFriends.clear();
		}
	}
}