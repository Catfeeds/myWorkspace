package me.suncloud.marrymemo.adpter.finder.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.subpage.MarkedKeyword;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.finder.SubPageMarkListActivity;

/**
 * 专栏关键词
 * Created by chen_bin on 2017/6/23 0023.
 */
public class SubPageKeywordsViewHolder extends BaseViewHolder<List<MarkedKeyword>> {
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R.id.keywords_layout)
    LinearLayout keywordsLayout;
    private int flowItemWidth;

    public SubPageKeywordsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.flowItemWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                56)) / 4;
    }

    @Override
    protected void setViewData(
            Context mContext, List<MarkedKeyword> keywords, int position, int viewType) {
        if (CommonUtil.isCollectionEmpty(keywords)) {
            keywordsLayout.setVisibility(View.GONE);
        } else {
            keywordsLayout.setVisibility(View.VISIBLE);
            int count = flowLayout.getChildCount();
            int size = keywords.size();
            if (count > size) {
                flowLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                Button btn = null;
                final MarkedKeyword keyword = keywords.get(i);
                if (count > i) {
                    btn = (Button) flowLayout.getChildAt(i);
                }
                if (btn == null) {
                    View.inflate(mContext, R.layout.sub_page_keywords_flow_item, flowLayout);
                    btn = (Button) flowLayout.getChildAt(flowLayout.getChildCount() - 1);
                    btn.getLayoutParams().width = flowItemWidth;
                }
                btn.setText(keyword.getTitle());
                if (!TextUtils.isEmpty(keyword.getColor())) {
                    btn.setTextColor(Color.parseColor(keyword.getColor()));
                } else {
                    btn.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
                }
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), SubPageMarkListActivity.class);
                        intent.putExtra("markId", keyword.getMarkId());
                        v.getContext()
                                .startActivity(intent);
                    }
                });
            }
        }
    }
}
