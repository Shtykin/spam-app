package ru.shtykin.antispam;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Objects;

public class IncomingCallReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
//        try {
//            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//            Log.e("DEBUG1", state);
//            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                Log.e("DEBUG1", number);
//            }
//        } catch (Exception e) {
//            Log.e("DEBUG1", Objects.requireNonNull(e.getMessage()));
//        }
    }
}
