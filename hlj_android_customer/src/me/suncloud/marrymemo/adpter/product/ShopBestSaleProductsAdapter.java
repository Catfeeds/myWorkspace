package me.suncloud.marrymemo.adpter.product;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerProductViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * Created by luohanlin on 2017/10/24.
 */

public class ShopBestSaleProductsAdapter extends RecyclerView.Adapter<BaseViewHolder<ShopProduct>> {

    private Context context;
    private List<ShopProduct> products;
    private int coverSize;

    public static final int TYPE_ITEM = 1;

    public ShopBestSaleProductsAdapter(
            Context context, List<ShopProduct> list) {
        this.context = context;
        this.products = list;
        coverSize = CommonUtil.dp2px(context, 86);
    }

    @Override
    public BaseViewHolder<ShopProduct> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.best_sale_product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<ShopProduct> holder, int position) {
        if (holder instanceof ProductViewHolder) {
            holder.setView(context,
                    position < products.size() ? products.get(position) : null,
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return CommonUtil.isCollectionEmpty(products) ? 0 : products.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }


    class ProductViewHolder extends TrackerProductViewHolder {
        @BindView(R.id.start_padding)
        View startPadding;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.end_padding)
        View endPadding;


        ProductViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

        @Override
        public View trackerView() {
            return imgCover;
        }

        @Override
        protected void setViewData(
                Context mContext, final ShopProduct product, int position, int viewType) {
            startPadding.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            endPadding.setVisibility(position == products.size() ? View.VISIBLE : View.GONE);
            if (position == products.size()) {
                tvPrice.setText("");
                imgCover.setImageResource(R.drawable.image_more_products);
                imgCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ProductMerchantActivity.class);
                        intent.putExtra("id",
                                products.get(0)
                                        .getMerchant()
                                        .getId());
                        context.startActivity(intent);
                    }
                });
            } else {
                tvPrice.setText(context.getString(R.string.label_price, product.getShowPrice()));
                MultiTransformation transformation = new MultiTransformation(new CenterCrop(),
                        new RoundedCorners(CommonUtil.dp2px(context, 2)));
                Glide.with(context)
                        .load(ImagePath.buildPath(product.getCoverPath())
                                .width(coverSize)
                                .height(coverSize)
                                .path())
                        .apply(new RequestOptions().transform(transformation))
                        .into(imgCover);
                imgCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShopProductDetailActivity.class);
                        intent.putExtra("id", product.getId());
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    static class ViewHolder {
        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
