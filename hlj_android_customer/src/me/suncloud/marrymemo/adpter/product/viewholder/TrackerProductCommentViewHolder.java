package me.suncloud.marrymemo.adpter.product.viewholder;

import android.content.Context;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ProductComment;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;

/**
 * Created by wangtao on 2017/12/14.
 */

public abstract class TrackerProductCommentViewHolder extends BaseViewHolder<ProductComment> {

    public TrackerProductCommentViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, ProductComment item, int position, int viewType) {
        try {
            HljVTTagger.buildTagger(trackerView())
                    .tagName(tagName())
                    .atPosition(position)
                    .dataId(item.getId())
                    .dataType("ShopProductComment")
                    .tag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract View trackerView();

    public String tagName() {
        return HljTaggerName.PRODUCT_COMMENT;
    }
}
