package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingGuest;

/**
 * Created by chen_bin on 2017/11/24 0024.
 */
public class WeddingGuestContactIndexViewHolder extends BaseViewHolder<WeddingGuest> {

    @BindView(R.id.tv_index)
    TextView tvIndex;

    public WeddingGuestContactIndexViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(Context mContext, WeddingGuest guest, int position, int viewType) {
        if (guest == null) {
            return;
        }
        tvIndex.setText(guest.getFirstChar());
    }

}
