package com.example.pc.simplepomodoro;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import alt.android.os.CountDownTimer;

public class TimerService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "your_package_name.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;
    Intent intent;

    static CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Starting timer...");

        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mpref.edit();

        intent = new Intent(COUNTDOWN_BR);

        int minutes = Integer.valueOf(mpref.getString("minutes", "")) * 60 * 1000;
        cdt = new CountDownTimer(minutes+2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                strDate = simpleDateFormat.format(calendar.getTime());
                Log.e("strDate", strDate);
                twoDatesBetweenTime();
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                stopSelf();
                cdt.cancel();
            }
        };
        cdt.start();
    }

    public void twoDatesBetweenTime() {


        try {
            date_current = simpleDateFormat.parse(strDate);
        } catch (Exception e) {

        }

        try {
            date_diff = simpleDateFormat.parse(mpref.getString("data", ""));
        } catch (Exception e) {

        }

        try {
            long diff = date_current.getTime() - date_diff.getTime();
            int int_minutes = Integer.valueOf(mpref.getString("minutes", ""));

            long int_timer = TimeUnit.MINUTES.toMillis(int_minutes);
            long long_minutes = int_timer - diff;
            long diffSeconds2 = long_minutes / 1000 % 60;
            long diffMinutes2 = long_minutes / (60 * 1000) % 60;
            long diffHours2 = long_minutes / (60*60*1000);


            if (long_minutes >= 0) {
                String str_testing = String.format("%02d:%02d", diffMinutes2, diffSeconds2);
                if(diffHours2 > 0) {
                    str_testing = String.format("%02d:%02d:%02d", diffHours2, diffMinutes2, diffSeconds2);
                }

                Log.e("TIME", str_testing);

                fn_update(str_testing);
            } else {
                mEditor.putBoolean("finish", true).commit();
                cdt.cancel();
            }
        }catch (Exception e){
            cdt.cancel();
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Finished");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void fn_update(String str_time){
        intent.putExtra("time",str_time);
        sendBroadcast(intent);
    }
}