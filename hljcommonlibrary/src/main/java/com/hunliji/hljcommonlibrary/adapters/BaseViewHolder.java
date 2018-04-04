package com.hunliji.hljcommonlibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTrackerParameter;
import com.hunliji.hljcommonlibrary.view_tracker.models.TrackerView;

import java.util.List;

/**
 * Created by werther on 16/7/27.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private T item;
    private int itemPosition;


    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void setView(Context mContext, T item, int position, int viewType) {
        this.itemPosition = position;
        this.item = item;
        try {
            setViewData(mContext, item, position, viewType);
            initTracker(itemView);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initTracker(View view) {
        try {
            List<TrackerView> trackerViews = HljTrackerParameter.INSTANCE.getViews(this.getClass()
                    .getName());
            if (CommonUtil.isCollectionEmpty(trackerViews)) {
                return;
            }
            for (TrackerView trackerView : trackerViews) {
                View v = view;
                if (!TextUtils.isEmpty(trackerView.getId())) {
                    v = view.findViewById(view.getContext()
                            .getResources()
                            .getIdentifier(trackerView.getId(),
                                    "id",
                                    view.getContext()
                                            .getPackageName()));
                }
                if (v != null) {
                    trackerView.tag(v, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T getItem() {
        return item;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    protected abstract void setViewData(Context mContext, T item, int position, int viewType);

}

