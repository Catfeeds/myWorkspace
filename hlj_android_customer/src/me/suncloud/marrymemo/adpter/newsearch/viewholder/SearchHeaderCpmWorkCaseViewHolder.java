package me.suncloud.marrymemo.adpter.newsearch.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkMediaItem;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkCaseViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.newsearch.NewSearchResultActivity;

/**
 * Created by hua_rong on 2018/1/9
 * 套餐结果页面 当店铺cpm为null时第一个position用一般cpm代替显示
 */

public class SearchHeaderCpmWorkCaseViewHolder extends TrackerWorkCaseViewHolder {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_cpm1)
    ImageView ivCpm1;
    @BindView(R.id.iv_cpm2)
    ImageView ivCpm2;
    @BindView(R.id.iv_cpm3)
    ImageView ivCpm3;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_place)
    TextView tvPlace;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.ll_item)
    LinearLayout llItem;
    @BindView(R.id.view_bottom)
    View viewBottom;
    @BindView(R.id.ll_price)
    LinearLayout llPrice;
    @BindView(R.id.ll_merchant)
    LinearLayout llMerchant;

    private OnItemClickListener itemClickListener;

    private int width;
    private int height;

    public SearchHeaderCpmWorkCaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        Context context = itemView.getContext();
        int screenWidth = CommonUtil.getDeviceSize(context).x;
        width = Math.round((screenWidth - CommonUtil.dp2px(context, 36)) / 3);
        height = Math.round(width * 3 / 4);
        ivCpm1.getLayoutParams().width = width;
        ivCpm2.getLayoutParams().width = width;
        ivCpm3.getLayoutParams().width = width;
        ivCpm1.getLayoutParams().height = height;
        ivCpm2.getLayoutParams().height = height;
        ivCpm3.getLayoutParams().height = height;
        llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem() != null && itemClickListener != null) {
                    itemClickListener.onItemClick(getItemPosition(), getItem());
                }
            }
        });
        ivLogo.getLayoutParams().width = CommonUtil.dp2px(context,23);
        ivLogo.getLayoutParams().height = CommonUtil.dp2px(context,23);
    }

    @Override
    public View trackerView() {
        return llItem;
    }

    @Override
    public String cpmSource() {
        return NewSearchResultActivity.CPM_SOURCE;
    }

    @Override
    protected void setViewData(
            Context context, Work work, final int position, int viewType) {
        if (work != null) {
            itemView.setVisibility(View.VISIBLE);
            List<WorkMediaItem> mediaItems = work.getMediaItems();
            tvTitle.setText(work.getTitle());
            if (CommonUtil.getCollectionSize(mediaItems) > 0) {
                Glide.with(context)
                        .load(ImagePath.buildPath(mediaItems.get(0)
                                .getItemCover())
                                .width(width)
                                .height(height)
                                .cropPath())
                        .into(ivCpm1);
            }
            if (CommonUtil.getCollectionSize(mediaItems) > 1) {
                Glide.with(context)
                        .load(ImagePath.buildPath(mediaItems.get(1)
                                .getItemCover())
                                .width(width)
                                .height(height)
                                .cropPath())
                        .into(ivCpm2);
            }
            if (CommonUtil.getCollectionSize(mediaItems) > 2) {
                Glide.with(context)
                        .load(ImagePath.buildPath(mediaItems.get(2)
                                .getItemCover())
                                .width(width)
                                .height(height)
                                .cropPath())
                        .into(ivCpm3);
            }
            Merchant merchant = work.getMerchant();
            if (merchant != null) {
                llMerchant.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(ImagePath.buildPath(merchant.getLogoPath())
                                .width(CommonUtil.dp2px(context, 23))
                                .height(CommonUtil.dp2px(context, 23))
                                .cropPath())
                        .into(ivLogo);
                tvName.setText(merchant.getName());
                tvPlace.setText(merchant.getShopArea()
                        .getName());
            } else {
                llMerchant.setVisibility(View.GONE);
            }
            if (work.getShowPrice() > 0) {
                llPrice.setVisibility(View.VISIBLE);
                tvPrice.setText(CommonUtil.formatDouble2String(work.getShowPrice()));
            } else {
                llPrice.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 当推荐的cpm只有一条时，隐藏底部间距
     */
    public void hideViewBottom() {
        viewBottom.setVisibility(View.GONE);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
