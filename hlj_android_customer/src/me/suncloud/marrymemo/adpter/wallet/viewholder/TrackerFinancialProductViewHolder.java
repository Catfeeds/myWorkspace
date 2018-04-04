package me.suncloud.marrymemo.adpter.wallet.viewholder;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;

import me.suncloud.marrymemo.model.wallet.FinancialProduct;

/**
 * Created by wangtao on 2017/12/22.
 */

public abstract class TrackerFinancialProductViewHolder extends BaseViewHolder<FinancialProduct> {

    public TrackerFinancialProductViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setView(Context mContext, FinancialProduct item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(tagName())
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType("FinancialMarket")
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setView(mContext, item, position, viewType);
    }

    public abstract View trackerView();

    public String tagName() {
        return HljTaggerName.FINANCIAL_MARKET;
    }
}
