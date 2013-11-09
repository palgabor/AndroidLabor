package hu.bute.daai.amorg.labor.xg5ort.fragment;

import hu.bute.daai.amorg.labor.xg5ort.MyLocationManager;
import hu.bute.daai.amorg.labor.xg5ort.R;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationDashboardFragment extends Fragment {

	public static final String TAG = "LocationDashboardFragment";
	public static final String TITLE = "Pozíció adatok";
	
	private TextView tvProviderValue;
	private TextView tvLatValue;
	private TextView tvLngValue;
	private TextView tvSpeedValue;
	private TextView tvAltValue;
	private TextView tvPosTimeValue;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.location_dashboard, container,
				false);
		return root;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initField(R.id.fieldProvider,getActivity().getString(R.string.txt_provider));
		initField(R.id.fieldLat,getActivity().getString(R.string.txt_latitude));
		initField(R.id.fieldLng,getActivity().getString(R.string.txt_longitude));
		initField(R.id.fieldSpeed,getActivity().getString(R.string.txt_speed));
		initField(R.id.fieldAlt,getActivity().getString(R.string.txt_alt));
		initField(R.id.fieldPosTime,getActivity().getString(R.string.txt_position_time));
	}

	private void initField(int fieldId, String headText) {
		View viewField = getView().findViewById(fieldId);
		TextView tvHead = (TextView) viewField.findViewById(R.id.tvHead);
		tvHead.setText(headText);
		
		switch (fieldId) {
			case R.id.fieldProvider:
				tvProviderValue = (TextView) viewField.findViewById(R.id.tvValue);
				break;
			case R.id.fieldLat:
				tvLatValue = (TextView) viewField.findViewById(R.id.tvValue);
				break;
			case R.id.fieldLng:
				tvLngValue = (TextView) viewField.findViewById(R.id.tvValue);
				break;
			case R.id.fieldSpeed:
				tvSpeedValue = (TextView) viewField.findViewById(R.id.tvValue);
				break;
			case R.id.fieldAlt:
				tvAltValue = (TextView) viewField.findViewById(R.id.tvValue);
				break;
			case R.id.fieldPosTime:
				tvPosTimeValue = (TextView) viewField.findViewById(R.id.tvValue);
				break;
			default:
				break;
		}
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
			
			tvProviderValue.setText(myLocation.getProvider());
			tvLatValue.setText(String.valueOf(myLocation.getLatitude()));
			tvLngValue.setText(String.valueOf(myLocation.getLongitude()));
			tvSpeedValue.setText(String.valueOf(myLocation.getSpeed()));
			tvAltValue.setText(String.valueOf(myLocation.getAltitude()));
			tvPosTimeValue.setText(new Date(myLocation.getTime()).toString());
        }
    };
}