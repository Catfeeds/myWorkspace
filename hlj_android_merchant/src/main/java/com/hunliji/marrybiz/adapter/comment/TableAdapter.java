package com.hunliji.marrybiz.adapter.comment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/4/15.
 * 评论管理的table--表单
 */

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private int width;
    private Context context;
    private List<String> dataList;
    private LayoutInflater inflater;

    public TableAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.dataList = dataList;
        width = CommonUtil.getDeviceSize(context).x;
        inflater = LayoutInflater.from(context);
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TableViewHolder(inflater.inflate(R.layout.item_commentary, parent, false));
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.textView.getLayoutParams();
        if (position < 5){
            holder.textView.setBackgroundColor(ContextCompat.getColor(context,R.color.bg_comment_title));
            params.height = CommonUtil.dp2px(context,38);
            holder.textView.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
            holder.textView.setTextSize(14);
        }else {
            holder.textView.setTextColor(ContextCompat.getColor(context,
                    position % 5 == 0 ? R.color.colorGray : R.color.colorBlack3));
            holder.textView.setTextSize(13);
            holder.textView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite));
            params.height = CommonUtil.dp2px(context,44);
        }
        params.width =  (width - CommonUtil.dp2px(context,40))/5;
        holder.textView.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

     class TableViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view)
        TextView textView;

         TableViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
