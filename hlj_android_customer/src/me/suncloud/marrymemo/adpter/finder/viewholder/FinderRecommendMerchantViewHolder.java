package me.suncloud.marrymemo.adpter.finder.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.interfaces.OnGetSimilarListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerMerchantViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;

/**
 * 发现页推荐流-商家
 * Created by chen_bin on 2018/2/2 0002.
 */
public class FinderRecommendMerchantViewHolder extends TrackerMerchantViewHolder {

    @BindView(R.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R.id.mask_view)
    View maskView;
    @BindView(R.id.btn_get_similar)
    ImageButton btnGetSimilar;
    @BindView(R.id.tv_relevant_hint)
    TextView tvRelevantHint;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_property)
    TextView tvProperty;
    @BindView(R.id.tv_case_count)
    TextView tvCaseCount;

    private int coverWidth;

    private OnGetSimilarListener onGetSimilarListener;

    @Override
    public String cpmSource() {
        return "find_page_recommands";
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    public FinderRecommendMerchantViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        coverWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px
                (itemView.getContext(),
                28)) / 2;
        imgCover.getLayoutParams().height = coverWidth;
        btnGetSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGetSimilarListener != null) {
                    onGetSimilarListener.onGetSimilar(getAdapterPosition());
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Merchant merchant = getItem();
                if (merchant == null) {
                    return;
                }
                Intent intent = new Intent(v.getContext(), MerchantDetailActivity.class);
                intent.putExtra(MerchantDetailActivity.ARG_ID, merchant.getId());
                v.getContext()
                        .startActivity(intent);
            }
        });
        try {
            HljVTTagger.buildTagger(btnGetSimilar)
                    .tagName(HljTaggerName.BTN_GET_SIMILAR)
                    .hitTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setViewData(Context mContext, Merchant merchant, int position, int viewType) {
        if (merchant == null) {
            return;
        }
        String coverPath = null;
        if (!CommonUtil.isCollectionEmpty(merchant.getPackages())) {
            coverPath = merchant.getPackages()
                    .get(0)
                    .getCoverPath();
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(coverPath)
                        .width(coverWidth)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvName.setText(merchant.getName());
        tvProperty.setText(merchant.getProperty()
                .getName());
        if (merchant.getActiveCaseCount() == 0) {
            tvCaseCount.setVisibility(View.GONE);
        } else {
            tvCaseCount.setVisibility(View.VISIBLE);
            tvCaseCount.setText(merchant.getActiveCaseCount() + "案例");
        }
    }

    /**
     * 是否显示找相似icon
     *
     * @param showSimilarIcon
     */
    public void setShowSimilarIcon(boolean showSimilarIcon) {
        if (showSimilarIcon) {
            maskView.setVisibility(View.VISIBLE);
            btnGetSimilar.setVisibility(View.VISIBLE);
        } else {
            maskView.setVisibility(View.GONE);
            btnGetSimilar.setVisibility(View.GONE);
        }
    }

    /**
     * 笔记中提及，提供XX套餐/案例
     *
     * @param showRelevantHint
     */
    public void setShowRelevantHint(boolean showRelevantHint) {
        tvRelevantHint.setVisibility(showRelevantHint ? View.VISIBLE : View.GONE);
    }

    public void setOnGetSimilarListener(
            OnGetSimilarListener onGetSimilarListener) {
        this.onGetSimilarListener = onGetSimilarListener;
    }
}
