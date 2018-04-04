package com.hunliji.hljcardcustomerlibrary.views.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.views.delegates.CardListBarDelegate;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.utils.PosterUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by wangtao on 2017/11/27.
 */

public class CustomerCardListFragment extends BaseCardListFragment {

    public static CustomerCardListFragment newInstance() {
        return new CustomerCardListFragment();
    }

    @Override
    protected CardListBarDelegate getActionBarDelegate() {
        return new CustomerActionBar(getContext());
    }

    @Override
    protected Observable<Poster> getPosterObb() {
        City city = LocationSession.getInstance()
                .getCity(getContext());
        return CommonApi.getBanner(getContext(),
                HljCommon.BLOCK_ID.CustomerCardListFragment,
                city.getCid())
                .map(new Func1<PosterData, Poster>() {
                    @Override
                    public Poster call(PosterData posterData) {
                        List<Poster> posters = PosterUtil.getPosterList(posterData.getFloors(),
                                HljCommon.POST_SITES.SITE_CUSTOMER_CARD_LIST,
                                false);
                        return CommonUtil.isCollectionEmpty(posters) ? null : posters.get(0);
                    }
                });
    }

    class CustomerActionBar implements CardListBarDelegate {

        @BindView(R2.id.btn_back)
        ImageButton btnBack;
        @BindView(R2.id.tv_toolbar_title)
        TextView tvToolbarTitle;
        @BindView(R2.id.btn_item)
        TextView btnItem;

        Unbinder unbinder;

        private Context context;

        private CustomerActionBar(Context context) {
            this.context = context;
        }

        @Override
        public void inflateActionBar(ViewGroup parent) {
            View actionBar = View.inflate(context, R.layout.hlj_customer_card_list_bar, parent);
            unbinder = ButterKnife.bind(this, actionBar);
            btnItem.setText(R.string.label_old_version___card);
            HljVTTagger.buildTagger(btnItem)
                    .tagName("old_card_button")
                    .hitTag();
        }

        @Override
        public void isHasOld(boolean isHasOld) {
            btnItem.setVisibility(isHasOld ? View.VISIBLE : View.GONE);
        }

        @Override
        public void unbind() {
            if (unbinder != null) {
                unbinder.unbind();
            }
        }


        @OnClick(R2.id.btn_item)
        public void onOldVersion() {
            ARouter.getInstance()
                    .build(RouterPath.IntentPath.Customer.CARD_V2_LIST)
                    .navigation(context);
        }


        @OnClick(R2.id.btn_back)
        public void onBackPressed() {
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().onBackPressed();
            }
        }
    }
}
