package com.hunliji.hljquestionanswer.utils;

import android.text.Html;
import android.text.TextUtils;

/**
 * Created by Suncloud on 2016/8/31.
 */
public final class EditWebUtil {

    /**
     * 获取html内容进行检测
     */
    public static final String BACK_CHECK_URL = "javascript:handler.onBackInfoCheck(zss_editor" +
            ".getHTML())";
    /**
     * 获取字数，图片数，html内容进行检测
     */
    public static final String POST_CHECK_URL = "javascript:handler.onPostInfoCheck(zss_editor" +
            ".getTextCount(),zss_editor.getImageCount(),zss_editor.getHTML())";
    /**
     * 插入图片前置操作
     */
    public static final String PREPARE_INSERT_URL = "javascript:zss_editor.prepareInsert()";

    /**
     * 编辑时设置内容
     */
    public static String setHtmlUrl(String content) {
        //问题内容格式化处理
        String html = content.replaceAll("\"", "\\\"")
                .replaceAll("“", "&quot;")
                .replaceAll("”", "&quot;")
                .replaceAll("\r", "\\r")
                .replaceAll("\n", "\\n");
        return String.format("javascript:zss_editor.setHTML('%s')", html);
    }

    /**
     * 插入图片
     *
     * @param path 图片地址支持本地地址
     * @param id   图片标识用于替换
     */
    public static String insertImageUrl(String path, String id) {
        return String.format("javascript:zss_editor.insertImage('%s','','%s')", path, id);
    }

    /**
     * 用文字替换图片 用于图片上传错误处理
     *
     * @param text 替换的文字
     * @param id   替换的图片标识
     */
    public static String exchangeImageToTextUrl(String text, String id) {
        return String.format("javascript:zss_editor.exchangeImageToText('%s','%s')", id, text);
    }

    /**
     * 替换图片地址 用于图片上传成功替换七牛地址
     *
     * @param path 图片地址
     * @param id   替换的图片标识
     */
    public static String exchangeImageSrcUrl(String path, String id) {
        return String.format("javascript:zss_editor.exchangeImageSrc('%s','%s')", path, id);
    }

    public interface OnEditWebCallback {

        void onBackInfoCheck(String html);

        void onPostInfoCheck(int textCount, int imageCount, String html);

        void onEditChange(int textCount, int imageCount);
    }



    public static boolean isEditEmpty(String html) {
        if (!TextUtils.isEmpty(html)) {
            String content = html.replace("&nbsp;", "");
            content = Html.fromHtml(content)
                    .toString();
            if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(content.trim())) {
                return false;
            }
        }
        return true;
    }
}
