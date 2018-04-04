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
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkCaseViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.CaseDetailActivity;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * 发现页 套餐案例
 * Created by chen_bin on 2018/2/2 0002.
 */
public class FinderRecommendWorkAndCaseViewHolder extends TrackerWorkCaseViewHolder {

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
    @BindView(R.id.tv_show_price)
    TextView tvShowPrice;
    @BindView(R.id.tv_mark)
    TextView tvMark;
    @BindView(R.id.tv_collect_count)
    TextView tvCollectCount;

    private int coverWidth;
    private int coverHeight;

    public final static int STYLE_RATIO_1_TO_1 = 0; //封面1:1比例
    public final static int STYLE_RATIO_3_TO_4 = 1; //封面3:4比例
    public final static int STYLE_RATIO_4_TO_3 = 2; //封面4:3比例

    private OnGetSimilarListener onGetSimilarListener;

    @Override
    public String cpmSource() {
        return "find_page_recommands";
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    public FinderRecommendWorkAndCaseViewHolder(View itemView, int style) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        coverWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - (CommonUtil.dp2px
                (itemView.getContext(),
                28))) / 2;
        switch (style) {
            case STYLE_RATIO_4_TO_3:
                coverHeight = Math.round(coverWidth * NoteMedia.RATIO_3_TO_4);
                break;
            case STYLE_RATIO_3_TO_4:
                coverHeight = Math.round(coverWidth * NoteMedia.RATIO_4_TO_3);
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
                Work work = getItem();
                if (work == null) {
                    return;
                }
                Intent intent;
                if (work.getCommodityType() == Work.COMMODITY_TYPE_WORK) {
                    intent = new Intent(v.getContext(), WorkActivity.class);
                    intent.putExtra(WorkActivity.ARG_ID, work.getId());
                } else {
                    intent = new Intent(v.getContext(), CaseDetailActivity.class);
                    intent.putExtra(CaseDetailActivity.ARG_ID, work.getId());
                }
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
    protected void setViewData(Context mContext, Work work, int position, int viewType) {
        if (work == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(work.getCoverPath())
                        .width(coverWidth)
                        .height(coverHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(work.getTitle());
        if (work.getCommodityType() == Work.COMMODITY_TYPE_WORK) { //套餐
            tvShowPrice.setVisibility(View.VISIBLE);
            tvShowPrice.setText(mContext.getString(R.string.label_price3,
                    CommonUtil.formatDouble2StringWithTwoFloat(work.getShowPrice())));
            tvMark.setVisibility(View.GONE);
        } else {
            tvShowPrice.setVisibility(View.GONE);
            if (CommonUtil.isCollectionEmpty(work.getMarks())) {
                tvMark.setVisibility(View.GONE);
            } else {
                tvMark.setVisibility(View.VISIBLE);
                tvMark.setText(work.getMarks()
                        .get(0)
                        .getName());
            }
        }
        tvCollectCount.setText(String.valueOf(work.getCollectorsCount()));
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
        if (!showRelevantHint) {
            tvRelevantHint.setVisibility(View.GONE);
        } else {
            tvRelevantHint.setVisibility(View.VISIBLE);
            Merchant merchant = getItem().getMerchant();
            if (getItem().getCommodityType() == Work.COMMODITY_TYPE_WORK) {
                tvRelevantHint.setText("提供" + merchant.getActiveWorkCount() + "个套餐");
            } else {
                tvRelevantHint.setText("提供" + merchant.getActiveCaseCount() + "个案例");
            }
        }
    }

    public void setOnGetSimilarListener(OnGetSimilarListener onGetSimilarListener) {
        this.onGetSimilarListener = onGetSimilarListener;
    }

}