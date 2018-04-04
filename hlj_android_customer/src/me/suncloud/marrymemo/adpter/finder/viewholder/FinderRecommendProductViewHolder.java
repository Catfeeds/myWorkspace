package me.suncloud.marrymemo.adpter.finder.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.interfaces.OnGetSimilarListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerProductViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * 发现页推荐流-婚品cell
 * Created by chen_bin on 2018/2/2 0002.
 */
public class FinderRecommendProductViewHolder extends TrackerProductViewHolder {

    @BindView(R.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R.id.mask_view)
    View maskView;
    @BindView(R.id.btn_get_similar)
    ImageButton btnGetSimilar;
    @BindView(R.id.tv_relevant_hint)
    TextView tvRelevantHint;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_slogan)
    TextView tvSlogan;
    @BindView(R.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R.id.tv_collect_count)
    TextView tvCollectCount;

    private int coverWidth;
    private int coverHeight;

    private OnGetSimilarListener onGetSimilarListener;

    public final static int STYLE_RATIO_1_TO_1 = 0; //封面1:1比例
    public final static int STYLE_RATIO_2_TO_3 = 1; //封面1:1.5比例

    @Override
    public View trackerView() {
        return itemView;
    }

    public FinderRecommendProductViewHolder(View itemView, int style) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        coverWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px
                (itemView.getContext(),
                28)) / 2;
        switch (style) {
            case STYLE_RATIO_2_TO_3:
                coverHeight = Math.round(coverWidth * 3.0f / 2.0f);
                break;
            default:
                coverHeight = coverWidth;
                break;
        }
        imgCover.getLayoutParams().height = coverHeight;
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
                ShopProduct product = getItem();
                if (product == null) {
                    return;
                }
                Intent intent = new Intent(v.getContext(), ShopProductDetailActivity.class);
                intent.putExtra(ShopProductDetailActivity.ARG_ID, product.getId());
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
    protected void setViewData(Context mContext, ShopProduct product, int position, int viewType) {
        if (product == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(product.getCoverPath())
                        .width(coverWidth)
                        .height(coverHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(product.getTitle());
        if (CommonUtil.isCollectionEmpty(product.getSlogans())) {
            tvSlogan.setVisibility(View.GONE);
        } else {
            tvSlogan.setVisibility(View.VISIBLE);
            tvSlogan.setText(product.getSlogans()
                    .get(0));
        }
        tvShowPrice.setText(mContext.getString(R.string.label_price, product.getShowPrice()));
        tvCollectCount.setText(String.valueOf(product.getCollectCount()));
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
     */
    public void setShowRelevantHint(boolean showRelevantHint) {
        tvRelevantHint.setVisibility(showRelevantHint ? View.VISIBLE : View.GONE);
    }

    public void setOnGetSimilarListener(
            OnGetSimilarListener onGetSimilarListener) {
        this.onGetSimilarListener = onGetSimilarListener;
    }

}
