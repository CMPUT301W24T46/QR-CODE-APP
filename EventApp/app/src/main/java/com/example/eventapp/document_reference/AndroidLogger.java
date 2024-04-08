package com.example.eventapp.document_reference;

import android.util.Log;

/**
 * AndroidLogger is an implementation of the {@link Logger} interface, utilizing Android's logging mechanism.
 * It provides a concrete method to log debug messages using Android's {@link Log} class. This allows for
 * consistent logging practices within Android applications, adhering to the platform's native logging standards.
 *
 * <p>Usage of this class enables decoupling of logging code from the Android framework, making it easier
 * to maintain and test code that requires logging functionality.</p>
 */


public class AndroidLogger implements Logger{

    /**
     * Logs a debug message using Android's logging system. The message is logged with the specified tag,
     * which can be used to categorize and filter log messages.
     *
     * @param tag     A {@link String} representing the tag associated with the log message. Tags are helpful
     *                for categorizing log messages and can be used for filtering log output.
     * @param message A {@link String} containing the message to be logged. This is the actual content that will
     *                appear in the log.
     */

    @Override
    public void debug(String tag, String message) {
        Log.d(tag, message);
    }
}
