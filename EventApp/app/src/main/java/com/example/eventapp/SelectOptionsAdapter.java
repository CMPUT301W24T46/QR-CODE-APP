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

public class SelectOptionsAdapter extends ArrayAdapter<String> {

    public SelectOptionsAdapter(Context context , String[]data){
        super(context , R.layout.cardview_account_options_content , R.id.accountOption , data);
    }

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
