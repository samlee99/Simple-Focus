package com.example.pc.simplepomodoro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rey.material.widget.Slider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by PC on 5/28/2017.
 */

public class TimerActivity extends FragmentActivity {

    private static final String TAG = "TimerActivity";

    private static final int NUM_PAGES = 2;
    @BindView(R.id.pager) ViewPager mPager;
    @BindView(R.id.indicator) CircleIndicator indicator;
    @BindView(R.id.timerTextView) TextView timerTextView;
    @BindView(R.id.circularProgress) CircularProgressBar progressBar;
    @BindView(R.id.startButton) ImageButton startButton;
    @BindView(R.id.cancelButton) ImageButton cancelButton;
    PagerAdapter mPagerAdapter;

    String date_time;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    int minutes = 1;
    static MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_screen_slide);

        ButterKnife.bind(this);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        indicator.setViewPager(mPager);


        progressBar.showProgressText(false);
        timerTextView.setText(String.format("%02d:00", minutes));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TimerFragment", "pressed play button");
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                date_time = simpleDateFormat.format(calendar.getTime());

                prefEditor.putString("data", date_time).commit();
                prefEditor.putString("minutes", Integer.toString(minutes));

                startService(new Intent(TimerActivity.this, TimerService.class));
                startButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "pressed pause button");
                TimerService.cdt.pause();
                stopService(new Intent(TimerActivity.this, TimerService.class));

                timerTextView.setText(String.format("%02d:00", minutes));
                cancelButton.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
            }
        });

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefEditor = pref.edit();

        try {
            String value = pref.getString("data", "");
            if(value.matches("")) {
                timerTextView.setText("");
            } else {
                if (pref.getBoolean("finish", false)) {
                    timerTextView.setText("");
                } else {
                    timerTextView.setText(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.COUNTDOWN_BR));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, TimerService.class));
        super.onDestroy();
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            String time = intent.getStringExtra("time");
            timerTextView.setText(time);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TimerFragment fragment = new TimerFragment();
            Bundle args = new Bundle();
            args.putInt("page_position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
