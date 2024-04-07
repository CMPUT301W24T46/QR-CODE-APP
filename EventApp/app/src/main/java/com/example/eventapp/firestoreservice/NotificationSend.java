package com.example.eventapp.firestoreservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * NotificationSend facilitates sending push notifications via Firebase Cloud Messaging (FCM).
 * It allows for the specification of a list of FCM tokens to which the notification will be sent,
 * along with the notification's title and body content.
 *
 * <p>The class supports sending notifications asynchronously using a thread pool to handle multiple
 * notification requests concurrently. It also includes a utility method to display a test notification
 * locally on the device.</p>
 */

public class NotificationSend {
    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAP97jXbE:APA91bESyLFtgoQbN27NmMKwne2epioyC9JD57zA5T9E1vtBLnKZ3MNCGBN2Z_3pPs2Uj4vY8TCfKVzqCAHWVBq_6XIk5SsAr7yhEpmGu6JikT213Lf8pCI3uihljKDqS8qVgACdqQGh";
    private static final String CONTENT_TYPE = "application/json";

    private List<String> tokens;
    private String title;
    private String body;
    private static ThreadPoolExecutor executor;

    static {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    /**
     * Constructs a new NotificationSend instance with specified FCM tokens, title, and body.
     *
     * @param tokens A list of FCM registration tokens to which the notification will be sent.
     * @param title  The title of the notification.
     * @param body   The body content of the notification.
     */

    public NotificationSend(List<String> tokens, String title, String body) {
        this.tokens = tokens;
        this.title = title;
        this.body = body;
    }

    /**
     * Default constructor for NotificationSend.
     */

    public NotificationSend() {

    }

    /**
     * Initiates the sending of the notification to the specified FCM tokens. This operation is performed
     * asynchronously in a separate thread to avoid blocking the main thread.
     */

    public void sendNotifications() {
        Log.d("Function Send " , "Called") ;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(FCM_API);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE);
                    conn.setDoOutput(true);

                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("notification", new JSONObject().put("title", title).put("body", body));
                    jsonBody.put("registration_ids", new JSONArray(tokens));

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(jsonBody.toString().getBytes("UTF-8"));
                    outputStream.close();

                    int responseCode = conn.getResponseCode();
                    System.out.println("Response Code : " + responseCode);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Displays a test notification on the device. This method is useful for testing and debugging notification
     * functionality without the need to send an actual FCM notification.
     *
     * @param context     The context from which the notification manager is obtained.
     * @param channelId   The ID of the notification channel (required for Android O and above).
     * @param title       The title of the test notification.
     * @param content     The content text of the test notification.
     */

    public static void testNotification(Context context, String channelId, String title, String content){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }
}
