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
 * AttendeeCheckInAdapter is an {@link ArrayAdapter} designed for adapting {@link AttendeeCheckInView} objects into
 * views within a ListView. This adapter is utilized to display a list of attendee check-ins, each with the attendee's name,
 * profile image, frequency of check-ins, and the date of the latest check-in.
 *
 * <p>The adapter inflates a custom layout for each item in the list, ensuring that the check-in details are presented
 * in a user-friendly format. The adapter expects a list of {@link AttendeeCheckInView} objects and uses the application
 * context to correctly inflate and populate the list item views.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Inflates a custom layout for attendee check-in items.</li>
 *     <li>Populates the layout with data from {@link AttendeeCheckInView} objects, including styling parts of the text for emphasis.</li>
 *     <li>Utilizes Glide for efficient and smooth loading of attendee profile images.</li>
 *     <li>Allows dynamic updates to the list of check-ins through {@link #setFilter(ArrayList)} method.</li>
 * </ul>
 */

public class AttendeeCheckInAdapter extends ArrayAdapter<AttendeeCheckInView> {

    private ArrayList<AttendeeCheckInView> checkIns;
    private Context context;

    /**
     * Constructs a new AttendeeCheckInAdapter.
     *
     * @param context  The current context. Used to inflate the layout file.
     * @param checkIns A list of {@link AttendeeCheckInView} objects to be displayed.
     */

    public AttendeeCheckInAdapter(Context context, ArrayList<AttendeeCheckInView> checkIns) {
        super(context, 0, checkIns);
        this.context = context;
        this.checkIns = checkIns;
    }

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

    public void setFilter(ArrayList<AttendeeCheckInView> checkInDataList) {
        checkIns.clear();
        checkIns.addAll(checkInDataList);
        notifyDataSetChanged();
    }
}