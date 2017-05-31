package com.example.pc.simplepomodoro;

import android.app.Dialog;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.rey.material.widget.CheckBox;
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
    @BindView(R.id.checkbox) CheckBox checkBox;
    @BindView(R.id.editButton) ImageButton editButton;
    PagerAdapter mPagerAdapter;

    String date_time;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    int minutes = 25;
    int paused;
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

        mediaPlayer = MediaPlayer.create(this, R.raw.rain);
        mediaPlayer.setLooping(true);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()) {
                    paused = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.seekTo(paused);
                    mediaPlayer.start();
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TimerFragment", "pressed play button");
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                date_time = simpleDateFormat.format(calendar.getTime());

                prefEditor.putString("data", date_time).commit();
                prefEditor.putString("minutes", Integer.toString(minutes)).commit();
                if(!checkBox.isChecked()) mediaPlayer.start();

                startService(new Intent(TimerActivity.this, TimerService.class));
                startButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "pressed pause button");
                TimerService.cdt.cancel();
                stopService(new Intent(TimerActivity.this, TimerService.class));

                timerTextView.setText(String.format("%02d:00", minutes));
                cancelButton.setVisibility(View.GONE);
                startButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
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
                    timerTextView.setText(String.format("%02d:00", minutes));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show()
    {
        final Dialog d = new Dialog(this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.numberpicker_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(60);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            }
        });
        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                minutes = np.getValue();
                timerTextView.setText(String.format("%02d:00", minutes));
                d.dismiss();
            }
        });
        d.show();
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
