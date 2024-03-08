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

/**
 * A custom ArrayAdapter to display admin options in a ListView.
 */
public class AdminOptionsAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] options;

    /**
     * Constructor for AdminOptionsAdapter.
     *
     * @param context The current context.
     * @param options Array of options to be displayed.
     */
    public AdminOptionsAdapter(Context context, String[] options) {
        super(context, 0, options);
        this.context = context;
        this.options = options;
    }

    /**
     * Provides a view for an AdapterView at a specific position
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse if possible.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cardview_admin_option, parent, false);
        }

        String option = getItem(position);

        TextView textView = convertView.findViewById(R.id.adminOption);
        textView.setText(option);

        return convertView;
    }
}
