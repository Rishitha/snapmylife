package com.eecs394.snapmylife;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
																				// currImageURI is the global variable Iím using to hold the content:// URI of the image
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

				public void postSPFfast(View view) {
								// Create a new HttpClient and Post Header
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/uploadFile.py");

								double slider_spf = 0.5;

								try {
												// Add your data
												List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
												nameValuePairs.add(new BasicNameValuePair("fps", Double.toString(slider_spf)));
												nameValuePairs.add(new BasicNameValuePair("username", "user1"));
												nameValuePairs.add(new BasicNameValuePair("password", "password1"));
												httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

												// Execute HTTP Post Request
												HttpResponse response = httpclient.execute(httppost);

								} catch (ClientProtocolException e) {
												// TODO Auto-generated catch block
								} catch (IOException e) {
												// TODO Auto-generated catch block
								}
				}

				public void postSPFmed(View view) {
								// Create a new HttpClient and Post Header
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/uploadFile.py");

								double slider_spf = 0.5;

								try {
												// Add your data
												List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
												nameValuePairs.add(new BasicNameValuePair("fps", Double.toString(slider_spf)));
												nameValuePairs.add(new BasicNameValuePair("username", "user1"));
												nameValuePairs.add(new BasicNameValuePair("password", "password1"));
												httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

												// Execute HTTP Post Request
												HttpResponse response = httpclient.execute(httppost);

								} catch (ClientProtocolException e) {
												// TODO Auto-generated catch block
								} catch (IOException e) {
												// TODO Auto-generated catch block
								}
				}

				public void postSPFslow(View view) {
								// Create a new HttpClient and Post Header
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/uploadFile.py");

								double slider_spf = 1;

								try {
												// Add your data
												List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
												nameValuePairs.add(new BasicNameValuePair("fps", Double.toString(slider_spf)));
												nameValuePairs.add(new BasicNameValuePair("username", "user1"));
												nameValuePairs.add(new BasicNameValuePair("password", "password1"));
												httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

												// Execute HTTP Post Request
												HttpResponse response = httpclient.execute(httppost);

								} catch (ClientProtocolException e) {
												// TODO Auto-generated catch block
								} catch (IOException e) {
												// TODO Auto-generated catch block
								}
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

												double slider_spf;
												// get slider_spf
												slider_spf = 0.5;

												// Add your data
												List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
												nameValuePairs.add(new BasicNameValuePair("post_type", "image"));
												nameValuePairs.add(new BasicNameValuePair("username", "user1"));
												nameValuePairs.add(new BasicNameValuePair("password", "password1"));
												nameValuePairs.add(new BasicNameValuePair("file", currImageStr));
												System.out.println("Finished with assigning name-value pairs");

												for(int index=0; index < nameValuePairs.size(); index++) {
																if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
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
												System.out.println("got http response:");
												System.out.println(response);


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

				@Override
								public boolean onCreateOptionsMenu(Menu menu) {
												getMenuInflater().inflate(R.menu.activity_main, menu);
												return true;
								}
}
