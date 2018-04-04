package com.hunliji.hljquestionanswer.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.QAMarkDetailActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 16/8/15.
 */
public class MarkAdapter extends BaseAdapter {

    private ArrayList<Mark> mData;
    private Context mContext;

    public MarkAdapter(
            Context mContext, ArrayList<Mark> data) {
        this.mContext = mContext;
        this.mData = data;
    }

    public void setData(ArrayList<Mark> data) {
        if (mData != null) {
            this.mData.clear();
            this.mData.addAll(data);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData == null ? 0 : mData.get(position)
                .getId();
    }

    @Override
    public View getView(
            final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.question_mark_list_item___qa, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        final Mark mark = mData.get(position);
        holder.markName.setText(mark.getName());
        holder.markName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mark!=null){
                    Intent intent = new Intent();
                    intent.setClass(mContext, QAMarkDetailActivity.class);
                    intent.putExtra("id",mark.getId());
                    intent.putExtra("isShowHot",true);
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R2.id.mark_name)
        TextView markName;
        @BindView(R2.id.mark_arrow)
        ImageView markArrow;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
