package ru.shtykin.antispam;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.navigation.ui.AppBarConfiguration;

import java.util.zip.Inflater;

public class FloatingWindowApp extends Service {


    private ViewGroup floatView;
    private WindowManager.LayoutParams floatWindowLayoutParams;
    private int LAYOUT_TYPE;
    private WindowManager windowManager;

    @Override
    public void onCreate() {
        super.onCreate();

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Log.e("DEBUG1", "width" + width);
        Log.e("DEBUG1", "height" + height);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        floatView = (ViewGroup) inflater.inflate(R.layout.floating_window, null);

        LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        floatWindowLayoutParams = new WindowManager.LayoutParams(
                ((int)(width * 0.55f)),
                ((int)(height * 0.55f)),
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        floatWindowLayoutParams.gravity = Gravity.CENTER;
        floatWindowLayoutParams.x = 0;
        floatWindowLayoutParams.y = 0;

        windowManager.addView(floatView, floatWindowLayoutParams);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
