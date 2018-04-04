package com.example.suncloud.hljweblibrary.client;

import android.net.Uri;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

/**
 * Created by Suncloud on 2016/8/20.
 */
public abstract class HljChromeClient extends WebChromeClient {

    @Override
    public void onGeolocationPermissionsShowPrompt(
            String origin, GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    public abstract void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture);

    public abstract void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType);
}
