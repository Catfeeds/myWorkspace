package com.hunliji.hljdebuglibrary.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.realm.HttpLogBlock;
import com.hunliji.hljdebuglibrary.R;
import com.hunliji.hljdebuglibrary.R2;
import com.hunliji.hljdebuglibrary.views.JsonViewerActivity;
import com.hunliji.hljhttplibrary.models.InterceptorLogger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by luohanlin on 2017/9/25.
 */

public class HttpLogsAdapter extends RecyclerView.Adapter<HttpLogsAdapter.HttpViewHolder> {

    private Context context;
    private List<HttpLogBlock> blockList;

    public HttpLogsAdapter(Context context, List<HttpLogBlock> blockList) {
        this.context = context;
        this.blockList = blockList;
    }

    @Override
    public HttpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.http_log_item, parent, false);

        return new HttpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HttpViewHolder holder, int position) {
        holder.setViewData(blockList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return blockList == null ? 0 : blockList.size();
    }

    class HttpViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_title)
        TextView tvTitle;

        HttpViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setViewData(final HttpLogBlock block, final int position) {
            if (block == null) {
                return;
            }

            final String[] strArray = block.getMessage()
                    .split(InterceptorLogger.LINE_BREAK);

            String title = !TextUtils.isEmpty(strArray[0]) ? strArray[0] : strArray[1];
            title = title.replace("http/1.1", "");
            tvTitle.setText(title);

            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, JsonViewerActivity.class);
                    intent.putExtra("msg", block.getMessage());
                    context.startActivity(intent);
                }
            });
        }
    }
}
