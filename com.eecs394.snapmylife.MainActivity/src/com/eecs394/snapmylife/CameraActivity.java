package com.eecs394.snapmylife;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.FileBody;
import com.parse.entity.mime.content.StringBody;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class CameraActivity extends Activity {
	public static final int MEDIA_TYPE_IMAGE = 1;
	
    private Camera mCamera;
    private CameraPreview mPreview;
    private int cameraRotation;
    private static String currentFilename;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

        int camId = 0;
        // Create an instance of Camera, create front if it exists
        if(checkCameraFrontHardware(CameraActivity.this)) {
        	camId = 1;
        }
        mCamera = getCameraInstance(camId);
        cameraRotation = setCameraDisplayOrientation(CameraActivity.this, camId, mCamera);
        if(cameraRotation==90 || cameraRotation==270) {
        	setContentView(R.layout.camera_layout_portrait);
        } else {
        	setContentView(R.layout.camera_layout_landscape);
        }

        RelativeLayout preview = (RelativeLayout) findViewById(R.id.camera_preview);
        
        Display display = getWindowManager().getDefaultDisplay();
        //Point size = new Point();
        //display.getSize(size); //This is what you should do, but the other is supported in earlier API levels
        int width = display.getWidth();
        int height = display.getHeight();
        
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size size = getBestPreviewSize(width, height);
        //parameters.setPreviewSize(size.width, size.height); //This is how it should be, but the preview is returning 1280x720 in GSIII, and I need to force it to 4:3
        parameters.setPreviewSize((size.height*4)/3, size.height);
        mCamera.setParameters(parameters);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        
        preview.addView(mPreview);
        
        File mydir = CameraActivity.this.getDir("SnapMyLife_Camera", Context.MODE_PRIVATE); //Creating an internal dir;
        File lastPicFile = new File(mydir, "lastPicFile");
        String lastFilePath = "";
        if(lastPicFile.exists()) {
        	try {
				BufferedReader in = new BufferedReader(new FileReader(lastPicFile));
				lastFilePath = in.readLine();
			} catch (Exception e) {
				
			}
        }
        
        File imgFile = new File(lastFilePath);
        int orientation = 0;
        if(imgFile.exists()){
        	try {
				ExifInterface exif = new ExifInterface(lastFilePath);
				orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        	Bitmap myBitmapRotate;
            ImageView myImage = (ImageView) findViewById(R.id.last_image_view);
//            if(orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            	Matrix matrix=new Matrix();
//            	myImage.setScaleType(ScaleType.MATRIX);   //required
//            	matrix.preRotate(270, 0, 0);
//            	myBitmapRotate = Bitmap.createBitmap(myBitmap, 0, 0,myBitmap.getWidth(),myBitmap.getHeight(), matrix, true);
//            	
//            	//myImage.setImageMatrix(matrix);
//            } else if(orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            	Matrix matrix=new Matrix();
//            	myImage.setScaleType(ScaleType.MATRIX);   //required
//            	matrix.postRotate(180);
//            	myBitmapRotate = Bitmap.createBitmap(myBitmap, 0, 0,myBitmap.getWidth(),myBitmap.getHeight(), matrix, true);
//            	//myImage.setImageMatrix(matrix);
//            } else if(orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            	Matrix matrix=new Matrix();
//            	myImage.setScaleType(ScaleType.MATRIX);   //required
//            	matrix.postRotate(90);
//            	myBitmapRotate = Bitmap.createBitmap(myBitmap, 0, 0,myBitmap.getWidth(),myBitmap.getHeight(), matrix, true);
//            	//myImage.setImageMatrix(matrix);
//            } else {
//            	myBitmapRotate = myBitmap;
//            }
//            myImage.setImageBitmap(myBitmapRotate);
            

            myImage.setImageBitmap(myBitmap); 

            myImage.setAlpha(127);
        }
        
        
	}

	private PictureCallback mPicture = new PictureCallback() {

	    public void onPictureTaken(byte[] data, Camera camera) {
	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            return;
	        }

	        try {
	        	
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	            ExifInterface exif;
	            switch (cameraRotation) {
		            case 0: 
		                break;
		            case 90:
		            	exif = new ExifInterface(currentFilename);
			            exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_ROTATE_90);
			            exif.saveAttributes();
			            break;
		            case 180:
		            	exif = new ExifInterface(currentFilename);
			            exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_ROTATE_180);
			            exif.saveAttributes();
			            break;
		            case 270:
		            	exif = new ExifInterface(currentFilename);
			            exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_ROTATE_270);
			            exif.saveAttributes();
			            break;	            
	            }
//	            Matrix matrix = new Matrix();
	            
//	            Bitmap currImageBitmap = (Bitmap) BitmapFactory.decodeFile(currentFilename);
//				if(currImageBitmap.getWidth() > currImageBitmap.getHeight()) {
//					Matrix matrix = new Matrix();
//					matrix.postRotate(90);
//					currImageBitmap = Bitmap.createBitmap(currImageBitmap, 0, 0, currImageBitmap.getWidth(), currImageBitmap.getHeight(), matrix, true);
//				}
	            File mydir = CameraActivity.this.getDir("SnapMyLife_Camera", Context.MODE_PRIVATE); //Creating an internal dir;
	            File lastPicFile = new File(mydir, "lastPicFile");
            	FileOutputStream out = new FileOutputStream(lastPicFile); //Use the stream as usual to write into the file
				OutputStreamWriter osw = new OutputStreamWriter(out);
				osw.write(currentFilename);
				osw.flush();
				osw.close();
	        } catch (FileNotFoundException e) {
	        	
	        } catch (IOException e) {
	        	
	        }
	        mCamera.startPreview();
	    }
	};
	

    public void takePicture(View v) {
        // get an image from the camera
        mCamera.takePicture(null, null, mPicture);
    }
    
    public void showHideLastPic(View view) {
    	ImageView myImage = (ImageView) findViewById(R.id.last_image_view);
    	Button showLastButton = (Button)findViewById(R.id.showLastPicBtn);
    	if(myImage.getVisibility() == View.VISIBLE) {
    		myImage.setVisibility(View.INVISIBLE);
    		showLastButton.setText("Overlay Last Picture");
    	} else if(myImage.getVisibility() == View.INVISIBLE) {
    		myImage.setVisibility(View.VISIBLE);
    		showLastButton.setText("Hide Last Picture");
    	}
    }

	
	/** A safe way to get an instance of the Camera object. */
	private static Camera getCameraInstance(int camId) {
	    Camera c = null;
	    try {
	    	c = Camera.open(camId); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SnapMyLife");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        currentFilename = mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
        mediaFile = new File(currentFilename);

        return mediaFile;
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		mCamera.release();
	}
	
	/** Check if this device has a front facing camera */
	private static boolean checkCameraFrontHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public static int setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0: degrees = 0; break;
			case Surface.ROTATION_90: degrees = 90; break;
			case Surface.ROTATION_180: degrees = 180; break;
			case Surface.ROTATION_270: degrees = 270; break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
		return result;
	}
	
	private Camera.Size getBestPreviewSize(int width, int height) {
		Camera.Size result = null;
		Camera.Parameters p = mCamera.getParameters();
		for (Camera.Size size : p.getSupportedPreviewSizes()) {
			if ((size.width<=width && size.height<=height) || (size.height <= width && size.width <= height)) {
				if (result==null) {
					result=size;
				} else {
					int resultArea=result.width*result.height;
					int newArea=size.width*size.height;

					if (newArea>resultArea) {
						result=size;
					}
				}
			}
		}
		return result;
	}
}

