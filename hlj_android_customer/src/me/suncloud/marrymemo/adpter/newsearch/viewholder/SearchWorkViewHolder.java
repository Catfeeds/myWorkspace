package me.suncloud.marrymemo.adpter.newsearch.viewholder;

import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;

import me.suncloud.marrymemo.view.newsearch.NewSearchResultActivity;

/**
 * Created by wangtao on 2018/2/6.
 */

public class SearchWorkViewHolder extends SmallWorkViewHolder {

    public SearchWorkViewHolder(ViewGroup parent) {
        super(parent);
    }

    public SearchWorkViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public String cpmSource() {
        return NewSearchResultActivity.CPM_SOURCE;
    }
}
