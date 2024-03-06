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
        super(context, 0, options);
        this.context = context;
        this.options = options;
    }

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
