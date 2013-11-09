package hu.bute.daai.amorg.labor.xg5ort.adapter;

import hu.bute.daai.amorg.labor.xg5ort.LocationFriend;
import hu.bute.daai.amorg.labor.xg5ort.LocationFriendsManager;
import hu.bute.daai.amorg.labor.xg5ort.R;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LocationFriendsAdapter extends BaseAdapter {

	private Location myLocation = null;
	private Context context;

	public LocationFriendsAdapter(Context aContext) {
		context = aContext;
		LocationFriendsManager.getInstance().clear();
		LocationFriendsManager.getInstance().addFriend(
				new LocationFriend("John Doe", "Zalaegerszeg", context));
		LocationFriendsManager.getInstance().addFriend(
				new LocationFriend("Jeff Johnson", "Miskolc", context));
		LocationFriendsManager.getInstance().addFriend(
				new LocationFriend("Test Elek", "Debrecen", context));
		LocationFriendsManager.getInstance().addFriend(
				new LocationFriend("James Demo", "Budapest", context));
	}
	
	public void setMyPosition(Location aMyLocation) {
		myLocation = aMyLocation;
		notifyDataSetChanged();
	}
	
	static class ViewHolder {
		TextView tvName;
		TextView tvAddres;
		TextView tvDistance;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			v = inflater.inflate(R.layout.friendrow, null);
			ViewHolder holder = new ViewHolder();
			holder.tvName = (TextView) v.findViewById(R.id.textViewName);
			holder.tvAddres = (TextView) v.findViewById(R.id.textViewAddress);
			holder.tvDistance = (TextView) v
					.findViewById(R.id.textViewDistance);
			v.setTag(holder);
		}

		final LocationFriend lFriend = LocationFriendsManager.getInstance()
				.getLocationFriend(position);
		if (lFriend != null) {
			ViewHolder holder = (ViewHolder) v.getTag();
			holder.tvName.setText(lFriend.getName());
			holder.tvAddres.setText(lFriend.getAddress());
			if (myLocation != null && lFriend.hasCoords()) {
				float[] results = new float[1];
				Location.distanceBetween(myLocation.getLatitude(),
						myLocation.getLongitude(), lFriend.getLat(),
						lFriend.getLng(), results);
				holder.tvDistance.setText("" + results[0] + " m");
			}
		}

		return v;
	}

	@Override
	public int getCount() {
		return LocationFriendsManager.getInstance().getLocationFriendsNum();
	}

	@Override
	public Object getItem(int position) {
		return LocationFriendsManager.getInstance().getLocationFriend(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}