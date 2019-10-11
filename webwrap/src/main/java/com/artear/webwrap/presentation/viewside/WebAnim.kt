package com.artear.webwrap.presentation.viewside

import android.view.animation.AlphaAnimation
import android.view.animation.Animation

/**
 * An animation provider for web view transition from loading to loaded state.
 */
class WebAnim {

    companion object {
        /**
         * The default animations duration for this provider
         */
        const val DURATION_ANIM_DEFAULT: Long = 1000
    }

    /**
     * Makes an alpha animation form 0 to 1.
     */
    fun createAlphaAnimation(durationAnim: Long = DURATION_ANIM_DEFAULT): Animation {
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = durationAnim
        alphaAnimation.fillBefore = true
        alphaAnimation.fillAfter = true
        return alphaAnimation
    }

}