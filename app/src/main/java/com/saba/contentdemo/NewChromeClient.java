package com.saba.contentdemo;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

/**
 * Created by AInamdar on 5/7/2018.
 */

public class NewChromeClient extends WebChromeClient{

    private final String TAG = "NewChromeClient";
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d(TAG,"2 = " + consoleMessage);
        return super.onConsoleMessage(consoleMessage);
    }
}
