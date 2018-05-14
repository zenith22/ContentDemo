package com.saba.contentdemo;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by AInamdar on 5/3/2018.
 */

public class WebviewUtil {

    public static void setWebviewSettingsForContent(WebView webView, boolean isPopWindow){

        WebSettings contentViewSettings = webView.getSettings();

        Log.d("WebviewUtil","user agent = " + contentViewSettings.getUserAgentString());

        contentViewSettings.setJavaScriptEnabled(true);
//        contentViewSettings.setUseWideViewPort(true);
//        contentViewSettings.setLoadWithOverviewMode(true);
        contentViewSettings.setDomStorageEnabled(true);
        contentViewSettings.setDatabaseEnabled(true);
        contentViewSettings.setAppCacheEnabled(true);
        contentViewSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        contentViewSettings.setSupportZoom(true);
        contentViewSettings.setBuiltInZoomControls(true);
        contentViewSettings.setAllowFileAccess(true);
        contentViewSettings.setAllowFileAccessFromFileURLs(true);
        contentViewSettings.setAllowContentAccess(true);
        contentViewSettings.setAllowUniversalAccessFromFileURLs(true);


        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);


        if(isPopWindow){
            contentViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            contentViewSettings.setSupportMultipleWindows(true);
        }
    }
}