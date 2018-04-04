package me.suncloud.marrymemo.adpter.newsearch.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerMerchantViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.newsearch.NewSearchFragment;
import me.suncloud.marrymemo.fragment.newsearch.OnSearchItemClickListener;

/**
 * Created by hua_rong on 2018/2/6
 * 搜索下拉商家类型提示
 */

public class SearchMerchantTipViewHolder extends TrackerMerchantViewHolder {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.ll_price)
    LinearLayout llPrice;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.view_line)
    View viewLine;

    private String keyword;

    private OnSearchItemClickListener onSearchItemClickListener;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public SearchMerchantTipViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSearchItemClickListener != null) {
                    onSearchItemClickListener.onSearchItemClick(keyword, getItem());
                }
            }
        });

    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    public String cpmSource() {
        return NewSearchFragment.CPM_SOURCE;
    }

    @Override
    protected void setViewData(
            Context context, Merchant merchant, int position, int viewType) {
        if (merchant != null) {
            viewLine.setVisibility(View.VISIBLE);
            llPrice.setVisibility(View.GONE);
            tvCount.setText(merchant.getShopArea()
                    .getName());
            tvPrice.setText(CommonUtil.formatDouble2String((merchant.getPriceStart())));
            llPrice.setVisibility(View.VISIBLE);
            String cpm = merchant.getCpm();
            ivLogo.setImageResource(TextUtils.isEmpty(cpm) ? R.mipmap.icon_search_shop : R.mipmap
                    .icon_search_recommend);
            String name = merchant.getName();
            if (!TextUtils.isEmpty(name)) {
                int start = name.indexOf("<em>");
                int end = name.indexOf("</em>");
                int length = end - start - 4;
                SpannableStringBuilder sp = new SpannableStringBuilder(name.replace("<em>", "")
                        .replace("</em>", ""));
                if (start >= 0 && end >= 0 && length > 0) {
                    sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,
                            R.color.colorPrimary)),
                            start,
                            start + length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvName.setText(sp);
            }
        }
    }

    public void isLastLine(boolean isLast) {
        viewLine.setVisibility(isLast ? View.GONE : View.VISIBLE);
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener onSearchItemClickListener) {
        this.onSearchItemClickListener = onSearchItemClickListener;
    }

}
