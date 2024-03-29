package com.z5h.seenth;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public double lastX = 0.0;
    public double lastY = 0.0;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0

//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        lastX = event.getX()/getWidth();
        lastY = event.getY()/getHeight();

        return super.onTouchEvent(event);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();





            MyPreviewCallback previewCallback = new MyPreviewCallback(mCamera, this);
            mCamera.setPreviewCallback(previewCallback);



        } catch (IOException e) {
            Log.d("surfaceCreated", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {


            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

            MyPreviewCallback previewCallback = new MyPreviewCallback(mCamera, this);
            mCamera.setPreviewCallback(previewCallback);



        } catch (Exception e){
            Log.d("surfaceChanged", "Error starting camera preview: " + e.getMessage());
        }
    }
}
