package com.eecs394.snapmylife;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.FileBody;
import com.parse.entity.mime.content.StringBody;

public class MainActivity extends Activity {
	
	Uri currImageURI;
	String currImageStr = "filler";
	String rstring = "*";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
       
    public void selectPicture(View view) {
    	Intent intent = new Intent();
    	intent.setType("image/*");
    	intent.setAction(Intent.ACTION_GET_CONTENT);
    	startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
	    	if (requestCode == 1) {
	    		// currImageURI is the global variable I’m using to hold the content:// URI of the image
	    		currImageURI = data.getData();
	    		System.out.println("URI extracted");
	    		System.out.println(currImageURI);
	    		currImageStr = getRealPathFromURI(currImageURI);
	    		System.out.println("currImageStr is: ");
	    		System.out.println(currImageStr);
	        	postData();
	        	System.out.println("After postData in selectPicture");
	    	}
    	}
    }
    
    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver()
                   .query(contentURI, null, null, null, null); 
        cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
        return cursor.getString(idx); 
    }
    
    
    public void postData() {
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy); 
    	
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/uploadFile.py");

        try {
        	MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        	
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("username", "user1"));
            nameValuePairs.add(new BasicNameValuePair("password", "password1"));
            nameValuePairs.add(new BasicNameValuePair("file", currImageStr));
            System.out.println("currImageStr is: ");
            System.out.println(currImageStr);
            System.out.println("Finished with assigning name-value pairs");
            
            for(int index=0; index < nameValuePairs.size(); index++) {
                if(nameValuePairs.get(index).getName().equalsIgnoreCase("file")) {
                    // If the key equals to "image", we use FileBody to transfer the data
                    entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
                    //entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
                } else {
                    // Normal string data
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                }
            }
  
            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httppost.setEntity(entity);
            
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost, localContext);
            rstring = getResponseBody(response);
            System.out.println("got http response:");
            System.out.println(rstring);
          
            
        	Context context = getApplicationContext();
        	CharSequence text = "post code complete!";
        	int duration = Toast.LENGTH_SHORT;
        	Toast toast = Toast.makeText(context, text, duration);
        	toast.show();
            
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    } 
    
    public void launchBrowser(View view){
    	if(rstring=="*"){
        	Context context = getApplicationContext();
        	CharSequence text = "Please upload a picture first";
        	int duration = Toast.LENGTH_SHORT;
        	Toast toast = Toast.makeText(context, text, duration);
        	toast.show();
    	}
    	else{
	    	String baseURL = "http://ec2-50-19-152-75.compute-1.amazonaws.com/";
	    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseURL + "PythonApp/3/gif/3.gif"));
	    	startActivity(browserIntent);
    	}
    }
    
    public static String getResponseBody(HttpResponse response){
    	String response_text = null;
    	HttpEntity entity = null;
    	try{
    		entity = response.getEntity();
    		response_text = _getResponseBody(entity);
    	} catch (ParseException e){
    		e.printStackTrace();
    	}catch(IOException e){
    		if(entity != null){
    			try {
    				entity.consumeContent();
    			}catch(IOException e1){
    			}
    		}
    	}
    	return response_text;
    }
    
    public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException {
    	if(entity == null){throw new IllegalArgumentException("HTTP entity may not be null"); }
    	InputStream instream = entity.getContent();
    	if (instream == null) {return "";}
    	if (entity.getContentLength() > Integer.MAX_VALUE) {throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");}
    	String charset = getContentCharSet(entity);
    	if(charset == null){
    		charset = HTTP.DEFAULT_CONTENT_CHARSET;
    	}
    	Reader reader = new InputStreamReader(instream, charset);
    	StringBuilder buffer = new StringBuilder();
    	try{
    		char[] tmp = new char[1024];
    		int l;
    		while((l=reader.read(tmp))!=-1){
    			buffer.append(tmp, 0, 1);
    		}
    	}finally {
    		reader.close();
    	}
    	return buffer.toString();
    }
    
    public static String getContentCharSet(final HttpEntity entity) throws ParseException {
    	if(entity==null) {throw new IllegalArgumentException("HTTP entity may not be null");}
    	String charset = null;
    	if (entity.getContentType()!=null){
    		HeaderElement values[] = entity.getContentType().getElements();
    		if(values.length>0){
    			NameValuePair param = values[0].getParameterByName("charset");
    			if(param!=null){
    				charset = param.getValue();
    			}
    		}
    	}
    	return charset;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}


