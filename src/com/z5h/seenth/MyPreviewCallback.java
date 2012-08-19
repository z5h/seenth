package com.z5h.seenth;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.rtp.AudioStream;

public class MyPreviewCallback implements Camera.PreviewCallback {

    private int width;
    private int height;
    private final byte[] buffer;
    private byte[] yValues;

    private int debugFrame = 0;
    private int frameCount;

    AudioTrack audioTrack;
    CameraPreview cameraPreview;

    public MyPreviewCallback(Camera camera, CameraPreview cameraPreview) {
        this.cameraPreview = cameraPreview;
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        Debugger.print("preview size = " + size.width + "," + size.height);
        width = size.width;
        height = size.height;


        yValues = new byte[width*height];

        int format = parameters.getPreviewFormat();
        if (format!=ImageFormat.NV21){
            throw new IllegalArgumentException("Camera's preview mode must be NV21");
        }

        buffer = new byte[(ImageFormat.getBitsPerPixel(format)*width*height)/8];
        camera.addCallbackBuffer(buffer);

        debugNthFrame(true, 10);
    }

    void foo(){

        double freq = 4400.0 * cameraPreview.lastX;
        int sampleRateInHz = 8000;

        if (audioTrack!=null){
            audioTrack.pause();
            audioTrack.flush();
        } else {
            audioTrack =
                new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRateInHz,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    1 * sampleRateInHz,
                    AudioTrack.MODE_STREAM);
        }

        byte[] sound = new byte[8000];
        for (int i=0; i<sound.length; i++){
            sound[i] = (byte)(cameraPreview.lastY*yValues[ ((int)(width * i /(sampleRateInHz/freq))) % yValues.length]);
        }

        audioTrack.write(sound,0,sound.length);

        audioTrack.play();
    }

    public void debugNthFrame(boolean debug, int n){
        if (debug){
            debugFrame = n;
            frameCount=0;
        } else {
            debugFrame = 0;
        }
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        for (int h=0; h<height; h++){
            for (int w=0; w<width; w++){
                int index = h*width + w;
                yValues[index] = data[index];
            }
        }
        foo();
        if (debugFrame>0){
            frameCount++;
            if (frameCount==debugFrame){
                frameCount=0;
                StringBuilder sb = new StringBuilder();
                char[] colors = new char[]{'#', '%',':','.',' ',' '};

                    for (int h=0; h<height; h+=2){
                        for (int w=0; w<width; w+=2){

                        int index = h*width + w;
                        char color = colors[((yValues[index]&0xff)*5)/0xff];
                        sb.append(color);
                    }
                    Debugger.print(sb.toString());
                    sb = new StringBuilder();
                }

            }
        }
    }

}
