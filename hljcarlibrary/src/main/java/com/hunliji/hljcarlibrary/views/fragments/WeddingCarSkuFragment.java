package com.hunliji.hljcarlibrary.views.fragments;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcarlibrary.models.CarShoppingCartItem;
import com.hunliji.hljcarlibrary.util.WeddingCarSession;
import com.hunliji.hljcarlibrary.util.WeddingCarSkuUtil;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcarlibrary.widgets.WeddingCarSkuItemView;
import com.hunliji.hljcommonlibrary.models.Rule;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSku;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSkuItem;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.WeddingCarRouteService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by jinxin on 2015/9/24.
 */
public class WeddingCarSkuFragment extends RefreshFragment {

    public static final String ARG_CAR_DETAIL = "car_detail";

    @BindView(R2.id.product_logo)
    ImageView productLogo;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.price)
    TextView price;
    @BindView(R2.id.sku_selected)
    TextView skuSelected;
    @BindView(R2.id.subtract)
    ImageView subtract;
    @BindView(R2.id.count)
    TextView count;
    @BindView(R2.id.plus)
    ImageView plus;
    @BindView(R2.id.inventory)
    TextView inventory;
    @BindView(R2.id.sku_layout_content)
    LinearLayout skuLayoutContent;
    @BindView(R2.id.progressbar_layout)
    RelativeLayout progressBar;
    @BindView(R2.id.cars_order_layout)
    RelativeLayout cars_order_layout;

    private RoundedImageView animStartView;
    private View animEndView;
    private View rootView;
    private WeddingCarProduct carProduct;
    private WeddingCarSkuUtil skuUtil;
    private WeddingCarSku selectCarSku;
    private Rule rule;
    private int showNum = 1;
    private boolean is_published;
    private Dialog carTeamDialog;
    private String priceString;

    /**
     * 已经选择的skuitem id 对应的是perproty id ,values对应的是选中的skuitem
     */
    private Map<Long, WeddingCarSkuItem> selectSkuItem;

    private boolean hasAddViewCompelete = false;
    private Unbinder unbinder;
    private WeddingCarRouteService routeService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            carProduct = arguments.getParcelable(ARG_CAR_DETAIL);
            if (carProduct != null) {
                rule = carProduct.getRule();
                is_published = carProduct.isPublished();
            }
        }

        initConstant();
    }

    private void initConstant() {
        selectSkuItem = new LinkedHashMap<>();
        routeService = (WeddingCarRouteService) ARouter.getInstance()
                .build(RouterPath.ServicePath.WEDDING_CAR_SERVICE)
                .navigation();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_wedding_car_sku___car, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initWidget();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAnimView();
    }

    private void initAnimView(){
        animStartView = (RoundedImageView) getActivity().findViewById(R.id.img_anim_start);
        animEndView = getActivity().findViewById(R.id.layout_car_team);

        if(carProduct == null){
            return;
        }
        int size =CommonUtil.dp2px(getContext(),56);
        Glide.with(getContext())
                .load(ImagePath.buildPath(carProduct.getCoverImage() == null ? null : carProduct
                        .getCoverImage()
                        .getImagePath())
                        .width(size)
                        .height(size)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(animStartView);
    }

    private void initWidget() {
        if (carProduct == null) {
            return;
        }
        title.setText(carProduct.getTitle());
        int size = CommonUtil.dp2px(getContext(),56);
        Glide.with(getContext())
                .load(ImagePath.buildPath(carProduct.getCoverImage() == null ? null : carProduct
                        .getCoverImage()
                        .getImagePath())
                        .width(size)
                        .height(size)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(productLogo);

        countPriceString(carProduct.getSkus());
        List<WeddingCarSku> skuList = carProduct.getSkus();
        if (skuList != null && skuList.size() > 0) {
            skuUtil = new WeddingCarSkuUtil(carProduct.getSkus());
            addSkuItemView(skuUtil.getAllShowValue());
        }

        plus.setImageResource(R.mipmap.icon_cross_add_primary_24_24);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) cars_order_layout
                .getLayoutParams();
        lp.width = CommonUtil.getDeviceSize(getContext()).x;
        lp.height = CommonUtil.getDeviceSize(getContext()).y * 3 / 4;
        lp.gravity = Gravity.BOTTOM;
        cars_order_layout.setLayoutParams(lp);
    }

    private void setSkuPrice() {
        if (selectCarSku != null) {
            int limitNum = selectCarSku.getLimitNum() - selectCarSku.getLimitSoldOut();
            String hint = String.format(getActivity().getResources()
                    .getString(R.string.label_inventory___car), String.valueOf(limitNum));
            if (rule == null || rule.getType() != 2) {
                //no inventory
                inventory.setVisibility(ViewGroup.GONE);
            } else {
                inventory.setVisibility(ViewGroup.VISIBLE);
            }
            inventory.setText(hint == null ? "" : hint);
            if (rule == null) {
                String priceTxt = String.format(getActivity().getResources()
                                .getString(R.string.label_price5___car),
                        NumberFormatUtil.formatDouble2String(selectCarSku.getActualPrice()));
                price.setText(priceTxt == null ? "" : priceTxt);
            } else {
                String priceTxt = String.format(getActivity().getResources()
                                .getString(R.string.label_price5___car),
                        NumberFormatUtil.formatDouble2String(selectCarSku.getSalePrice()));
                price.setText(priceTxt == null ? "" : priceTxt);
            }
        } else {
            inventory.setVisibility(ViewGroup.GONE);
            price.setText(priceString == null ? "" : priceString);
        }
    }


    private void countPriceString(List<WeddingCarSku> skus) {
        double maxPrice = 0;
        double minPrice = 0;
        double tempPrice = 0;
        double[] prices = new double[skus.size()];
        WeddingCarSku sku = null;
        for (int i = 0; i < skus.size(); i++) {
            sku = skus.get(i);
            if (sku == null) {
                continue;
            }
            if (rule == null) {
                tempPrice = sku.getActualPrice();
            } else {
                tempPrice = sku.getSalePrice();
            }
            prices[i] = tempPrice;
        }
        Arrays.sort(prices);
        if (prices.length <= 0) {
            return;
        }
        maxPrice = prices[prices.length - 1];
        minPrice = prices[0];
        if (minPrice == maxPrice) {
            priceString = String.format(getString(R.string.label_price5___car),
                    NumberFormatUtil.formatDouble2String(minPrice));
        } else {
            priceString = String.format(getString(R.string.label_price8___car),
                    NumberFormatUtil.formatDouble2String(minPrice),
                    NumberFormatUtil.formatDouble2String(maxPrice));
        }
        price.setText(priceString);
    }

    /**
     * 判断是不是 只有一个规格
     *
     * @param data
     * @return
     */
    private boolean allOneMode(Map<String, List<WeddingCarSkuItem>> data) {
        boolean result = true;
        for (String pro : data.keySet()) {
            List<WeddingCarSkuItem> items = data.get(pro);
            if (items.size() > 1) {
                result = false;
            }
        }
        return result;
    }

    private void addSkuItemView(Map<String, List<WeddingCarSkuItem>> data) {
        boolean allOneMode = allOneMode(data);
        for (String pro : data.keySet()) {
            WeddingCarSkuItemView view = new WeddingCarSkuItemView(getActivity());
            view.setProperty(pro);
            List<WeddingCarSkuItem> list = data.get(pro);
            if (allOneMode) {
                //只有一条默认当前sku全部选中
                view.setLayout(list, true);
                for (WeddingCarSkuItem item : list) {
                    if (selectSkuItem.containsKey(item.getPropertyId())) {
                        selectSkuItem.remove(item.getPropertyId());
                    }
                    selectSkuItem.put(item.getPropertyId(), item);
                }
                selectCarSku = skuUtil.getSelectSku(selectSkuItem);
                setSkuSelected();
                setSkuPrice();
            } else {
                view.setLayout(list);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setOnCarSkuItemOnChildChangeListener(new WeddingCarSkuItemView
                    .OnCarSkuItemOnChildChangeListener() {

                @Override
                public void onCarSkuItemChildChanged(
                        String property,
                        WeddingCarSkuItemView group,
                        FlowLayout layout,
                        View childView,
                        int index) {
                    WeddingCarSkuItem item = (WeddingCarSkuItem) childView.getTag();
                    CheckBox box = (CheckBox) childView;
                    if (!box.isChecked()) {
                        selectSkuItem.remove(item.getPropertyId());
                    } else {
                        if (selectSkuItem.containsKey(item.getPropertyId())) {
                            selectSkuItem.remove(item.getPropertyId());
                        }
                        selectSkuItem.put(item.getPropertyId(), item);
                    }
                    selectCarSku = skuUtil.getSelectSku(selectSkuItem);
                    notifySkuLayoutChanged();
                    setSkuSelected();
                    setSkuPrice();
                }
            });
            skuLayoutContent.addView(view, params);
        }
        setSkuSelected();
        hasAddViewCompelete = true;
    }

    private void removeSelectItem(List<WeddingCarSkuItem> itemList, WeddingCarSkuItem item) {
        if (itemList == null || item == null) {
            return;
        }
        Iterator<WeddingCarSkuItem> iterator = itemList.iterator();
        while (iterator.hasNext()) {
            WeddingCarSkuItem skuItem = iterator.next();
            if (skuItem.getPropertyId() == item.getPropertyId()) {
                iterator.remove();
            }
        }
    }

    private void setSkuSelected() {
        //所有的type - 已选择的type
        StringBuilder buffer = new StringBuilder();
        if (selectCarSku != null) {
            List<WeddingCarSkuItem> itemList = new ArrayList<>(selectCarSku.getSkuItem());
            if (itemList == null) {
                return;
            }
            Iterator<Long> itemIterator = selectSkuItem.keySet()
                    .iterator();
            while (itemIterator.hasNext()) {
                long key = itemIterator.next();
                WeddingCarSkuItem item = selectSkuItem.get(key);
                removeSelectItem(itemList, item);
            }
            if (itemList.isEmpty()) {
                buffer.append("已选“");
                Iterator<Long> iterator = selectSkuItem.keySet()
                        .iterator();
                while (iterator.hasNext()) {
                    long key = iterator.next();
                    WeddingCarSkuItem item = selectSkuItem.get(key);
                    buffer.append(item.getValue())
                            .append(";");
                }
                buffer.append("” ");
            } else {
                buffer.append("请选择:");
                for (WeddingCarSkuItem item : itemList) {
                    //拼接
                    buffer.append(item.getProperty())
                            .append(";");
                }
            }
        } else {
            List<String> allType = skuUtil.getAllPropertyString();
            Iterator<Long> iterator = selectSkuItem.keySet()
                    .iterator();
            while (iterator.hasNext()) {
                long key = iterator.next();
                WeddingCarSkuItem item = selectSkuItem.get(key);
                if (allType.contains(item.getProperty())) {
                    allType.remove(item.getProperty());
                }
            }
            if (allType.size() == selectSkuItem.size()) {
                buffer.append("请选择规格 ");
            } else {
                buffer.append("请选择:");
                for (String type : allType) {
                    //拼接
                    buffer.append(type)
                            .append(";");
                }
            }
        }

        if (buffer.length() > 0 && buffer.lastIndexOf(";") >= 0) {
            buffer.deleteCharAt(buffer.lastIndexOf(";"));
        }
        skuSelected.setText(buffer.toString());
    }

    private void notifySkuLayoutChanged() {
        for (int i = 0, childCount = skuLayoutContent.getChildCount(); i < childCount; i++) {
            WeddingCarSkuItemView view = (WeddingCarSkuItemView) skuLayoutContent.getChildAt(i);
            if (view == null) {
                continue;
            }
            String currentProperty = view.getProperty();
            Map<Long, WeddingCarSkuItem> selectSkuItemCopy = new LinkedHashMap<>();
            selectSkuItemCopy.putAll(selectSkuItem);
            long currentPropertyId = skuUtil.getPropertyId(currentProperty);
            if (currentPropertyId > 0) {
                if (selectSkuItemCopy.containsKey(currentPropertyId)) {
                    selectSkuItemCopy.remove(currentPropertyId);
                }
            }
            Map<String, List<WeddingCarSkuItem>> showValue = skuUtil.getShowValueBySearchItem(
                    selectSkuItemCopy);
            if (showValue == null) {
                continue;
            }
            List<WeddingCarSkuItem> item = showValue.get(currentProperty);
            view.setData(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder != null){
            unbinder.unbind();
        }
    }

    @OnClick(R2.id.layout_sku_content)
    void onSkuLayoutContent(){
        WeddingCarProductDetailActivity carDetailActivity = (WeddingCarProductDetailActivity) getActivity();
        if (carDetailActivity != null) {
            carDetailActivity.backSkuFragment();
        }
    }

    @OnClick(R2.id.close_btn)
    public void closeSku() {
        WeddingCarProductDetailActivity carDetailActivity = (WeddingCarProductDetailActivity) getActivity();
        if (carDetailActivity != null) {
            carDetailActivity.backSkuFragment();
        }
    }


    @OnClick(R2.id.subtract)
    public void subtract() {
        selectCarSku = skuUtil.getSelectSku(selectSkuItem);
        if (selectCarSku == null) {
            ToastUtil.showToast(getActivity(), null, R.string.msg_product_choice_sku___car);
            return;
        }
        // showNum  must more than 0
        if (showNum <= 1) {
            return;
        } else {
            showNum--;
            if (showNum <= 1) {
                plus.setImageResource(R.mipmap.icon_cross_add_primary_24_24);
                subtract.setImageResource(R.mipmap.icon_subtraction_gray_24_4);
            } else {
                plus.setImageResource(R.mipmap.icon_cross_add_primary_24_24);
                subtract.setImageResource(R.mipmap.icon_subtraction_primary_24_4);
            }
        }
        count.setText(String.valueOf(showNum));
    }

    @OnClick(R2.id.plus)
    public void plus() {
        selectCarSku = skuUtil.getSelectSku(selectSkuItem);
        if (selectCarSku == null) {
            ToastUtil.showToast(getActivity(), null, R.string.msg_product_choice_sku___car);
            return;
        }
        // just limit quantity rule ,has inventory
        if (rule == null || rule.getType() != 2) {
            // no num limit
            showNum++;
            plus.setImageResource(showNum <= 1 ? R.mipmap.icon_cross_add_primary_24_24 : R
                    .mipmap.icon_cross_add_primary_24_24);
            subtract.setImageResource(showNum <= 1 ? R.mipmap.icon_subtraction_gray_24_4 : R
                    .mipmap.icon_subtraction_primary_24_4);
        } else {
            int limitNum = selectCarSku.getLimitNum() - selectCarSku.getLimitSoldOut();
            if (showNum >= limitNum) {
                ToastUtil.showToast(getActivity(), null, R.string.msg_product_count_out___car);
                return;
            } else {
                showNum++;
                if (showNum >= limitNum) {
                    plus.setImageResource(R.mipmap.icon_cross_add_gray_24_24);
                    subtract.setImageResource(R.mipmap.icon_subtraction_primary_24_4);
                } else {
                    plus.setImageResource(R.mipmap.icon_cross_add_primary_24_24);
                    subtract.setImageResource(R.mipmap.icon_subtraction_primary_24_4);
                }
            }
        }
        count.setText(String.valueOf(showNum));
    }



    @OnClick(R2.id.btn_confirm)
    public void confirm() {
        if (!hasAddViewCompelete) {
            return;
        }
        selectCarSku = skuUtil.getSelectSku(selectSkuItem);
        if (selectCarSku == null) {
            ToastUtil.showToast(getActivity(), null, R.string.msg_product_choice_sku___car);
            return;
        }
        List<WeddingCarSkuItem> itemList = new ArrayList<>(selectCarSku.getSkuItem());
        if (itemList == null) {
            return;
        }
        Iterator<Long> itemIterator = selectSkuItem.keySet()
                .iterator();
        while (itemIterator.hasNext()) {
            long key = itemIterator.next();
            WeddingCarSkuItem item = selectSkuItem.get(key);
            removeSelectItem(itemList, item);
        }
        if (!itemList.isEmpty()) {
            ToastUtil.showToast(getActivity(), null, R.string.msg_product_choice_sku___car);
            return;
        }
        if (!is_published) {
            //下架
            ToastUtil.showToast(getActivity(), null, R.string.msg_product_sold_out___car);
        } else {
            int limitNum = selectCarSku.getLimitNum() - selectCarSku.getLimitSoldOut();
            if (rule != null && rule.getType() == 2) {
                //有库存
                int carCount = Integer.MIN_VALUE;
                if (!(carProduct != null && routeService != null)) {
                    return;
                }
                carCount = WeddingCarSession.getInstance()
                        .getCarCartCount(getContext(), carProduct.getId());
                if (limitNum <= 0) {
                    ToastUtil.showToast(getActivity(), null, R.string.msg_product_sell_out___car);
                    return;
                } else if (limitNum < carCount + showNum) {
                    ToastUtil.showToast(getActivity(), null, R.string.msg_product_sell_out___car);
                    return;
                }
            }
            WeddingCarSession carSession = WeddingCarSession.getInstance();
            if (carSession.carCartCityChecked(getContext(), carProduct.getCityCode())) {
                carSession.addCarCart(getContext(),
                        new CarShoppingCartItem(carProduct, selectCarSku, showNum));
                ToastUtil.showToast(getActivity(), null, R.string.msg_product_add_completed___car);
                showAnimation();
            } else {
                showAddCarTeamDialog();
            }
        }
    }

    private void showAddCarTeamDialog() {
        if (carTeamDialog == null) {
            carTeamDialog = new Dialog(getActivity(), R.style.BubbleDialogTheme);
            carTeamDialog.setContentView(R.layout.hlj_dialog_confirm___cm);
            TextView msgAlertTv = (TextView) carTeamDialog.findViewById(R.id.tv_alert_msg);
            Button confirmBtn = (Button) carTeamDialog.findViewById(R.id.btn_confirm);
            final Button cancelBtn = (Button) carTeamDialog.findViewById(R.id.btn_cancel);
            msgAlertTv.setText(R.string.msg_confirm_caraddteam___car);
            confirmBtn.setText(R.string.label_join_team___car);
            cancelBtn.setText(R.string.label_wrong_action___car);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carTeamDialog.dismiss();
                    if (!(carProduct != null && routeService != null)) {
                        return;
                    }
                    WeddingCarSession carSession = WeddingCarSession.getInstance();
                    if (carSession != null) {
                        carSession.clearCart(getContext());
                        carSession.addCarCart(getContext(),
                                new CarShoppingCartItem(carProduct, selectCarSku, showNum));
                    }
                    ToastUtil.showToast(getActivity(), null, R.string.msg_product_add_completed___car);
                    showAnimation();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carTeamDialog.dismiss();
                }
            });
            Window window = carTeamDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(getContext());
            params.width = Math.round(point.x * 27 / 32);
            window.setGravity(Gravity.CENTER);
            window.setAttributes(params);
        }
        carTeamDialog.show();
    }

    private void showAnimation() {
        animStartView.setVisibility(View.VISIBLE);
        animStartView.setAlpha(1.0f);
        final AnimationSet animationSet = new AnimationSet(true);

        final int[] startPosition = CommonUtil.getViewCenterPositionOnScreen(animStartView);
        final int[] endPosition = CommonUtil.getViewCenterPositionOnScreen(animEndView);
        int widthDistance = endPosition[0] - startPosition[0];
        int heightDistance = endPosition[1] - startPosition[1];

        ScaleAnimation scaleAnimation1 = new ScaleAnimation(0,
                1.2f,
                0,
                1.2f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation1.setDuration(200);
        final ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.2f,
                1,
                1.2f,
                1,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation2.setDuration(200);

        ScaleAnimation scaleAnimation3 = new ScaleAnimation(1,
                0.2f,
                1,
                0.2f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation3.setInterpolator(new AccelerateInterpolator());
        scaleAnimation3.setDuration(1200);
        TranslateAnimation animation1 = new TranslateAnimation(0, widthDistance, 0, heightDistance);
        animation1.setDuration(1200);
        animationSet.addAnimation(scaleAnimation3);
        animationSet.addAnimation(animation1);

        final AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0);
        alphaAnimation.setDuration(200);
        alphaAnimation.setStartOffset(1000);
        animationSet.addAnimation(alphaAnimation);

        scaleAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animStartView.clearAnimation();
                animStartView.startAnimation(scaleAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        scaleAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animStartView.clearAnimation();
                animStartView.startAnimation(animationSet);
                closeSku();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animStartView.setVisibility(View.GONE);
                animStartView.setAlpha(0.0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animStartView.startAnimation(scaleAnimation1);
    }

    @Override
    public void refresh(Object... params) {

    }
}