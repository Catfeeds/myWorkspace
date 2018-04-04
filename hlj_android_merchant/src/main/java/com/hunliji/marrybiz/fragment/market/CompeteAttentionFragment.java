package com.hunliji.marrybiz.fragment.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;

/**
 * 竞对关注fragment
 * Created by jinxin on 2016/6/17.
 */
public class CompeteAttentionFragment extends BaseSameBusinessFragment {

    public static CompeteAttentionFragment newInstance() {
        return new CompeteAttentionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlSpec = Constants.HttpPath.GET_COMPETE_BUSINESS;
        showMyMerchant = false;
    }

    @Override
    protected void setEmptyView() {
        View emptyView = listView.getRefreshableView()
                .getEmptyView();
        if (emptyView == null) {
            String html = getString(R.string.label_compete_empty, "+");
            imgEmptyHint.setVisibility(View.GONE);
            textEmptyHint.setText(Html.fromHtml(html));
            textEmptyHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            textEmptyHint.setTextColor(getResources().getColor(R.color.colorGray));
            textEmptyHint.setVisibility(View.VISIBLE);
            emptyHintLayout.setVisibility(View.VISIBLE);
            listView.getRefreshableView()
                    .setEmptyView(emptyHintLayout);
        }
    }
}
