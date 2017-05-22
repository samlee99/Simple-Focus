package com.example.pc.simplepomodoro;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by PC on 5/22/2017.
 */

public class TasksListViewAdapter extends ArrayAdapter<Tasks> {

    private Context context;
    private List<Tasks> tasks;
    private int resource;

    public TasksListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Tasks> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.tasks = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(resource, parent, false);

        //Set up what goes in cells here like text views and set their texts etc.

        return view;
    }
}
