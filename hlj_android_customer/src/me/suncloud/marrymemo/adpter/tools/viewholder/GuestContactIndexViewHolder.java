package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by chen_bin on 2017/11/22 0022.
 */
public class GuestContactIndexViewHolder extends BaseViewHolder<String> {

    @BindView(R.id.tv_index)
    TextView tvIndex;

    public GuestContactIndexViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(Context mContext, String str, int position, int viewType) {
        tvIndex.setText(str);
    }
}
