package com.hunliji.hljdebuglibrary.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.hunliji.hljdebuglibrary.R;

import java.util.List;

/**
 * Created by wangtao on 2018/3/1.
 */

public class TrackerLogsAdapter extends RecyclerView.Adapter<TrackerLogsAdapter.TrackerLogViewHolder> {

    private List<String> logs;

    public TrackerLogsAdapter(List<String> logs) {
        this.logs = logs;
    }

    @Override
    public TrackerLogViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        return new TrackerLogViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(TrackerLogViewHolder holder, int position) {
        holder.setLog(logs.get(getItemCount()-1-position));
    }

    @Override
    public int getItemCount() {
        return logs==null?0:logs.size();
    }


    class TrackerLogViewHolder extends RecyclerView.ViewHolder{

        private TextView tvText;

        TrackerLogViewHolder(ViewGroup parent) {
            this(LayoutInflater.from(parent.getContext()).inflate(R.layout.tracker_log_item,parent,false));
        }

        private TrackerLogViewHolder(View view){
            super(view);
            tvText=view.findViewById(R.id.tv_text);
        }

        public void setLog(String log){
            tvText.setText(log);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            tvText.setText(gson.toJson(new JsonParser().parse(log)));
        }
    }
}
