package com.example.pc.simplepomodoro;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;

/**
 * Created by PC on 5/29/2017.
 */

public class TimerFragment extends Fragment {

    private static final String TAG = "TimerFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.content);

        int position = getArguments().getInt("page_position");
        switch(position) {
            case 0:
                scrollView.setBackgroundResource(R.drawable.oceanwater);
                break;
            case 1:
                scrollView.setBackgroundResource(R.drawable.rain);
                break;
            default:
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        return rootView;
    }
}
