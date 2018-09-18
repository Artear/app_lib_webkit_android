package com.artear.webwrapexample

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast

class Main2Activity : AppCompatActivity() {
    private var wv: WebView? = null

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        wv = findViewById<WebView>(R.id.webView)
        wv!!.settings.javaScriptEnabled = true
        wv!!.loadUrl("http://192.168.15.126/native/app/article/4401")
        //wv!!.addJavascriptInterface(jsInterface, "Native")
        wv!!.addJavascriptInterface(WebAppInterface(this), "Native")
    }

}

class WebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun log(callback: Int, toast: String) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun images(callback: Int, images: Array<String>) {
        Toast.makeText(mContext, images.last(), Toast.LENGTH_SHORT).show()
    }
}
