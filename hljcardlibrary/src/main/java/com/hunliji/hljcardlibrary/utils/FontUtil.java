package com.hunliji.hljcardlibrary.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.api.CardApi;
import com.hunliji.hljcardlibrary.models.Font;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wangtao on 2017/6/22.
 */

public class FontUtil {

    private static FontUtil INSTANCE;
    private List<Font> fonts;

    public static FontUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FontUtil();
        }
        return INSTANCE;
    }

    private List<Font> getFonts(Context context) {
        if (fonts != null) {
            return fonts;
        }
        if (context != null) {
            try {
                InputStream in = null;
                if (context.getFileStreamPath(HljCard.FONTS_FILE) != null && context
                        .getFileStreamPath(
                        HljCard.FONTS_FILE)
                        .exists()) {
                    in = context.openFileInput(HljCard.FONTS_FILE);
                }
                if (in == null) {
                    in = context.getResources()
                            .openRawResource(R.raw.fonts);
                }
                JsonElement jsonElement = new JsonParser().parse(new InputStreamReader(in));
                fonts = GsonUtil.getGsonInstance()
                        .fromJson(jsonElement, new TypeToken<List<Font>>() {}.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fonts == null) {
            fonts = new ArrayList<>();
        }
        return fonts;
    }

    public Font getFont(Context context,String name) {
        if(TextUtils.isEmpty(name)){
            return null;
        }
        if (Build.VERSION.SDK_INT<
                Build.VERSION_CODES.LOLLIPOP) {
            switch (name){
                case "FZFengYaSongS-GB":
                    return null;
            }
        }
        List<Font> fonts=getFonts(context);
        if(CommonUtil.isCollectionEmpty(fonts)){
            return null;
        }
        for (Font font:fonts){
            if(name.equals(font.getName())){
                return font;
            }
        }
        return null;
    }

    public Subscription synchronizeFonts(Context context) {
        return CardApi.getFont(context)
                .subscribe(new Subscriber<List<Font>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Font> fonts) {
                        if (CommonUtil.isCollectionEmpty(fonts)) {
                            FontUtil.this.fonts = fonts;
                        }
                    }
                });
    }
}
