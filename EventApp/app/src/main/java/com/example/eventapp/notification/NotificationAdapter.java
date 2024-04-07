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

/**
 * An ArrayAdapter for displaying {@link Notification} objects in a ListView.
 * Each notification item layout includes a title, message, and timestamp.
 */

public class NotificationAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notifications;
    private Context context;

    /**
     * Constructs a new {@link NotificationAdapter} instance.
     *
     * @param context       The current context used to inflate layout files.
     * @param notifications The list of {@link Notification} objects to be displayed in the ListView.
     */

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */

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

    /**
     * Static inner class used to hold references to the relevant views within a list item layout.
     * This prevents unnecessary calls to findViewById, increasing performance.
     */

    private static class ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView messageTextView;
    }

    /**
     * Updates the list of notifications and refreshes the ListView.
     *
     * @param notifications The new list of {@link Notification} objects to display.
     */

    public void setNotifications(List<Notification> notifications) {
        this.notifications.clear();
        this.notifications.addAll(notifications);
        notifyDataSetChanged();
    }

}
