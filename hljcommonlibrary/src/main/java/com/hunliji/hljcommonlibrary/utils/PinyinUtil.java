package com.hunliji.hljcommonlibrary.utils;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

/**
 * 中文转换成拼音
 * Created by chen_bin on 2017/2/23 0023.
 */
public class PinyinUtil {

    /**
     * 全拼音
     *
     * @param str
     * @return
     */
    public static String getPinyin(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String convert;
        HanyuPinyinOutputFormat pyFormat = new HanyuPinyinOutputFormat();
        pyFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);    //设置样式
        pyFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pyFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        try {
            convert = PinyinHelper.toHanyuPinyinString(str, pyFormat, "");
        } catch (Exception e) {
            convert = str;
        }
        return convert.toUpperCase();
    }

    /**
     * 简拼
     *
     * @param str
     * @return
     */
    public static String getShortPy(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert.toUpperCase();
    }
}
