package amorg.labo11.service;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

// Sajat ContentProvider a foldrengesek eleresehez
public class EarthquakeProvider extends ContentProvider {

	// publikus URI, amin elerheto a provider 
	// ez alapjan kell kitolteni az AndroidManifest-ben az 'android:authorities' attributumot
	public static final Uri EARTHQUAKEPROVIDER_CONTENT_URI = Uri.parse("content://amorg.labo11.provider.earthquake/earthquakes");

	// UriMatcher segedosztaly hasznalata a bejovo Uri-k ellenorzesehez
	// Ha az Uri vege 'earthquakes', akkor minden foldrengest vissza kell adnunk
	// Ha az Uri vege 'earthquakes/[ID]', akkor csak azt az egyet
	private static final UriMatcher uriMatcher;
	// Segedkonstansok a ketfele lekereshez (egy vagy tobb)
	private static final int QUAKES = 1;
	private static final int QUAKE_ID = 2;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("amorg.labo11.provider.Earthquake", "earthquakes", QUAKES);
		uriMatcher.addURI("amorg.labo11.provider.Earthquake", "earthquakes/#", QUAKE_ID);
	}
	
	@Override
	public boolean onCreate() {
		// Adatbazis megnyitasa
		EarthquakeDatabaseHelper dbHelper = new EarthquakeDatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
		earthquakeDB = dbHelper.getWritableDatabase();
		return (earthquakeDB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		//Ez a ContentProvider legfontosabb metodusa, ezt fogjak hivni akik hasznalni akarjak 
		
		// segedosztaly hogy ne kelljen kezzel SQL query-t irni
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(EARTHQUAKE_TABLE);

		// UriMatcher hasznalata, ha van ID, akkor a query WHERE reszebe berakjuk az id=[ID] -t
		switch (uriMatcher.match(uri)) {
			case QUAKE_ID:
				queryBuilder.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
				break;
			default:
				break;
		}

		// Ha a bejovo query-ben nincs rendezes megadva, akkor alapertelmezett beallitasa
		String orderBy = "";
		// igy is lehet String uresseget vizsgalni
		if (TextUtils.isEmpty(sort)) {
			orderBy = KEY_DATE;
		} else {
			orderBy = sort;
		}

		// Lefuttatjuk a query-t
		Cursor cursor = queryBuilder.query(earthquakeDB, projection, selection, selectionArgs, null, null, orderBy);
		return cursor;
	}

	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {
		// Uj elem felvetele, sikeres beszuras eseten visszater az ID-val
		long rowID = earthquakeDB.insert(EARTHQUAKE_TABLE, "quake", _initialValues);

		// Keszitunk egy Uri-t az uj beszurt elemre, es visszaterunk vele
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(EARTHQUAKEPROVIDER_CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + _uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// torles, nem akarjuk hasznalni, de kotelezo megirni
		int count;

		switch (uriMatcher.match(uri)) {
		case QUAKES:
			count = earthquakeDB.delete(EARTHQUAKE_TABLE, where, whereArgs);
			break;

		case QUAKE_ID:
			String segment = uri.getPathSegments().get(1);
			count = earthquakeDB.delete(EARTHQUAKE_TABLE, KEY_ID + "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		// nem akarjuk hasznalni, de kotelezo megirni
		int count;
		switch (uriMatcher.match(uri)) {
		case QUAKES:
			count = earthquakeDB.update(EARTHQUAKE_TABLE, values, where, whereArgs);
			break;

		case QUAKE_ID:
			String segment = uri.getPathSegments().get(1);
			count = earthquakeDB
					.update(EARTHQUAKE_TABLE, values, KEY_ID + "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		// megadjuk a szolgaltatott adat tipusat
		// mivel sajat formatum, vnd-vel kezdjuk (Vendor)
		switch (uriMatcher.match(uri)) {
			case QUAKES:
				return "vnd.android.cursor.dir/vnd.amorg.earthquake";
			case QUAKE_ID:
				return "vnd.android.cursor.item/vnd.amorg.earthquake";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	// A hatterben levo konkret adattarolast egy SQLite adatbazis vegzi
	private SQLiteDatabase earthquakeDB;

	private static final String TAG = "EarthquakeProvider";
	private static final String DATABASE_NAME = "earthquakes.db";
	private static final int DATABASE_VERSION = 1;
	private static final String EARTHQUAKE_TABLE = "earthquakes";

	// oszlopok nevei
	public static final String KEY_ID = "_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_DETAILS = "details";
	public static final String KEY_LOCATION_LAT = "latitude";
	public static final String KEY_LOCATION_LNG = "longitude";
	public static final String KEY_MAGNITUDE = "magnitude";
	public static final String KEY_LINK = "link";

	// oszlopok sorszamai
	public static final int DATE_COLUMN = 1;
	public static final int DETAILS_COLUMN = 2;
	public static final int LONGITUDE_COLUMN = 3;
	public static final int LATITUDE_COLUMN = 4;
	public static final int MAGNITUDE_COLUMN = 5;
	public static final int LINK_COLUMN = 6;

	// SQLiteOpenHelper-t hasznalunk az adatbazis megnyitasara
	private static class EarthquakeDatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_CREATE = "create table " + EARTHQUAKE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " + KEY_DATE
				+ " INTEGER, " + KEY_DETAILS + " TEXT, " + KEY_LOCATION_LAT + " FLOAT, " + KEY_LOCATION_LNG + " FLOAT, " + KEY_MAGNITUDE + " FLOAT, "
				+ KEY_LINK + " TEXT);";

		public EarthquakeDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + EARTHQUAKE_TABLE);
			onCreate(db);
		}
	}
}