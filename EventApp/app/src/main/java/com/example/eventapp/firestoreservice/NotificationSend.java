package com.example.eventapp.firestoreservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

    public NotificationSend(List<String> tokens, String title, String body) {
        this.tokens = tokens;
        this.title = title;
        this.body = body;
    }

    public void sendNotifications() {
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
}
