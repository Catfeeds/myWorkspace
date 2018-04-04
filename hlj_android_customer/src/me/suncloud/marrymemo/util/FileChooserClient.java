package me.suncloud.marrymemo.util;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

/**
 * Created by Suncloud on 2015/1/14.
 */
public abstract class FileChooserClient extends WebChromeClient {

    public abstract void openFileChooser(ValueCallback<Uri> uploadMsg,String acceptType, String capture);

    public abstract void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType);

    public abstract void openFileChooser(ValueCallback<Uri> uploadMsg);

}
