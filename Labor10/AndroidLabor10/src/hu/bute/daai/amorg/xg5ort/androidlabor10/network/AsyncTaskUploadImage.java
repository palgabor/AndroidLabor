package hu.bute.daai.amorg.xg5ort.androidlabor10.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class AsyncTaskUploadImage extends AsyncTask<String, Void, String> {

	public interface UploadCompleteListener {
		   public void onTaskComplete(String aResult);
		   public void onError(String aError);
	}
	
	private Context context = null;
	private ProgressDialog progressDialog = null;
	private byte[] imageToUpload;
	private UploadCompleteListener listener;
	private String error = null;

	public AsyncTaskUploadImage(Context context, byte[] aImageToUpload, UploadCompleteListener aListener) {
	    this.context = context; 
	    imageToUpload = aImageToUpload;
	    listener = aListener;
	}
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(this.context);
	    progressDialog.setMessage("Kérem várjon...");
	    progressDialog.show();
	}
	
	@Override
	protected String doInBackground(String... params) {
		String result = "";
	    HttpClient httpclient = new DefaultHttpClient();
	    InputStream is = null;
	    HttpPost httpPost = new HttpPost(params[0]); 
	    MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	    ByteArrayBody bab = new ByteArrayBody(imageToUpload, "tmp.jpg");
        reqEntity.addPart("img", bab);
        httpPost.setEntity(reqEntity);
	    HttpResponse response;
	    try {
	        response = httpclient.execute(httpPost);
	        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	        {
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {
		            
		        	BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
		        	StringBuilder sb = new StringBuilder();
		        	String line;
		        	while ((line = reader.readLine()) != null) {
		        		sb.append(line);
		        	}

		        	result = sb.toString();
		        }
		        else
		        	error = "HttpEntity is empty";
	        }
	    } catch (Exception e) {
	    	error = "Error: "+e.getMessage();
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
	    return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
	    progressDialog.dismiss();
	    if (error != null) {
	    	listener.onError(error);
	    }
	    else {
	    	listener.onTaskComplete(result);
	    }
	}
} 
