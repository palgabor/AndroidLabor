package hu.bute.daai.amorg.xg5ort.androidlabor10.activity;

import hu.bute.daai.amorg.xg5ort.androidlabor10.R;
import hu.bute.daai.amorg.xg5ort.androidlabor10.network.AsyncTaskUploadImage;
import hu.bute.daai.amorg.xg5ort.androidlabor10.network.AsyncTaskUploadImage.UploadCompleteListener;
import hu.bute.daai.amorg.xg5ort.androidlabor10.view.DrawerImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends Activity implements UploadCompleteListener{
	public static final String APPTAG = "ANDLAB9";
	public static final String IMAGEPATH = 
		Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp_image.jpg";
	public static final String MYCAMRESULT = "MYCAM";
	
	private final int REQUEST_CAMERA_IMAGE = 101;
	private final int REQUEST_MY_CAMERA_IMAGE = 102;
	private DrawerImageView ivDrawer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ivDrawer = (DrawerImageView)findViewById(R.id.ivDrawer);
        
        final ImageButton imgBtnDelete = (ImageButton)findViewById(R.id.imgBtnDelete);
        imgBtnDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ivDrawer.clearDrawing();
			}
		});
        
        final ImageButton imgBtnPhoto = (ImageButton)findViewById(R.id.imgBtnPhoto);
        imgBtnPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				File imageFile = new File(IMAGEPATH);
		    	Uri imageFileUri = Uri.fromFile(imageFile);
		    	
		    	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);  
		    	cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
		    	startActivityForResult(cameraIntent, REQUEST_CAMERA_IMAGE);
			}
		});
        
        final ImageButton imgBtnOwnPhoto = (ImageButton)findViewById(R.id.imgBtnOwnPhoto);
        imgBtnOwnPhoto.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		Intent i = new Intent(getApplicationContext(), CameraActivity.class);
        		startActivityForResult(i, REQUEST_MY_CAMERA_IMAGE);
        	}
        }); 
        
        final ImageButton imgBtnUpload = (ImageButton)findViewById(R.id.imgBtnUpload);
        imgBtnUpload.setEnabled(false);
        imgBtnUpload.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		
        		ivDrawer.invalidate();
        		ivDrawer.refreshDrawableState();
        		Bitmap bm = ivDrawer.getDrawingCache();
        		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        		bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        		byte[] b = baos.toByteArray();  
        		AsyncTaskUploadImage taskUpload = new AsyncTaskUploadImage(MainActivity.this, b, MainActivity.this);
        		taskUpload.execute("http://atleast.aut.bme.hu/AndroidGallery/api.php?action=uploadImage");
        	}
        });
        
        final ImageButton imgBtnUploadWithoutDrawing = (ImageButton)findViewById(R.id.imgBtnUploadWithoutDrawing);
        imgBtnUploadWithoutDrawing.setEnabled(false);
        imgBtnUploadWithoutDrawing.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		ivDrawer.invalidate();
        		ivDrawer.refreshDrawableState();
        		Bitmap bm = ivDrawer.getDrawingCache();
        		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        		bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        		byte[] b = baos.toByteArray();  
        		AsyncTaskUploadImage taskUpload = new AsyncTaskUploadImage(MainActivity.this, b, MainActivity.this);
        		taskUpload.execute("http://atleast.aut.bme.hu/AndroidGallery/api.php?action=uploadImage");
        	}
        });
        
    }
    
	// Visszatérés a kamerától
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_CAMERA_IMAGE) {
			if (resultCode == RESULT_OK)
			{
				try {
					File imageFile = new File(IMAGEPATH);
			    	FileInputStream fis = new FileInputStream(imageFile);
					Bitmap img = BitmapFactory.decodeStream(fis);
					ivDrawer.setImageBitmap(img);
					findViewById(R.id.imgBtnUpload).setEnabled(true);
					findViewById(R.id.imgBtnUploadWithoutDrawing).setEnabled(true);
				} catch(Exception e) {}
			}
	    }
	    else if (requestCode == REQUEST_MY_CAMERA_IMAGE) {
	    	if (resultCode == RESULT_OK)
	    	{
	    		String camResult = data.getExtras().getString(MYCAMRESULT);
	    		
	    		if (camResult.startsWith("OK"))
	    		{
	    			try {
	    				File imageFile = new File(IMAGEPATH);
	    		    	FileInputStream fis = new FileInputStream(imageFile);
	    				Bitmap img = BitmapFactory.decodeStream(fis);
	    				ivDrawer.setImageDrawable(new BitmapDrawable(getResources(), img));
	    				ivDrawer.invalidate();
	    				ivDrawer.refreshDrawableState();
	    				findViewById(R.id.imgBtnUpload).setEnabled(true);
	    				findViewById(R.id.imgBtnUploadWithoutDrawing).setEnabled(true);
	    			} catch(Exception e) {}
	    		}
	    		else
	    		{
	    			Toast.makeText(this, camResult, Toast.LENGTH_LONG).show();
	    		}
	    	}
	    } 
	}
    
    
    @Override
    public void onTaskComplete(String aResult) {
    	Toast.makeText(this, aResult, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String aError) {
    	Toast.makeText(this, aError, Toast.LENGTH_LONG).show();
    } 
} 
