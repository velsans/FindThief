package com.findthief;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CameraView extends Activity implements SurfaceHolder.Callback,
        View.OnClickListener {
    private static final String TAG = "CameraTest";
    Camera mCamera;
    boolean mPreviewRunning = false;
    GMailSender sender;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.cameraview);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);

        // mSurfaceView.setOnClickListener(this);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);

        // mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(TAG, "surfaceChanged");

        // XXX stopPreview() will crash if preview is not running
        if (mPreviewRunning) {
            mCamera.stopPreview();
        }

        Camera.Parameters p = mCamera.getParameters();

        mCamera.setParameters(p);

        mCamera.startPreview();
        mPreviewRunning = true;
        mCamera.takePicture(null, null, mPictureCallback);

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");
        // mCamera.stopPreview();
        // mPreviewRunning = false;
        // mCamera.release();

        stopCamera();
    }

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    String date, time;

    public void onClick(View v) {
        mCamera.takePicture(null, mPictureCallback, mPictureCallback);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");

        int i = findFrontFacingCamera();

        if (i > 0) ;
        while (true) {
            try {
                this.mCamera = Camera.open(i);
                try {
                    this.mCamera.setPreviewDisplay(holder);
                    return;
                } catch (IOException localIOException2) {
                    stopCamera();
                    return;
                }
            } catch (RuntimeException localRuntimeException) {
                localRuntimeException.printStackTrace();
                if (this.mCamera == null) continue;
                stopCamera();
                this.mCamera = Camera.open(i);
                try {
                    this.mCamera.setPreviewDisplay(holder);
                    Log.d("HiddenEye Plus", "Camera open RE");
                    return;
                } catch (IOException localIOException1) {
                    stopCamera();
                    localIOException1.printStackTrace();
                    return;
                }

            } catch (Exception localException) {
                if (this.mCamera != null) stopCamera();
                localException.printStackTrace();
                return;
            }
        }
    }

    private void stopCamera() {
        if (this.mCamera != null) {
            /*
             * this.mCamera.stopPreview(); this.mCamera.release(); this.mCamera = null;
             */
            this.mPreviewRunning = false;
        }
    }

    private int findFrontFacingCamera() {
        int i = Camera.getNumberOfCameras();
        for (int j = 0; ; j++) {
            if (j >= i) return -1;
            Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(j, localCameraInfo);
            if (localCameraInfo.facing == 1) return j;
        }
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Void doInBackground(Void... mApi) {
            try {
                // Add subject, Body, your mail Id, and receiver mail Id.
                // SendAttachmentInEmail.SendMailWithPhoto("Theft Notification", "Some one try to access your Device!!", AdminReceiver.possibleEmail, CameraView.f.toString());
                //SendAttachmentInEmail.SendMail();
                sender.sendMail_Attachment("Theft Notification", "Some one try to access your Device!!", AdminReceiver.possibleEmail, AdminReceiver.possibleEmail, Config.fileLocation.toString());
                // sender.createEmailWithAttachment
                //Creating SendMail object
                //SendMail sm = new SendMail(getApplicationContext(), AdminReceiver.possibleEmail, "Theft Notification", "Some one try to access your Device!!");
                //Executing sendmail to send email
                //sm.execute();
            } catch (Exception ex) {
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Toast.makeText("Email send", Toast.LENGTH_SHORT).show();
        }
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            if (data != null) {
                // Intent mIntent = new Intent();
                // mIntent.putExtra("image",imageData);

                mCamera.stopPreview();
                mPreviewRunning = false;
                mCamera.release();

                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                            data.length, opts);
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth = width;
                    int newHeight = height;

                    // calculate the scale - in this case = 0.4f
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;

                    // createa matrix for the manipulation
                    Matrix matrix = new Matrix();
                    // resize the bit map
                    matrix.postScale(scaleWidth, scaleHeight);
                    // rotate the Bitmap
                    matrix.postRotate(-90);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            width, height, matrix, true);

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90,
                            bytes);

                    // you can create a new file name "test.jpg" in sdcard
                    // folder.
                    long milis = System.currentTimeMillis();
                    date = DateUtil.timeMilisToString(milis, "dd-MMM-yyyy");
                    time = DateUtil.timeMilisToString(milis, "hh:mm");
                    ActivityCompat.requestPermissions(CameraView.this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            100);
                    String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String rootPath = storagePath + "/Theft_Alert";
                    String fileName = date + time + ".jpg";
                    ///storage/emulated/0/Theft_Alert+11-Jul-201903:51.jpg
                    File root = new File(rootPath);
                    if (!root.mkdirs()) {
                        Log.i("Test", "This path is already exist: " + root.getAbsolutePath());
                    }
                    Config.fileLocation = new File(rootPath + "/" + fileName);
                    try {
                        int permissionCheck = ContextCompat.checkSelfPermission(CameraView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            if (!Config.fileLocation.createNewFile()) {
                                Log.i("Test", "This file is already exist: " + Config.fileLocation.getAbsolutePath());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  File f = new File(Environment.getExternalStorageDirectory(), "/Theft_Alert" + File.separator + date + time + ".jpg");
                    System.out.println("File F : " + Config.fileLocation);
                    // f.createNewFile();
                    // write the bytes in file
                    FileOutputStream fo = new FileOutputStream(Config.fileLocation);
                    fo.write(bytes.toByteArray());
                    // remember close de FileOutput
                    fo.close();
                    // Email With Attachment
                    sender = new GMailSender(Config.EMAIL, Config.PASSWORD);
                    new MyAsyncClass().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // StoreByteImage(mContext, imageData, 50,"ImageName");
                // setResult(FOTO_MODE, mIntent);
                setResult(585);
                finish();
            }
        }
    };
}