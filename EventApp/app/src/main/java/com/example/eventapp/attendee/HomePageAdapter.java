package com.example.eventapp.attendee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;

public class HomePageAdapter extends ArrayAdapter<String> {

    public HomePageAdapter(Context context , String[]data){
        super(context , R.layout.cardview_event_homepage , R.id.homeAction , data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View itemView = convertView ;

        if(itemView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext()) ;
            itemView = inflater.inflate(R.layout.cardview_event_homepage , parent , false) ;
        }

        TextView accountOptionText = itemView.findViewById(R.id.homeAction) ;
        accountOptionText.setText(getItem(position));

        return itemView ;
    }
}
