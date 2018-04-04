package me.suncloud.marrymemo.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.model.orders.ProductOrderExpressInfo;
import me.suncloud.marrymemo.model.orders.ShippingStatus;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;
import me.suncloud.marrymemo.viewholder.shoppingcart.ShoppingCartGridViewHolder;
import rx.Observable;
import rx.functions.Func2;


public class ShippingStatusActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private ProductOrderExpressInfo expressInfo;
    private List<ShippingStatus> statuses;
    private List<ShopProduct> shopProducts;
    private ShippingStatusAdapter statusAdapter;
    private List<ItemType> itemTypeList;
    private HljHttpSubscriber refreshSub;
    private long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hlj_common_fragment_ptr_recycler_view___cm);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        statuses = new ArrayList<>();
        shopProducts = new ArrayList<>();
        itemTypeList = new ArrayList<>();
        if (getIntent() != null) {
            orderId = getIntent().getLongExtra("order_id", 0L);
        }
        calculateItemType();
        statusAdapter = new ShippingStatusAdapter();
    }

    private void initWidget() {
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.setOnRefreshListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(manager);
        recyclerView.getRefreshableView()
                .setAdapter(statusAdapter);
    }

    private void initLoad() {
        onRefresh(null);
    }

    private void setResultZip(ResultZip resultZip) {
        setStatues(resultZip.productOrderExpressInfo);
        setProduct(resultZip.productData);
        statusAdapter.notifyDataSetChanged();
    }

    private void setStatues(ProductOrderExpressInfo info) {
        if (info == null) {
            return;
        }
        statuses.clear();
        statuses.addAll(info.getShippingStatusList());
        expressInfo = info;
    }

    private void setProduct(HljHttpData<List<ShopProduct>> listHljHttpData) {
        shopProducts.clear();
        if (listHljHttpData != null && listHljHttpData.getData() != null) {
            shopProducts.addAll(listHljHttpData.getData());
        }
        calculateItemType();
    }

    private void calculateItemType() {
        itemTypeList.clear();
        //header
        ItemType item = new ItemType();
        item.type = ShippingStatusAdapter.ITEM_HEADER;
        itemTypeList.add(item);

        //物流信息
        for (int i = 0; i < statuses.size(); i++) {
            ItemType type = new ItemType();
            type.type = ShippingStatusAdapter.ITEM_STATUES;
            type.collectPosition = i;
            itemTypeList.add(type);
        }

        //为你推荐
        int count = getProductRowCount();
        if (count > 0) {
            ItemType itemRecommend = new ItemType();
            itemRecommend.type = ShippingStatusAdapter.ITEM_RECOMMEND;
            itemTypeList.add(itemRecommend);
        }

        //为你推荐的婚品
        for (int i = 0; i < count; i++) {
            ItemType type = new ItemType();
            type.type = ShippingStatusAdapter.ITEM_PRODUCT;
            type.collectPosition = i;
            itemTypeList.add(type);
        }
    }

    private int getProductRowCount() {
        return shopProducts.isEmpty() ? 0 : (int) (Math.ceil(shopProducts.size() * 1.0f / 2));
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);

        refreshSub = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(refreshView == null ? progressBar : null)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        setResultZip(resultZip);
                    }

                })
                .build();
        Observable<ProductOrderExpressInfo> expressObb = ProductApi.getOrderExpressInfo(orderId);
        Observable<HljHttpData<List<ShopProduct>>> productObb = ProductApi
                .getUserRecommendProduct(1);
        Observable.zip(expressObb,
                productObb,
                new Func2<ProductOrderExpressInfo, HljHttpData<List<ShopProduct>>, Object>() {
                    @Override
                    public Object call(
                            ProductOrderExpressInfo productOrderExpressInfo,
                            HljHttpData<List<ShopProduct>> listHljHttpData) {
                        ResultZip zip = new ResultZip();
                        zip.productOrderExpressInfo = productOrderExpressInfo;
                        zip.productData = listHljHttpData;
                        return zip;
                    }
                })
                .subscribe(refreshSub);
    }

    class ShippingStatusAdapter extends RecyclerView.Adapter<BaseViewHolder> {

        public static final int ITEM_HEADER = 10;
        public static final int ITEM_STATUES = 11;
        public static final int ITEM_RECOMMEND = 12;
        public static final int ITEM_PRODUCT = 13;

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = null;
            switch (viewType) {
                case ITEM_HEADER:
                    itemView = getLayoutInflater().inflate(R.layout.shipping_status_header,
                            parent,
                            false);
                    return new HeaderViewHolder(itemView);
                case ITEM_STATUES:
                    itemView = getLayoutInflater().inflate(R.layout.shipping_status_list_item,
                            parent,
                            false);
                    return new StatusViewHolder(itemView);
                case ITEM_RECOMMEND:
                    itemView = getLayoutInflater().inflate(R.layout.layout_recomment_item,
                            parent,
                            false);
                    TextView tv = (TextView) itemView.findViewById(R.id.tv_recommend);
                    tv.setTextColor(getResources().getColor(R.color.colorBlack2));
                    tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    itemView.findViewById(R.id.left)
                            .setVisibility(View.GONE);
                    itemView.findViewById(R.id.right)
                            .setVisibility(View.GONE);
                    return new ExtraBaseViewHolder(itemView);
                case ITEM_PRODUCT:
                    itemView = getLayoutInflater().inflate(R.layout.shipping_status_product_item,
                            parent,
                            false);
                    return new ShopProductViewHolder(itemView);
                default:
                    break;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            ItemType type = itemTypeList.get(position);
            switch (viewType) {
                case ITEM_HEADER:
                    holder.setView(ShippingStatusActivity.this, expressInfo, 0, ITEM_HEADER);
                    break;
                case ITEM_STATUES:
                    holder.setView(ShippingStatusActivity.this,
                            statuses.get(type.collectPosition),
                            type.collectPosition,
                            ITEM_STATUES);
                    break;
                case ITEM_PRODUCT:
                    int row = type.collectPosition;
                    int start = row * 2;
                    int end = Math.min(start + 1, shopProducts.size() - 1);
                    List<ShopProduct> productList = new ArrayList<>();
                    productList.add(shopProducts.get(start));
                    if (!(row == getProductRowCount() - 1 && shopProducts.size() % 2 != 0)) {
                        productList.add(shopProducts.get(end));
                    }
                    holder.setView(ShippingStatusActivity.this,
                            productList,
                            type.collectPosition,
                            ITEM_PRODUCT);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            ItemType type = itemTypeList.get(position);
            return type.type;
        }

        @Override
        public int getItemCount() {
            return itemTypeList.size();
        }
    }

    class HeaderViewHolder extends BaseViewHolder<ProductOrderExpressInfo> {

        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_shipping_company)
        TextView tvShippingCompany;
        @BindView(R.id.tv_shipping_no)
        TextView tvShippingNo;
        @BindView(R.id.tv_copy_number)
        TextView tvCopyNumber;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, ProductOrderExpressInfo item, int position, int viewType) {
            if (item == null) {
                return;
            }
            tvStatus.setText(getString(R.string.label_shipping_status, item.getState()));
            if (TextUtils.isEmpty(item.getTypeName())) {
                tvShippingCompany.setText(R.string.label_no_shipping_status);
                tvShippingNo.setVisibility(View.GONE);
            } else {
                tvShippingCompany.setText(getString(R.string.label_shipping_company,
                        item.getTypeName()));
                tvShippingNo.setVisibility(View.VISIBLE);
                tvShippingNo.setText(item.getTrackingNo());
                if (TextUtils.isEmpty(item.getTrackingNo())) {
                    tvCopyNumber.setVisibility(View.GONE);
                } else {
                    tvCopyNumber.setVisibility(View.VISIBLE);
                }
            }
        }

        @OnClick({R.id.tv_shipping_no, R.id.tv_copy_number})
        void onCopy() {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText(null, tvShippingNo.getText()));
            Toast.makeText(ShippingStatusActivity.this,
                    R.string.msg_copied_shipping_no,
                    Toast.LENGTH_SHORT)
                    .show();
        }

        @OnClick(R.id.layout_refresh)
        void onRefreshStatue() {
            onRefresh(null);
        }
    }

    class StatusViewHolder extends BaseViewHolder<ShippingStatus> {

        @BindView(R.id.line_top)
        View lineTop;
        @BindView(R.id.line_bottom)
        View lineBottom;
        @BindView(R.id.img_dot)
        ImageView imgDot;
        @BindView(R.id.left_side)
        LinearLayout leftSide;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_date)
        TextView tvDate;

        public StatusViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, ShippingStatus shippingStatus, int position, int viewType) {
            if (position == 0) {
                lineTop.setVisibility(View.INVISIBLE);
                lineBottom.setVisibility(View.VISIBLE);
                imgDot.setImageResource(R.drawable.icon_dot_primary_32_32);
                lineBottom.setBackgroundColor(0xffffe3de);
                tvStatus.setTextColor(ContextCompat.getColor(ShippingStatusActivity.this,
                        R.color.colorPrimary));
                tvDate.setTextColor(ContextCompat.getColor(ShippingStatusActivity.this,
                        R.color.colorPrimary));
            } else if (position == 1) {
                lineTop.setVisibility(View.VISIBLE);
                if (statuses.size() == 2) {
                    lineBottom.setVisibility(View.INVISIBLE);
                } else {
                    lineBottom.setVisibility(View.VISIBLE);
                }

                lineTop.setBackgroundColor(0xffffe3de);
                lineBottom.setBackgroundColor(0xfff5f5f5);
                imgDot.setImageResource(R.drawable.icon_dot_gray_32_32);
                tvStatus.setTextColor(ContextCompat.getColor(ShippingStatusActivity.this,
                        R.color.colorGray));
                tvDate.setTextColor(ContextCompat.getColor(ShippingStatusActivity.this,
                        R.color.colorGray));
            } else if (position == statuses.size() - 1) {
                lineTop.setVisibility(View.VISIBLE);
                lineBottom.setVisibility(View.INVISIBLE);
                if (statuses.size() == 2) {
                    lineTop.setBackgroundColor(0xffffe3de);
                } else {
                    lineTop.setBackgroundColor(0xfff5f5f5);
                }
                imgDot.setImageResource(R.drawable.icon_dot_gray_32_32);
                tvStatus.setTextColor(ContextCompat.getColor(ShippingStatusActivity.this,
                        R.color.colorGray));
                tvDate.setTextColor(ContextCompat.getColor(ShippingStatusActivity.this,
                        R.color.colorGray));
            } else {
                lineTop.setVisibility(View.VISIBLE);
                lineBottom.setVisibility(View.VISIBLE);
                lineTop.setBackgroundColor(0xfff5f5f5);
                lineBottom.setBackgroundColor(0xfff5f5f5);
                imgDot.setImageResource(R.drawable.icon_dot_gray_32_32);
                tvStatus.setTextColor(ContextCompat.getColor(ShippingStatusActivity.this,
                        R.color.colorGray));
                tvDate.setTextColor(ContextCompat.getColor(ShippingStatusActivity.this,
                        R.color.colorGray));
            }

            if (shippingStatus.getTime() != null) {
                tvDate.setText(shippingStatus.getTime()
                        .toString("yyyy-MM-dd HH:mm:ss"));
            }
            tvStatus.setText(shippingStatus.getStatus());
        }
    }

    class ShopProductViewHolder extends BaseViewHolder<List<ShopProduct>> {

        @BindView(R.id.layout_product)
        LinearLayout layoutProduct;
        @BindView(R.id.layout_product_left)
        RelativeLayout layoutProductLeft;
        @BindView(R.id.layout_product_right)
        RelativeLayout layoutProductRight;
        @BindView(R.id.layout_product_divider)
        Space layoutProductDivider;
        int width;

        public ShopProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            width = (CommonUtil.getDeviceSize(ShippingStatusActivity.this).x - CommonUtil.dp2px(
                    ShippingStatusActivity.this,
                    8)) / 2;
            layoutProductLeft.getChildAt(0)
                    .getLayoutParams().width = width;
            layoutProductRight.getChildAt(0)
                    .getLayoutParams().width = width;
        }

        @Override
        protected void setViewData(
                Context mContext, List<ShopProduct> item, int position, int viewType) {
            setProduct(item, position);
        }

        public void setProduct(List<ShopProduct> productList, int row) {
            if (row == 0) {
                //第一行去掉顶部10dp 间距
                layoutProductDivider.setVisibility(View.GONE);
            } else {
                layoutProductDivider.setVisibility(View.VISIBLE);
            }
            if (productList == null || productList.isEmpty()) {
                layoutProduct.setVisibility(View.GONE);
                return;
            }
            layoutProductLeft.setVisibility(View.GONE);
            layoutProductRight.setVisibility(View.GONE);
            layoutProduct.setVisibility(View.VISIBLE);
            for (int i = 0, size = productList.size(); i < size; i++) {
                View child = layoutProduct.getChildAt(i);
                child.getLayoutParams().width = width;
                ShopProduct product = productList.get(i);
                if (product != null) {
                    child.setVisibility(View.VISIBLE);
                    setProduct(child, product, i);
                } else {
                    child.setVisibility(View.GONE);
                }
            }
        }

        public void setProduct(View itemView, ShopProduct product, int position) {
            ShoppingCartGridViewHolder holder = (ShoppingCartGridViewHolder) itemView.getTag();
            if (holder == null) {
                holder = new ShoppingCartGridViewHolder(itemView);
                itemView.setTag(holder);
            }
            holder.setView(itemView.getContext(), product, position, 0);
            holder.setOnItemClickListener(new OnItemClickListener<ShopProduct>() {
                @Override
                public void onItemClick(int position, ShopProduct product) {
                    if (product != null && product.getId() > 0) {
                        Intent intent = new Intent(ShippingStatusActivity.this,
                                ShopProductDetailActivity.class);
                        intent.putExtra("id", product.getId());
                        startActivity(intent);
                    }
                }
            });
        }

    }

    class ItemType {
        public int type;
        public int collectPosition;
    }

    class ResultZip extends HljHttpResultZip {
        @HljRZField
        ProductOrderExpressInfo productOrderExpressInfo;
        @HljRZField
        HljHttpData<List<ShopProduct>> productData;
    }
}
