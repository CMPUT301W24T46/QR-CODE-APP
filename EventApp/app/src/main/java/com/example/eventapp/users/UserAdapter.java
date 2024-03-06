package com.example.eventapp.users;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;

import java.util.ArrayList;
import java.util.Arrays;

public class UserAdapter extends ArrayAdapter<User> {

    private ArrayList<User> users ;
    private Context context ;

    public UserAdapter(Context context , ArrayList<User> users){
        super(context , 0, users);
        this.context = context ;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.content_profile_display, parent, false);
        }

        User user = users.get(position);

        if (user != null) {
            TextView profileName = view.findViewById(R.id.profileName);
            TextView profileRole = view.findViewById(R.id.profileRole);
            ImageView eventImageView = view.findViewById(R.id.profileImage);
            Button viewEventButton = view.findViewById(R.id.viewProfileButton);

            String username = user.getName();
// if username is null or empty, then display the id instead
            if (TextUtils.isEmpty(username)) {
                username = user.getId();
            }

//        Sets navigations for each list item.
            viewEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("UserName", user.getName());
                    bundle.putString("ImageURL", Arrays.toString(user.getImageData()));
                Navigation.findNavController(v).navigate(R.id.action_attendeeEvent_to_attendeeEventInformation , bundle);
                }
            });

//            Glide.with(context).load(user.getImageData()).centerCrop().into(eventImageView);

            profileName.setText(username);
            profileRole.setText(user.getRole());
            eventImageView.setImageResource(R.drawable.ic_home);
        }

        return view;
    }

    public void setFilter(ArrayList<User> userDataList){
        users.clear();
        users.addAll(userDataList) ;
    }
}
