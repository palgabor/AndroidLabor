package hu.bute.daai.amorg.labor.xg5ort;

import hu.bute.daai.amorg.labor.xg5ort.adapter.FriendsFragmentPagerAdapter;
import hu.bute.daai.amorg.labor.xg5ort.adapter.LocationFriendsAdapter;
import hu.bute.daai.amorg.labor.xg5ort.fragment.FriendsListFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	private static final int REQUEST_PICK_CONTACT = 100;
	private ViewPager pager;
	private MyLocationManager myLocMan;
	private FriendsFragmentPagerAdapter fragmentAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pager = (ViewPager) findViewById(R.id.mainViewPager);
		fragmentAdapter = new FriendsFragmentPagerAdapter(
				getSupportFragmentManager());
		pager.setAdapter(fragmentAdapter);
		
		myLocMan = new MyLocationManager(this);		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (myLocMan != null) {
			myLocMan.startLocationMonitoring();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (myLocMan != null) {
			myLocMan.stopLocationMonitoring();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, 
			int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_PICK_CONTACT:
					Uri contactUri = data.getData();
					if (contactUri != null) {
						Cursor c = null;
						Cursor addrCur = null;
						try {
							ContentResolver cr = getContentResolver();
							c = cr.query(contactUri,null,null, null, null);
							if (c != null && c.moveToFirst()) {
								String id = c.getString(c.getColumnIndex(BaseColumns._ID));
								String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
								String address = "";
								
								addrCur = cr.query( 
									    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, 
									    null,
									    ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?", 
									    new String[]{id}, null); 
								
								if (addrCur != null && addrCur.moveToFirst()) {
									address = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DATA));
								}
								
								LocationFriendsManager.getInstance().addFriend(
										new LocationFriend(name, address, getApplicationContext()));
								FriendsListFragment friendsListFragment = fragmentAdapter
										.getFriendsListFragment();
								if (friendsListFragment != null) {
									LocationFriendsAdapter adapter = (LocationFriendsAdapter) friendsListFragment
											.getListAdapter();
									if (adapter != null) {
										adapter.notifyDataSetChanged();
									}
								}
							}
						} finally {
							if (c != null) {
								c.close();
							}
							if (addrCur != null) {
								addrCur.close();
							}
						}
					}
					
					break;
				default:
					break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.itemAddContact)
		{
	        Intent intent = new Intent(
	    		Intent.ACTION_PICK,
	    		ContactsContract.Contacts.CONTENT_URI);
	        startActivityForResult(intent, REQUEST_PICK_CONTACT);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
}
