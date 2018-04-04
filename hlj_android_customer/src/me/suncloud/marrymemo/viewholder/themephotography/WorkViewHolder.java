package me.suncloud.marrymemo.viewholder.themephotography;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigWorkViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.themephotography.ThemeWorkListActivity;

/**
 * Created by jinxin on 2016/10/13.
 */

public class WorkViewHolder extends RecyclerView.ViewHolder {
    public View itemView;
    public int workImgWidth;
    public int topPadding;
    public int leftPadding;
    public Context mContext;
    public Point point;
    public DisplayMetrics dm;
    private JourneyTheme theme;
    private String cpmSourceOfType;
    private int themeType;

    public static final String HOT_UNIT_CITY_CPM_SOURCE = "hot_unit_city_lv_pai";

    @BindView(R.id.tv_all_info)
    public TextView tvAllInfo;
    @BindView(R.id.iv_arrow_right)
    public ImageView ivArrowRight;
    @BindView(R.id.layout_work)
    public LinearLayout layoutWork;
    @BindView(R.id.layout_title)
    public LinearLayout layoutTitle;

    public WorkViewHolder(
            View itemView, Context context, JourneyTheme theme, int type) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
        this.theme = theme;
        this.themeType = type;
        mContext = context;
        point = JSONUtil.getDeviceSize(mContext);
        dm = mContext.getResources()
                .getDisplayMetrics();
        workImgWidth = Math.round(dm.density * 116);
        topPadding = Math.round(dm.density * 10);
        leftPadding = Math.round(dm.density * 12);
        List<Work> works = theme.getWorks();
        if (works.size() < 8) {
            ivArrowRight.setVisibility(View.GONE);
            tvAllInfo.setVisibility(View.GONE);
        }
        if (themeType == Constants.THEME_TYPE.UNIT) {
            cpmSourceOfType = HOT_UNIT_CITY_CPM_SOURCE;
        }
    }

    public void setWork(Work work, int collectPosition, int size, boolean showHeader) {
        layoutTitle.setVisibility(showHeader ? View.VISIBLE : View.GONE);
        if (work == null || work.getId() == 0) {
            layoutWork.setVisibility(View.GONE);
            return;
        } else {
            layoutWork.setVisibility(View.VISIBLE);
        }
        CityCpmBigWorkViewHolder holder = (CityCpmBigWorkViewHolder) layoutWork.getTag();
        if (holder == null) {
            holder = new CityCpmBigWorkViewHolder(layoutWork);
            layoutWork.setTag(holder);
        }
        holder.setShowBottomLineView(collectPosition < size - 1);
        holder.setView(mContext, work, collectPosition, 0);
        holder.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object object) {
                if (object != null) {
                    Work work = (Work) object;
                    String link = work.getLink();
                    if (JSONUtil.isEmpty(link)) {
                        Intent intent = new Intent(mContext, WorkActivity.class);
                        intent.putExtra("id", work.getId());
                        mContext.startActivity(intent);
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else {
                        HljWeb.startWebView((Activity) mContext, link);
                    }
                }
            }
        });
    }

    @OnClick({R.id.tv_all_info, R.id.iv_arrow_right})
    public void onClick() {
        Intent intent = new Intent(mContext, ThemeWorkListActivity.class);
        intent.putExtra("id", theme.getId());
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    class CityCpmBigWorkViewHolder extends BigWorkViewHolder {
        public CityCpmBigWorkViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public String cpmSource() {
            return cpmSourceOfType;
        }
    }
}

