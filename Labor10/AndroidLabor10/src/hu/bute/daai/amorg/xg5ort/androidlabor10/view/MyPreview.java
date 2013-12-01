package hu.bute.daai.amorg.xg5ort.androidlabor10.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyPreview extends SurfaceView implements SurfaceHolder.Callback {

	// Log tag
	public static final String TAG = MyPreview.class.getSimpleName();

	// State
	private SurfaceHolder mHolder;
	private Camera mCamera;

	public MyPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyPreview(Context context) {
		super(context);
		init();
	}

	private void init() {
		mHolder = getHolder();
		mHolder.addCallback(this);
	}

	public void setCamera(Camera camera) {
		mCamera = camera;

		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Failed to start camera preview!");
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// Not used
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Not used
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		if (mHolder.getSurface() == null || mCamera == null) {
			return;
		}

		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Tried to stop a non-existing camera preview!");
		}

		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Failed to restart camera preview!");
		}
	}
} 