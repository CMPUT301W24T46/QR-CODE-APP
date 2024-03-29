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

/**
 * Custom ArrayAdapter for displaying items in the home page list.
 * This adapter is used to populate the ListView in the home page fragment.
 */
public class HomePageAdapter extends ArrayAdapter<String> {

    /**
     * Constructs a new HomePageAdapter.
     *
     * @param context The context.
     * @param data    The array of data to be displayed.
     */
    public HomePageAdapter(Context context , String[]data){
        super(context , R.layout.cardview_event_homepage , R.id.homeAction , data);
    }

    /**
     * Returns a View that displays the data at the specified position in the data set.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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
