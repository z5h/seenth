package com.z5h.seenth;

import android.hardware.Camera;
import android.util.Log;

public class MyPreviewCallback implements Camera.PreviewCallback {
    long lastLog = 0;

    public void onPreviewFrame(byte[] data, Camera camera) {


        Debugger.print("#######################");
        camera.getParameters().getPreviewSize();
        Debugger.print("1");
        if (data != null) {

            Debugger.print("2");
            // Preprocessing
            Camera.Parameters mParameters = camera.getParameters();
            Camera.Size mSize = mParameters.getPreviewSize();
            int mWidth = mSize.width;
            int mHeight = mSize.height;
            int[] rgba = new int[mWidth * mHeight];

            // Decode Yuv data to integer array
            decodeYUV420SP(rgba, data, mWidth, mHeight);
            Debugger.print("3");

            int r, g, b, y, u, v;

            if (lastLog == 0) {
                lastLog = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - lastLog > 5000) {

                StringBuilder sb = new StringBuilder(mWidth*mHeight);
                for (int j = 0; j < mHeight; j++) {
                    for (int i = 0; i < mWidth; i++) {

                        int index = mWidth * j + i;

                        r = (rgba[index] & 0xff000000) >> 24;
                        g = (rgba[index] & 0xff0000) >> 16;
                        b = (rgba[index] & 0xff00) >> 8;
                        sb.append((char)b);

                    }
                    sb.append('\n');
                }
                Debugger.print(sb.toString());
                lastLog = System.currentTimeMillis();
            }

            Debugger.print("4");
            // Converting int mIntArray to Bitmap and
            // than image preprocessing
            // and back to mIntArray.

            // Encode intArray to Yuv data
            try {
//                encodeYUV420SP(data, rgba, mWidth, mHeight);
            } catch (Exception e) {
                Debugger.print("AAAA", e);
            }
        }

    }

    static public void decodeYUV420SP(int[] rgba, byte[] yuv420sp, int width,
                                      int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int)yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                rgba[yp] = ((r << 14) & 0xff000000) | ((g << 6) & 0xff0000)
                        | ((b >> 2) | 0xff00);
            }
        }
    }


    static public void encodeYUV420SP(byte[] yuv420sp, int[] rgba,
                                      int width, int height) {
        final int frameSize = width * height;

        int[] U, V;
        U = new int[frameSize];
        V = new int[frameSize];

        int r, g, b, y, u, v;
        for (int j = 0; j < height; j++) {
            int index = width * j;
            for (int i = 0; i < width; i++) {
                r = (rgba[index] & 0xff000000) >> 24;
                g = (rgba[index] & 0xff0000) >> 16;
                b = (rgba[index] & 0xff00) >> 8;

                // rgb to yuv
                y = (66 * r + 129 * g + 25 * b + 128) >> 8 + 16;
                u = (-38 * r - 74 * g + 112 * b + 128) >> 8 + 128;
                v = (112 * r - 94 * g - 18 * b + 128) >> 8 + 128;

                // clip y
                yuv420sp[index++] = (byte)((y < 0) ? 0 : ((y > 255) ? 255 : y));
                U[index] = u;
                V[index++] = v;
            }
        }

    }
}
