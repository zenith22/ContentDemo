package com.saba.contentdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.net.URLDecoder;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private WebView scormContentWV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView.setWebContentsDebuggingEnabled(true);

        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d(TAG, "absolutePath = " + absolutePath);

        scormContentWV = findViewById(R.id.scormContentWV);

        scormContentWV.setWebChromeClient(new MyChromeClient());
        scormContentWV.setWebViewClient(new MyWebViewClient());

        WebviewUtil.setWebviewSettingsForContent(scormContentWV, true);


        TextView logsTV = findViewById(R.id.logsTV);
        logsTV.append("User Agent = " + scormContentWV.getSettings().getUserAgentString());

        loadWV();
    }

    private void loadWV() {

        String frameset = "<html><head></head>\n" +
                "<frameset framespacing=\"0\" rows=\"*,0\" frameborder=\"0\" noresize>\n" +
                "<frame src=\"file:///android_asset/test1.html\">\n" +
                "</frameset></html>";

        scormContentWV.loadDataWithBaseURL("file:///", frameset, "text/html", "UTF-8", null);
    }


    private class MyChromeClient extends WebChromeClient {


        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(TAG, "-------->onJsAlert Message::" + message);
            return false;

        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            Log.d(TAG, "-------->onCreateWindow --------->");

            final WebView newWebView = new WebView(view.getContext());
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.topMargin = PixelUtil.dpToPx(MainActivity.this, 40);
            newWebView.setLayoutParams(params);

            WebviewUtil.setWebviewSettingsForContent(newWebView, false);

            newWebView.setWebViewClient(new NewWebviewClient(MainActivity.this));
            newWebView.setWebChromeClient(new NewChromeClient());

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

//                view.addView(newWebView);

            return true;
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
            Log.d(TAG, "-------->onConsoleMessage = " + cm.message() + " -- From line " + cm.lineNumber() + " of " + cm.sourceId());
            return true;
        }


        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            Log.d(TAG, "-------->onProgressChanged = " + newProgress);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            Log.d(TAG, "MyChromeClient - onCloseWindow------------>");
            if (window != null) {
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


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "-------->shouldOverrideUrlLoading::LoadUrl = " + url);
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
                Log.d(TAG, "-------->DownloadUtils.getInstance().isFile(url)");
                return true;
            } else if (url.contains("file:///") || url.contains("file://GD_Content")) {
                Log.d(TAG, "-------->url.contains(\"file:///\") || url.contains(\"file://GD_Content\")");
                return false;
            } else if (url.compareToIgnoreCase("about:blank") == 0) {
                //Tell the WebView that *WE* will be taking care of about:blank & that
                //it should leave it be. This is because if we let the WebView load a:b
                //then later when user clicks Done, we get all sorts of JavaScript
                //errors when trying to unload
                Log.d(TAG, "-------->url.compareToIgnoreCase(\"about:blank\") == 0");
                return true;
            } else {
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "-------->onPageFinished = " + url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "-------->onPageStarted = " + url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG, "-------->onReceivedError" + description);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }
}
