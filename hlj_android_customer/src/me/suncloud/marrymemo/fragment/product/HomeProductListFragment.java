package me.suncloud.marrymemo.fragment.product;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

import java.util.List;

/**
 * 首页继承的婚品列表
 * Created by jinxin on 2017/9/14 0014.
 */

public class HomeProductListFragment extends ProductListFragment {


    public static HomeProductListFragment newInstance(
            String url, String propertyId) {
        HomeProductListFragment fragment = new HomeProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("propertyId", propertyId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView
                .getLayoutManager();
        int[] positions = layoutManager.findLastVisibleItemPositions(new int[2]);
        int position = positions[0];
        getHomeFeedsFragmentCallBack().onFiltrateAnimation(HomeProductListFragment.this,
                position <= 5);
    }

    @Override
    public void refresh(Object... params) {
        onRefresh(null);
    }

    @Override
    public void scrollTop() {
        onScrollTop();
    }

    @Override
    protected void onSubNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
        if (getHomeFeedsFragmentCallBack() != null) {
            getHomeFeedsFragmentCallBack().onRefreshComplete(HomeProductListFragment
                    .this);
        }
        if (getHomeFeedsFragmentCallBack() != null) {
            getHomeFeedsFragmentCallBack().onShowEmptyView(HomeProductListFragment
                    .this, propertyId, CommonUtil.isCollectionEmpty(adapter.getProducts()));
        }
    }

    @Override
    protected void onSubError(Object o) {
        if (getHomeFeedsFragmentCallBack() != null) {
            getHomeFeedsFragmentCallBack().onRefreshComplete(HomeProductListFragment
                    .this);
        }
        if (getHomeFeedsFragmentCallBack() != null) {
            getHomeFeedsFragmentCallBack().onShowEmptyView(HomeProductListFragment
                    .this, propertyId, CommonUtil.isCollectionEmpty(adapter.getProducts()));
        }
    }
}
