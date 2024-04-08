package com.example.eventapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * An adapter class that extends {@link ArrayAdapter<String>} for displaying account options
 * in a ListView or any other AdapterView.
 * <p>
 * This adapter is responsible for creating views for each item in the data set. Each item
 * in the list is represented as a string and displayed in a TextView within a custom layout
 * defined in 'cardview_account_options_content.xml'.
 */

public class SelectOptionsAdapter extends ArrayAdapter<String> {

    /**
     * Constructs a new {@link SelectOptionsAdapter} instance.
     *
     * @param context The current context.
     * @param data    The array of strings to represent in the ListView.
     */

    public SelectOptionsAdapter(Context context , String[]data){
        super(context , R.layout.cardview_account_options_content , R.id.accountOption , data);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     * <p>
     * This method gets a View that displays the data at the specified position in the data set.
     * It uses a LayoutInflater to inflate the custom layout 'cardview_account_options_content.xml'.
     * If a recycled view (convertView) is not null, it reuses it; otherwise, it inflates a new view.
     * The text for each item is set to a TextView defined in the custom layout.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View itemView = convertView ;

        if(itemView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext()) ;
            itemView = inflater.inflate(R.layout.cardview_account_options_content , parent , false) ;
        }

        TextView accountOptionText = itemView.findViewById(R.id.accountOption) ;
        accountOptionText.setText(getItem(position));

        return itemView ;
    }
}
