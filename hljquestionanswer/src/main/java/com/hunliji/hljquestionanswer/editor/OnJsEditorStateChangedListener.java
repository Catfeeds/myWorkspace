package com.hunliji.hljquestionanswer.editor;


import java.util.Map;

public interface OnJsEditorStateChangedListener {
    void onDomLoaded();

    void onMediaTapped(String mediaId, String mediaUrl, String uploadStatus);

    void onGetHtmlResponse(Map<String, String> stringStringMap);
}
