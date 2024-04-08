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



public class EventAdapter extends ArrayAdapter<Event> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private ArrayList<Event> events ;
    private Context context ;
    private OnEventClickListener listener;

    public EventAdapter(Context context , ArrayList<Event> events, OnEventClickListener listener){
        super(context , 0, events);
        this.context = context ;
        this.events = events ;
        this.listener = listener;
    }

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

    public void setFilter(ArrayList<Event> eventDataList){
        events.clear();
        events.addAll(eventDataList) ;
    }
}