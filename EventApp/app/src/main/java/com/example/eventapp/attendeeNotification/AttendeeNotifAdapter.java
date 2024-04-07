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
 * AttendeeNotifAdapter is an ArrayAdapter specifically designed to adapt {@link NotificationItem} objects into views
 * for a ListView. This adapter is utilized to display a list of notifications, each with a title, timestamp, and content,
 * within the app's notification UI.
 *
 * <p>It inflates a custom layout for each item in the list, ensuring that the notification details are presented in a
 * user-friendly format. The adapter expects a list of {@link NotificationItem} objects and contextual information to
 * correctly inflate and populate the list item views.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Inflates a custom layout for notification items.</li>
 *     <li>Populates the layout with data from {@link NotificationItem} objects.</li>
 *     <li>Allows dynamic updates to the list of notifications through {@link #setData(List)} method.</li>
 * </ul>
 */

public class AttendeeNotifAdapter extends ArrayAdapter<NotificationItem> {
    private final Context context;
    private final ArrayList<NotificationItem> notificationItems;

    /**
     * Constructs a new AttendeeNotifAdapter.
     *
     * @param context            The current context. Used to inflate the layout file.
     * @param notificationItems  A list of {@link NotificationItem} objects to be displayed.
     */

    public AttendeeNotifAdapter(Context context, ArrayList<NotificationItem> notificationItems) {
        super(context, R.layout.fragment_notification_list_item, notificationItems);
        this.context = context;
        this.notificationItems = notificationItems;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position      The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView   The old view to reuse, if possible. Note: You should check that this view is non-null
     *                      and of an appropriate type before using. If it is not possible to convert this view to
     *                      display the correct data, this method can create a new view.
     * @param parent        The parent that this view will eventually be attached to.
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
     * Updates the adapter's data set and refreshes the attached view to reflect the new data.
     * This method can be used to dynamically update the list of notifications displayed by the adapter.
     *
     * @param newData A new list of {@link NotificationItem} objects to replace the old data set.
     */

    public void setData(List<NotificationItem> newData) {
        clear();
        if (newData != null) {
            addAll(newData);
            notifyDataSetChanged();
        }
    }

}
