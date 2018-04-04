package com.hunliji.hljcommonlibrary.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.R;
import com.hunliji.hljcommonlibrary.models.realm.CrashInfo;
import com.hunliji.hljcommonlibrary.views.activities.CrashInfoActivity;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by wangtao on 2017/1/21.
 */

public class CrashRecyclerAdapter extends RecyclerView.Adapter<CrashRecyclerAdapter
        .CrashViewHolder> {

    private List<CrashInfo> crashs;

    public void setCrashs(List<CrashInfo> crashs) {
        this.crashs = crashs;
        notifyDataSetChanged();
    }

    @Override
    public CrashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CrashViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.crash_item___cm, parent, false));
    }

    @Override
    public void onBindViewHolder(CrashViewHolder holder, int position) {
        holder.setViewData(crashs.get(position));
    }

    @Override
    public int getItemCount() {
        return crashs==null?0:crashs.size();
    }

    class CrashViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVersion;
        private TextView tvTime;
        private TextView tvInfo;
        private CrashInfo crashInfo;

        CrashViewHolder(View itemView) {
            super(itemView);
            tvVersion = (TextView) itemView.findViewById(R.id.tv_version);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_info);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CrashInfoActivity.class);
                    intent.putExtra("info", crashInfo.getMsg());
                    v.getContext()
                            .startActivity(intent);
                }
            });
        }

        public void setViewData(CrashInfo crashInfo) {
            this.crashInfo = crashInfo;
            tvVersion.setText(crashInfo.getAppVersion());
            tvTime.setText(new DateTime(crashInfo.getTime()).toString("yyyy-MM-dd HH:mm:ss"));
            tvInfo.setText(crashInfo.getMsg());
        }
    }
}
