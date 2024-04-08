package com.example.eventapp.event;
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

import com.bumptech.glide.Glide;
import com.example.eventapp.R;

import java.util.ArrayList;

/**
 * EventAdapter is a custom {@link ArrayAdapter} for {@link Event} objects, designed to display a list of events
 * in a user interface. It supports custom layouts for each event item, including event details and an image.
 * The adapter also incorporates an interface {@link OnEventClickListener} to handle clicks on individual event items.
 *
 * <p>Each event item layout includes an event name, date, image, and a button to view the event. Clicking on the
 * 'view event' button triggers a callback to the {@link OnEventClickListener} interface, allowing for custom handling
 * of event item clicks, such as navigating to an event detail screen.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Inflates a custom layout for event items within a list or grid.</li>
 *     <li>Utilizes Glide for efficient loading and display of event images.</li>
 *     <li>Provides a mechanism for handling event item clicks through the {@link OnEventClickListener} interface.</li>
 *     <li>Allows dynamic updates to the event list through the {@link #setFilter(ArrayList)} method.</li>
 * </ul>
 */

public class EventAdapter extends ArrayAdapter<Event> {

    /**
     * Interface definition for a callback to be invoked when an event item is clicked.
     */

    public interface OnEventClickListener {

        /**
         * Called when an event item is clicked.
         *
         * @param event The {@link Event} object associated with the clicked item.
         */


        void onEventClick(Event event);
    }

    private ArrayList<Event> events ;
    private Context context ;
    private OnEventClickListener listener;

    /**
     * Constructs a new EventAdapter.
     *
     * @param context The current context.
     * @param events A list of {@link Event} objects to be displayed.
     * @param listener An implementation of {@link OnEventClickListener} for handling event item clicks.
     */

    public EventAdapter(Context context , ArrayList<Event> events, OnEventClickListener listener){
        super(context , 0, events);
        this.context = context ;
        this.events = events ;
        this.listener = listener;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext()) ;
            view = inflater.inflate(R.layout.content_of_event_display_list , parent , false) ;
        }

        Event event = events.get(position) ;
        TextView eventName= view.findViewById(R.id.eventDescription);
        TextView eventDate = view.findViewById(R.id.eventDate);
        ImageView eventImageView = view.findViewById(R.id.userProfileImage) ;
        Button viewEventButton = view.findViewById(R.id.btnViewEvent) ;

        // Handle button click
        viewEventButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(events.get(position));
            }
        });

//        Sets navigations for each list item.
//        viewEventButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("EventName", event.getEventName());
//                bundle.putString("ImageURL", event.getImageURL());
//                Navigation.findNavController(v).navigate(R.id.action_attendeeEvent_to_attendeeEventInformation , bundle);
//            }
//        });

        Glide.with(context).load(event.getImageURL()).centerCrop().into(eventImageView) ;
        eventName.setText(event.getEventName());
        eventDate.setText(event.getEventDate());
        return view;
    }

    /**
     * Updates the adapter's data set and refreshes the attached view to reflect the new data.
     *
     * @param eventDataList A new list of {@link Event} objects to replace the old data set.
     */

    public void setFilter(ArrayList<Event> eventDataList){
        events.clear();
        events.addAll(eventDataList) ;
    }
}