package com.hunliji.hljnotelibrary.views.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljemojilibrary.adapters.EmojiPagerAdapter;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.slider.library.Indicators.CirclePageIndicator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 评论的积类
 * Created by chen_bin on 2017/4/14 0014.
 */
public class BasePostCommentActivity extends Activity implements EmojiPagerAdapter
        .OnFaceItemClickListener {
    @BindView(R2.id.root_layout)
    LinearLayout rootLayout;
    @BindView(R2.id.comment_layout)
    RelativeLayout commentLayout;
    @BindView(R2.id.et_content)
    public EditText etContent;
    @BindView(R2.id.emoji_layout)
    LinearLayout emojiLayout;
    @BindView(R2.id.btn_add_emoji)
    ImageView btnAddEmoji;
    @BindView(R2.id.face_pager)
    ViewPager facePager;
    @BindView(R2.id.flow_indicator)
    CirclePageIndicator flowIndicator;
    private InputMethodManager imm;
    private CharSequence ss;
    private String commentContent;
    private boolean isShowImm;
    private boolean isShowEmoji;
    private boolean isMeasured;
    private int mStart;
    private int editCount;
    private int emojiSize;
    private int maxLength;
    private int emojiImageSize;
    private int emojiPageHeight;
    private CommentInterface commentInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment___note);
        ButterKnife.bind(this);
        initValue();
        initViews();
    }

    private void initValue() {
        commentContent = getIntent().getStringExtra("comment_content");
        isShowEmoji = getIntent().getBooleanExtra("is_show_emoji", false);
        emojiSize = CommonUtil.dp2px(this, 20);
        emojiImageSize = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 20)) / 7;
        emojiPageHeight = Math.round(emojiImageSize * 3 + CommonUtil.dp2px(this, 20));
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        maxLength = commentInterface.getMaxLength();
    }

    private void initViews() {
        setOnTextWatcher();
        etContent.requestFocus();
        etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        if (!TextUtils.isEmpty(commentInterface.getHint())) {
            etContent.setHint(commentInterface.getHint());
        }
        if (!TextUtils.isEmpty(commentContent)) {
            etContent.setText(EmojiUtil.parseEmojiByText2(this, commentContent, emojiSize));
            etContent.setSelection(etContent.length());
        }
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });
        setEmojiViewPager();
        setKeyboardListener();
        if (isShowEmoji) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN |
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            emojiLayout.setVisibility(View.VISIBLE);
            btnAddEmoji.setImageResource(R.mipmap.icon_keyboard_round_gray);
        }
    }

    private void setOnTextWatcher() {
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0) {
                    DynamicDrawableSpan[] spans = etContent.getText()
                            .getSpans(start, start + count, DynamicDrawableSpan.class);
                    int size = spans.length;
                    if (size > 0) {
                        for (DynamicDrawableSpan span : spans) {
                            etContent.getText()
                                    .removeSpan(span);
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ss = s.subSequence(start, start + count)
                        .toString();
                mStart = start;
                editCount = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                int editStart = etContent.getSelectionEnd();
                int editEnd = etContent.getSelectionEnd();
                etContent.removeTextChangedListener(this);
                int outCount = CommonUtil.getTextLength(s) - maxLength;
                boolean isOut = false;
                while (outCount > 0) {
                    isOut = true;
                    editStart -= outCount;
                    s.delete(editStart, editEnd);
                    if (ss.length() > 0) {
                        ss = ss.subSequence(0, ss.length() - outCount);
                        editCount -= outCount;
                    }
                    editEnd = editStart;
                    outCount = CommonUtil.getTextLength(s) - maxLength;
                }
                if (isOut) {
                    ToastUtil.showToast(BasePostCommentActivity.this,
                            getString(R.string.msg_merchant_feed_comment___note, maxLength),
                            0);
                }
                if (ss.length() > 0) {
                    etContent.getText()
                            .replace(mStart,
                                    mStart + editCount,
                                    EmojiUtil.parseEmojiByText(BasePostCommentActivity.this,
                                            ss.toString(),
                                            emojiSize));
                }
                etContent.setSelection(editStart);
                etContent.addTextChangedListener(this);
            }
        });
    }

    private void setEmojiViewPager() {
        EmojiPagerAdapter emojiPagerAdapter = new EmojiPagerAdapter(this, emojiImageSize, this);
        facePager.setAdapter(emojiPagerAdapter);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(EmojiUtil.getFaceMap(this)
                .keySet());
        emojiPagerAdapter.setTags(arrayList);
        flowIndicator.setViewPager(facePager);
        facePager.getLayoutParams().height = emojiPageHeight;
    }

    private void setKeyboardListener() {
        rootLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                isShowImm = (double) (bottom - top) / height < 0.8;
                if (isShowImm) {
                    isShowEmoji = false;
                    emojiLayout.setVisibility(View.GONE);
                    btnAddEmoji.setImageResource(R.mipmap.icon_face_black_50_50);
                    if (!isMeasured && commentInterface.isNeedMeasureKeyboardHeight()) {
                        isMeasured = true;
                        RxBus.getDefault()
                                .post(new RxEvent(RxEvent.RxEventType.MEASURE_KEYBOARD_HEIGHT,
                                        height - bottom - top - CommonUtil.getStatusBarHeight(
                                                BasePostCommentActivity.this) + commentLayout
                                                .getMeasuredHeight()));
                    }
                } else if (isShowEmoji) {
                    emojiLayout.setVisibility(View.VISIBLE);
                    btnAddEmoji.setImageResource(R.mipmap.icon_keyboard_round_gray);
                }
            }
        });
    }

    @OnClick(R2.id.tv_send)
    void onSend() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (TextUtils.isEmpty(etContent.getText())) {
            ToastUtil.showToast(this, null, R.string.msg_post_text_empty___note);
            return;
        }
        commentInterface.onComment();
    }

    @OnClick(R2.id.btn_add_emoji)
    void onAddEmoji() {
        if (isShowImm) {
            isShowEmoji = true;
            if (imm != null && getCurrentFocus() != null) {
                imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } else if (!isShowEmoji) {
            isShowEmoji = true;
            emojiLayout.setVisibility(View.VISIBLE);
            btnAddEmoji.setImageResource(R.mipmap.icon_keyboard_round_gray);
        } else {
            isShowEmoji = false;
            if (imm != null && getCurrentFocus() != null) {
                imm.toggleSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (isShowEmoji) {
            isShowEmoji = false;
            emojiLayout.setVisibility(View.GONE);
            btnAddEmoji.setImageResource(R.mipmap.icon_face_black_50_50);
            return;
        }
        Intent intent = getIntent();
        intent.putExtra("comment_content",
                etContent.getText()
                        .toString());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onFaceItemClickListener(
            AdapterView<?> parent, View view, int position, long id) {
        String tag = (String) parent.getAdapter()
                .getItem(position);
        if (!TextUtils.isEmpty(tag)) {
            if (tag.equals("delete")) {
                if (etContent.isFocused()) {
                    EmojiUtil.deleteTextOrImage(etContent);
                }
            } else {
                if (etContent.isFocused()) {
                    StringBuilder ss = new StringBuilder(tag);
                    int start = etContent.getSelectionStart();
                    int end = etContent.getSelectionEnd();
                    if (start == end) {
                        etContent.getText()
                                .insert(start, ss);
                    } else {
                        etContent.getText()
                                .replace(start, end, ss);
                    }
                }
            }
        }
    }

    public interface CommentInterface {

        String getHint();

        boolean isNeedMeasureKeyboardHeight();

        int getMaxLength();

        void onComment();

    }

    public void setCommentInterface(CommentInterface commentInterface) {
        this.commentInterface = commentInterface;
    }

}
