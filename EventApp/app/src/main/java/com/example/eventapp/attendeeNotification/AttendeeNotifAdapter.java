package com.example.eventapp.attendeeNotification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.eventapp.R;

import java.util.ArrayList;
import java.util.List;

public class AttendeeNotifAdapter extends ArrayAdapter<NotificationItem> {
    private final Context context;
    private final ArrayList<NotificationItem> notificationItems;

    public AttendeeNotifAdapter(Context context, ArrayList<NotificationItem> notificationItems) {
        super(context, R.layout.fragment_notification_list_item, notificationItems);
        this.context = context;
        this.notificationItems = notificationItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rowView = inflater.inflate(R.layout.fragment_notification_list_item, parent, false);

        TextView notificationTitle = rowView.findViewById(R.id.notificationTitle);
        TextView notificationDate = rowView.findViewById(R.id.notificationDate);
        TextView notificationContent = rowView.findViewById(R.id.notificationContent);

        NotificationItem notificationItem = notificationItems.get(position);
        notificationTitle.setText(notificationItem.getTitle());
        notificationDate.setText(notificationItem.getTimestamp());
        notificationContent.setText(notificationItem.getContent());

        return rowView;
    }
    public void setData(List<NotificationItem> newData) {
        clear();
        if (newData != null) {
            addAll(newData);
            notifyDataSetChanged();
        }
    }

}
