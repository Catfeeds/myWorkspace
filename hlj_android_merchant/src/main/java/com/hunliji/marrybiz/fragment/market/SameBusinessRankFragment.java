package com.hunliji.marrybiz.fragment.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;

/**
 * 同业排名fragment
 * Created by jinxin on 2016/6/17.
 */
public class SameBusinessRankFragment extends BaseSameBusinessFragment {

    public static SameBusinessRankFragment newInstance() {
        return new SameBusinessRankFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlSpec = Constants.HttpPath.GET_SAME_BUSINESS;
        showMyMerchant = true;
    }


    @Override
    protected void setEmptyView() {
        View emptyView = listView.getRefreshableView()
                .getEmptyView();
        if (emptyView == null) {
            imgEmptyHint.setVisibility(View.VISIBLE);
            textEmptyHint.setText(getString(R.string.no_item));
            textEmptyHint.setVisibility(View.VISIBLE);
            emptyHintLayout.setVisibility(View.VISIBLE);
            listView.getRefreshableView()
                    .setEmptyView(emptyHintLayout);
        }
    }
}
