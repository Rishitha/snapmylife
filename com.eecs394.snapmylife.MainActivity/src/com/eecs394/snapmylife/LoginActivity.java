package com.eecs394.snapmylife;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import android.content.Context;
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


public class LoginActivity extends Activity {
 
 EditText txtUserName;
 EditText txtPassword;
 Button btnLogin;
 Button btnCancel;
 
 /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 setContentView(R.layout.activity_login);
	 
	 File mydir = LoginActivity.this.getDir("BeardFlip_Login", Context.MODE_PRIVATE); //Creating an internal dir;
	 File userPass = new File(mydir, "userPass");
	 //userPass = LoginActivity.this.getFileStreamPath("userPass");
	 if(userPass.exists()){
		 Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
	 }else{ 
		 
	 txtUserName=(EditText)this.findViewById(R.id.txtUname);
	 txtPassword=(EditText)this.findViewById(R.id.txtPwd);
	 btnLogin=(Button)this.findViewById(R.id.btnLogin);
	 btnLogin=(Button)this.findViewById(R.id.btnLogin);
	 btnLogin.setOnClickListener(new OnClickListener() {

		 public void onClick(View v) {

			 String rspnse = login(txtUserName.getText().toString(), txtPassword.getText().toString()).trim();
			 
			 if(rspnse.equalsIgnoreCase("True")){
				Toast.makeText(LoginActivity.this, "Login Successful",Toast.LENGTH_LONG).show();			
				try{
					File mydir = LoginActivity.this.getDir("BeardFlip_Login", Context.MODE_PRIVATE); //Creating an internal dir;
					File userPass = new File(mydir, "userPass"); //Getting a file within the dir.
					FileOutputStream out = new FileOutputStream(userPass); //Use the stream as usual to write into the file
					OutputStreamWriter osw = new OutputStreamWriter(out);
					osw.write(txtUserName.getText().toString());
					osw.write("/");
					osw.write(txtPassword.getText().toString());
					osw.write("/");
					osw.flush();
					osw.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
			 }else if(rspnse.equalsIgnoreCase("False")){
				 Toast.makeText(LoginActivity.this, "Invalid Login",Toast.LENGTH_LONG).show();
			 }else{
				 Toast.makeText(LoginActivity.this, "Neither True nor False", Toast.LENGTH_LONG).show();
			 }
		 }
		 
	 });   
	 }
 }
	 



public String login(String username, String password) {
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	StrictMode.setThreadPolicy(policy); 

	// Create a new HttpClient and Post Header
	HttpClient httpclient = new DefaultHttpClient();
	HttpContext localContext = new BasicHttpContext();
	HttpPost httppost = new HttpPost("http://ec2-50-19-152-75.compute-1.amazonaws.com/PythonApp/login.py");

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
		HttpResponse response = httpclient.execute(httppost, localContext);
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

public void registerMe(View view){
	Intent intent = new Intent(LoginActivity.this, Register.class);
	startActivity(intent);
}

}


