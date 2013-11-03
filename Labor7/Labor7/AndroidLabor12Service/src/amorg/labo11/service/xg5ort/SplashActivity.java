package amorg.labo11.service.xg5ort;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
boolean showSplash = true;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // set fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        setContentView(R.layout.splash);
        
        //showing the splash screen for 1500 ms, then navigates to MainActivity
        Thread splashDelay= new Thread() {
        	@Override
        	public void run() {
        		try {
	                long millis = 0; 
	                while(showSplash && millis < 1500) { 
	                	sleep(100);
	                	millis+=100;
	                }
	                startActivity(new Intent().setClass(getApplicationContext(), MainActivity.class));	               
	        	}
        		catch(Exception e) {
        			e.printStackTrace();
        		}
                finish();
        	}
        };
        splashDelay.start();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	//keydown event closes the splash screen
    	super.onKeyUp(keyCode, event);    	
    	showSplash = false;
    	return true;
   }
    
    public boolean onTouchEvent(MotionEvent event) {
    	//screen touch event closes the splash screen    	
    	super.onTouchEvent(event);
    	showSplash = false;
    	return true;
    }
}

//D7:F2:01:39:7D:DC:E4:EF:C4:D0:7C:65:02:F6:E6:D8:A3:96:12:F8