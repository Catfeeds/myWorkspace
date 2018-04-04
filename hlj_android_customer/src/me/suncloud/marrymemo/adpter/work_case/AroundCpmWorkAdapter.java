package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.work_case.AroundCpmWorkFragment;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * Created by luohanlin on 2017/12/8.
 */

public class AroundCpmWorkAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<Work> works;
    private OnItemClickListener onItemClickListener;
    public static final int TYPE_ITEM = 1;

    private int imgWidth;
    private int imgHeight;
    private int ruleWidth;
    private int ruleHeight;

    public AroundCpmWorkAdapter(
            Context context, List<Work> works) {
        this.context = context;
        this.works = works;
        imgWidth = CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 32);
        imgHeight = Math.round(imgWidth * 10.0f / 16.0f);
        this.ruleWidth = CommonUtil.dp2px(context, 120);
        this.ruleHeight = CommonUtil.dp2px(context, 100);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.around_cpm_item_layout, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof WorkViewHolder) {
            ((WorkViewHolder) holder).setView(context,
                    works.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return works == null ? 0 : works.size();
    }

    class WorkViewHolder extends TrackerWorkViewHolder {
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;
        @BindView(R.id.img_badge)
        ImageView imgBadge;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_area_name)
        TextView tvAreaName;
        @BindView(R.id.area_layout)
        LinearLayout areaLayout;
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        View view;

        WorkViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            imgCover.getLayoutParams().height = imgHeight;
        }

        @Override
        public String cpmSource() {
            return AroundCpmWorkFragment.CPM_SOURCE;
        }

        @Override
        public View trackerView() {
            return view;
        }

        @Override
        protected void setViewData(Context mContext, final Work item, int position, int viewType) {
            if (item == null) {
                return;
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WorkActivity.class);
                    intent.putExtra("id", item.getId());
                    context.startActivity(intent);
                }
            });

            Glide.with(context)
                    .load(ImagePath.buildPath(item.getCoverPath())
                            .width(imgWidth)
                            .height(imgHeight)
                            .path())
                    .into(imgCover);

            tvTitle.setText(item.getTitle());
            setMerchant(item);
            tvPrice.setText(context.getString(R.string.label_price,
                    CommonUtil.formatDouble2String(item.getShowPrice())));
            WorkRule rule = item.getRule();
            if (rule == null || TextUtils.isEmpty(rule.getBigImg())) {
                imgBadge.setVisibility(View.GONE);
            } else {
                imgBadge.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(ImagePath.buildPath(rule.getBigImg())
                                .width(ruleWidth)
                                .height(ruleHeight)
                                .cropPath())
                        .into(imgBadge);
            }
        }

        private void setMerchant(Work work) {
            Merchant merchant = work.getMerchant();
            if (merchant == null) {
                return;
            }
            tvMerchantName.setText(merchant.getName());
            if (TextUtils.isEmpty(merchant.getCityName())) {
                areaLayout.setVisibility(View.GONE);
            } else {
                areaLayout.setVisibility(View.VISIBLE);
                tvAreaName.setText(merchant.getCityName());
            }
        }
    }
}
