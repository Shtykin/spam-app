package ru.shtykin.antispam;

import android.graphics.PixelFormat;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import ru.shtykin.antispam.data.RepositoryImpl;
import ru.shtykin.antispam.domain.Repository;

public class MyCallScreeningService extends CallScreeningService {

    @Override
    public void onScreenCall(@NonNull Call.Details details) {
        if(details.getCallDirection() != Call.Details.DIRECTION_INCOMING) return;
        CallResponse.Builder response = new CallResponse.Builder();
        String phone = details.getHandle().getSchemeSpecificPart();
        respondToCall(details, response.build() );

        String output;
        try {
            output = String.format("%s (%s) %s-%s", phone.substring(0, 2), phone.substring(2, 5), phone.substring(5, 8),
                    phone.substring(8, 12));
        } catch (Exception e) {
            output = phone;
        }

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup floatView = (ViewGroup) inflater.inflate(R.layout.floating_window, null);
        WindowManager.LayoutParams floatWindowLayoutParams = new WindowManager.LayoutParams(
                ((int)(width * 0.9f)),
                500,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
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
