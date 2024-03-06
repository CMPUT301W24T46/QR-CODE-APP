package com.example.eventapp.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;

public class AdminOptionsAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] options;

    public AdminOptionsAdapter(Context context, String[] options) {
        // Since you're customizing the view, pass in 0 for the resource ID and your options array.
        super(context, 0, options);
        this.context = context;
        this.options = options;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cardview_admin_option, parent, false);
        }

        // Get the data item for this position
        String option = getItem(position);

        // Lookup view for data population
        TextView textView = convertView.findViewById(R.id.adminOption);
        // Populate the data into the template view using the data object
        textView.setText(option);

        // Return the completed view to render on screen
        return convertView;
    }
}
