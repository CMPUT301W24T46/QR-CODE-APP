package com.example.eventapp.event;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {

    private ArrayList<Event> events ;
    private Context context ;

    public EventAdapter(Context context , ArrayList<Event> events){
        super(context , 0, events);
        this.context = context ;
        this.events = events ;
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
        ImageView eventImageView = view.findViewById(R.id.eventImageList) ;

        Glide.with(context).load(event.getImageURL()).centerCrop().into(eventImageView) ;
        eventName.setText(event.getEventName());
        return view;
    }

    public void setFilter(ArrayList<Event> eventDataList){
        events.clear();
        events.addAll(eventDataList) ;
    }
}
