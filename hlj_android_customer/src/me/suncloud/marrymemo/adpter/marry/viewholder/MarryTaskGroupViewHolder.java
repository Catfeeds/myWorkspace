package me.suncloud.marrymemo.adpter.marry.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.marry.MarryTask;

/**
 * Created by hua_rong on 2017/12/8
 */

public class MarryTaskGroupViewHolder extends BaseViewHolder<MarryTask> {

    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    @BindView(R.id.tv_group_money)
    TextView tvGroupMoney;

    public MarryTaskGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, MarryTask marryTask, int position, int viewType) {
        tvGroupMoney.setVisibility(View.GONE);
        tvGroupName.setText(marryTask.getTitle());
    }
}
