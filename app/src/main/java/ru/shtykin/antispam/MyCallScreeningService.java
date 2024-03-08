package ru.shtykin.antispam;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class MyCallScreeningService extends CallScreeningService {
    private ViewGroup floatView;
    private WindowManager.LayoutParams floatWindowLayoutParams;
    private int LAYOUT_TYPE;
    private WindowManager windowManager;
    @Override
    public void onScreenCall(@NonNull Call.Details details) {
        CallResponse.Builder response = new CallResponse.Builder();
        Log.e("DEBUG1", "Call screening service triggered");
        String phone = details.getHandle().getSchemeSpecificPart();
        Log.e("DEBUG1", phone);
        respondToCall(details, response.build() );

        Log.e("DEBUG1", "message");
//        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                180, // Ширина экрана
//                180, // Высота экрана
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Говорим, что приложение будет поверх других. В поздних API > 26, данный флаг перенесен на TYPE_APPLICATION_OVERLAY
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // Необходимо для того чтобы TouchEvent'ы в пустой области передавались на другие приложения
//                PixelFormat.TRANSLUCENT); // Само окно прозрачное
//        RelativeLayout rootView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.floating_window, null);
//        manager.addView(rootView, params);


        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        floatView = (ViewGroup) inflater.inflate(R.layout.floating_window, null);

        LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        floatWindowLayoutParams = new WindowManager.LayoutParams(
                ((int)(width * 0.9f)),
                500,
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        floatWindowLayoutParams.gravity = Gravity.CENTER;
        floatWindowLayoutParams.x = 0;
        floatWindowLayoutParams.y = 0;

        windowManager.addView(floatView, floatWindowLayoutParams);

        Button btnClose = floatView.findViewById(R.id.btn_close);
        TextView phoneTextView = floatView.findViewById(R.id.text_view_phone);
        phoneTextView.setText(phone);
        phoneTextView.setTextColor(Color.RED);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(floatView);
            }
        });

    }
}
