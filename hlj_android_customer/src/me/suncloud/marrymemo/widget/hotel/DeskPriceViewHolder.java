package me.suncloud.marrymemo.widget.hotel;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.adapters.FiltrateMenuAdapter;
import com.hunliji.hljcommonlibrary.models.DeskPriceLabel;
import com.hunliji.hljcommonlibrary.models.SortLabel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/8/2.桌数价格筛选
 */

public class DeskPriceViewHolder implements AdapterView.OnItemClickListener {

    @BindView(R2.id.sort_menu_list)
    ListView deskMenuList;
    @BindView(R2.id.menu_info_layout)
    FrameLayout menuInfoLayout;
    @BindView(R2.id.menu_bg_layout)
    RelativeLayout menuBgLayout;

    private Context mContext;
    private int dividerHeight;
    private ArrayList<DeskPriceLabel> deskPriceLabels;
    private DeskPriceLabel mDeskPriceLabel;
    private boolean isShow;

    private View rootView;
    private FiltrateMenuAdapter deskMenuAdapter;
    private OnDeskPriceFilterListener onDeskPriceFilterListener;

    public static DeskPriceViewHolder newInstance(
            Context context, OnDeskPriceFilterListener listener) {
        View view = View.inflate(context, R.layout.service_sort_filter_view___cv, null);
        DeskPriceViewHolder holder = new DeskPriceViewHolder(context, view, listener);
        holder.init();
        return holder;
    }

    private DeskPriceViewHolder(
            Context context, View view, OnDeskPriceFilterListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.onDeskPriceFilterListener = listener;
        ButterKnife.bind(this, view);
    }

    private void init() {
        deskPriceLabels = new ArrayList<>();
        dividerHeight = Math.max(1, CommonUtil.dp2px(mContext, 1) / 2);
        deskMenuAdapter = new FiltrateMenuAdapter(mContext, R.layout.filtrate_menu_list_item2___cm);
        deskMenuAdapter.setItems(deskPriceLabels);
        deskMenuList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        deskMenuList.setItemsCanFocus(true);
        deskMenuList.setOnItemClickListener(this);
        deskMenuList.setAdapter(deskMenuAdapter);
    }

    public void refreshDeskPrice(ArrayList<DeskPriceLabel> desksPrices) {
        deskPriceLabels.clear();
        deskPriceLabels.addAll(desksPrices);
        deskMenuAdapter.setItems(deskPriceLabels);
    }

    public DeskPriceLabel getDeskPriceLabel() {
        return mDeskPriceLabel;
    }

    public boolean isShow() {
        return isShow;
    }

    public View getRootView() {
        return rootView;
    }

    public void showDeskView() {
        int position = deskPriceLabels.indexOf(mDeskPriceLabel);
        deskMenuList.setVisibility(View.VISIBLE);
        deskMenuList.setDividerHeight(dividerHeight);
        deskMenuList.setItemChecked(position, true);
        showMenuAnimation();
    }

    @OnClick(R2.id.menu_bg_layout)
    public void onMenuBgLayoutClicked() {
        hideMenuAnimation();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDeskPriceLabel = (DeskPriceLabel) parent.getAdapter()
                .getItem(position);
        if (onDeskPriceFilterListener != null) {
            hideMenuAnimation();
            onDeskPriceFilterListener.onFilterRefresh(mDeskPriceLabel);
        }
    }

    public interface OnDeskPriceFilterListener {
        void onFilterRefresh(DeskPriceLabel deskPriceLabel);
    }

    private void showMenuAnimation() {
        if (isShow) {
            return;
        }
        isShow = true;
        menuBgLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_up);
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (menuBgLayout != null) {
                    menuBgLayout.setBackgroundResource(R.color.transparent_black);
                }
            }
        });
        menuInfoLayout.startAnimation(animation);
    }

    public void hideMenuAnimation() {
        if (!isShow) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuBgLayout.setVisibility(View.GONE);
                isShow = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menuInfoLayout.startAnimation(animation);
    }

}
