package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.CaseDetailActivity;

/**
 * 商家案例列表适配器
 * Created by chen_bin on 2016/11/15 0015.
 */

public class MerchantCaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<Work> cases;
    private LayoutInflater inflater;
    private static final int ITEM_TYPE_LIST = 0;
    private static final int ITEM_TYPE_FOOTER = 1;
    private int imageWidth;
    private int imageHeight;

    public MerchantCaseRecyclerAdapter(Context context) {
        this.context = context;
        this.imageWidth = CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 32);
        this.imageHeight = Math.round(imageWidth * 10.0f / 16.0f);
        this.inflater = LayoutInflater.from(context);
    }

    public void setCases(List<Work> cases) {
        this.cases = cases;
        notifyDataSetChanged();
    }

    public void addCases(List<Work> cases) {
        if (!CommonUtil.isCollectionEmpty(cases)) {
            int start = getItemCount() - getFooterViewCount();
            this.cases.addAll(cases);
            notifyItemRangeInserted(start, cases.size());
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(cases);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new CaseViewHolder(inflater.inflate(R.layout.merchant_case_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, cases.get(position), position, viewType);
                break;
        }
    }

    public class CaseViewHolder extends BaseViewHolder<Work> {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_collect_count)
        TextView tvCollectCount;

        CaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imgCover.getLayoutParams().width = imageWidth;
            imgCover.getLayoutParams().height = imageHeight;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work work = getItem();
                    if (work != null && work.getId() > 0) {
                        Intent intent = new Intent(context, CaseDetailActivity.class);
                        JSONObject jsonObject = TrackerUtil.getSiteJson("S2/A1",
                                getAdapterPosition() + 1,
                                "案例" + work.getId() + work.getTitle());
                        if (jsonObject != null) {
                            intent.putExtra("site", jsonObject.toString());
                        }
                        intent.putExtra("id", work.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, final Work item, final int position, int viewType) {
            if (item == null) {
                return;
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(item.getCoverPath())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
            tvTitle.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item.getTitle());
            tvCollectCount.setText(String.valueOf(item.getCollectorsCount()));
            tvCollectCount.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable
                            (context,
                    item.isCollected() ? R.drawable.icon_star_white_28_28_selected : R.drawable
                            .icon_star_white_28_28_nomal),
                    null,
                    null,
                    null);
        }
    }
}
