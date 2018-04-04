package me.suncloud.marrymemo.adpter.home;


import com.hunliji.hljcommonlibrary.views.fragments.HomePageScrollAbleFragment;

/**
 * 为兼容首页婚品和feeds流 ,HomeFeedsFragmentCallBack抽出来使用ScrollAbleFragment
 * 作为参数
 * Created by jinxin on 2017/9/13 0013.
 */

public interface HomeFeedsFragmentCallBack {

    void onRefreshComplete(HomePageScrollAbleFragment fragment);

    void onFiltrateAnimation(HomePageScrollAbleFragment fragment, boolean isHide);

    void onShowEmptyView(HomePageScrollAbleFragment fragment, String propertyId, boolean isEmpty);
}
