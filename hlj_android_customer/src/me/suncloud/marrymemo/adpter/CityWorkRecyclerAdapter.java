package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * Created by Suncloud on 2015/10/30.
 */
public class CityWorkRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder<Work>> {

    private int width;
    private int badgeWidth;
    private ArrayList<Work> works;
    private Context context;

    public CityWorkRecyclerAdapter(Context context) {
        this.context = context;
        DisplayMetrics dm = context.getResources()
                .getDisplayMetrics();
        width = Math.round(dm.density * 136);
        badgeWidth = Math.round(dm.density * 60);
        works = new ArrayList<>();
    }

    public void setWorks(ArrayList<Work> works) {
        if (works != null) {
            this.works.clear();
            this.works.addAll(works);
            notifyDataSetChanged();
        }
    }

    @Override
    public BaseViewHolder<Work> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.relative_city_work_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Work> holder, int position) {
        if (holder instanceof WorkViewHolder) {
            holder.setView(context, works.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return works.size();
    }


    private class WorkViewHolder extends BaseViewHolder<Work> {
        private ImageView cover;
        private ImageView badge;
        private TextView title;
        private TextView price;
        private TextView kind;

        public WorkViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            badge = (ImageView) itemView.findViewById(R.id.badge);
            title = (TextView) itemView.findViewById(R.id.title);
            price = (TextView) itemView.findViewById(R.id.price);
            kind = (TextView) itemView.findViewById(R.id.kind);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getItem()==null){
                        return;
                    }
                    Intent intent = new Intent(v.getContext(), WorkActivity.class);
                    intent.putExtra("id", getItem().getId());
                    v.getContext().startActivity(intent);
                    if(v.getContext() instanceof Activity) {
                        ((Activity) v.getContext()).overridePendingTransition(R.anim.slide_in_right, R
                                .anim.activity_anim_default);
                    }
                }
            });
        }

        @Override
        protected void setViewData(Context mContext, Work work, int position, int viewType) {
            title.setText(work.getTitle());
            price.setText(Util.formatDouble2String(work.getShowPrice()));
            kind.setText(work.getKind());
            String path = JSONUtil.getImagePath(work.getCoverPath(), width);
            if (!JSONUtil.isEmpty(path)) {
                if (!path.equals(cover.getTag())) {
                    cover.setTag(path);
                    ImageLoadTask task = new ImageLoadTask(cover);
                    task.loadImage(path,
                            width,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(mContext.getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                }
            } else {
                cover.setImageBitmap(null);
                cover.setTag(null);
            }
            if (work.getRule() != null && work.getRule()
                    .getId() > 0 && !JSONUtil.isEmpty(work.getRule()
                    .getShowimg())) {
                badge.setVisibility(View.VISIBLE);
                String badgePath = JSONUtil.getImagePath(work.getRule()
                        .getShowimg(), badgeWidth);
                if (!badgePath.equals(badge.getTag())) {
                    badge.setVisibility(View.VISIBLE);
                    ImageLoadTask task = new ImageLoadTask(badge, null, null, 0, true, true);
                    badge.setTag(badgePath);
                    task.loadImage(badgePath,
                            badgeWidth,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(mContext.getResources(),
                                    R.mipmap.icon_empty_image,
                                    task));
                }
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }
}
