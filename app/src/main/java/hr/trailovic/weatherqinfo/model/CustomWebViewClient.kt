package hr.trailovic.weatherqinfo.model

import android.content.Context
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient


class CustomWebViewClient(private val context: Context, private val baseUrl: String) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//        return super.shouldOverrideUrlLoading(view, request)
//        return request?.url?.host != baseUrl
        val requestUrl = request?.url?.host
        if (requestUrl == baseUrl) {
            return false
        } else {
            val newIntent = Intent(Intent.ACTION_VIEW, request?.url)
            context.startActivity(newIntent)

            return true
        }
    }
}