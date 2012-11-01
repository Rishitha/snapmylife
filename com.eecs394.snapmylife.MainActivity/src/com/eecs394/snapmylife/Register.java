package com.eecs394.snapmylife;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.StringBody;
import com.parse.signpost.http.HttpResponse;


public class Register extends Activity {
 
 EditText txtUserName;
 EditText txtPassword;
 Button btnRegister;
 
 /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_register);

	 txtUserName=(EditText)this.findViewById(R.id.registerUname);
	 txtPassword=(EditText)this.findViewById(R.id.registerPwd);
	 btnRegister=(Button)this.findViewById(R.id.btnRegister);
	 btnRegister.setOnClickListener(new OnClickListener() {


		 public void onClick(View v) {
			 String rspnse = register(txtUserName.getText().toString(), txtPassword.getText().toString()).trim();
			 if(rspnse.equalsIgnoreCase("True")){
				 Toast.makeText(Register.this, "Account Created Successfully",Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Register.this, MainActivity.class);
					startActivity(intent);
			 }else if(rspnse.equalsIgnoreCase("False")){
				 Toast.makeText(Register.this, "Username/Password already in use.",Toast.LENGTH_LONG).show();
			 }else{
				 Toast.makeText(Register.this, "Neither True nor False", Toast.LENGTH_LONG).show();
			 }
		 }
	 });       
 }



public String register(String username, String password) {
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	StrictMode.setThreadPolicy(policy); 

	// Create a new HttpClient and Post Header
	HttpClient httpclient = new DefaultHttpClient();
	HttpContext localContext = new BasicHttpContext();
	HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/registerUser.py");

	try {
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


		// Add your data
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		System.out.println("Finished with assigning name-value pairs");

		for(int index=0; index < nameValuePairs.size(); index++) {
				// Normal string data
				entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));	
		}

		//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		httppost.setEntity(entity);

		// Execute HTTP Post Request
		org.apache.http.HttpResponse response = httpclient.execute(httppost, localContext);
		//Get string from server's response
		HttpEntity httpentity = response.getEntity();
		String TF = EntityUtils.toString(httpentity);
		System.out.println("Response is:");
		System.out.println(TF);
		return TF;

	} catch (ClientProtocolException e) {
		return "ClientProtocolException";
	} catch (IOException e) {
		return "IOException";
	}

} 

}


