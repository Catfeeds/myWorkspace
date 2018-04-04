package com.hunliji.marrybiz.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.OverScrollViewPager;
import com.hunliji.hljcommonlibrary.views.widgets.OverscrollContainer;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.WorkItemAdapter;
import com.hunliji.marrybiz.model.Comment;
import com.hunliji.marrybiz.model.Item;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.NewMerchant;
import com.hunliji.marrybiz.model.Rule;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.model.WorkParameter;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.TimeUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.ItemPageViewActivity;
import com.hunliji.marrybiz.view.WorkCommentListActivity;
import com.hunliji.marrybiz.view.shop.ShopWebViewActivity;
import com.hunliji.marrybiz.widget.FlowLayout;
import com.hunliji.marrybiz.widget.MyScrollView;
import com.hunliji.marrybiz.widget.TBLayout;
import com.slider.library.Indicators.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Suncloud on 2016/1/13.
 */
public class WorkFragment extends RefreshFragment implements TBLayout.OnPullListener, TBLayout
        .OnPageChangedListener, TBLayout.OnScrollListener, WorkItemAdapter.OnItemClickListener,
        OverscrollContainer.OnLoadListener, View.OnClickListener, MyScrollView
                .OnScrollChangedListener {


    @BindView(R.id.scroll_view)
    MyScrollView scrollView;
    @BindView(R.id.work_info_layout)
    TBLayout workInfoLayout;
    @BindView(R.id.privilege_arrow)
    ImageView privilegeArrow;
    @BindView(R.id.earnest)
    TextView earnest;
    @BindView(R.id.earnest_layout)
    RelativeLayout earnestLayout;
    @BindView(R.id.gift)
    TextView gift;
    @BindView(R.id.gift_layout)
    RelativeLayout giftLayout;
    @BindView(R.id.pay_all)
    TextView payAll;
    @BindView(R.id.pay_all_layout)
    RelativeLayout payAllLayout;
    @BindView(R.id.work_privilege_layout)
    RelativeLayout workPrivilegeLayout;

    private View progressBar;
    private View shadowView;
    private View actionLayout;
    private View rootView;

    private Work work;

    private int itemsHeight;
    private int showHeight;


    private ArrayList<Item> items;
    private WorkInfoFragment workInfoFragment;

    private SaleTimeViewHolder saleHolder;
    private Handler handler = new Handler();
    private Runnable timeDownRun = new Runnable() {
        @Override
        public void run() {
            setSaleView();
        }
    };
    private boolean isSnapshot;

    private String earnestStr;
    private String giftStr;
    private String payAllStr;

    private Dialog dialog;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        shadowView = getActivity().findViewById(R.id.shadow_view);
        actionLayout = getActivity().findViewById(R.id.action_holder_layout2);
        progressBar = getActivity().findViewById(R.id.progressBar);
        items = new ArrayList<>();
        Point point = JSONUtil.getDeviceSize(getActivity());
        itemsHeight = Math.round(point.x * 3 / 4);
        showHeight = Math.round(itemsHeight - 45 * getResources().getDisplayMetrics().density) -
                HljBaseActivity.getStatusBarHeight(
                getContext());
        if (getArguments() != null) {
            work = (Work) getArguments().getSerializable("work");
            String json = getArguments().getString("json");
            isSnapshot = getArguments().getBoolean("isSnapshot", false);
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray array = jsonObject.optJSONArray("media_items");
                if (array != null && array.length() > 0) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        Item item = new Item(array.optJSONObject(i));
                        if (!JSONUtil.isEmpty(item.getMediaPath())) {
                            item.setType(Constants.ItemType.WorkItem);
                            items.add(item);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_work, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        workInfoFragment = (WorkInfoFragment) Fragment.instantiate(getActivity(),
                WorkInfoFragment.class.getName());
        Bundle args = new Bundle();
        args.putSerializable("work", work);
        args.putBoolean("isSnapshot", isSnapshot);
        workInfoFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.info, workInfoFragment, "workInfoFragment");
        ft.commit();
        workInfoLayout.setOnScrollListener(this);
        workInfoLayout.setOnContentChangeListener(this);
        workInfoLayout.setOnPullListener(this);
        scrollView.setOnScrollChangedListener(this);
        rootView.findViewById(R.id.heard_view)
                .getLayoutParams().height = itemsHeight;
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager
                .OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    float alpha = Math.min((float) scrollView.getScrollY() / showHeight, 1f);
                    actionLayout.setAlpha(alpha);
                    shadowView.setAlpha(1 - alpha);
                } else {
                    actionLayout.setAlpha(1);
                    shadowView.setAlpha(0);
                }
            }
        });
        progressBar.setVisibility(View.GONE);
        initHeardItemsView();
        if (work != null) {
            initWorkInfo(work);
            initMerchantView(work.getMerchant());
            initMerchantPrivilege(work.getMerchant());
            rootView.findViewById(R.id.drag_hint)
                    .setVisibility(View.VISIBLE);
        }
        workInfoLayout.initData();
        return rootView;
    }


    private void initHeardItemsView() {
        CirclePageIndicator indicator = (CirclePageIndicator) rootView.findViewById(R.id
                .flow_indicator);
        OverScrollViewPager itemsView = (OverScrollViewPager) rootView.findViewById(R.id
                .items_view);
        WorkItemAdapter itemAdapter = new WorkItemAdapter(getActivity(), items, true);
        itemAdapter.setOnItemClickListener(this);
        itemsView.setOverable(true);
        itemsView.setOnLoadListener(this);
        itemsView.getOverscrollView()
                .setAdapter(itemAdapter);
        indicator.setViewPager(itemsView.getOverscrollView());
    }

    private void initWorkInfo(Work work) {
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        titleView.setText(work.getTitle());
        TextView property = (TextView) rootView.findViewById(R.id.property);
        property.setText(work.getKind());
        setSaleView();
        TextView originalPrice = (TextView) rootView.findViewById(R.id.original_price);
        if (work.getMarketPrice() > 0) {
            originalPrice.setVisibility(View.VISIBLE);
            originalPrice.getPaint()
                    .setAntiAlias(true);
            originalPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            originalPrice.setText("  " + getString(R.string.rmb) + Util.formatFloat2String(work
                    .getMarketPrice()) + "  ");
        } else {
            originalPrice.setVisibility(View.GONE);
        }
        if (work.getCommentsCount() > 0 && !isSnapshot) {
            rootView.findViewById(R.id.comment_layout)
                    .setVisibility(View.VISIBLE);
            TextView commentCount = (TextView) rootView.findViewById(R.id.comment_count);
            if (work.getCommentsCount() > 1) {
                rootView.findViewById(R.id.more_comment)
                        .setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.more_comment)
                        .setOnClickListener(this);
                commentCount.setText(getString(R.string.label_comment_count5,
                        work.getCommentsCount()));
            } else {
                rootView.findViewById(R.id.more_comment)
                        .setVisibility(View.GONE);
                commentCount.setText(R.string.label_review2);
            }
            Comment comment = work.getLatestComment();
            ImageView userIcon = (ImageView) rootView.findViewById(R.id.avatar);
            TextView userName = (TextView) rootView.findViewById(R.id.name);
            TextView content = (TextView) rootView.findViewById(R.id.content);
            TextView time = (TextView) rootView.findViewById(R.id.time);
            if (comment.getUser() != null) {
                int size = Math.round(getResources().getDisplayMetrics().density * 30);
                String url = JSONUtil.getAvatar(comment.getUser()
                        .getAvatar(), size);
                if (!JSONUtil.isEmpty(url)) {
                    userIcon.setTag(url);
                    ImageLoadTask task = new ImageLoadTask(userIcon, 0);
                    task.loadImage(url,
                            userIcon.getLayoutParams().width,
                            ScaleMode.WIDTH,
                            new AsyncBitmapDrawable(getResources(),
                                    BitmapFactory.decodeResource(getResources(),
                                            R.mipmap.icon_avatar_primary),
                                    task));
                } else {
                    userIcon.setImageResource(R.mipmap.icon_avatar_primary);
                }
                userName.setText(comment.getUser()
                        .getName());
            }
            content.setText(comment.getContent());
            if (comment.getTime() != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string
                        .format_date_type4),
                        Locale.getDefault());
                time.setText(timeFormat.format(comment.getTime()));
            }
        } else {
            rootView.findViewById(R.id.comment_layout)
                    .setVisibility(View.GONE);
        }
        if (JSONUtil.isEmpty(work.getDescribe())) {
            rootView.findViewById(R.id.describe_layout)
                    .setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.describe_layout)
                    .setVisibility(View.VISIBLE);
            TextView describe = (TextView) rootView.findViewById(R.id.describe);
            describe.setText(work.getDescribe());
        }
        if (work.getParameters() == null || work.getParameters()
                .isEmpty()) {
            rootView.findViewById(R.id.parameters_layout)
                    .setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.parameters_layout)
                    .setVisibility(View.VISIBLE);
            LinearLayout parameterList = (LinearLayout) rootView.findViewById(R.id.parameter_list);
            rootView.findViewById(R.id.more_parameter)
                    .setOnClickListener(this);
            rootView.findViewById(R.id.more_parameter)
                    .setVisibility(work.getParameters()
                            .size() > work.getShowParameters()
                            .size() ? View.VISIBLE : View.GONE);
            for (WorkParameter parameter : work.getShowParameters()) {
                setViewValue(View.inflate(getActivity(), R.layout.work_parameter_view, null),
                        parameter,
                        parameterList);
            }
        }
    }

    private void initMerchantPrivilege(NewMerchant merchant) {
        if (merchant != null) {
            if (isSnapshot) {
                ((TextView) rootView.findViewById(R.id.cost_effective_content)).setText(merchant
                        .getCostEffective());
                rootView.findViewById(R.id.cost_effective_layout)
                        .setVisibility(JSONUtil.isEmpty(merchant.getCostEffective()) ? View.GONE
                                : View.VISIBLE);
            }
            ArrayList<String> promiseItems = new ArrayList<>();
            if (merchant.getMerchantPromise() != null) {
                promiseItems.addAll(merchant.getMerchantPromise());
            }
            if (merchant.getChargeBack() != null) {
                promiseItems.addAll(merchant.getChargeBack());
            }
            if (!promiseItems.isEmpty()) {
                rootView.findViewById(R.id.merchant_promise_layout)
                        .setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.merchant_promise_layout)
                        .setOnClickListener(this);
                FlowLayout itemsLayout = (FlowLayout) rootView.findViewById(R.id.items_layout);
                int size = promiseItems.size();
                int count = itemsLayout.getChildCount();
                if (count > size) {
                    itemsLayout.removeViews(size, count - size);
                }
                for (int i = 0; i < size; i++) {
                    String item = promiseItems.get(i);
                    View view = null;
                    if (i < count) {
                        view = itemsLayout.getChildAt(i);
                    }
                    if (view == null) {
                        view = View.inflate(getActivity(), R.layout.merchant_promise_item, null);
                        itemsLayout.addView(view);
                    }
                    TextView itemView = (TextView) view.findViewById(R.id.item);
                    itemView.setText(item);
                }
            } else {
                rootView.findViewById(R.id.merchant_promise_layout)
                        .setVisibility(View.GONE);
            }
        } else {
            rootView.findViewById(R.id.cost_effective_layout)
                    .setVisibility(View.GONE);
            rootView.findViewById(R.id.promise_layout)
                    .setVisibility(View.GONE);
        }
    }

    public void setViewValue(View view, WorkParameter workParameter, LinearLayout parameterList) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.lineLayout.setVisibility(parameterList.getChildCount() == 0 ? View.GONE : View
                .VISIBLE);
        holder.name.setText(workParameter.getTitle());
        int size = workParameter.getChildren() == null ? 0 : workParameter.getChildren()
                .size();
        int childCount = holder.itemList.getChildCount();
        if (childCount > size) {
            holder.itemList.removeViews(size, childCount - size);
        }
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                WorkParameter parameter = workParameter.getChildren()
                        .get(i);
                View childView = null;
                if (i < childCount) {
                    childView = holder.itemList.getChildAt(i);
                }
                if (childView == null) {
                    childView = View.inflate(getActivity(),
                            R.layout.work_parameter_item_view,
                            null);
                    holder.itemList.addView(childView);
                }
                ChildViewHolder childViewHolder = (ChildViewHolder) childView.getTag();
                if (childViewHolder == null) {
                    childViewHolder = new ChildViewHolder(childView);
                    childView.setTag(childViewHolder);
                }
                childViewHolder.key.setText(parameter.getTitle());
                childViewHolder.value.setText(parameter.getValues());
            }
        }
        parameterList.addView(view);
    }

    static class ViewHolder {
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.item_list)
        LinearLayout itemList;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ChildViewHolder {
        @BindView(R.id.key)
        TextView key;
        @BindView(R.id.value)
        TextView value;

        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void initMerchantView(final NewMerchant merchant) {
        if (merchant != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            rootView.findViewById(R.id.merchant_layout)
                    .setOnClickListener(this);
            if (work.isLvpai()) {
                rootView.findViewById(R.id.layout)
                        .setVisibility(View.GONE);
            }
            View bondLayout = rootView.findViewById(R.id.bond_layout);
            bondLayout.setOnClickListener(this);
            ImageView bondIconView = (ImageView) rootView.findViewById(R.id.bond_icon);
            TextView merchantName = (TextView) rootView.findViewById(R.id.merchant_name);
            TextView merchantAddress = (TextView) rootView.findViewById(R.id.merchant_address);
            ImageView merchantLogo = (ImageView) rootView.findViewById(R.id.merchant_logo);
            int padding = 0;
            if (merchant.getBondSign() != null) {
                bondIconView.setVisibility(View.VISIBLE);
                bondLayout.setVisibility(isSnapshot ? View.GONE : View.VISIBLE);
                padding += Math.round(20 * dm.density);
            } else {
                bondIconView.setVisibility(View.GONE);
                bondLayout.setVisibility(View.GONE);
            }
            ImageView levelView = (ImageView) rootView.findViewById(R.id.level_icon);
            int res = 0;
            switch (merchant.getGrade()) {
                case 2:
                    res = R.drawable.icon_merchant_level2;
                    break;
                case 3:
                    res = R.drawable.icon_merchant_level3;
                    break;
                case 4:
                    res = R.drawable.icon_merchant_level4;
                    break;
                default:
                    break;
            }
            if (res != 0) {
                levelView.setVisibility(View.VISIBLE);
                levelView.setImageResource(res);
                padding += Math.round(70 * dm.density);
            } else {
                levelView.setVisibility(View.GONE);
            }
            merchantName.setPadding(0, 0, padding, 0);
            merchantName.setText(merchant.getName());
            merchantAddress.setText(merchant.getAddress());
            int logoWidth = Math.round(40 * dm.density);
            String logoPath = JSONUtil.getImagePath2(merchant.getLogoPath(), logoWidth);
            if (!JSONUtil.isEmpty(logoPath)) {
                ImageLoadTask loadTask = new ImageLoadTask(merchantLogo, null, 0);
                merchantLogo.setTag(logoPath);
                loadTask.loadImage(logoPath,
                        logoWidth,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(),
                                R.mipmap.icon_empty_image,
                                loadTask));
            }
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        if (work == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.more_comment:
                Intent intent = new Intent(getActivity(), WorkCommentListActivity.class);
                intent.putExtra("w_id", work.getId());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            case R.id.merchant_layout:
                MerchantUser merchantUser = Session.getInstance()
                        .getCurrentUser(getContext());
                if (merchantUser.getShopType() == MerchantUser.SHOP_TYPE_SERVICE) {
                    //0 服务商家，1 婚品商家 2.婚车
                    intent = new Intent(getContext(), ShopWebViewActivity.class);
                    intent.putExtra("title", getString(R.string.label_preview_merchant));
                    intent.putExtra("type", 3);
                    intent.putExtra("path",
                            String.format(Constants.WEB_HOST + Constants.HttpPath.GET_SHOP_PREVIEW,
                                    merchantUser.getMerchantId(),
                                    1));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.bond_layout:
                if (!isSnapshot && work.getMerchant() != null && !JSONUtil.isEmpty(work
                        .getMerchant()
                        .getBondSignUrl())) {
                    String path = work.getMerchant()
                            .getBondSignUrl();
                    intent = new Intent(getActivity(), HljWebViewActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
                break;
            case R.id.more_parameter:
                showWorkInfo(1);
                break;
            case R.id.work_privilege_layout:
                if (JSONUtil.isEmpty(earnestStr) && JSONUtil.isEmpty(giftStr) && JSONUtil.isEmpty(
                        payAllStr)) {
                    return;
                }
                if (dialog != null && dialog.isShowing()) {
                    return;
                }
                if (dialog == null) {
                    dialog = new Dialog(getActivity(), R.style.BubbleDialogTheme);
                    dialog.setContentView(R.layout.dialog_work_privilege);
                    dialog.findViewById(R.id.action_cancel)
                            .setOnClickListener(this);
                    Window win = dialog.getWindow();
                    WindowManager.LayoutParams params = win.getAttributes();
                    Point point = JSONUtil.getDeviceSize(getActivity());
                    params.width = point.x;
                    win.setAttributes(params);
                    win.setGravity(Gravity.BOTTOM);
                    win.setWindowAnimations(R.style.dialog_anim_rise_style);
                }
                if (JSONUtil.isEmpty(earnestStr)) {
                    dialog.findViewById(R.id.earnest_layout)
                            .setVisibility(View.GONE);
                } else {
                    dialog.findViewById(R.id.earnest_layout)
                            .setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.earnest)).setText(earnestStr);
                }
                if (JSONUtil.isEmpty(giftStr)) {
                    dialog.findViewById(R.id.gift_layout)
                            .setVisibility(View.GONE);
                } else {
                    dialog.findViewById(R.id.gift_layout)
                            .setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.gift)).setText(giftStr);
                }
                if (JSONUtil.isEmpty(payAllStr)) {
                    dialog.findViewById(R.id.pay_all_layout)
                            .setVisibility(View.GONE);
                } else {
                    dialog.findViewById(R.id.pay_all_layout)
                            .setVisibility(View.VISIBLE);
                    ((TextView) dialog.findViewById(R.id.pay_all)).setText(payAllStr);
                }
                dialog.show();
                break;
            case R.id.action_cancel:
                if (dialog != null)
                    dialog.dismiss();
                break;
            case R.id.merchant_promise_layout:
                if (work.getMerchant() == null) {
                    return;
                }
                Util.startWebView(getActivity(),
                        work.getMerchant()
                                .getGuidePath());
                break;
            default:
                break;
        }
    }

    @Override
    public void OnItemClickListener(Object item, int position) {
        Item t = (Item) item;
        if (t != null) {
            Intent intent = new Intent(getActivity(), ItemPageViewActivity.class);
            intent.putExtra("items", items);
            intent.putExtra("position", items.indexOf(t));
            startActivity(intent);
        }
    }

    @OnClick(R.id.backtop_btn)
    public void backTop() {
        workInfoLayout.scrollToTop();
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onLoad() {
        showWorkInfo(0);
    }

    private void showWorkInfo(int position) {
        WorkInfoFragment workInfoFragment = (WorkInfoFragment) getFragmentManager()
                .findFragmentByTag(
                "workInfoFragment2");
        if (workInfoFragment != null) {
            return;
        }
        workInfoFragment = (WorkInfoFragment) Fragment.instantiate(getActivity(),
                WorkInfoFragment.class.getName());
        Bundle args = new Bundle();
        args.putSerializable("work", work);
        args.putBoolean("isSnapshot", isSnapshot);
        args.putInt("position", position);
        workInfoFragment.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_right);
        ft.add(R.id.info2, workInfoFragment, "workInfoFragment2");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onPageChanged(int stub) {
        float alpha;
        if (stub == TBLayout.SCREEN_FOOTER) {
            alpha = 1;
        } else {
            alpha = Math.min((float) scrollView.getScrollY() / showHeight, 1f);
        }
        actionLayout.setAlpha(alpha);
        shadowView.setAlpha(1 - alpha);
    }

    @Override
    public boolean headerFootReached(MotionEvent event) {
        return scrollView.getScrollY() + scrollView.getHeight() >= scrollView.getChildAt(0)
                .getHeight();
    }

    @Override
    public boolean footerHeadReached(MotionEvent event) {
        return workInfoFragment == null || workInfoFragment.isTop();
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            return;
        }
        float alpha;
        if (t > showHeight) {
            alpha = 1;
        } else if (t == 0) {
            alpha = 0;
        } else {
            alpha = (float) t / showHeight;
        }
        actionLayout.setAlpha(alpha);
        shadowView.setAlpha(1 - alpha);
    }

    @Override
    public void onScrollChanged(int t) {
        if (scrollView.getScaleY() < showHeight) {
            int y = scrollView.getScrollY() + t;
            float alpha;
            if (y > showHeight) {
                alpha = 1;
            } else if (y == 0) {
                alpha = 0;
            } else {
                alpha = (float) y / showHeight;
            }
            actionLayout.setAlpha(alpha);
            shadowView.setAlpha(1 - alpha);
        }
    }

    private void setSaleView() {
        if (saleHolder == null) {
            saleHolder = new SaleTimeViewHolder();
        }
        Rule rule = work.getRule();
        Date date = new Date();
        if (rule != null && rule.getId() > 0 && (rule.getEnd_time() == null || rule.getEnd_time()
                .after(date))) {
            if (rule.getStart_time() == null || rule.getStart_time()
                    .before(date)) {
                int payAllPercentPrice = (int) (work.getSalePayAllPercent() * work.getSale_price());
                setWorkPrivilegeLayout(work.getSaleEarnestMoney(),
                        work.getOrderGift(),
                        payAllPercentPrice,
                        work.getPayAllGift());

                saleHolder.prepareLayout.setVisibility(View.GONE);
                saleHolder.price.setText(Util.formatFloat2String(work.getSale_price()));
                if (JSONUtil.isEmpty(rule.getShowtxt())) {
                    saleHolder.type.setVisibility(View.GONE);
                } else {
                    saleHolder.type.setVisibility(View.VISIBLE);
                    saleHolder.type.setText(work.getRule()
                            .getShowtxt());
                }
                if (work.getLimit_num() > 0) {
                    saleHolder.limitCountLayout.setVisibility(View.VISIBLE);
                    saleHolder.limitCount.setText(String.valueOf(work.getLimit_num() - work
                            .getLimit_sold_out()));
                } else {
                    saleHolder.limitCountLayout.setVisibility(View.GONE);
                }
                if (rule.getEnd_time() != null) {
                    saleHolder.activityCountDown.setVisibility(View.VISIBLE);
                    long millisInFuture = rule.getEndTimeInMillis() - date.getTime();
                    saleHolder.activityCountDown.setText(getString(R.string.label_work_left3,
                            millisFormat(millisInFuture)));
                    if (millisInFuture >= 0) {
                        handler.postDelayed(timeDownRun, 1000);
                    }
                }
            } else {
                int payAllPercentPrice = (int) (work.getPayAllPercent() * work.getActualPrice());
                setWorkPrivilegeLayout(work.getEarnestMoney(),
                        work.getOrderGift(),
                        payAllPercentPrice,
                        work.getPayAllGift());

                long millisInStart = rule.getStartTimeInMillis() - date.getTime();
                saleHolder.prepareLayout.setVisibility(View.VISIBLE);
                saleHolder.type.setVisibility(View.GONE);
                saleHolder.limitCountLayout.setVisibility(View.GONE);
                saleHolder.activityCountDown.setVisibility(View.GONE);
                saleHolder.price.setText(Util.formatFloat2String(work.getActualPrice()));
                saleHolder.prepareLabel.setText(rule.getName() + getString(R.string
                        .label_work_discount_label));
                saleHolder.preparePrice.setText(getString(R.string.label_price5,
                        Util.formatDouble2String(work.getSale_price())));
                saleHolder.prepareCountDown.setText(getString(R.string.label_work_left1) +
                        millisFormat(
                        millisInStart));
                if (millisInStart >= 0) {
                    handler.postDelayed(timeDownRun, 1000);
                }
            }
        } else {
            saleHolder.price.setText(Util.formatFloat2String(work.getActualPrice()));
            saleHolder.type.setVisibility(View.GONE);
            saleHolder.activityCountDown.setVisibility(View.GONE);
            saleHolder.limitCountLayout.setVisibility(View.GONE);
            saleHolder.prepareLayout.setVisibility(View.GONE);
            int payAllPercentPrice = (int) (work.getPayAllPercent() * work.getActualPrice());
            setWorkPrivilegeLayout(work.getEarnestMoney(),
                    work.getOrderGift(),
                    payAllPercentPrice,
                    work.getPayAllGift());
        }
    }

    private void setWorkPrivilegeLayout(
            double earnestMoney, String orderGift, double payAllPercentPrice, String payAllGift) {
        if (earnestMoney > 0) {
            earnestStr = getString(R.string.label_earnest, Util.formatDouble2String(earnestMoney));
        }
        if (!JSONUtil.isEmpty(orderGift)) {
            giftStr = getString(R.string.label_gift, orderGift);
        }
        if (payAllPercentPrice > 0) {
            payAllStr = getString(R.string.label_pay_all_percent,
                    Util.formatDouble2String(payAllPercentPrice));
        } else if (!JSONUtil.isEmpty(payAllGift)) {
            payAllStr = getString(R.string.label_pay_all_gift, payAllGift);
        }

        if (JSONUtil.isEmpty(earnestStr) && JSONUtil.isEmpty(giftStr) && JSONUtil.isEmpty
                (payAllStr)) {
            workPrivilegeLayout.setVisibility(View.GONE);
            return;
        }
        workPrivilegeLayout.setVisibility(View.VISIBLE);
        workPrivilegeLayout.setOnClickListener(null);
        privilegeArrow.setVisibility(View.GONE);

        Point point = JSONUtil.getDeviceSize(getActivity());
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int minWidth = Math.round(dm.density * 16);
        int width = point.x - Math.round(dm.density * 12);

        if (!JSONUtil.isEmpty(earnestStr)) {
            earnestLayout.setVisibility(View.VISIBLE);
            width = Math.round(width - earnest.getPaint()
                    .measureText(earnestStr + "…") - minWidth - dm.density * 12);
        } else {
            earnestLayout.setVisibility(View.GONE);
        }
        int w = !JSONUtil.isEmpty(giftStr) || !JSONUtil.isEmpty(payAllStr) ? minWidth * 2 : 0;
        if (width < w) {
            earnest.setText(earnestStr + "…");
            privilegeArrow.setVisibility(View.VISIBLE);
            workPrivilegeLayout.setOnClickListener(this);
            giftLayout.setVisibility(View.GONE);
            payAllLayout.setVisibility(View.GONE);
            return;
        }

        earnest.setText(earnestStr);
        if (!JSONUtil.isEmpty(giftStr)) {
            giftLayout.setVisibility(View.VISIBLE);
            width = Math.round(width - gift.getPaint()
                    .measureText(giftStr + "…") - minWidth - dm.density * 12);
        } else {
            giftLayout.setVisibility(View.GONE);
        }
        w = !JSONUtil.isEmpty(payAllStr) ? minWidth * 2 : 0;
        if (width < w) {
            gift.setText(giftStr + "…");
            privilegeArrow.setVisibility(View.VISIBLE);
            workPrivilegeLayout.setOnClickListener(this);
            payAllLayout.setVisibility(View.GONE);
            return;
        }

        gift.setText(giftStr);
        if (!JSONUtil.isEmpty(payAllStr)) {
            payAllLayout.setVisibility(View.VISIBLE);
            payAll.setText(payAllStr);
            width = Math.round(width - payAll.getPaint()
                    .measureText(payAllStr) - minWidth - dm.density * 12);
        } else {
            payAllLayout.setVisibility(View.GONE);
        }
        if (width < 0) {
            privilegeArrow.setVisibility(View.VISIBLE);
            workPrivilegeLayout.setOnClickListener(this);
        }
    }

    private String millisFormat(long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        return (days > 0 ? getString(R.string.label_day,
                days) : "") + TimeUtil.countDownMillisFormat(getActivity(), millisTime);
    }

    private class SaleTimeViewHolder {
        View prepareLayout;
        View limitCountLayout;
        TextView type;
        TextView price;
        TextView preparePrice;
        TextView prepareLabel;
        TextView limitCount;
        TextView activityCountDown;
        TextView prepareCountDown;

        private SaleTimeViewHolder() {
            prepareLayout = rootView.findViewById(R.id.prepare_layout);
            limitCountLayout = rootView.findViewById(R.id.limit_count_layout);
            price = (TextView) rootView.findViewById(R.id.price);
            type = (TextView) rootView.findViewById(R.id.discount_type);
            prepareLabel = (TextView) rootView.findViewById(R.id.prepare_label);
            preparePrice = (TextView) rootView.findViewById(R.id.prepare_price);
            limitCount = (TextView) rootView.findViewById(R.id.limit_count);
            activityCountDown = (TextView) rootView.findViewById(R.id.activity_count_down);
            prepareCountDown = (TextView) rootView.findViewById(R.id.prepare_count_down);
        }

    }

    @Override
    public void onResume() {
        if (work != null && work.getRule() != null && work.getRule()
                .getId() > 0 && rootView != null) {
            setSaleView();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (handler != null) {
            handler.removeCallbacks(timeDownRun);
        }
        super.onPause();
    }
}
