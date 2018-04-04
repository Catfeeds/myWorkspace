package me.suncloud.marrymemo.fragment.product;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

import me.suncloud.marrymemo.R;

/**
 * Created by jinxin on 2017/9/14 0014.
 */

public class HomeSelectedProductListFragment extends SelectedProductListFragment {

    public static HomeSelectedProductListFragment newInstance(
            String url, String propertyId) {
        HomeSelectedProductListFragment fragment = new HomeSelectedProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("propertyId", propertyId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void refresh(Object... params) {
        onRefresh(null);
    }

    @Override
    protected void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findLastVisibleItemPosition();
        getHomeFeedsFragmentCallBack().onFiltrateAnimation(HomeSelectedProductListFragment.this,
                position <= 5);
    }

    @Override
    public void scrollTop() {
        onScrollTop();
    }

    @Override
    protected void onSubNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
        if (getHomeFeedsFragmentCallBack() != null) {
            getHomeFeedsFragmentCallBack().onRefreshComplete(HomeSelectedProductListFragment
                    .this);
        }
        if (getHomeFeedsFragmentCallBack() != null) {
            getHomeFeedsFragmentCallBack().onShowEmptyView(HomeSelectedProductListFragment
                    .this, propertyId, CommonUtil.isCollectionEmpty(adapter.getProducts()));
        }
    }

    @Override
    protected void onSubError(Object o) {
        //失败时parent回调
        if (getHomeFeedsFragmentCallBack() != null) {
            getHomeFeedsFragmentCallBack().onRefreshComplete(HomeSelectedProductListFragment
                    .this);
        }
        if (getHomeFeedsFragmentCallBack() != null) {
            getHomeFeedsFragmentCallBack().onShowEmptyView(HomeSelectedProductListFragment
                    .this, propertyId, CommonUtil.isCollectionEmpty(adapter.getProducts()));
        }
    }
}
