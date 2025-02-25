package com.example.ireapplication.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import android.view.View

/**
 * Utility class for centralized error handling and logging.
 * Use this class to:
 * 1. Log errors consistently across the app
 * 2. Show user-friendly error messages
 * 3. Track and debug issues during testing
 */
object ErrorHandler {
    private const val TAG = "IREApp"
    private var isDebugMode = true  // TODO: Set to false for production

    /**
     * Log an error with stack trace and optionally show a user message
     * @param context Context for showing UI messages
     * @param error The exception or error to handle
     * @param userMessage Optional message to show to the user
     * @param showToast Whether to show a toast message (true) or Snackbar (false)
     */
    fun handleError(
        context: Context,
        error: Throwable,
        userMessage: String? = null,
        showToast: Boolean = false
    ) {
        // Always log the error for debugging
        Log.e(TAG, "Error: ${error.message}", error)

        // Show user message if provided
        userMessage?.let {
            if (showToast) {
                showToast(context, it)
            } else {
                // Find a suitable view for Snackbar in the current activity
                (context as? ErrorHandling)?.getRootView()?.let { view ->
                    showSnackbar(view, it)
                }
            }
        }

        // Additional debug information in debug mode
        if (isDebugMode) {
            Log.d(TAG, "Debug Information:")
            Log.d(TAG, "Error Class: ${error.javaClass.simpleName}")
            Log.d(TAG, "Stack Trace: ${error.stackTraceToString()}")
        }
    }

    /**
     * Log a debug message (only in debug mode)
     */
    fun logDebug(message: String, error: Throwable? = null) {
        if (isDebugMode) {
            if (error != null) {
                Log.d(TAG, message, error)
            } else {
                Log.d(TAG, message)
            }
        }
    }

    /**
     * Show a toast message
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Show a Snackbar message
     */
    fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    /**
     * Log specific types of errors with custom handling
     */
    fun handleNetworkError(context: Context, error: Throwable) {
        handleError(
            context,
            error,
            "Network error. Please check your connection.",
            true
        )
    }

    fun handleCameraError(context: Context, error: Throwable) {
        handleError(
            context,
            error,
            "Camera error. Please try again.",
            true
        )
    }

    fun handleQRScanError(context: Context, error: Throwable) {
        handleError(
            context,
            error,
            "Failed to scan QR code. Please try again.",
            true
        )
    }

    fun handleStorageError(context: Context, error: Throwable) {
        handleError(
            context,
            error,
            "Storage error. Please check app permissions.",
            true
        )
    }
}

/**
 * Interface to be implemented by activities that want to show Snackbar messages
 */
interface ErrorHandling {
    fun getRootView(): View
} 