package com.hunliji.hljemojilibrary.widgets;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljemojilibrary.R;
import com.hunliji.hljemojilibrary.adapters.EmojiPagerAdapter;
import com.slider.library.Indicators.CirclePageIndicator;


/**
 * Created by wangtao on 2017/3/27.
 */

public class EmojiMenuLayout extends FrameLayout {

    private OnEmojiItemClickListener onEmojiItemClickListener;
    private EmojiPagerAdapter emojiPagerAdapter;
    private FaceMode mode;

    public enum FaceMode{
        HLJFace,
        EMFace
    }


    public EmojiMenuLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public EmojiMenuLayout(
            @NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiMenuLayout(
            @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int emojiSize = Math.round((CommonUtil.getDeviceSize(getContext()).x - CommonUtil.dp2px(getContext(),20))/ 7);
        View view = inflate(getContext(), R.layout.widget_emoji_menu_layout___emoji, this);
        ViewPager emojiPager =  view.findViewById(R.id.emoji_pager);
        emojiPager.getLayoutParams().height = Math.round(emojiSize * 3 + CommonUtil.dp2px(getContext(),20));
        emojiPagerAdapter = new EmojiPagerAdapter(getContext(), emojiSize,
                new EmojiPagerAdapter.OnFaceItemClickListener() {
                    @Override
                    public void onFaceItemClickListener(AdapterView<?> parent,
                                                        View view,
                                                        int position,
                                                        long id) {
                        String emoji= (String) parent.getAdapter().getItem(position);
                        if (TextUtils.isEmpty(emoji)) {
                            return;
                        }
                        if(onEmojiItemClickListener!=null){
                            onEmojiItemClickListener.onEmojiItemClickListener(emoji);
                        }
                    }
                });
        setMode(FaceMode.HLJFace);
        emojiPager.setAdapter(emojiPagerAdapter);
        CirclePageIndicator emojiIndicator =  view.findViewById(R.id
                .emoji_indicator);
        emojiIndicator.setViewPager(emojiPager);
    }

    public void setMode(@NonNull FaceMode mode) {
        if(emojiPagerAdapter==null){
            return;
        }
        if(mode==this.mode){
            return;
        }
        this.mode=mode;
        switch (mode){
            case EMFace:
                emojiPagerAdapter.setTags(EmojiUtil.getEMFaceMap(getContext())
                        .keySet());
                break;
            case HLJFace:
                emojiPagerAdapter.setTags(EmojiUtil.getFaceMap(getContext())
                        .keySet());
                break;
        }
    }


    public void setOnEmojiItemClickListener(OnEmojiItemClickListener onEmojiItemClickListener) {
        this.onEmojiItemClickListener = onEmojiItemClickListener;
    }

    public interface OnEmojiItemClickListener {
        void onEmojiItemClickListener(String emoji);
    }
}
