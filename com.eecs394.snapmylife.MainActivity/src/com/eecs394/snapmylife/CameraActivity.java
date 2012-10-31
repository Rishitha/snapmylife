package com.eecs394.snapmylife;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
        int rotation = setCameraDisplayOrientation(CameraActivity.this, camId, mCamera);
        if(rotation==90 || rotation==270) {
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

        Button captureButton = (Button)findViewById(R.id.button_capture);
        captureButton.setText("W: " + (size.height*4)/3 + " H: " + size.height);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        
        preview.addView(mPreview);
        
        File imgFile = new File(Environment.getExternalStorageDirectory().getPath(), "DCIM/Camera/20121030_172948.jpg");
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.last_image_view);
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
//	            ExifInterface exif = new ExifInterface(currentFilename);
//	            exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""+ExifInterface.ORIENTATION_ROTATE_270);
//	            exif.saveAttributes();
//	            Matrix matrix = new Matrix();
	            
	            Bitmap currImageBitmap = (Bitmap) BitmapFactory.decodeFile(currentFilename);
				if(currImageBitmap.getWidth() > currImageBitmap.getHeight()) {
					Matrix matrix = new Matrix();
					matrix.postRotate(90);
					currImageBitmap = Bitmap.createBitmap(currImageBitmap, 0, 0, currImageBitmap.getWidth(), currImageBitmap.getHeight(), matrix, true);
				}
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

