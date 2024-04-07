package com.example.eventapp.firestoreservice;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.eventapp.MainActivity;
import com.example.eventapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * MyFirebaseMessagingService extends {@link FirebaseMessagingService} to handle incoming
 * Firebase Cloud Messaging (FCM) messages for the application. This service is responsible
 * for processing FCM messages and displaying notifications to the user.
 *
 * <p>When an FCM message is received, this service extracts the notification details and
 * displays a notification using the system's {@link NotificationManager}. The notification
 * directs the user to the application's {@link MainActivity} when tapped.</p>
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    private String eventId ;
    private String userId ;

    /**
     * Called when a message is received.
     *
     * <p>This method is called on the application's main thread, so long-running operations
     * should be performed asynchronously or in the context of a separate thread.</p>
     *
     * @param remoteMessage An instance of {@link RemoteMessage} representing the message received.
     */

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Handle FCM messages here.
        // For example, you could store the message in Firestore.
        if (remoteMessage.getNotification() != null) {
            // Log the message for debugging
            Log.d("Notification Body", "Notification Message Body: " + remoteMessage.getNotification().getBody());

            // Call method to handle the notification
            displayNotification(remoteMessage);
        }
    }

    /**
     * Constructs and displays a system notification based on the contents of the received FCM message.
     * The notification includes a title and body text extracted from the {@link RemoteMessage} and
     * an intent that opens {@link MainActivity} when the notification is tapped.
     *
     * @param remoteMessage The {@link RemoteMessage} containing the notification data.
     */

    private void displayNotification(RemoteMessage remoteMessage) {
        //Assuming your app's main activity is MainActivity
        // Change this to your actual main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id); // Define this string in your strings.xml
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground) // Set your app's icon
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }


}
