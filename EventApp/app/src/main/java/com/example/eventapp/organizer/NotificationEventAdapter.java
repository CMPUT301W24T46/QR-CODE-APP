package com.example.eventapp.organizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;
import com.example.eventapp.event.Event;

import java.util.ArrayList;

public class NotificationEventAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events;
    private Context context;

    public NotificationEventAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.content_of_notification_event_display_list, parent, false);
        }

        // Get the current event
        Event currentEvent = events.get(position);

        // Find views in the layout
        TextView eventDescriptionTextView = listItemView.findViewById(R.id.eventDescription);
        TextView eventDateTextView = listItemView.findViewById(R.id.eventDate);
        ImageView eventImageView = listItemView.findViewById(R.id.eventImageList);
        Button selectEventButton = listItemView.findViewById(R.id.btnSelectEvent);

        // Set data to views
        eventDescriptionTextView.setText(currentEvent.getEventDescription());
        eventDateTextView.setText(currentEvent.getEventDate());
        // Set image using Glide or any other image loading library
        // Glide.with(context).load(currentEvent.getImageUrl()).into(eventImageView);

        // Handle button click if needed
        selectEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                // For example, launch a new activity or perform some action
            }
        });

        return listItemView;
    }
}
