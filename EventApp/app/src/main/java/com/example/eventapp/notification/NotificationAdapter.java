package com.example.eventapp.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.eventapp.R;
import java.util.ArrayList;
import java.util.List;

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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_notification_list_item, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = convertView.findViewById(R.id.notificationTitle);
            holder.dateTextView = convertView.findViewById(R.id.notificationDate);
            holder.messageTextView = convertView.findViewById(R.id.notificationContent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Notification notification = getItem(position);
        if (notification != null) {
            holder.titleTextView.setText(notification.getTitle());
            holder.dateTextView.setText(notification.getTimestamp());
            holder.messageTextView.setText(notification.getMessage());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView messageTextView;
    }


    public void setNotifications(List<Notification> notifications) {
        this.notifications.clear();
        this.notifications.addAll(notifications);
        notifyDataSetChanged();
    }

}
