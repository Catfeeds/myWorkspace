package com.hunliji.hljnotelibrary.views.widgets;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.interfaces.ITagView;
import com.hunliji.hljnotelibrary.models.Direction;

public class TagTextView extends AppCompatTextView implements ITagView {

    private Direction mDirection;

    public TagTextView(Context context) {
        this(context, null);
    }

    public TagTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextColor(Color.WHITE);
        setMaxLines(1);
        setEllipsize(TextUtils.TruncateAt.END);
        setTextSize(12);
        setShadowLayer(6, 0, 0, Color.parseColor("#7F000000"));
        setPadding(CommonUtil.dp2px(getContext(), 8),
                CommonUtil.dp2px(getContext(), 4),
                CommonUtil.dp2px(getContext(), 8),
                CommonUtil.dp2px(getContext(), 4));
    }

    @Override
    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    @Override
    public Direction getDirection() {
        return mDirection;
    }

}