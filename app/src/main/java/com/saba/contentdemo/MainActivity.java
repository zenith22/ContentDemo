package com.saba.contentdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;
import java.net.URLDecoder;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView.setWebContentsDebuggingEnabled(true);

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d(TAG,"absolutePath = " + absolutePath);

        WebView scormContentWV = findViewById(R.id.scormContentWV);

        scormContentWV.setWebChromeClient(new MyChromeClient(false));
        scormContentWV.setWebViewClient(new MyWebViewClient(false));

        WebSettings contentViewSettings = scormContentWV.getSettings();
        contentViewSettings.setJavaScriptEnabled(true);
        contentViewSettings.setUseWideViewPort(true);
        contentViewSettings.setLoadWithOverviewMode(true);
        contentViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
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


        contentViewSettings.setSupportMultipleWindows(true);
//        scormContentWV.loadUrl("http://10.70.8.133:8090/scorm/ScormTesting.html");

        scormContentWV.loadUrl("file:///storage/emulated/0/Sample/localFile.html");

    }



    private class MyChromeClient extends WebChromeClient {


        private final boolean isScorm;

        private MyChromeClient(boolean isScorm){
            this.isScorm = isScorm;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(TAG,"-------->onJsAlert Message::"+ message);
            return false;

        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            Log.d(TAG,"-------->onCreateWindow::"+resultMsg);

            Message href = view.getHandler().obtainMessage();
            view.requestFocusNodeHref(href);

            String url = href.getData().getString("url");
            Log.d(TAG,"-------->onCreateWindow::url = "+url);

            WebView.HitTestResult result = view.getHitTestResult();
            int type = result.getType();
            String data = result.getExtra();
            Log.d(TAG,"-------->onCreateWindow::type = "+type + ":: data = " + data);

            if(!isScorm) {
                final WebView newWebView = new WebView(view.getContext());
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.topMargin = PixelUtil.dpToPx(MainActivity.this,40);
                newWebView.setLayoutParams(params);

                WebSettings contentViewSettings = newWebView.getSettings();
                contentViewSettings.setJavaScriptEnabled(true);
                contentViewSettings.setUseWideViewPort(true);
                contentViewSettings.setLoadWithOverviewMode(true);
                contentViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
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

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                newWebView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.d(TAG,"-------->onPageStarted url = " + url);
                    }
                });
                return true;
            }

            return super.onCreateWindow(view,isDialog,isUserGesture,resultMsg);
        }

        public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize,
                                            long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(estimatedSize * 2);
        }


        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebChromeClient#onConsoleMessage(android.webkit. ConsoleMessage)
         */
        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d(TAG,"-------->Console" + cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId() );
            //DO NOT try some exitAndSync() adventure here. It starts saveInBackground & whatnot, and
            //then in the meantime the user presses DONE, we have unknown behavior
            return true;
        }



        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebChromeClient#onProgressChanged(android.webkit.WebView , int)
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            Log.d(TAG,"-------->onProgressChanged = "+ newProgress );
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            Log.d(TAG, "MyChromeClient - onCloseWindow------------>");
            if(!isScorm && window != null) {
                window.onPause();
                window.destroy();

                WebView parent = (WebView) window.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
            }
        }
    }


    public class MyWebViewClient extends WebViewClient {


        private final boolean isScorm;

        private MyWebViewClient(boolean isScorm) {
            this.isScorm = isScorm;
        }




        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit .WebView,
         * java.lang.String)
         * Should return true if we want to tell the webview that we are going to handle the url our way (i.e. tell webview get lost and don't do anything to our url)
         * return false if we want the webview to handle our url
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG,"-------->shouldOverrideUrlLoading::LoadUrl = " + url);
            String fileType = null;
            if (url.startsWith("intent://")) {
                try {
                    Context context = view.getContext();
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    String packageName = intent.getPackage();
                    if (intent != null) {
                        view.stopLoading();

                        return true;
                    }
                } catch (URISyntaxException e) {
                    Log.e(TAG, "Can't resolve intent://", e);
                }
                return false;
            } else if (DownloadUtils.getInstance().isFile(url)) {
                Log.d(TAG,"-------->DownloadUtils.getInstance().isFile(url)");
                return true;
            } else if (url.contains("file:///") || url.contains("file://GD_Content")) {
                Log.d(TAG,"-------->url.contains(\"file:///\") || url.contains(\"file://GD_Content\")");
                return false;
            } else if (url.compareToIgnoreCase("about:blank") == 0) {
                //Tell the WebView that *WE* will be taking care of about:blank & that
                //it should leave it be. This is because if we let the WebView load a:b
                //then later when user clicks Done, we get all sorts of JavaScript
                //errors when trying to unload
                Log.d(TAG,"-------->url.compareToIgnoreCase(\"about:blank\") == 0");
                return true;
            }else {
                Log.d(TAG,"-------->Last else where iScorm = " + isScorm);
                if(isScorm) {
                    return true;
                }else{
                    return false;
                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG,"-------->onPageFinished = " + url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG,"-------->onPageStarted = " + url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG,"-------->onReceivedError" + description);
        }

    }
}
