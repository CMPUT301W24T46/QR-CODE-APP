package com.example.eventapp.document_reference;

import android.util.Log;

public class AndroidLogger implements Logger{
    @Override
    public void debug(String tag, String message) {
        Log.d(tag, message);
    }
}
