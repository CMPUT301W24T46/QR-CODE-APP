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

/**
 * An adapter class that extends ArrayAdapter to manage and display a list of notification items
 * for attendees within an event application context. Each item in the list represents a single
 * notification, including details such as the title, timestamp, and content of the notification.
 * The adapter uses a custom layout for each item in the list, specified by R.layout.fragment_notification_list_item.
 */

public class AttendeeNotifAdapter extends ArrayAdapter<NotificationItem> {
    private final Context context;
    private final ArrayList<NotificationItem> notificationItems;

    /**
     * Constructs a new AttendeeNotifAdapter.
     *
     * @param context            The current context. This value cannot be null.
     * @param notificationItems  The data source for the adapter, which is a list of NotificationItem objects.
     */

    public AttendeeNotifAdapter(Context context, ArrayList<NotificationItem> notificationItems) {
        super(context, R.layout.fragment_notification_list_item, notificationItems);
        this.context = context;
        this.notificationItems = notificationItems;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position      The position of the item within the adapter's data set of the item whose view
     *                      we want.
     * @param convertView   The old view to reuse, if possible. Note: You should check that this view
     *                      is non-null and of an appropriate type before using. If it is not possible to convert
     *                      this view to display the correct data, this method can create a new view.
     * @param parent        The parent that this view will eventually be attached to. This value must never be null.
     * @return              A View corresponding to the data at the specified position.
     */

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

    /**
     * Updates the adapter's data set and refreshes the attached view, if necessary.
     *
     * @param newData The new data set to replace the old one. This value may be null to indicate no data.
     */

    public void setData(List<NotificationItem> newData) {
        clear();
        if (newData != null) {
            addAll(newData);
            notifyDataSetChanged();
        }
    }

}
