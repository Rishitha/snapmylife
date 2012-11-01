package com.eecs394.snapmylife;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
	String usernameZ = "";
	String passwordZ = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 try{
			 File mydir = MainActivity.this.getDir("BeardFlip_Login", Context.MODE_PRIVATE); //Creating an internal dir;
			 File userPass = new File(mydir, "userPass");
			 FileInputStream fIn = new FileInputStream(userPass);
			 InputStreamReader isr = new InputStreamReader(fIn);
			 char[] inputBuffer = new char[100];
			 isr.read(inputBuffer);
			 String readString = new String(inputBuffer);
			 String[] passArray = readString.split("/");
			 isr.close();
			 usernameZ = passArray[0];
			 passwordZ = passArray[1];
			 System.out.println(usernameZ);
			 System.out.println(passwordZ);
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		
	}

	//Gets called when user presses "Choose Picture" button. Starts the gallery activity
	public void selectPicture(View view) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
	}
	
	private static final int CAMERA_PIC_REQUEST = 1337;
	
	public void takePicture(View view) {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}

	//After user has chosen or taken a picture, this method is called. It gets the path of the chosen picture and calls postData.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				// currImageURI is the global variable I'm using to hold the content:// URI of the image
				currImageURI = data.getData();
				System.out.println("URI extracted");
				System.out.println(currImageURI);
				currImageStr = getRealPathFromURI(currImageURI);
				System.out.println("currImageStr is: ");
				System.out.println(currImageStr);
				postData();
				System.out.println("After postData in selectPicture");
			} else if(requestCode == CAMERA_PIC_REQUEST) {
				System.out.println("CameraPicRequest");
				currImageURI = data.getData();
				Bitmap currImageBitmap = (Bitmap) data.getExtras().get("data");
				System.out.println(currImageBitmap.getWidth());
				System.out.println(currImageBitmap.getHeight());
//				if(currImageBitmap.getWidth() > currImageBitmap.getHeight()) {
//					Matrix matrix = new Matrix();
//					matrix.postRotate(90);
//					currImageBitmap = Bitmap.createBitmap(currImageBitmap, 0, 0, currImageBitmap.getWidth(), currImageBitmap.getHeight(), matrix, true);
//				}
				
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

	//This gets the path from the chosen picture's URI
	private String getRealPathFromURI(Uri contentURI) {
		Cursor cursor = getContentResolver()
				.query(contentURI, null, null, null, null); 
		cursor.moveToFirst(); 
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		return cursor.getString(idx); 
	}

	//Called by "fast" button
	public void postSPFfast(View view) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/uploadFile.py");

		double slider_spf = 0.2;

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("post_type", "speed"));
			nameValuePairs.add(new BasicNameValuePair("gifSpeed", Double.toString(slider_spf)));
			nameValuePairs.add(new BasicNameValuePair("username", usernameZ));
			nameValuePairs.add(new BasicNameValuePair("password", passwordZ));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			//Get string from server's response
			HttpEntity httpentity = response.getEntity();
			rstring = EntityUtils.toString(httpentity);
			System.out.println("RESPONSE IS: ");
			System.out.println(rstring);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		Context context = getApplicationContext();
		CharSequence text = "gif set to speed: fast";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	//Called by "medium" button
	public void postSPFmed(View view) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/uploadFile.py");

		double slider_spf = 0.5;

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("post_type", "speed"));
			nameValuePairs.add(new BasicNameValuePair("gifSpeed", Double.toString(slider_spf)));
			nameValuePairs.add(new BasicNameValuePair("username", usernameZ));
			nameValuePairs.add(new BasicNameValuePair("password", passwordZ));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			//Get string from server's response
			HttpEntity httpentity = response.getEntity();
			rstring = EntityUtils.toString(httpentity);
			System.out.println("RESPONSE IS: ");
			System.out.println(rstring);
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		Context context = getApplicationContext();
		CharSequence text = "gif set to speed: medium";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	//Called by "slow" button
	public void postSPFslow(View view) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/uploadFile.py");

		double slider_spf = 1;

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("post_type", "speed"));
			nameValuePairs.add(new BasicNameValuePair("gifSpeed", Double.toString(slider_spf)));
			nameValuePairs.add(new BasicNameValuePair("username", usernameZ));
			nameValuePairs.add(new BasicNameValuePair("password", passwordZ));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			//Get string from server's response
			HttpEntity httpentity = response.getEntity();
			rstring = EntityUtils.toString(httpentity);
			System.out.println("RESPONSE IS: ");
			System.out.println(rstring);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		Context context = getApplicationContext();
		CharSequence text = "gif set to speed: slow";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	//Posts the picture using an HTTP POST to the script on the server which handles incoming pictures.
		public void clearData(View view) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy); 

			//Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/clear.py");

			try {
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
				nameValuePairs.add(new BasicNameValuePair("username", usernameZ));
				nameValuePairs.add(new BasicNameValuePair("password", passwordZ));
				nameValuePairs.add(new BasicNameValuePair("clear", "True"));
				System.out.println("Finished with assigning name-value pairs");

				for(int index=0; index < nameValuePairs.size(); index++) {
						// Normal string data
						entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));	
				}

				//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				httppost.setEntity(entity);

				//Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost, localContext); //Never used, but leaving for the time being because I don't know why it was originally here
				
				//Get string from server's response
				//HttpEntity httpentity = response.getEntity();
				//rstring = EntityUtils.toString(httpentity);
				//System.out.println("RESPONSE IS: ");
				//System.out.println(rstring);
				rstring = "*";

				Context context = getApplicationContext();
				CharSequence text = "Data Cleared!";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();

			} //catch (ClientProtocolException e) { //Only called if we actually need HttpResponse...
				// TODO Auto-generated catch block
			//} 
			catch (IOException e) {
				// TODO Auto-generated catch block
			}
		} 
	//Posts the picture using an HTTP POST to the script on the server which handles incoming pictures.
	public void postData() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/uploadFile.py");

		try {
			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			//double slider_spf;//Never used, but leaving for the time being because I don't know why it was originally here
			// get slider_spf
			//slider_spf = 0.5;

			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("post_type", "image"));
			nameValuePairs.add(new BasicNameValuePair("username", usernameZ));
			nameValuePairs.add(new BasicNameValuePair("password", passwordZ));
			nameValuePairs.add(new BasicNameValuePair("file", currImageStr));
			System.out.println("Finished with assigning name-value pairs");

			for(int index=0; index < nameValuePairs.size(); index++) {
				if(nameValuePairs.get(index).getName().equalsIgnoreCase("file")) {
					// If the key equals to "image", we use FileBody to transfer the data
					entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
				} else {
					// Normal string data
					entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
				}
			}

			//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httppost.setEntity(entity);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost, localContext);
			//Get string from server's response
			HttpEntity httpentity = response.getEntity();
			rstring = EntityUtils.toString(httpentity);
			System.out.println("RESPONSE IS: ");
			System.out.println(rstring);


			Context context = getApplicationContext();
			CharSequence text = "Upload Complete!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	} 
	
	public void launchBrowser(View view) {
		if(rstring=="*"){
			Context context = getApplicationContext();
			CharSequence text = "Please upload a picture first";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}else{
			//String baseURL = "http://ec2-50-19-152-75.compute-1.amazonaws.com/";
			//Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseURL + "PythonApp/3/gif/3.gif"));
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rstring));
			startActivity(browserIntent);
		}
	}
	
	public void launchCamera(View view) {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtra("username", usernameZ);
		intent.putExtra("password", passwordZ);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void logout(View view){
		File mydir = MainActivity.this.getDir("BeardFlip_Login", Context.MODE_PRIVATE); //Creating an internal dir;
		File userPass = new File(mydir, "userPass");
		if (userPass.delete()) {
			System.out.println("File Successfully deleted!");
			
		} else {
			  System.out.println("File not deleted");
		}
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
}
