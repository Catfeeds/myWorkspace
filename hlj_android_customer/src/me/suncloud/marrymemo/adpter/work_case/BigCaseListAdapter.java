package me.suncloud.marrymemo.adpter.work_case;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigCaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.CaseDetailActivity;

/**
 * Created by wangtao on 2016/12/9.
 */

public class BigCaseListAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        OnItemClickListener<Work> {

    private Context context;
    private View footerView;
    private List<Work> works;
    public static final int ITEM = 1;
    public static final int FOOTER = -1;

    public BigCaseListAdapter(
            Context context, View footerView) {
        this.context = context;
        this.footerView = footerView;
    }

    public void setCases(List<Work> cases) {
        this.works = cases;
        notifyDataSetChanged();
    }

    public void clear() {
        if (works != null) {
            works.clear();
        }
        notifyDataSetChanged();
    }

    public void addCases(List<Work> cases) {
        if (works == null) {
            works = new ArrayList<>();
        }
        int start = works.size();
        works.addAll(cases);
        notifyItemRangeInserted(start, cases.size());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                BigCaseViewHolder holder = new BigCaseViewHolder(BigCaseViewHolder.getView(parent));
                holder.setOnItemClickListener(this);
                return holder;
            case FOOTER:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case ITEM:
                holder.setView(context, works.get(position), position, type);
                break;
        }
    }

    @Override
    public int getItemCount() {
        int size = works == null ? 0 : works.size();
        if (size > 0 && footerView != null) {
            size++;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (works != null && works.size() > position) {
            return ITEM;
        }
        return FOOTER;
    }

    @Override
    public void onItemClick(int position, Work work) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, CaseDetailActivity.class);
        intent.putExtra("id", work.getId());
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }
}
