package hu.bute.daai.amorg.xg5ort.androidlabor8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpManager {
	
	public interface HttpManagerListener {
		public void responseArrived(String aResponse);
		public void errorOccured(String aError);
	}
	
	public static final int EMESSAGE_LEFT = 1;
	public static final int EMESSAGE_RIGHT = 2;
	public static final int EMESSAGE_UP = 3;
	public static final int EMESSAGE_DOWN = 4;
	
	private HttpManagerListener listener;
	
	public HttpManager(HttpManagerListener aListener)
	{
		listener = aListener;
	}
	
	public void execute(String url)
	{
	    HttpClient httpclient = new DefaultHttpClient();
	    InputStream is = null;
	    HttpGet httpget = new HttpGet(url); 
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpget);
	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	        {
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {
		            is = entity.getContent();
		            ByteArrayOutputStream bos = new ByteArrayOutputStream();
					int inChar;
					while ((inChar = is.read()) != -1)
					{
						bos.write(inChar);
					}
					
					listener.responseArrived(bos.toString());
		        }
		        else
		        	listener.errorOccured("HttpEntity is empty");
	        }
	    } catch (Exception e) {
	    	listener.errorOccured(e.getMessage());
	    } finally {
	    	if (is != null)
	    	{
	    		try {
	    			is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
}