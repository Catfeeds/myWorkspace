package me.suncloud.marrymemo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LongSparseArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.product.Sku;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;
import me.suncloud.marrymemo.modulehelper.ModuleUtils;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.ProductOrderConfirmActivity;
import me.suncloud.marrymemo.widget.FlowLayout;
import rx.Subscription;

/**
 * Created by Suncloud on 2015/8/6.
 */
public class ProductSkuFragment extends Fragment implements FlowLayout
        .OnChildCheckedChangeListener, TextWatcher {

    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.sku_name)
    TextView skuName;
    @BindView(R.id.count)
    EditText countView;
    @BindView(R.id.inventory)
    TextView inventory;
    @BindView(R.id.plus)
    ImageView plusView;
    @BindView(R.id.subtract)
    ImageView subtractView;
    TextView tvCartCount;
    @BindView(R.id.skus_layout)
    FlowLayout skusLayout;
    private ShopProduct product;
    private Sku currentSku;
    private Toast toast;
    private boolean addCart;
    private InputMethodManager imm;
    private RoundedImageView animStartView;
    private View animEndView;
    private Unbinder unbinder;
    private LongSparseArray<Integer> countArray;
    private Subscription addSubscription;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View rootView = inflater.inflate(R.layout.fragment_product_sku, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClose();
            }
        });
        TextView title = (TextView) rootView.findViewById(R.id.title);
        ImageView productLogo = (ImageView) rootView.findViewById(R.id.product_logo);
        countView.addTextChangedListener(this);
        countView.setEnabled(false);
        if (getArguments() != null) {
            product = getArguments().getParcelable("product");
            addCart = getArguments().getBoolean("addCart", true);
        }
        if (product != null) {
            title.setText(product.getTitle());
            int size = Math.round(getResources().getDisplayMetrics().density * 56);
            String url = JSONUtil.getImagePath2(product.getCoverPath(), size);
            if (!JSONUtil.isEmpty(url)) {
                ImageLoadTask task = new ImageLoadTask(productLogo);
                productLogo.setTag(url);
                task.loadImage(url,
                        size,
                        ScaleMode.ALL,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            }
            if (product.getSkus() != null && !product.getSkus()
                    .isEmpty()) {
                countArray = new LongSparseArray<>();
                int height = Math.round(getResources().getDisplayMetrics().density * 30);
                for (Sku sku : product.getSkus()) {
                    CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.sku_choice_view, null);
                    checkBox.setText(sku.getName());
                    checkBox.setTag(sku);
                    checkBox.setEnabled(sku.getShowNum() > 0);
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup
                            .LayoutParams.WRAP_CONTENT,
                            height);
                    skusLayout.addView2(checkBox, params);
                }
                skusLayout.setOnChildCheckedChangeListener(this);
                if (skusLayout.getChildCount() == 1) {
                    ((CheckBox) skusLayout.getChildAt(0)).setChecked(true);
                    skusLayout.getChildAt(0)
                            .setEnabled(false);
                } else if (product.getFloorPrice() == product.getTopPrice()) {
                    price.setText(getString(R.string.label_price,
                            NumberFormatUtil.formatDouble2StringWithTwoFloat(product
                                    .getFloorPrice())));
                } else {
                    price.setText(getString(R.string.label_shop_by_price,
                            NumberFormatUtil.formatDouble2StringWithTwoFloat(product
                                    .getFloorPrice()),
                            NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getTopPrice
                                    ())));
                }
            }
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        tvCartCount = getActivity().findViewById(R.id.tv_cart_count);
        animStartView = (RoundedImageView) getActivity().findViewById(R.id.shop_product_skuImage);
        animEndView = getActivity().findViewById(R.id.btn_shopping_cart);

        int size = Math.round(getResources().getDisplayMetrics().density * 56);
        ImageLoadTask task = new ImageLoadTask(animStartView, 0);
        String url = JSONUtil.getImagePath2(product.getCoverPath(), size);
        if (!JSONUtil.isEmpty(url)) {
            animStartView.setTag(url);
            task.loadImage(url,
                    size,
                    ScaleMode.ALL,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        }
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.close_btn)
    public void onClose() {
        //        dismiss();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    private void showToast(int id) {
        if (toast == null) {
            toast = Toast.makeText(this.getContext(), id, Toast.LENGTH_SHORT);
        } else {
            toast.setText(id);
        }
        toast.show();
    }

    @OnClick(R.id.subtract)
    public void onSubtract() {
        if (currentSku != null) {
            if (countSubtract(currentSku)) {
                int count = getCount(currentSku);
                countView.setText(String.valueOf(count));
                plusView.setImageResource(count < currentSku.getShowNum() ? R.drawable
                        .icon_cross_add_primary_24_24 : R.drawable.icon_cross_add_gray_24_24);
                subtractView.setImageResource(count > 1 ? R.drawable
                        .icon_subtraction_primary_24_4 : R.drawable.icon_subtraction_gray_24_4);
            }
        } else {
            showToast(R.string.msg_product_choice_sku);
        }
    }

    @OnClick(R.id.plus)
    public void onPlus() {
        if (currentSku != null) {
            if (countPlus(currentSku)) {
                int count = getCount(currentSku);
                countView.setText(String.valueOf(count));
                plusView.setImageResource(count < currentSku.getShowNum() ? R.drawable
                        .icon_cross_add_primary_24_24 : R.drawable.icon_cross_add_gray_24_24);
                subtractView.setImageResource(count > 1 ? R.drawable
                        .icon_subtraction_primary_24_4 : R.drawable.icon_subtraction_gray_24_4);
            } else {
                showToast(R.string.msg_product_count_out);
            }
        } else {
            showToast(R.string.msg_product_choice_sku);
        }
    }

    @OnClick(R.id.btn_confirm)
    public void onConfirm() {
        if (!AuthUtil.loginBindCheck(getActivity()) || product == null) {
            return;
        }
        if (addSubscription != null && !addSubscription.isUnsubscribed()) {
            return;
        }
        if (currentSku == null) {
            showToast(R.string.msg_product_choice_sku);
        } else if (getCount(currentSku) == 0) {
            showToast(R.string.msg_product_count_empty);
        } else {
            final ShoppingCartGroup cartGroup = ModuleUtils.initShoppingCartGroup(product,
                    currentSku,
                    getCount(currentSku));
            if (addCart) {
                new HljTracker.Builder(getContext()).eventableType("Article")
                        .eventableId(product.getId())
                        .action("in_cart")
                        .additional(String.valueOf(currentSku.getId()))
                        .sid("AA1/H1")
                        .pos(2)
                        .desc("加入购物车")
                        .build()
                        .send();
                addSubscription = ProductApi.addCart(currentSku.getId(),
                        product.getId(),
                        getCount(currentSku))
                        .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
                                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                                .setOnNextListener(new SubscriberOnNextListener<Long>() {
                                    @Override
                                    public void onNext(Long id) {
                                        if (id > 0) {
                                            cartGroup.getCartList()
                                                    .get(0)
                                                    .setId(id);
                                            Session.getInstance()
                                                    .setNewCart(true);
                                            Session.getInstance().refreshCartItemsCount(getActivity());
                                            showAnimation();
                                        }

                                    }
                                })
                                .build());
            } else {
                new HljTracker.Builder(getContext()).eventableType("Article")
                        .eventableId(product.getId())
                        .action("buy")
                        .additional(String.valueOf(currentSku.getId()))
                        .sid("AA1/H1")
                        .pos(3)
                        .desc("立即购买")
                        .build()
                        .send();
                Intent intent = new Intent(getContext(), ProductOrderConfirmActivity.class);
                intent.putExtra("cart_group", cartGroup);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, 0);
                onClose();

            }
        }
    }

    /**
     * alpha y = -15/8 * x + 13/8
     * scale y = -15/13 * x + 1
     * trans y = (widthDistance or heightDistance)*15L/13 * x
     */
    private void showAnimation() {
        animStartView.setVisibility(View.VISIBLE);
        animStartView.setAlpha(1.0f);
        final AnimationSet animationSet = new AnimationSet(true);

        final int[] startPosition = JSONUtil.getViewCenterPositionOnScreen(animStartView);
        final int[] endPosition = JSONUtil.getViewCenterPositionOnScreen(animEndView);
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
                showToast(R.string.msg_product_add_completed);
                onClose();
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


    public void refresh() {
        int count = 0;
        if (currentSku != null) {
            count = getCount(currentSku);
            if (count == 0) {
                count = 1;
                countArray.put(currentSku.getId(), count);
            }
            price.setText(getString(R.string.label_price,
                    NumberFormatUtil.formatDouble2StringWithTwoFloat(currentSku.getShowPrice())));
            skuName.setText(getString(R.string.label_product_sku, currentSku.getName()));
            countView.setText(String.valueOf(count));
            inventory.setText(getString(R.string.label_inventory, currentSku.getShowNum() + ""));
        } else {
            if (product.getFloorPrice() == product.getTopPrice()) {
                price.setText(getString(R.string.label_price,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getFloorPrice())));
            } else {
                price.setText(getString(R.string.label_shop_by_price,
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getFloorPrice()),
                        NumberFormatUtil.formatDouble2StringWithTwoFloat(product.getTopPrice())));
            }
            skuName.setText(null);
            inventory.setText(null);
        }
        plusView.setImageResource(currentSku != null && count < currentSku.getShowNum() ? R
                .drawable.icon_cross_add_primary_24_24 : R.drawable.icon_cross_add_gray_24_24);
        subtractView.setImageResource(currentSku != null && count > 1 ? R.drawable
                .icon_subtraction_primary_24_4 : R.drawable.icon_subtraction_gray_24_4);
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null) {
            View view = getActivity().findViewById(R.id.info_content);
            if (view != null && getFragmentManager().getBackStackEntryCount() == 0) {
                view.setAlpha(0);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (addSubscription != null && !addSubscription.isUnsubscribed()) {
            addSubscription.unsubscribe();
        }
        skusLayout.setOnChildCheckedChangeListener(null);
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        hideSoftInput();
        super.onPause();
    }

    private void hideSoftInput() {
        imm.hideSoftInputFromWindow(countView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onCheckedChange(View childView, int index) {
        Sku sku = (Sku) childView.getTag();
        if (((CompoundButton) childView).isChecked() && !sku.equals(currentSku)) {
            currentSku = sku;
            countView.setEnabled(true);
            countView.setSelection(countView.length());
        } else if (!((CompoundButton) childView).isChecked() && sku.equals(currentSku)) {
            currentSku = null;
            countView.setEnabled(false);
        }
        refresh();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }


    @Override
    public void afterTextChanged(Editable s) {
        if (currentSku == null) {
            return;
        }
        if (s.length() == 0) {
            countArray.put(currentSku.getId(), 0);
            plusView.setImageResource(R.drawable.icon_cross_add_primary_24_24);
            subtractView.setImageResource(R.drawable.icon_subtraction_gray_24_4);
            return;
        }
        int textCount = 1;
        try {
            textCount = Integer.valueOf(s.toString());
        } catch (NumberFormatException ignored) {
        }
        int count = Math.max(1, Math.min(textCount, currentSku.getShowNum()));
        int select = countView.getSelectionStart();
        countView.removeTextChangedListener(this);
        countArray.put(currentSku.getId(), count);
        countView.setText(String.valueOf(count));
        plusView.setImageResource(count < currentSku.getShowNum() ? R.drawable
                .icon_cross_add_primary_24_24 : R.drawable.icon_cross_add_gray_24_24);
        subtractView.setImageResource(count > 1 ? R.drawable.icon_subtraction_primary_24_4 : R
                .drawable.icon_subtraction_gray_24_4);
        countView.addTextChangedListener(this);
        if (select < countView.length()) {
            countView.setSelection(Math.max(1, Math.min(select, countView.length() - 1)));
        } else {
            countView.setSelection(countView.length());
        }
    }

    private int getCount(Sku sku) {
        if (countArray.get(sku.getId()) == null) {
            return 1;
        }
        return countArray.get(sku.getId());
    }

    private boolean countSubtract(Sku sku) {
        int count = getCount(sku);
        if (count > 1) {
            countArray.put(sku.getId(), --count);
            return true;
        }
        return false;
    }

    private boolean countPlus(Sku sku) {
        int count = getCount(sku);
        if (count < sku.getShowNum()) {
            countArray.put(sku.getId(), ++count);
            return true;
        }
        return false;
    }
}
