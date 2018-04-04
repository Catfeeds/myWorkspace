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

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.search.TipSearchType;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.newsearch.OnSearchItemClickListener;

/**
 * Created by hua_rong on 2018/2/6
 * 搜索下拉正常类型提示
 */

public class SearchNormalTipViewHolder extends BaseViewHolder<TipSearchType> {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_price)
    LinearLayout llPrice;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.view_line)
    View viewLine;

    private String keyword;
    private OnSearchItemClickListener onSearchItemClickListener;

    public SearchNormalTipViewHolder(View itemView) {
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

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    protected void setViewData(
            Context context, TipSearchType newSearchType, int position, int viewType) {
        if (newSearchType != null) {
            viewLine.setVisibility(View.VISIBLE);
            llPrice.setVisibility(View.GONE);
            String category = newSearchType.getKey();
            String title = NewSearchApi.getTitle(keyword, category);
            String count = newSearchType.getDocStringContent();
            if (!TextUtils.isEmpty(title)) {
                SpannableStringBuilder sp = new SpannableStringBuilder(title);
                if (!TextUtils.isEmpty(keyword)) {
                    int index = title.indexOf(keyword);
                    sp.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,
                            R.color.colorPrimary)),
                            index,
                            index + keyword.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvName.setText(sp);
            }
            tvCount.setText(count);
            ivLogo.setImageResource(NewSearchApi.getImageResId(category));
        }
    }


    @Override
    public void setView(Context mContext, TipSearchType item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(itemView)
                    .tagName(HljTaggerName.TIP_KEYWORD)
                    .atPosition(position)
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_KEYWORD)
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_KEYWORD,
                            NewSearchApi.getTitle(keyword, item.getKey()))
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public void isLastLine(boolean isLast) {
        viewLine.setVisibility(isLast ? View.GONE : View.VISIBLE);
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener onSearchItemClickListener) {
        this.onSearchItemClickListener = onSearchItemClickListener;
    }

}
