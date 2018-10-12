package com.artear.webwrap.presentation.viewside

import android.view.animation.AlphaAnimation
import android.view.animation.Animation


class WebAnim {

    companion object {
        const val DURATION_ANIM_DEFAULT: Long = 1000
    }

    fun createAlphaAnimation(durationAnim: Long = DURATION_ANIM_DEFAULT): Animation {
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = durationAnim
        alphaAnimation.fillBefore = true
        alphaAnimation.fillAfter = true
        return alphaAnimation
    }

}