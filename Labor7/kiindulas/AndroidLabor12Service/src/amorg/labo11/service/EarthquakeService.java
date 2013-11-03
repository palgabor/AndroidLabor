package amorg.labo11.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class EarthquakeService extends Service {

	public static final String NEW_EARTHQUAKE_FOUND = "New_Earthquake_Found";
	private Notification newEarthquakeNotification;
	public static final int NOTIFICATION_ID = 1;
	private EarthquakeLookupTask lastLookup = null;
	AlarmManager alarmManager;
	PendingIntent alarmIntent;

	@Override
	public IBinder onBind(Intent intent) {
		// nem akarjuk mas alkalmazasbol elerhetove tenni
		// (messze ez a leggyakoribb eset)
		// TODO helyes visszateresi ertek beallitasa
	}
	
	@Override
	public void onCreate() {
		
		// Notification osszeallitasa
		long now = System.currentTimeMillis();
		newEarthquakeNotification = new Notification(R.drawable.icon, getString(R.string.notification_tickertext), now);
		
		// referencia az AlarmManager-re
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		// PendingIntent osszallitasa, ami periodikusan elsul
		// idozites beallitasa az onStartCommand-ban
		Intent intentToFire = new Intent(EarthquakeAlarmReceiver.ACTION_REFRESH_EARTHQUAKE_ALARM);
		alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// beallitasok lekerese
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		// automatikus frissites beallitasa, ha szukseges
		boolean autoUpdate = prefs.getBoolean(Preferences.PREF_AUTO_UPDATE, false);
		if (autoUpdate) {
			String updateFreqString = prefs.getString(Preferences.PREF_UPDATE_FREQ, "0");
			int updateFreq = Integer.parseInt(updateFreqString);
			// ismetlodo alarm beallitasa a megadott idokozzel
			// ennek hatasara fog a Service periodikusan lefutni
			int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
			long timeToRefresh = SystemClock.elapsedRealtime() + updateFreq * 60 * 1000;
			alarmManager.setRepeating(alarmType, timeToRefresh, updateFreq * 60 * 1000, alarmIntent);
		} else
			alarmManager.cancel(alarmIntent);
		
		// frissites inditasa
		refreshEarthquakes();
		
		// nem szukseges mindig ujrainditani, megteszi a beallitott alarm
		return Service.START_NOT_STICKY;
	};
	
	// inditja a frissitest, ha epp nem fut
	private void refreshEarthquakes() {
		if (lastLookup == null || lastLookup.getStatus().equals(AsyncTask.Status.FINISHED)) {
			lastLookup = new EarthquakeLookupTask();
			lastLookup.execute((Void[]) null);
		}
	}

	// Sajat AsyncTask leszarmazott, ami a hatterben letolti es feldolgozza a foldrengesek XML-t
	private class EarthquakeLookupTask extends AsyncTask<Void, Quake, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			// Get the XML
			URL url;
			try {
				url = new URL(getString(R.string.quake_feed));
				// Az URL meghivasa
				URLConnection connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				int responseCode = httpConnection.getResponseCode();
				
				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream in = httpConnection.getInputStream();
					
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();

					// Az XML feldolgozasa
					Document dom = db.parse(in);
					Element docEle = dom.getDocumentElement();

					// A foldrenges elemek listaja
					NodeList entryList = docEle.getElementsByTagName("entry");
					if (entryList != null && entryList.getLength() > 0) {
						for (int i = 0; i < entryList.getLength(); i++) {
							// XML elemek
							Element entry = (Element) entryList.item(i);
							Element title = (Element) entry.getElementsByTagName("title").item(0);
							Element g = (Element) entry.getElementsByTagName("georss:point").item(0);
							Element when = (Element) entry.getElementsByTagName("updated").item(0);
							Element link = (Element) entry.getElementsByTagName("link").item(0);
							
							// Szukseges adatok kinyerese
							String details = title.getFirstChild().getNodeValue();
							String linkString = "http://earthquake.usgs.gov" + link.getAttribute("href");
							String point = g.getFirstChild().getNodeValue();
							String dt = when.getFirstChild().getNodeValue();
							
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
							Date date = new GregorianCalendar(0, 0, 0).getTime();
							try {
								date = sdf.parse(dt);
							} catch (ParseException e) {
								e.printStackTrace();
							}

							String[] location = point.split(" ");
							Location loc = new Location("dummyGPS");
							loc.setLatitude(Double.parseDouble(location[0]));
							loc.setLongitude(Double.parseDouble(location[1]));

							String magnitudeString = details.split(" ")[1];
							int end = magnitudeString.length() - 1;
							double magnitude = Double.parseDouble(magnitudeString.substring(0, end));
							
							details = details.split(",")[1].trim();
							
							/* Minden rendelkezesre all a megfelelo formatumban */
							
							Quake quake = new Quake(date, details, loc, magnitude, linkString);

							// Uj foldrenges objektum feldolgozasa
							addNewQuake(quake);
							// frissitjuk a notification-t
							publishProgress(quake);
						}
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} finally {
			}
			return null;
		}
		
		// A publishProgress hivas hatasara lefuto esemenykezelo
		// Notification frissitese
		@Override
		protected void onProgressUpdate(Quake... values) {
			Quake foundQuake = values[0];
			
			String expandedText = foundQuake.getDate().toString();
			String expandedTitle = "M:" + foundQuake.getMagnitude() + " " + foundQuake.getDetails();
			
			Intent startActivityIntent = new Intent(EarthquakeService.this, MainActivity.class);
			PendingIntent launchIntent = PendingIntent.getActivity(getApplicationContext(), 0, startActivityIntent, 0);
			
			newEarthquakeNotification.setLatestEventInfo(getApplicationContext(), expandedTitle, expandedText, launchIntent);
			newEarthquakeNotification.when = java.lang.System.currentTimeMillis();
			
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(NOTIFICATION_ID, newEarthquakeNotification);

			Log.v("Foldrenges", "Uj foldrenges: " + expandedTitle);
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Service megallitasa (ez akkor fut le, ha mar vegzett a feldolgozassal)
		}
	}

	// az uj foldrenges felvetele a ContentProvider-be
	private void addNewQuake(Quake quake) {
		ContentResolver cr = getContentResolver();
		// Megnezzuk hogy nincs-e mar benne (ido vizsgalat)
		String w = EarthquakeProvider.KEY_DATE + " = " + quake.getDate().getTime();

		// Ha meg nincs, akkor letrehozzuk
		if (cr.query(EarthquakeProvider.EARTHQUAKEPROVIDER_CONTENT_URI, null, w, null, null).getCount() == 0) {
			ContentValues values = new ContentValues();
			values.put(EarthquakeProvider.KEY_DATE, quake.getDate().getTime());
			values.put(EarthquakeProvider.KEY_DETAILS, quake.getDetails());

			double lat = quake.getLocation().getLatitude();
			double lng = quake.getLocation().getLongitude();
			
			values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
			values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
			values.put(EarthquakeProvider.KEY_LINK, quake.getLink());
			values.put(EarthquakeProvider.KEY_MAGNITUDE, quake.getMagnitude());

			cr.insert(EarthquakeProvider.EARTHQUAKEPROVIDER_CONTENT_URI, values);
			// broadcast kuldese az uj elemrol
			announceNewQuake(quake);
		}
	}

	// osszeallitja es elkuldi az uj foldrenges ertesitest
	private void announceNewQuake(Quake quake) {
		Intent intent = new Intent(NEW_EARTHQUAKE_FOUND);
		intent.putExtra("date", quake.getDate().getTime());
		intent.putExtra("details", quake.getDetails());
		intent.putExtra("longitude", quake.getLocation().getLongitude());
		intent.putExtra("latitude", quake.getLocation().getLatitude());
		intent.putExtra("magnitude", quake.getMagnitude());
		
		// TODO ertesitsuk a feliratkozott komponenseket az uj foldrengesrol
	}

}