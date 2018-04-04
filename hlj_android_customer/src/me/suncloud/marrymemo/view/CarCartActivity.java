package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcarlibrary.models.CarCartCheck;
import com.hunliji.hljcarlibrary.models.CarShoppingCartItem;
import com.hunliji.hljcarlibrary.util.WeddingCarSession;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcarlibrary.views.activities.WeddingCarSubPageActivity;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSkuItem;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.HljTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * 我的车队
 * Created by Suncloud on 2015/10/13.
 */
@Route(path = RouterPath.IntentPath.Customer.WEDDING_CAR_TEAM_ACTIVITY)
public class CarCartActivity extends HljBaseActivity implements View.OnClickListener,
        PullToRefreshBase
        .OnRefreshListener<ListView>, AdapterView.OnItemClickListener {

    public final String ARG_CITY = "city";

    @BindView(R.id.cb_select_all)
    CheckBox cbSelectAll;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_saved_money)
    TextView tvSavedMoney;
    @BindView(R.id.list)
    PullToRefreshListView list;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout;
    private ArrayList<CarShoppingCartItem> cartItems;
    private ArrayList<CarShoppingCartItem> nowItems;
    private CarCartAdapter carCartAdapter;
    private int size;
    private boolean isLoad;
    private Dialog dialog;
    private Dialog invalidDialog;
    private double totalPrice;
    private boolean isCreate;
    private Dialog progressDialog;
    private City city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            city = getIntent().getParcelableExtra(ARG_CITY);
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        size = Math.round(dm.density * 64);
        cartItems = new ArrayList<>();
        carCartAdapter = new CarCartAdapter();
        setContentView(R.layout.activity_car_cart);
        ButterKnife.bind(this);
        list.setAdapter(carCartAdapter);
        list.setOnRefreshListener(this);
        list.setOnItemClickListener(this);
        progressBar.setVisibility(View.VISIBLE);
        isCreate = true;
        new HljTracker.Builder(this).eventableType("Car")
                .action("inspect")
                .build()
                .send();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (invalidDialog != null) {
                    invalidDialog.dismiss();
                }
                break;
            case R.id.btn_empty_hint:
                Intent intent = new Intent(this, WeddingCarSubPageActivity.class);
                intent.putExtra(WeddingCarSubPageActivity.ARG_POSITION, true);
                if (city != null) {
                    intent.putExtra(WeddingCarSubPageActivity.ARG_CITY_ID, city.getCid());
                    intent.putExtra(WeddingCarSubPageActivity.ARG_CITY_NAME, city.getName());
                }
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            boolean isCreate = nowItems == null || nowItems.isEmpty();
            nowItems = WeddingCarSession.getInstance()
                    .getCarCartItems(this);
            if (nowItems != null && !nowItems.isEmpty()) {
                String url = Constants.getAbsUrl(Constants.HttpPath.CHECK_IS_PUBLISHED_URL);
                for (CarShoppingCartItem item : nowItems) {
                    if (isCreate) {
                        item.setChecked(false);
                    }
                    if (item.getCarSku() != null) {
                        url += (url.contains("?") ? "&" : "?") + "ids[]=" + item.getId();
                    }
                }
                new CheckIsPublishedTask().executeOnExecutor(Constants.LISTTHEADPOOL, url);
                bottomLayout.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                list.onRefreshComplete();
                View emptyView = list.getRefreshableView()
                        .getEmptyView();
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    list.setEmptyView(emptyView);
                }
                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
                ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);
                TextView btnEmptyHint = (TextView) emptyView.findViewById(R.id.btn_empty_hint);

                imgEmptyHint.setVisibility(View.VISIBLE);
                imgNetHint.setVisibility(View.GONE);
                textEmptyHint.setVisibility(View.VISIBLE);
                btnEmptyHint.setVisibility(View.VISIBLE);
                textEmptyHint.setText(R.string.msg_car_cart_empty);
                btnEmptyHint.setText(R.string.btn_add_car);
                btnEmptyHint.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CarShoppingCartItem item = (CarShoppingCartItem) parent.getAdapter()
                .getItem(position);
        if (item != null && item.getCarProduct() != null && item.getCarProduct()
                .getId() > 0) {
            Intent intent = new Intent(this, WeddingCarProductDetailActivity.class);
            intent.putExtra(WeddingCarProductDetailActivity.ARG_ID,
                    item.getCarProduct()
                            .getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    private class CheckIsPublishedTask extends AsyncTask<String, Object, JSONArray> {


        private CheckIsPublishedTask() {
            isLoad = true;
        }

        private CheckIsPublishedTask(ArrayList<CarShoppingCartItem> cartItems) {
            this.checkedItems = cartItems;
        }

        private ArrayList<CarShoppingCartItem> checkedItems;

        @Override
        protected JSONArray doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONArray("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            progressBar.setVisibility(View.GONE);
            if (checkedItems != null && !checkedItems.isEmpty()) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                boolean isChange = false;
                ArrayList<CarShoppingCartItem> items = new ArrayList<>();
                if (jsonArray != null) {
                    for (int i = 0; i < size; i++) {
                        CarCartCheck check = new CarCartCheck(jsonArray.optJSONObject(i));
                        for (CarShoppingCartItem cartItem : checkedItems) {
                            if (cartItem.getId() == check.getId()) {
                                CarShoppingCartItem item = cartItem.cartClone();
                                item.setCarCartCheck(check);
                                if (!check.isPublished() || (check.getShowNum() >= 0 && item
                                        .getQuantity() > check.getShowNum())) {
                                    isChange = true;
                                    if (check.isPublished() && check.getShowNum() > 0) {
                                        item.setQuantity(check.getShowNum());
                                        items.add(item);
                                    }
                                } else {
                                    items.add(item);
                                }
                            }
                        }
                    }
                }
                if (!isChange && items.isEmpty()) {
                    items.addAll(checkedItems);
                }
                if (items.isEmpty()) {
                    Util.showToast(CarCartActivity.this, null, R.string.hint_car_cart_all_invalid);
                    onRefresh(null);
                    return;
                } else if (isChange) {
                    showHintDialog(items);
                } else {
                    Intent intent = new Intent(CarCartActivity.this,
                            NewCarOrderConfirmActivity.class);
                    intent.putParcelableArrayListExtra("items", items);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            } else {
                list.onRefreshComplete();
                isLoad = false;
                if (jsonArray != null) {
                    int size = jsonArray.length();
                    ArrayList<Long> checkIds = new ArrayList<>();
                    for (CarShoppingCartItem item : cartItems) {
                        if (item.isValid() && item.isChecked()) {
                            checkIds.add(item.getId());
                        }
                    }
                    cartItems.clear();
                    for (int i = 0; i < size; i++) {
                        CarCartCheck check = new CarCartCheck(jsonArray.optJSONObject(i));
                        for (CarShoppingCartItem cartItem : nowItems) {
                            if (cartItem.getId() == check.getId()) {
                                CarShoppingCartItem item = cartItem.cartClone();
                                item.setCarCartCheck(check);
                                item.setChecked(checkIds.contains(cartItem.getId()));
                                if (check.getShowNum() >= 0 && item.getQuantity() > check
                                        .getShowNum()) {
                                    CarShoppingCartItem invalidItem = item.cartClone();
                                    invalidItem.setIsValid(false);
                                    invalidItem.setQuantity(item.getQuantity() - check.getShowNum
                                            ());
                                    cartItems.add(invalidItem);
                                    if (check.getShowNum() > 0) {
                                        item.setQuantity(check.getShowNum());
                                        cartItems.add(item);
                                    }
                                } else {
                                    cartItems.add(item);
                                }
                            }
                        }
                    }
                    Collections.sort(cartItems, new Comparator<CarShoppingCartItem>() {

                        @Override
                        public int compare(CarShoppingCartItem cart1, CarShoppingCartItem cart2) {
                            if (cart1.isValid()) {
                                return -1;
                            } else if (cart2.isValid()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }

                    });
                    if (isCreate) {
                        isCreate = false;
                        cbSelectAll.setChecked(true);
                        onSelectAll();
                    } else {
                        setPriceInfo();
                        carCartAdapter.notifyDataSetChanged();
                    }
                    bottomLayout.setVisibility(View.VISIBLE);
                }
            }
            if (cartItems.isEmpty()) {
                View emptyView = list.getRefreshableView()
                        .getEmptyView();
                bottomLayout.setVisibility(View.GONE);
                if (emptyView == null) {
                    emptyView = findViewById(R.id.empty_hint_layout);
                    list.setEmptyView(emptyView);
                }
                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id.img_empty_hint);
                ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                TextView textEmptyHint = (TextView) emptyView.findViewById(R.id.text_empty_hint);
                TextView btnEmptyHint = (TextView) emptyView.findViewById(R.id.btn_empty_hint);

                imgEmptyHint.setVisibility(View.GONE);
                imgNetHint.setVisibility(View.VISIBLE);
                textEmptyHint.setVisibility(View.VISIBLE);
                btnEmptyHint.setVisibility(View.GONE);
                textEmptyHint.setText(R.string.net_disconnected);
            }
            super.onPostExecute(jsonArray);
        }
    }

    private void showHintDialog(final ArrayList<CarShoppingCartItem> items) {
        if (invalidDialog != null && invalidDialog.isShowing()) {
            return;
        }
        if (invalidDialog == null) {
            invalidDialog = new Dialog(CarCartActivity.this, R.style.BubbleDialogTheme);
            invalidDialog.setContentView(R.layout.dialog_confirm);
            invalidDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(CarCartActivity.this);
            TextView textView = (TextView) invalidDialog.findViewById(R.id.tv_alert_msg);
            textView.setText(R.string.hint_car_cart_invalid);
            Window window = invalidDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(CarCartActivity.this);
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        invalidDialog.findViewById(R.id.btn_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        invalidDialog.dismiss();
                        onRefresh(null);
                        Intent intent = new Intent(CarCartActivity.this,
                                NewCarOrderConfirmActivity.class);
                        intent.putExtra("items", items);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
        invalidDialog.show();
    }

    @OnClick(R.id.cb_select_all)
    public void onSelectAll() {
        for (CarShoppingCartItem item : cartItems) {
            item.setChecked(cbSelectAll.isChecked());
        }
        setPriceInfo();
        carCartAdapter.notifyDataSetChanged();
    }

    private void setPriceInfo() {
        totalPrice = 0;
        double totalMarketPrice = 0;
        boolean isAll = !cartItems.isEmpty() && cartItems.get(0)
                .isValid();
        for (CarShoppingCartItem item : cartItems) {
            if (item.isValid()) {
                if (item.isChecked()) {
                    totalMarketPrice += item.getCarCartCheck()
                            .getMarketPrice() * item.getQuantity();
                    totalPrice += item.getCarCartCheck()
                            .getShowPrice() * item.getQuantity();
                } else {
                    isAll = false;
                }
            }
        }
        tvTotalPrice.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2String((totalPrice))));
        tvSavedMoney.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2String((totalMarketPrice - totalPrice))));
        cbSelectAll.setChecked(isAll);
    }

    public void onConfirm(View view) {
        ArrayList<CarShoppingCartItem> selectItem = new ArrayList<>();
        String url = Constants.getAbsUrl(Constants.HttpPath.CHECK_IS_PUBLISHED_URL);
        for (CarShoppingCartItem item : cartItems) {
            if (item.isValid() && item.isChecked()) {
                selectItem.add(item);
                url += (url.contains("?") ? "&" : "?") + "ids[]=" + item.getId();
            }
        }
        if (selectItem.isEmpty()) {
            Util.showToast(this, null, R.string.msg_empty_cart_select);
            return;
        }
        if (progressDialog == null) {
            progressDialog = DialogUtil.createProgressDialog(this);
        }
        progressDialog.show();
        new HljTracker.Builder(this).eventableType("Car")
                .action("post")
                .additional(String.valueOf(totalPrice))
                .build()
                .send();
        new CheckIsPublishedTask(selectItem).executeOnExecutor(Constants.LISTTHEADPOOL, url);
    }

    private class OnCartCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        private CarShoppingCartItem cartItem;

        public void setCartItem(CarShoppingCartItem cartItem) {
            this.cartItem = cartItem;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked != cartItem.isChecked()) {
                cartItem.setChecked(isChecked);
                carCartAdapter.notifyDataSetChanged();
                setPriceInfo();
            }
        }
    }

    private class OnCartCountClickListener implements View.OnClickListener {

        private CarShoppingCartItem cartItem;

        private OnCartCountClickListener(CarShoppingCartItem cartItem) {
            this.cartItem = cartItem;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.plus:
                    if (cartItem.quantityPlus()) {
                        WeddingCarSession.getInstance()
                                .cartQuantityChange(CarCartActivity.this, cartItem.getId(), 0);
                        setPriceInfo();
                        carCartAdapter.notifyDataSetChanged();
                    } else {
                        Util.showToast(CarCartActivity.this,
                                null,
                                R.string.msg_cannot_add_more_quantity);
                    }
                    break;
                case R.id.subtract:
                    if (cartItem.quantitySubtract()) {
                        WeddingCarSession.getInstance()
                                .cartQuantityChange(CarCartActivity.this, cartItem.getId(), 1);
                        setPriceInfo();
                        carCartAdapter.notifyDataSetChanged();
                    } else {
                        Util.showToast(CarCartActivity.this,
                                null,
                                R.string.msg_cannot_subtract_more_quantity);
                    }
                    break;
            }
        }
    }

    private class OnCartDeleteClickListener implements View.OnClickListener {

        private CarShoppingCartItem cartItem;

        private OnCartDeleteClickListener(CarShoppingCartItem cartItem) {
            this.cartItem = cartItem;
        }

        @Override
        public void onClick(View v) {
            if (dialog != null && dialog.isShowing()) {
                return;
            }
            if (dialog == null) {
                dialog = new Dialog(CarCartActivity.this, R.style.BubbleDialogTheme);
                dialog.setContentView(R.layout.dialog_confirm);
                dialog.findViewById(R.id.btn_cancel)
                        .setOnClickListener(CarCartActivity.this);
                TextView textView = (TextView) dialog.findViewById(R.id.tv_alert_msg);
                textView.setText(R.string.msg_delete_car_cart_item);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                Point point = JSONUtil.getDeviceSize(CarCartActivity.this);
                params.width = Math.round(point.x * 27 / 32);
                window.setAttributes(params);
            }
            dialog.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            cartItems.remove(cartItem);
                            WeddingCarSession.getInstance()
                                    .removeCarCart(CarCartActivity.this, cartItem);
                            new HljTracker.Builder(CarCartActivity.this).eventableId(cartItem
                                    .getCarProduct()
                                    .getId())
                                    .eventableType("Car")
                                    .action("remove")
                                    .build()
                                    .send();
                            if (cartItems.isEmpty()) {
                                View emptyView = list.getRefreshableView()
                                        .getEmptyView();
                                if (emptyView == null) {
                                    emptyView = findViewById(R.id.empty_hint_layout);
                                    list.setEmptyView(emptyView);
                                }
                                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                                        .img_empty_hint);
                                ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id
                                        .img_net_hint);
                                TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                                        .text_empty_hint);
                                TextView btnEmptyHint = (TextView) emptyView.findViewById(R.id
                                        .btn_empty_hint);

                                imgEmptyHint.setVisibility(View.VISIBLE);
                                imgNetHint.setVisibility(View.GONE);
                                textEmptyHint.setVisibility(View.VISIBLE);
                                btnEmptyHint.setVisibility(View.VISIBLE);
                                textEmptyHint.setText(R.string.msg_car_cart_empty);
                                btnEmptyHint.setText(R.string.btn_add_car);
                                btnEmptyHint.setOnClickListener(CarCartActivity.this);
                                bottomLayout.setVisibility(View.GONE);
                            } else {
                                bottomLayout.setVisibility(View.VISIBLE);
                            }
                            carCartAdapter.notifyDataSetChanged();
                            setPriceInfo();
                        }
                    });
            dialog.show();
        }
    }

    class ViewHolder {
        @BindView(R.id.empty_view)
        RelativeLayout emptyView;
        @BindView(R.id.divider1)
        View divider1;
        @BindView(R.id.divider2)
        View divider2;
        @BindView(R.id.cb_item)
        CheckBox cbItem;
        @BindView(R.id.tv_invalid)
        TextView tvInvalid;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_sku_info)
        TextView tvSkuInfo;
        @BindView(R.id.subtract)
        ImageView subtract;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.plus)
        ImageView plus;
        @BindView(R.id.quantity_set_layout)
        LinearLayout quantitySetLayout;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_market_price)
        TextView tvMarketPrice;
        @BindView(R.id.btn_delete)
        ImageButton btnDelete;
        OnCartCheckedChangeListener onCartCheckedChangeListener;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            tvMarketPrice.getPaint()
                    .setAntiAlias(true);
            tvMarketPrice.getPaint()
                    .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        }
    }

    class CarCartAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cartItems.size();
        }

        @Override
        public Object getItem(int position) {
            return cartItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.car_cart_list_item,
                        parent,
                        false);
                holder = new ViewHolder(convertView);
                holder.onCartCheckedChangeListener = new OnCartCheckedChangeListener();
                holder.cbItem.setOnCheckedChangeListener(holder.onCartCheckedChangeListener);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            CarShoppingCartItem carShoppingCartItem = cartItems.get(position);
            Glide.with(CarCartActivity.this)
                    .load(ImagePath.buildPath(carShoppingCartItem.getCarProduct()
                            .getCoverImage() == null ? null : carShoppingCartItem.getCarProduct()
                            .getCoverImage()
                            .getImagePath())
                            .width(size)
                            .height(size)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(holder.imgCover);

            holder.tvTitle.setText(carShoppingCartItem.getCarProduct()
                    .getTitle());
            holder.tvPrice.setText(getString(R.string.label_price5,
                    Util.formatDouble2String(carShoppingCartItem.getCarCartCheck()
                            .getShowPrice())));
            if (carShoppingCartItem.getCarCartCheck()
                    .getMarketPrice() > 0) {
                holder.tvMarketPrice.setVisibility(View.VISIBLE);
                holder.tvMarketPrice.setText(getString(R.string.label_price5,
                        Util.formatDouble2String(carShoppingCartItem.getCarCartCheck()
                                .getMarketPrice())));
            } else {
                holder.tvMarketPrice.setVisibility(View.GONE);
            }
            CarShoppingCartItem frontItem = position == 0 ? null : cartItems.get(position - 1);
            CarShoppingCartItem laterItem = position + 1 < cartItems.size() ? cartItems.get
                    (position + 1) : null;
            holder.btnDelete.setOnClickListener(new OnCartDeleteClickListener(carShoppingCartItem));
            if (frontItem == null || frontItem.isValid() != carShoppingCartItem.isValid()) {
                holder.emptyView.setVisibility(View.VISIBLE);
            } else {
                holder.emptyView.setVisibility(View.GONE);
            }
            if (laterItem == null || laterItem.isValid() != carShoppingCartItem.isValid()) {
                holder.divider1.setVisibility(View.GONE);
                holder.divider2.setVisibility(View.VISIBLE);
            } else {
                holder.divider1.setVisibility(View.VISIBLE);
                holder.divider2.setVisibility(View.GONE);
            }
            if (carShoppingCartItem.isValid()) {
                holder.onCartCheckedChangeListener.setCartItem(carShoppingCartItem);
                holder.cbItem.setChecked(carShoppingCartItem.isChecked());
                convertView.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                holder.quantitySetLayout.setVisibility(View.VISIBLE);
                StringBuffer stringBuffer = new StringBuffer(getString(R.string.label_sku));
                if (carShoppingCartItem.getCarSku()
                        .getSkuItem() != null && !carShoppingCartItem.getCarSku()
                        .getSkuItem()
                        .isEmpty()) {
                    int size = carShoppingCartItem.getCarSku()
                            .getSkuItem()
                            .size();
                    for (int i = 0; i < size; i++) {
                        WeddingCarSkuItem item = carShoppingCartItem.getCarSku()
                                .getSkuItem()
                                .get(i);
                        if (i > 0) {
                            stringBuffer.append(";");
                        }
                        stringBuffer.append(item.getValue());
                    }
                }
                holder.tvSkuInfo.setText(stringBuffer);
                holder.cbItem.setVisibility(View.VISIBLE);
                holder.tvInvalid.setVisibility(View.GONE);
                holder.count.setText(String.valueOf(carShoppingCartItem.getQuantity()));
                holder.plus.setImageResource(carShoppingCartItem.getCarCartCheck()
                        .getShowNum() < 0 || carShoppingCartItem.getQuantity() <
                        carShoppingCartItem.getCarCartCheck()
                        .getShowNum() ? R.drawable.icon_cross_add_primary_24_24 : R.drawable
                        .icon_cross_add_gray_24_24);
                holder.subtract.setImageResource(carShoppingCartItem.getQuantity() > 1 ? R
                        .drawable.icon_subtraction_primary_24_4 : R.drawable
                        .icon_subtraction_gray_24_4);
                holder.plus.setOnClickListener(new OnCartCountClickListener(carShoppingCartItem));
                holder.subtract.setOnClickListener(new OnCartCountClickListener
                        (carShoppingCartItem));
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.color_white8));
                holder.cbItem.setVisibility(View.GONE);
                holder.tvInvalid.setVisibility(View.VISIBLE);
                holder.quantitySetLayout.setVisibility(View.GONE);
                holder.tvSkuInfo.setText(R.string.btn_sold_out);
            }
            return convertView;
        }
    }
}
