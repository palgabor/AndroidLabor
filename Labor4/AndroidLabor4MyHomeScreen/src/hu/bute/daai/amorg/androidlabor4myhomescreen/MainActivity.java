package hu.bute.daai.amorg.androidlabor4myhomescreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static ArrayList<ApplicationInfo> mApplications;
	private static GridView mGrid;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		loadApplications();
		bindApplications();
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void loadApplications(){
		// PackageManager referencia
		PackageManager manager = getPackageManager();
		
		// listat keszitunk az osszes megjelenitendo alkalmazasrol
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
		// rendezzuk nev szerint
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
		// feltoltjuk az ApplicationInfo tombot minden megjelenitendo alkalamzashoz
		if (apps != null) {
			final int count = apps.size();
		 
			if (mApplications == null) {
				mApplications = new ArrayList<ApplicationInfo>(count);
			}
			mApplications.clear();
		 
			for (int i = 0; i < count; i++) {
				ApplicationInfo application = new ApplicationInfo();
				ResolveInfo info = apps.get(i);
				// alkalmazas neve
				application.title = info.loadLabel(manager);
				// ahhoz hogy kattintasra el tudjuk inditani, kelleni fog egy megfelelo Activity-re mutato Intent
				application.setActivity(new ComponentName(
						info.activityInfo.applicationInfo.packageName,
						info.activityInfo.name),
						Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				// ikon
				application.icon = info.activityInfo.loadIcon(manager);
		 
				mApplications.add(application);
			}
		}
	} 
	
	/**
	* ApplicationsAdapter letrehozasa es feltoltese,
	* kattintas esemenykezelo bekotese
	*/
	private void bindApplications() {
		if (mGrid == null) {
			mGrid = (GridView) findViewById(R.id.all_apps);
		}
		mGrid.setAdapter(new ApplicationsAdapter(this, mApplications));
		mGrid.setSelection(0);
		mGrid.setOnItemClickListener(new ApplicationLauncher());
	}

	/**
	* Egyedi grid adatper az ApplicationInfo tombhoz
	*/
	private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
		private Rect mOldBounds = new Rect();
	 
		public ApplicationsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
			super(context, 0, apps);
		}
	 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ApplicationInfo info = mApplications.get(position);
			// ez egy trukk a meglevo convertView objektum ujrahasznositasahoz
			// sokat javit a sebessegen, erdemes hasznalni!
			if (convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.activity_main, parent, false);
			}
	 
			Drawable icon = info.icon;
	 
			if (!info.filtered) {
				final Resources resources = getContext().getResources();
				int width = (int) resources.getDimension(android.R.dimen.app_icon_size);
				int height = (int) resources.getDimension(android.R.dimen.app_icon_size);
	 
				final int iconWidth = icon.getIntrinsicWidth();
				final int iconHeight = icon.getIntrinsicHeight();
	 
				if (icon instanceof PaintDrawable) {
					PaintDrawable painter = (PaintDrawable) icon;
					painter.setIntrinsicWidth(width);
					painter.setIntrinsicHeight(height);
				}
	 
				if (width > 0 && height > 0 && (width < iconWidth || height < iconHeight)) {
					final float ratio = (float) iconWidth / iconHeight;
	 
					if (iconWidth > iconHeight) {
						height = (int) (width / ratio);
					} else if (iconHeight > iconWidth) {
						width = (int) (height * ratio);
					}
	 
					final Bitmap.Config c =
							icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
					final Bitmap thumb = Bitmap.createBitmap(width, height, c);
					final Canvas canvas = new Canvas(thumb);
					canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, 0));
					// Copy the old bounds to restore them later
					// If we were to do oldBounds = icon.getBounds(),
					// the call to setBounds() that follows would
					// change the same instance and we would lose the
					// old bounds
					mOldBounds.set(icon.getBounds());
					icon.setBounds(0, 0, width, height);
					icon.draw(canvas);
					icon.setBounds(mOldBounds);
					icon = info.icon = new BitmapDrawable(thumb);
					info.filtered = true;
				}
			}
	 
			//final TextView textView = (TextView) convertView.findViewById(R.id.action_settings);
			//textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
			//textView.setText(info.title);
	 
			return convertView;
		}
	}
	
	/**
	* Segedosztaly ami inditja a kattintott alkalmazast
	*/
	private class ApplicationLauncher implements AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView parent, View v, int position, long id) {
			ApplicationInfo app = (ApplicationInfo) parent.getItemAtPosition(position);
			startActivity(app.intent);
		}
	}
}
