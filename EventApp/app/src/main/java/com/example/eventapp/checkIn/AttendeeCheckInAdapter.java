package com.example.eventapp.checkIn;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.eventapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Custom ArrayAdapter to display check-in information for attendees in a list view.
 * Each list item shows an attendee's name, profile image, check-in frequency, and the latest check-in date.
 */

public class AttendeeCheckInAdapter extends ArrayAdapter<AttendeeCheckInView> {

    private ArrayList<AttendeeCheckInView> checkIns;
    private Context context;

    /**
     * Constructs a new AttendeeCheckInAdapter.
     *
     * @param context  The current context.
     * @param checkIns The list of AttendeeCheckInView objects to represent in the ListView.
     */

    public AttendeeCheckInAdapter(Context context, ArrayList<AttendeeCheckInView> checkIns) {
        super(context, 0, checkIns);
        this.context = context;
        this.checkIns = checkIns;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position    The position in the data set of the data item to display.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using.
     * @param parent      The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AttendeeCheckInView attendeeCheckInView = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_attendee_check_in, parent, false);
        }

        TextView attendeeName = convertView.findViewById(R.id.attendeeName);
        ImageView attendeeImage = convertView.findViewById(R.id.userProfileImage);
        TextView checkInFrequency = convertView.findViewById(R.id.checkInFrequency);
        TextView latestCheckIn = convertView.findViewById(R.id.latestCheckIn);



        // Set the data from the attendeeCheckInView object
        attendeeName.setText(attendeeCheckInView.getAttendeeName());

        // Frequency text
        String checkInCount = String.valueOf(attendeeCheckInView.getCheckInFrequency());

        String fullText = "Checked in " + checkInCount + " times";

        SpannableString spannableString = new SpannableString(fullText);

        // Find the start and end points of the check-in count within the full text
        int start = fullText.indexOf(checkInCount);
        int end = start + checkInCount.length();

        // Apply a style to make the check-in count slightly bold
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // Set the SpannableString to the TextView
        checkInFrequency.setText(spannableString);


        // Latest Check In Text
        if (attendeeCheckInView.getLatestCheckIn() != null) {
            Date date = attendeeCheckInView.getLatestCheckIn().toDate();

            // Format the Date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(date);
            latestCheckIn.setText("Last Check-in: " + formattedDate);
        } else {
            latestCheckIn.setText("Last Check-in: N/A");
        }

        // Load the profile image
        Glide.with(context).load(attendeeCheckInView.getProfileImageUrl()).into(attendeeImage);

        return convertView;
    }

    /**
     * Updates the data set used by the adapter and refreshes the ListView.
     *
     * @param checkInDataList The new set of AttendeeCheckInView data.
     */

    public void setFilter(ArrayList<AttendeeCheckInView> checkInDataList) {
        checkIns.clear();
        checkIns.addAll(checkInDataList);
        notifyDataSetChanged();
    }
}