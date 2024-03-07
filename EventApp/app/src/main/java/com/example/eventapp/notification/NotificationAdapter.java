package com.example.eventapp.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.eventapp.R;
import java.util.ArrayList;

public class NotificationAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.fragment_organizer_notification, parent, false);
        }

        Notification notification = getItem(position);
        if (notification != null) {
//            TextView title = view.findViewById(R.id.notificationTitle);
//            TextView message = view.findViewById(R.id.notificationMessage);
//            title.setText(notification.getTitle());
//            message.setText(notification.getMessage());
        }
        return view;
    }
}
