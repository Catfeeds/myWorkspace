package me.suncloud.marrymemo.adpter.finder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.MerchantProperty;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.viewholder.UserPrepareCategoryViewHolder;
import me.suncloud.marrymemo.model.finder.UserPrepareCategory;

/**
 * 获取备婚阶段
 * Created by chen_bin on 2017/10/17 0017.
 */
public class UserPrepareCategoryListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private List<UserPrepareCategory> categories;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public UserPrepareCategoryListAdapter(
            Context context, List<UserPrepareCategory> categories) {
        this.context = context;
        this.categories = categories;
        this.inflater = LayoutInflater.from(context);
    }

    public List<UserPrepareCategory> getCategories() {
        return categories;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(categories);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserPrepareCategoryViewHolder categoryViewHolder = new UserPrepareCategoryViewHolder(
                inflater.inflate(R.layout.user_prepare_category_list_item, parent, false));
        categoryViewHolder.setOnItemClickListener(onItemClickListener);
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, categories.get(position), position, getItemViewType(position));
    }
}
