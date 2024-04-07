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

/**
 * CheckInAdapter is an {@link ArrayAdapter} designed to adapt {@link CheckIn} objects into views
 * within a ListView or any other view structure that supports adapters. This adapter is utilized to display
 * a list of check-in records, each with an attendee's ID, check-in location, and check-in time.
 *
 * <p>The adapter inflates a custom layout for each item in the list, ensuring that the check-in details are presented
 * in a user-friendly format. The adapter expects a list of {@link CheckIn} objects and uses the application
 * context to correctly inflate and populate the list item views.</p>
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Inflates a custom layout for check-in items.</li>
 *     <li>Populates the layout with data from {@link CheckIn} objects.</li>
 *     <li>Allows dynamic updates to the list of check-ins through the {@link #setFilter(ArrayList)} method.</li>
 * </ul>
 *
 * <p>Note: The getView method currently contains a TODO statement indicating that it needs to be implemented.
 * The commented code provides a guideline for how to inflate the custom layout and populate it with data.</p>
 */

public class CheckInAdapter extends ArrayAdapter<CheckIn> {

    private ArrayList<CheckIn> checkIns;
    private Context context;

    /**
     * Constructs a new CheckInAdapter.
     *
     * @param context  The current context. Used to inflate the layout file.
     * @param checkIns A list of {@link CheckIn} objects to be displayed.
     */

    public CheckInAdapter(Context context, ArrayList<CheckIn> checkIns) {
        super(context, 0, checkIns);
        this.context = context;
        this.checkIns = checkIns;
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position      The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView   The old view to reuse, if possible. Note: You should check that this view is non-null
     *                      and of an appropriate type before using. If it is not possible to convert this view to
     *                      display the correct data, this method can create a new view.
     * @param parent        The parent that this view will eventually be attached to.
     * @return              A View corresponding to the data at the specified position.
     */

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

    /**
     * Updates the adapter's data set and refreshes the attached view to reflect the new data.
     * This method can be used to dynamically update the list of check-ins displayed by the adapter.
     *
     * @param checkInDataList A new list of {@link CheckIn} objects to replace the old data set.
     */

    public void setFilter(ArrayList<CheckIn> checkInDataList) {
        checkIns.clear();
        checkIns.addAll(checkInDataList);
        notifyDataSetChanged();
    }
}
