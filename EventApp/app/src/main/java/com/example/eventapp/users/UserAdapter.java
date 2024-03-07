package com.example.eventapp.users;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.eventapp.admin.AdminDeleteProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
            ImageView profileImageView = view.findViewById(R.id.profileImage);
            Button viewEventButton = view.findViewById(R.id.viewProfileButton);

            String username = user.getName();
// if username is null or empty, then display the id instead
            if (TextUtils.isEmpty(username)) {
                username = user.getId();
            }

            viewEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("UserName", user.getName());
//                    bundle.putString("ImageURL", Arrays.toString(user.getImageData()));

                    Intent intent = new Intent(context, AdminDeleteProfile.class);
                    HashMap <String, String> userData = new HashMap<>();
                    userData.put("id", user.getId());
                    userData.put("name", user.getName());
                    userData.put("contact", user.getContactInformation());
                    userData.put("homepage", user.getHomepage());
                    userData.put("imageURL", user.getImageURL());
                    userData.put("typeOfUser", user.getTypeOfUser());
                    intent.putExtra("userData", userData);
                    context.startActivity(intent);
                }
            });


            profileName.setText(username);
            profileRole.setText(user.getTypeOfUser());

            String imageURL = user.getImageURL();


            if (! TextUtils.isEmpty(imageURL)) {
                Log.d("UserAdapter", "Image URL for user " + user.getName() + ": " + imageURL);
                Glide.with(context).load(user.getImageURL()).centerCrop().into(profileImageView);

            } else {
                profileImageView.setImageResource(R.drawable.ic_home);
            }

        }

        return view;
    }

    public void setFilter(ArrayList<User> userDataList){
        users.clear();
        users.addAll(userDataList) ;
    }
}
