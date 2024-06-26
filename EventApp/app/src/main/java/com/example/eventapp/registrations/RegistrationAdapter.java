package com.example.eventapp.registrations;

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
import com.example.eventapp.checkIn.AttendeeCheckInView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * An adapter for displaying a list of registration entries in a ListView. Each registration entry is represented by
 * {@link Registration} and includes details such as the attendee's ID, the registration date, and an attendee image.
 */

public class RegistrationAdapter extends ArrayAdapter<Registration> {

    private ArrayList<Registration> registrations;
    private Context context;

    /**
     * Constructs a new {@link RegistrationAdapter}.
     *
     * @param context The current context.
     * @param registrations The list of registration entries to display.
     */

    public RegistrationAdapter(Context context, ArrayList<Registration> registrations) {
        super(context, 0, registrations);
        this.context = context;
        this.registrations = registrations;
    }

    /**
     * Provides a holder for the views for each registration entry.
     */

    private static class ViewHolder {
        TextView attendeeName;
        TextView registrationDate;
        ImageView attendeeImage;
    }

    /**
     * Gets a view that displays the data at the specified position in the data set.
     *
     * @param position The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Registration registration = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_event_registration, parent, false);
            holder = new ViewHolder();
            holder.attendeeName = convertView.findViewById(R.id.registratorName);
            holder.registrationDate = convertView.findViewById(R.id.registrationDate);
            holder.attendeeImage = convertView.findViewById(R.id.registorImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set the data from the registration object
        holder.attendeeName.setText(registration.getAttendeeId()); // Ensure this returns the correct data

        // Date of Registration
        if (registration.getRegistrationDate() != null) {
            Date date = registration.getRegistrationDate().toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(date);
            holder.registrationDate.setText("Date of sign-up: " + formattedDate);
        } else {
            holder.registrationDate.setText("Date of sign-up: N/A");
        }

        // Load the profile image
        Glide.with(context).load(registration.getAttendeeImageURL()).into(holder.attendeeImage);

        return convertView;
    }

    /**
     * Updates the list of registrations within the adapter and refreshes the attached view.
     *
     * @param registrationDataList The new list of registrations to display.
     */

    public void setFilter(ArrayList<Registration> registrationDataList) {
        registrations.clear();
        registrations.addAll(registrationDataList);
        notifyDataSetChanged();
    }
}
