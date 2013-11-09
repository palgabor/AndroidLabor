package hu.bute.daai.amorg.labor.xg5ort.fragment;

import hu.bute.daai.amorg.labor.xg5ort.LocationFriend;
import hu.bute.daai.amorg.labor.xg5ort.LocationFriendsManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FriendsMapFragment extends SupportMapFragment {

	public static final String TAG = "FriendsMapFragment";
	public static final String TITLE = "Térkép";
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getMap() != null){
			getMap().clear();
			
			getMap().setMyLocationEnabled(true);
			getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47, 19), 5));
			getMap().getUiSettings().setAllGesturesEnabled(false);
		}
		
		for (int i=0; i<LocationFriendsManager.getInstance().getLocationFriendsNum(); i++) {
			LocationFriend tmpFriend = LocationFriendsManager.getInstance().getLocationFriend(i);
			if (tmpFriend.hasCoords()){
				getMap().addMarker(
						  new MarkerOptions().
						  position(new LatLng(tmpFriend.getLat(), tmpFriend.getLng())).
						  title(tmpFriend.getName()));
			}
		}
	}
}