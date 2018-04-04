package com.hunliji.hljquestionanswer.editor;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;

public class JsCallbackReceiver {
    private static final String JS_CALLBACK_DELIMITER = "~";

    private static final String CALLBACK_DOM_LOADED = "callback-dom-loaded";
    private static final String CALLBACK_IMAGE_TAP = "callback-image-tap";

    private static final String CALLBACK_RESPONSE_STRING = "callback-response-string";

    public static final String JS_CALLBACK_HANDLER = "nativeCallbackHandler";

    private final OnJsEditorStateChangedListener mListener;

    private Set<String> mPreviousStyleSet = new HashSet<>();

    public JsCallbackReceiver(OnJsEditorStateChangedListener listener) {
        mListener = listener;
    }

    @JavascriptInterface
    public void executeCallback(String callbackId, String params) {
        switch (callbackId) {
            case CALLBACK_DOM_LOADED:
                mListener.onDomLoaded();
                break;
            case CALLBACK_IMAGE_TAP:
                Log.d("JsCallbackReceiver", "Image tapped, " + params);

                String uploadStatus = "";

                List<String> mediaIds = new ArrayList<>();
                mediaIds.add("id");
                mediaIds.add("url");
                mediaIds.add("meta");
                mediaIds.add("type");

                Set<String> mediaDataSet = HtmlUtils.splitValuePairDelimitedString(params, JS_CALLBACK_DELIMITER, mediaIds);
                Map<String, String> mediaDataMap = HtmlUtils.buildMapFromKeyValuePairs(mediaDataSet);

                String mediaId = mediaDataMap.get("id");

                String mediaUrl = mediaDataMap.get("url");
                if (mediaUrl != null) {
                    mediaUrl = HtmlUtils.decodeHtml(mediaUrl);
                }

                String mediaMeta = mediaDataMap.get("meta");

                if (mediaMeta != null) {
                    mediaMeta = HtmlUtils.decodeHtml(mediaMeta);

                    try {
                        JSONObject mediaMetaJson = new JSONObject(mediaMeta);
                        String classes = CommonUtil.getString(mediaMetaJson, "classes");
                        Set<String> classesSet = HtmlUtils.splitDelimitedString(classes, ", ");

                        if (classesSet.contains("uploading")) {
                            uploadStatus = "uploading";
                        } else if (classesSet.contains("failed")) {
                            uploadStatus = "failed";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("JsCallbackReceiver", "Media meta data from callback-image-tap was not JSON-formatted");
                    }
                }

                mListener.onMediaTapped(mediaId, mediaUrl, uploadStatus);
                break;
            case CALLBACK_RESPONSE_STRING:
                Log.d("JsCallbackReceiver", callbackId + ": " + params);
                Set<String> responseDataSet;
                if (params.startsWith("function=") && params.contains(JS_CALLBACK_DELIMITER)) {
                    String functionName = params.substring("function=".length(), params.indexOf(JS_CALLBACK_DELIMITER));

                    List<String> responseIds = new ArrayList<>();
                    switch (functionName) {
                        case "getHTMLForCallback":
                            responseIds.add("id");
                            responseIds.add("contents");
                            break;
                        case "getSelectedTextToLinkify":
                            responseIds.add("result");
                            break;
                        case "getFailedMedia":
                        case "getLocalMedia":
                            responseIds.add("ids");
                    }

                    responseDataSet = HtmlUtils.splitValuePairDelimitedString(params, JS_CALLBACK_DELIMITER, responseIds);
                } else {
                    responseDataSet = HtmlUtils.splitDelimitedString(params, JS_CALLBACK_DELIMITER);
                }
                mListener.onGetHtmlResponse(HtmlUtils.buildMapFromKeyValuePairs(responseDataSet));
                break;
            default:
                Log.d("JsCallbackReceiver", "Unhandled callback: " + callbackId + ":" + params);
        }
    }
}
