package com.hunliji.hljdebuglibrary.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljdebuglibrary.R;
import com.hunliji.hljdebuglibrary.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luohanlin on 2017/9/25.
 */

public class ActivityNameAdapter extends RecyclerView.Adapter<ActivityNameAdapter.ClassNameViewHolder> {

    private List<String> classNames;

    public ActivityNameAdapter(List<String> classNames) {
        this.classNames = classNames;
    }

    @Override
    public ClassNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_name_item, parent, false);

        return new ClassNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassNameViewHolder holder, int position) {
        holder.setViewData(classNames.get(position), position);
    }

    @Override
    public int getItemCount() {
        return classNames == null ? 0 : classNames.size();
    }

    class ClassNameViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_title)
        TextView tvTitle;

        ClassNameViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setViewData(final String className, final int position) {
            tvTitle.setText(className);
        }
    }
}
