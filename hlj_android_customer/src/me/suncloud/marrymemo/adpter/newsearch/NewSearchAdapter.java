package me.suncloud.marrymemo.adpter.newsearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.search.TipSearchType;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.newsearch.viewholder.SearchMerchantTipViewHolder;
import me.suncloud.marrymemo.adpter.newsearch.viewholder.SearchNormalTipViewHolder;
import me.suncloud.marrymemo.fragment.newsearch.OnSearchItemClickListener;

/**
 * Created by hua_rong on 2018/1/8
 * 下拉提示弹窗
 */

public class NewSearchAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private String keyword;
    private static final int TYPE_CPM_MERCHANT = 1;
    private static final int TYPE_ITEM = 2;
    private OnSearchItemClickListener onSearchItemClickListener;

    private ArrayList<Object> objects = new ArrayList<>();


    public NewSearchAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setObjects(
            List<TipSearchType> eCommerces,
            List<Merchant> cpmMerchants,
            List<TipSearchType> contents) {
        if (!objects.isEmpty()) {
            objects.clear();
        }
        if (!CommonUtil.isCollectionEmpty(eCommerces)) {
            objects.addAll(eCommerces);
        }
        int cpmSize = CommonUtil.getCollectionSize(cpmMerchants);
        if (objects.size() > 2) {
            if (cpmSize > 0 && cpmMerchants.get(0) != null) {
                objects.add(2, cpmMerchants.get(0));
            }
            if (cpmSize > 1 && cpmMerchants.get(1) != null) {
                objects.add(3, cpmMerchants.get(1));
            }
        } else {
            if (cpmSize > 0 && cpmMerchants.get(0) != null) {
                objects.add(cpmMerchants.get(0));
            }
            if (cpmSize > 1 && cpmMerchants.get(1) != null) {
                objects.add(cpmMerchants.get(1));
            }
        }
        if (!CommonUtil.isCollectionEmpty(contents)) {
            objects.addAll(contents);
        }
        notifyDataSetChanged();
    }

    public void clearObject() {
        if (!CommonUtil.isCollectionEmpty(objects)) {
            objects.clear();
            notifyDataSetChanged();
        }
    }


    public ArrayList<Object> getObjects() {
        return objects;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.new_search_item_layout, parent, false);
        switch (viewType) {
            case TYPE_CPM_MERCHANT:
                return new SearchMerchantTipViewHolder(view);
            default:
                return new SearchNormalTipViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof SearchNormalTipViewHolder) {
            SearchNormalTipViewHolder viewHolder = (SearchNormalTipViewHolder) holder;
            viewHolder.setKeyword(keyword);
            viewHolder.setOnSearchItemClickListener(onSearchItemClickListener);
            viewHolder.setView(context,
                    (TipSearchType) objects.get(position), position, getItemViewType(position));
            viewHolder.isLastLine(position == getItemCount() - 1);
        }else if (holder instanceof SearchMerchantTipViewHolder){
            SearchMerchantTipViewHolder viewHolder = (SearchMerchantTipViewHolder) holder;
            viewHolder.setKeyword(keyword);
            viewHolder.setOnSearchItemClickListener(onSearchItemClickListener);
            viewHolder.setView(context, (Merchant) objects.get(position), position, getItemViewType(position));
            viewHolder.isLastLine(position == getItemCount() - 1);
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    @Override
    public int getItemViewType(int position) {
        Object object = objects.get(position);
        if (object instanceof Merchant) {
            return TYPE_CPM_MERCHANT;
        }
        return TYPE_ITEM;
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener onSearchItemClickListener) {
        this.onSearchItemClickListener = onSearchItemClickListener;
    }

}
