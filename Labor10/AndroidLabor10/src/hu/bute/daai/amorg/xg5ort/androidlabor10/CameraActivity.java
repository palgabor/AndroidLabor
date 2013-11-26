package hu.bute.daai.amorg.xg5ort.androidlabor10;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CameraActivity extends Activity {

	// State
	private Camera camera;

	// UI
	private ViewGroup previewLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layoutcamera);

		Button btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
		btnTakePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// get an image from the camera
				camera.takePicture(null, null, mPicture);
			}
		});

		ToggleButton btnEffect = (ToggleButton) findViewById(R.id.btnEffect);
		btnEffect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Camera.Parameters parameters = camera.getParameters();

				if (isChecked) {
					parameters.setColorEffect(Parameters.EFFECT_NEGATIVE);
				} else {
					parameters.setColorEffect(Parameters.EFFECT_NONE);
				}

				camera.setParameters(parameters);
			}
		});
		btnEffect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Camera.Parameters parameters = camera.getParameters();
				parameters.setColorEffect(Parameters.EFFECT_NEGATIVE);
				camera.setParameters(parameters);
			}
		});

		previewLayout = (ViewGroup) findViewById(R.id.camera_preview);
	}

	@Override
	protected void onResume() {
		super.onResume();

		CameraTask task = new CameraTask();
		task.execute();
	}

	@Override
	protected void onPause() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}

		super.onPause();
	}

	private PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			String result = "";
			try {
				File tmpImage = new File(MainActivity.IMAGEPATH);
				if (tmpImage.exists())
					tmpImage.delete();
				tmpImage.createNewFile();
				FileOutputStream buf = new FileOutputStream(tmpImage);
				buf.write(data);
				buf.flush();
				buf.close();
				result = "OK";
			} catch (Exception e) {
				result = "ERROR: " + e.getMessage();
			}

			Intent resultIntent = new Intent();
			resultIntent.putExtra(MainActivity.MYCAMRESULT, result);
			setResult(MainActivity.RESULT_OK, resultIntent);
			finish();
		}
	};

	private class CameraTask extends AsyncTask<Void, Void, Camera> {

		private static final int MAX_RETRY_COUNT = 3;

		@Override
		protected Camera doInBackground(Void... params) {
			Camera result = null;
			int trialCount = 0;

			while (result == null && trialCount < MAX_RETRY_COUNT) {
				result = Camera.open(0);
				trialCount++;
			}

			return result;
		}

		@Override
		protected void onPostExecute(Camera result) {
			super.onPostExecute(result);

			if (result != null) {
				camera = result;

				// Lekerdezzuk a lehetseges preview mereteket
				List<Size> supportedPreviewSizes = camera.getParameters()
						.getSupportedPreviewSizes();

				// Kiszorjuk azokat, amik tul nagyok a rendelkezesre allo
				// merethez kepest
				int availableWidth = previewLayout.getWidth();
				int availableHeight = previewLayout.getHeight();

				Iterator<Size> iter = supportedPreviewSizes.iterator();
				while (iter.hasNext()) {
					Size current = iter.next();
					if (current.width > availableWidth
							|| current.height > availableHeight) {
						iter.remove();
					}
				}

				// A maradeknak vesszuk a maximumat
				Size selectedPreviewSize = Collections.max(
						supportedPreviewSizes, new Comparator<Camera.Size>() {
							@Override
							public int compare(Size lhs, Size rhs) {
								return (lhs.width * lhs.height) < (rhs.width * rhs.height) ? -1
										: 1;
							}
						});

				// Letrehozzuk a Preview View-t a kivalasztott meretekkel es
				// hozzaadjuk a layout-hoz
				previewLayout.removeAllViews();

				MyPreview myPreview = new MyPreview(CameraActivity.this);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						selectedPreviewSize.width, selectedPreviewSize.height);
				myPreview.setLayoutParams(lp);

				previewLayout.addView(myPreview);

				// Bellitjuk a kameranak is a kivalasztott preview felbontast
				// majd odaadjuk a Preview nezetunknek
				camera.getParameters().setPreviewSize(
						selectedPreviewSize.width, selectedPreviewSize.height);
				myPreview.setCamera(camera);
			} else {
				Toast.makeText(CameraActivity.this,
						"Nem sikerult a kamera inicializalasa!",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
} 
