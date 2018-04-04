package me.suncloud.marrymemo.adpter.work_case;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;

import org.json.JSONObject;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * 商家套餐列表适配器
 * Created by chen_bin on 2016/11/11 0011.
 */

public class MerchantWorkRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<Work> works;
    private LayoutInflater inflater;
    private static final int ITEM_TYPE_LIST = 0;
    private static final int ITEM_TYPE_FOOTER = 1;
    private int style; //套餐通用样式类型

    public MerchantWorkRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setWorks(List<Work> works) {
        this.works = works;
        notifyDataSetChanged();
    }

    public void addWorks(List<Work> works) {
        if (!CommonUtil.isCollectionEmpty(works)) {
            int start = getItemCount() - getFooterViewCount();
            this.works.addAll(works);
            notifyItemRangeInserted(start, works.size());
            if (start > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(works);
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
                SmallWorkViewHolder holder = new SmallWorkViewHolder(inflater.inflate(R.layout
                                .small_common_work_item___cv,
                        parent,
                        false));
                holder.setStyle(style);
                holder.setOnItemClickListener(new OnItemClickListener<Work>() {
                    @Override
                    public void onItemClick(int position, Work work) {
                        if (work != null && work.getId() > 0) {
                            Intent intent = new Intent(context, WorkActivity.class);
                            JSONObject jsonObject = TrackerUtil.getSiteJson("S2/A1",
                                    works.indexOf(work) + 1,
                                    "套餐" + work.getId() + work.getTitle());
                            if (jsonObject != null) {
                                intent.putExtra("site", jsonObject.toString());
                            }
                            intent.putExtra("id", work.getId());
                            context.startActivity(intent);
                        }
                    }
                });
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof SmallWorkViewHolder) {
                    SmallWorkViewHolder workViewHolder = (SmallWorkViewHolder) holder;
                    workViewHolder.setShowBottomThinLineView(position < works.size() - 1);
                    workViewHolder.setView(context, works.get(position), position, viewType);
                }
                break;
        }
    }
}
