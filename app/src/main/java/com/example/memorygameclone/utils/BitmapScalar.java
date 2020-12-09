package com.example.memorygameclone.utils;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapScalar {

    private static BitmapScalar bitmapScalar = null;
    private static final String TAG = "shit";

    private BitmapScalar() {
    }

    public static BitmapScalar getInstance() {
        if (bitmapScalar == null) {
            bitmapScalar = new BitmapScalar();
        }
        return bitmapScalar;
    }

    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor =  (width / (float) b.getWidth());
        Log.d(TAG, "scaleToFitWidth: " + factor);
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    public static Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor =  (height / (float) b.getHeight());
        Log.d(TAG, "scaleToFitHeight: " + factor);
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height,true);
    }
}
