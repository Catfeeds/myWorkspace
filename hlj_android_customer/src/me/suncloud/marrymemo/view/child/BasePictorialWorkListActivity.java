package me.suncloud.marrymemo.view.child;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.modelwrappers.MerchantServiceFilter;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonviewlibrary.widgets.ServiceWorkFilterViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.work_case.ChildWorkListFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

/**
 * Created by jinxin on 2017/12/8 0008.
 */

public abstract class BasePictorialWorkListActivity extends HljBaseNoBarActivity {

    public static final String ARG_PROPERTY_ID = "property_id";
    public static final String ARG_CATEGORY_ID = "category_id";
    public static final String ARG_SECOND_CATEGORY_ID = "second_category_id";

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    @BindView(R.id.msg_layout)
    RelativeLayout msgLayout;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.layout_filter)
    RelativeLayout layoutFilter;
    @BindView(R.id.layout_content)
    RelativeLayout layoutContent;

    private NoticeUtil noticeUtil;
    protected long propertyId;
    protected long categoryId;
    protected long secondCategoryId;
    protected String sort;
    protected long cid;
    protected MerchantServiceFilter serviceFilter;
    protected ServiceWorkFilterViewHolder serviceWorkFilterViewHolder;
    protected ChildWorkListFragment workListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_child);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
    }

    private void initConstant() {
        if (getIntent() != null) {
            propertyId = getIntent().getLongExtra(ARG_PROPERTY_ID,
                    Merchant.PROPERTY_WEDDING_DRESS_PHOTO);
            categoryId = getIntent().getLongExtra(ARG_CATEGORY_ID, 0L);
            secondCategoryId = getIntent().getLongExtra(ARG_SECOND_CATEGORY_ID, 0L);
        }
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        City mCity = Session.getInstance()
                .getMyCity(this);
        if (mCity != null) {
            cid = mCity.getId();
        }
        serviceFilter = new MerchantServiceFilter();
        workListFragment = ChildWorkListFragment.getInstance(propertyId,
                categoryId,
                secondCategoryId);
    }

    private void initWidget() {
        setDefaultStatusBarPadding();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_content, workListFragment)
                .commit();
        setFilterView();
    }

    private void setFilterView() {
        serviceWorkFilterViewHolder = ServiceWorkFilterViewHolder.newInstance(this,
                cid,
                propertyId,
                secondCategoryId,
                new ServiceWorkFilterViewHolder.OnFilterResultListener() {
                    @Override
                    public void onFilterResult(
                            String sort,
                            long cid,
                            long areaId,
                            double minPrice,
                            double maxPrice,
                            List<String> tags) {
                        BasePictorialWorkListActivity.this.sort = sort;
                        BasePictorialWorkListActivity.this.cid = cid;
                        serviceFilter.setShopAreaId(areaId);
                        serviceFilter.setPriceMin(minPrice);
                        serviceFilter.setPriceMax(maxPrice);
                        serviceFilter.setTags(tags);
                        onFilterRefresh();
                    }
                });
        layoutFilter.removeAllViews();
        layoutFilter.addView(serviceWorkFilterViewHolder.getRootView());
    }

    public void onFilterRefresh() {
        workListFragment.setFilterParams(cid, sort, serviceFilter);
        workListFragment.refresh();
    }

    @Override
    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    @Override
    public void onBackPressed() {
        if (serviceWorkFilterViewHolder != null && serviceWorkFilterViewHolder.isShow()) {
            serviceWorkFilterViewHolder.hideFilterView();
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.btn_back)
    void onBack() {
        onBackPressed();
    }

    /**
     * 跳转通知私信
     */
    @OnClick(R.id.msg_layout)
    public void onMessage() {
        if (Util.loginBindChecked(this, Constants.RequestCode.NOTIFICATION_PAGE)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        if (serviceWorkFilterViewHolder != null) {
            serviceWorkFilterViewHolder.onDestroy();
        }
    }
}
