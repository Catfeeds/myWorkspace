package com.hunliji.hljvideolibrary.view;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljvideolibrary.R;
import com.hunliji.hljvideolibrary.R2;
import com.hunliji.hljvideolibrary.interfaces.VideoUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luohanlin on 2017/7/7.
 */

public class TimelineAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private static int imageSize;
    private static Uri uri;
    private static long totalCount;
    private static long interval;

    TimelineAdapter(
            Context context, int size, Uri uri, long count, long interval) {
        TimelineAdapter.uri = uri;
        this.context = context;
        this.inflater = LayoutInflater.from(this.context);
        imageSize = size;
        totalCount = count;
        TimelineAdapter.interval = interval;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.time_line_item, parent, false);
        return new ViewHolder(view, imageSize);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, null, position, 0);
    }

    @Override
    public int getItemCount() {
        return (int) totalCount;
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R2.id.image_view)
        ImageView imageView;
        @BindView(R2.id.tv_index)
        TextView tvIndex;

        ViewHolder(View view, int size) {
            super(view);
            ButterKnife.bind(this, view);
            imageView.getLayoutParams().width = size;
            imageView.getLayoutParams().height = size;
        }

        @Override
        protected void setViewData(Context mContext, Object item, int position, int viewType) {
            tvIndex.setText(String.valueOf(position));
            if (position == 0) {
                tvIndex.setText(String.valueOf(totalCount));
            }
            tvIndex.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions();
            long frame = position * interval;
            options.frame(frame);
            options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            options.centerCrop();
            options.override(imageSize, imageSize);

            Glide.with(mContext)
                    .asBitmap()
                    .load(uri)
                    .apply(options)
                    .into(imageView);
        }
    }
}
