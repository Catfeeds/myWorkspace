package com.hunliji.hljimagelibrary.views.widgets;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljimagelibrary.R;

/**
 * Created by wangtao on 2017/5/16.
 */

public class CountSelectView extends FrameLayout {

    private ImageView ivSelect;
    private TextView tvSelect;
    private boolean countEnable;

    public CountSelectView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CountSelectView(
            @NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountSelectView(
            @NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        LayoutInflater.from(context)
                .inflate(R.layout.count_select_view___img, this, true);
        ivSelect = (ImageView) findViewById(R.id.iv_select);
        tvSelect = (TextView) findViewById(R.id.tv_select);
    }

    public void setSelected(int selectedIndex) {
        if (selectedIndex >= 0) {
            if (countEnable) {
                tvSelect.setVisibility(VISIBLE);
                ivSelect.setVisibility(GONE);
                tvSelect.setText(String.valueOf(selectedIndex + 1));
            } else {
                ivSelect.setImageResource(R.mipmap.icon_check_round_primary_40_40);
            }
        } else {
            if(countEnable) {
                tvSelect.setVisibility(GONE);
                ivSelect.setVisibility(VISIBLE);
            }
            ivSelect.setImageResource(R.mipmap.icon_check_round_gray_40_40);
        }
    }


    public void setCountEnable(boolean countEnable) {
        this.countEnable = countEnable;
    }
}
