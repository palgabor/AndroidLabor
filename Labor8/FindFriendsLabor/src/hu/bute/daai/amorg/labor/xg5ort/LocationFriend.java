package hu.bute.daai.amorg.labor.xg5ort;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class LocationFriend {

	private String name;
	private String address;
	private boolean hasCoords = false;
	private double lat = 0;
	private double lng = 0;

	public LocationFriend(String aName, String aAddress, Context aContext) {
		name = aName;
		address = aAddress;
		setCoords(aContext);
	}
	
	private void setCoords(final Context aContext) {
		new Thread() {
			public void run() {
				try {
					Geocoder geocoder = new Geocoder(aContext);
					List<Address> locations = null;
					locations = geocoder.getFromLocationName(address, 1);
					lat = locations.get(0).getLatitude();
					lng = locations.get(0).getLongitude();
					hasCoords = true;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public boolean hasCoords() {
		return hasCoords;
	}
}
