package com.artear.webwrapexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.artear.webwrap.WebWrapper
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    private lateinit var webWrapper: WebWrapper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        webWrapper = WebWrapper(webViewExample)
        lifecycle.addObserver(webWrapper)
    }
}