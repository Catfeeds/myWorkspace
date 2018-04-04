package me.suncloud.marrymemo.adpter.marry.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.marry.MarryBook;
import me.suncloud.marrymemo.model.marry.RecordBook;

/**
 * Created by hua_rong on 2017/12/8
 */

public class MarryBookGroupViewHolder extends BaseViewHolder<MarryBook> {

    @BindView(R.id.tv_group_name)
    TextView tvGroupName;
    @BindView(R.id.tv_group_money)
    TextView tvGroupMoney;

    public MarryBookGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, MarryBook marryBook, int position, int viewType) {
        if (marryBook != null) {
            RecordBook recordBook = marryBook.getType()
                    .getParent();
            tvGroupName.setText(recordBook.getTitle());
            tvGroupMoney.setText(String.format(Locale.getDefault(),
                    "￥%.2f",
                    marryBook.getParentPrice()));
        }
    }
}
