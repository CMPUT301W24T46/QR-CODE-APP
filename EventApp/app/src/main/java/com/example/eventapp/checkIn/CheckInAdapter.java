package com.example.eventapp.checkIn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CheckInAdapter extends ArrayAdapter<CheckIn> {

    private ArrayList<CheckIn> checkIns;
    private Context context;

    public CheckInAdapter(Context context, ArrayList<CheckIn> checkIns) {
        super(context, 0, checkIns);
        this.context = context;
        this.checkIns = checkIns;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        // TODO
//        if (view == null) {
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            view = inflater.inflate(R.layout.checkin_item, parent, false);
//        }
//
//        CheckIn checkIn = checkIns.get(position);
//
//        TextView attendeeIdView = view.findViewById(R.id.attendeeId);
//        TextView checkInLocationView = view.findViewById(R.id.checkInLocation);
//        TextView checkInTimeView = view.findViewById(R.id.checkInTime);
//
//        GeoPoint location = checkIn.getCheckInLocation();
//        Timestamp time = checkIn.getCheckInTime();
//
//        // Format and set the location and time
//        String locationText = String.format(Locale.getDefault(), "Lat: %.3f, Lng: %.3f", location.getLatitude(), location.getLongitude());
//        String timeText = dateFormat.format(time.toDate());
//
//        attendeeIdView.setText(checkIn.getAttendeeId());
//        checkInLocationView.setText(locationText);
//        checkInTimeView.setText(timeText);

        return view;
    }

    public void setFilter(ArrayList<CheckIn> checkInDataList) {
        checkIns.clear();
        checkIns.addAll(checkInDataList);
        notifyDataSetChanged();
    }
}
