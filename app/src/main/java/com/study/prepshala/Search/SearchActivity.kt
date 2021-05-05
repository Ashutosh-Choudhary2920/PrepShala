package com.study.prepshala.Search

import android.os.Build
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.study.prepshala.R
import kotlinx.android.synthetic.main.activity_search.*
//import org.webrtc.Logging.d


class SearchActivity : AppCompatActivity() {
    companion object {
        const val URL: String = ""
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        webViewInitializer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun webViewInitializer() {
        val urlQuery = intent.getStringExtra(URL)
        webView.webViewClient = WebViewClient()
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccessFromFileURLs
        webView.settings.allowFileAccess
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true


        webView.setWebChromeClient(object : WebChromeClient() {
            // Need to accept permissions to use the camera
            override fun onPermissionRequest(request: PermissionRequest) {
                //L.d("onPermissionRequest")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.resources)
                }
            }
        })

        if (urlQuery != null) {
            webView.loadUrl(urlQuery)
        }
    }
}