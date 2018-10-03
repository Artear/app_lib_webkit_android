package com.artear.webwrap

import android.app.Activity
import android.support.v4.app.Fragment
import android.view.View
import timber.log.Timber

/**
 * Clean Log - Calls the specified function [messageId] resource using self context and delegates to
 * Timber the log.
 */

inline fun <T : Activity> T.log(messageId: Int, vararg params: () -> Int) {
    Timber.d(resources.getString(messageId), *params)
}

/**
 * Clean Log - Calls the specified function [messageId] resource using self context and delegates to
 * Timber the log.
 */

inline fun <T : Fragment> T.log(vararg params: Any, messageId: () -> Int) {
    Timber.d(resources.getString(messageId()), *params)
}

/**
 * Clean Log - Calls the specified function [messageId] resource using self context and delegates to
 * Timber the log.
 */

inline fun <T : View> T.log(vararg params: Any, messageId: () -> Int) {
    Timber.d(resources.getString(messageId()), *params)
}

/**
 * Clean Log - Calls the specified function [messageId] resource using self context and delegates to
 * Timber the log.
 */

inline fun <T : View> T.logError(vararg params: Any, messageId: () -> Int) {
    Timber.e(resources.getString(messageId()), *params)
}


/**
 * Standard Log - Calls the specified function [message] and delegates to Timber the log.
 */
inline fun log(message: () -> String) {
    Timber.d(message())
}
