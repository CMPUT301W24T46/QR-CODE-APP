package com.example.eventapp.document_reference;

import android.util.Log;

/**
 * The Logger interface provides an abstraction for logging operations. Implementations of this interface
 * can specify how messages are logged within the application. This allows for flexible logging strategies,
 * such as logging to the Android Logcat, to a file, or to external services.
 */

public interface Logger {

    /**
     * Logs a debug message with the specified tag and message. This method is intended to be used for
     * logging information that is helpful for debugging the application. The actual logging mechanism
     * depends on the implementation of this interface.
     *
     * @param tag     A String tag that identifies the source of the log message. This is usually the name
     *                of the class where the log call is made.
     * @param message The message to log. This should provide relevant information for debugging purposes.
     */

    void debug(String tag, String message);

}
