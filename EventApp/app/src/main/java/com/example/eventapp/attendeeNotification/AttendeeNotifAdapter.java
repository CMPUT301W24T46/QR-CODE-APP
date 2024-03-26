package com.example.eventapp.attendeeNotification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.eventapp.R;

import java.util.ArrayList;

public class AttendeeNotifAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String>  allNotifications;

    public AttendeeNotifAdapter(Context context, ArrayList<String> allNotifications) {
        super(context, R.layout.notification_list_item, allNotifications);
        this.context = context;
        this.allNotifications = allNotifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notification_list_item, parent, false);

        // Get references to views in the custom layout
        TextView notificationView = rowView.findViewById(R.id.notificationInfo);

        // Bind data to views
        notificationView.setText(allNotifications.get(position));

        return rowView;
    }
}
