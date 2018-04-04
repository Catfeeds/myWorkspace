package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 基础授信headerViewHolder
 * Created by chen_bin on 2017/8/10 0010.
 */
public class BasicAuthItemHeaderViewHolder extends BaseViewHolder<JsonElement> {
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.header_layout)
    LinearLayout headerLayout;

    public BasicAuthItemHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        int headerWidth = CommonUtil.getDeviceSize(itemView.getContext()).x;
        int headerHeight = Math.round(headerWidth * 340.0f / 640.f);
        headerLayout.getLayoutParams().width = headerWidth;
        headerLayout.getLayoutParams().height = headerHeight;
    }

    @Override
    protected void setViewData(
            Context mContext, JsonElement jsonElement, int position, int viewType) {
        if (jsonElement == null) {
            return;
        }
        String realName = CommonUtil.getAsString(jsonElement, "real_name");
        if (TextUtils.isEmpty(realName)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = realName.length(); i < size; i++) {
            sb.append(i > 0 ? "*" : realName.charAt(i));
        }
        tvName.setText(sb.toString());
    }
}
