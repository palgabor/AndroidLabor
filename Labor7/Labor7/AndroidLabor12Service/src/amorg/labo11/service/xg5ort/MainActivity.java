package amorg.labo11.service.xg5ort;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	static final private int MENU_UPDATE = Menu.FIRST;
	static final private int MENU_PREFERENCES = Menu.FIRST + 1;
	static final private int MENU_EARTHQUAKE_MAP = Menu.FIRST + 2;

	static final private int QUAKE_DIALOG = 1;
	private static final int SHOW_PREFERENCES = 1;

	NotificationManager notificationManager;
	EarthquakeReceiver receiver;
	ListView earthquakeListView;
	ArrayAdapter<Quake> quakeAdapter;
	ArrayList<Quake> earthquakesList = new ArrayList<Quake>();
	
	// listabol kivalasztott foldrenges taroloja
	Quake selectedQuake;

	int minimumMagnitude = 0;
	boolean autoUpdate = false;
	int updateFreq = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// ListView referencia
		earthquakeListView = (ListView) this.findViewById(R.id.earthquakeListView);
		
		// valamely sorra kattintva megjelenitunk egy dialogusablakot a reszletekkel 
		earthquakeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView _av, View _v, int index, long arg3) {
				selectedQuake = earthquakesList.get(index);
				// TODO a dialogusablak megjelenitese a kovetkezo utasitas hatasara
				showDialog(QUAKE_DIALOG);
			}
		});
		
		// ArrayAdapter a foldrengesek megjelenitesehez
		quakeAdapter = new ArrayAdapter<Quake>(this, android.R.layout.simple_list_item_1, earthquakesList);
		earthquakeListView.setAdapter(quakeAdapter);
		
		// referencia a NotificationManager-re
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// betoltjuk a foldrengeseket a sajat ContentProvider-bol
		loadQuakesFromProvider();
		// frissitjuk a parametereket a beallitasok alapjan
		updateFromPreferences();
		// elinditjuk a Service-t
		refreshEarthquakes();

	}
	
	// A kapott foldrengest hozzaadjuk az ArrayList-hez
	private void addQuakeToArray(Quake quake) {
		// ha a foldrenges erossege a beallitasokban megadott ertek folott van 
		// akkor hozzaadjuk az earthquakesList tombhoz, es ertesitjuk az Adaptert a valtozasrol
		if(quake.getMagnitude() > minimumMagnitude) {
			earthquakesList.add(quake);
			quakeAdapter.add(quake);
		}
		
	}

	// A sajat provider-bol betoltjuk az ott talalhato foldrengeseket
	private void loadQuakesFromProvider() {
		// TODO tomb uritese
		
		// default ContentResolver-t hasznaljuk
		ContentResolver cr = getContentResolver();

		// lekerunk minden foldrengest
		Cursor cursor = cr.query(EarthquakeProvider.EARTHQUAKEPROVIDER_CONTENT_URI, null, null, null, null);
		// a cursor eletciklusanak kezeleset az Androidra bizzuk
		startManagingCursor(cursor);
		
		// hatultesztelo ciklussal vegigmegyunk a Cursor altal mutatott halmazon
		if (cursor.moveToFirst()) {
			do {
				// Kinyerjuk az adatokat
				Long datems = cursor.getLong(EarthquakeProvider.DATE_COLUMN);
				String details = cursor.getString(EarthquakeProvider.DETAILS_COLUMN);
				Float lat = cursor.getFloat(EarthquakeProvider.LATITUDE_COLUMN);
				Float lng = cursor.getFloat(EarthquakeProvider.LONGITUDE_COLUMN);
				Double mag = cursor.getDouble(EarthquakeProvider.MAGNITUDE_COLUMN);
				String link = cursor.getString(EarthquakeProvider.LINK_COLUMN);

				Location location = new Location("dummy");
				location.setLongitude(lng);
				location.setLatitude(lat);

				Date date = new Date(datems);
				
				// letrehozzuk az uj objektumot
				Quake newQuake = new Quake(date, details, location, mag, link);
				// ha az uj foldrenges erossege a beallitasoknak megfelelo, akkor hozzaadjuk a tombhoz
				addQuakeToArray(newQuake);
			} while (cursor.moveToNext());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Update");
		menu.add("Preferences");
		menu.add("Eartquake map");
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO menu kivalasztasanak kezelese
		// MENU_UPDATE: Foldrengesek azonnali frissitese
		// MENU_PREFERENCES: Beallitasok megjelenitese (ertesulni akarunk az eredmenyrol)
		// MENU_EARTHQUAKE_MAP: terkep megjelenitese (nem akarunk ertesulni)
		switch(item.getItemId())
        {
            case MENU_UPDATE:
            	updateFromPreferences();
                  return true;
            case MENU_PREFERENCES:
                // do whatever
                return true;
            case MENU_EARTHQUAKE_MAP:
                // do whatever
                return true;
        }
		
		return false;
	}
	
	
	
	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
			case (QUAKE_DIALOG):
				LayoutInflater li = LayoutInflater.from(this);
				View quakeDetailsView = li.inflate(R.layout.quake_details, null);
				// TODO dialogusablak feldobasa
		}
		return null;
	}
	
	// elinditja a foldrengeseket letolto Service-t
	private void refreshEarthquakes() {
		// TODO Service azonnali inditasa
	}
	
	// minimumMagnitude, updateFreq és autoUpdate parameterek beolvasasa a beallitasokbol
	private void updateFromPreferences() {
		// TODO beallitasok alapjan a parameterek beolvasasa
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// TODO Ha RESULT_OK-val tert vissza a beallitasok, akkor parameterek es foldrengesek frissitese

	}
	
	public class EarthquakeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			loadQuakesFromProvider();
			notificationManager.cancel(EarthquakeService.NOTIFICATION_ID);
		}
	}

	@Override
	public void onResume() {
		// folytatodaskor a BroadcastReceiver regisztralasa
		notificationManager.cancel(EarthquakeService.NOTIFICATION_ID);
		IntentFilter filter = new IntentFilter(EarthquakeService.NEW_EARTHQUAKE_FOUND);
		receiver = new EarthquakeReceiver();
		registerReceiver(receiver, filter);
		
		// betoltjuk ujra a foldrengeseket, a legutobbi futas ota lehet hogy vannak ujak
		loadQuakesFromProvider();
		super.onResume();
	}

	@Override
	public void onPause() {
		// ha hatterbe kerul az activity, akkor nem kell a receiver
		unregisterReceiver(receiver);
		super.onPause();
	}
}
