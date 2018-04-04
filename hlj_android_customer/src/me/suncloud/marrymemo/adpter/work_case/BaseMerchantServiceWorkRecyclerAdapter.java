package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

/**
 * Created by chen_bin on 2017/8/2 0002.
 */
public class BaseMerchantServiceWorkRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    protected Context context;
    protected View footerView;
    protected View cpmView;
    protected List<Work> works;
    protected LayoutInflater inflater;
    protected int cpmRandomIndex = 1; // 1~3的随机数
    protected static final int ITEM_TYPE_LIST = 0;
    protected static final int ITEM_TYPE_FOOTER = 1;
    protected static final int ITEM_TYPE_CPM = 2;

    public BaseMerchantServiceWorkRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setWorks(List<Work> works) {
        this.works = works;
        int worksSize = CommonUtil.getCollectionSize(this.works);
        cpmRandomIndex = 1 + (int) (Math.random() * (worksSize < 3 ? worksSize : 3));
        //1~3的随机数
        notifyDataSetChanged();
    }

    public void addWorks(List<Work> works) {
        if (!CommonUtil.isCollectionEmpty(works)) {
            int start = getItemCount() - getFooterViewCount();
            this.works.addAll(works);
            notifyItemRangeInserted(start, works.size());
        }
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public int getCpmViewCount() {
        return !CommonUtil.isCollectionEmpty(works) && cpmView != null ? 1 : 0;
    }

    public void setCpmView(View cpmView) {
        this.cpmView = cpmView;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + getCpmViewCount() + CommonUtil.getCollectionSize(works);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else if (getCpmViewCount() > 0 && position == cpmRandomIndex) {
            return ITEM_TYPE_CPM;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_TYPE_CPM:
                return new ExtraBaseViewHolder(cpmView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, getItem(position), position, viewType);
                break;
        }
    }

    public Work getItem(int position) {
        int index = position;
        if (getCpmViewCount() > 0 && position > cpmRandomIndex) {
            index = index - 1;
        }
        return works.get(index);
    }

}
