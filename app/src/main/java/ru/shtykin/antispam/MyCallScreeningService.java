package ru.shtykin.antispam;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
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

import java.util.List;
import java.util.Objects;

import kotlin.text.Regex;
import ru.shtykin.antispam.data.RepositoryImpl;
import ru.shtykin.antispam.domain.Repository;

public class MyCallScreeningService extends CallScreeningService {
    private ViewGroup floatView;
    private WindowManager.LayoutParams floatWindowLayoutParams;
    private int LAYOUT_TYPE;
    private WindowManager windowManager;

    @Override
    public void onScreenCall(@NonNull Call.Details details) {
        CallResponse.Builder response = new CallResponse.Builder();
        String phone = details.getHandle().getSchemeSpecificPart();
        Log.e("DEBUG1", phone);
        respondToCall(details, response.build() );

        String output;
        try {
            output = String.format("%s (%s) %s-%s", phone.substring(0, 2), phone.substring(2, 5), phone.substring(5, 8),
                    phone.substring(8, 12));
        } catch (Exception e) {
            output = phone;
        }
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

        Button btnClose = floatView.findViewById(R.id.buttonClose);
        TextView phoneTextView = floatView.findViewById(R.id.textViewPhone);
        TextView stateTextView = floatView.findViewById(R.id.textViewState);
        phoneTextView.setText(output);
        if (isSpam(phone)) {
            phoneTextView.setTextColor(getResources().getColor(R.color.red, null));
            stateTextView.setText("Спам");
        }
        else {
            phoneTextView.setTextColor(getResources().getColor(R.color.green, null));
            stateTextView.setText("Не спам");
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                windowManager.removeView(floatView);
            }
        });

    }

    private boolean isSpam(String phone) {
        Repository repository = new RepositoryImpl();
        List<String> list = repository.getNumbers();
        for (String element: list ) {
            if (Objects.equals(element, phone)) {
                return true;
            }
        }
        return false;
    }
}
