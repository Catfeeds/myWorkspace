package me.suncloud.marrymemo.adpter.wallet;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.wallet.viewholder.TrackerFinancialProductViewHolder;
import me.suncloud.marrymemo.model.wallet.FinancialProduct;
import me.suncloud.marrymemo.util.FinancialJumpUtil;

/**
 * Created by luohanlin on 2017/12/18.
 */

public class FinancialCreditCardAdapter extends RecyclerView
        .Adapter<BaseViewHolder<FinancialProduct>> {

    private Context context;
    private List<FinancialProduct> products;

    private int imgWidth;
    private int imgHeight;

    public FinancialCreditCardAdapter(
            Context context, List<FinancialProduct> bindInfoList) {
        this.context = context;
        this.products = bindInfoList;
        imgWidth = CommonUtil.getDeviceSize(context).x;
        imgHeight = imgWidth * 259 / 1080;
    }

    @Override
    public BaseViewHolder<FinancialProduct> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.financial_bind_card_item, parent, false);
        return new FinancialCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<FinancialProduct> holder, int position) {
        holder.setView(context, products.get(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    class FinancialCardViewHolder extends TrackerFinancialProductViewHolder {
        @BindView(R.id.top_padding)
        View topPadding;
        @BindView(R.id.img_cover)
        ImageView imgCover;

        FinancialCardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            imgCover.getLayoutParams().width = imgWidth;
            imgCover.getLayoutParams().height = imgHeight;

        }

        @Override
        public View trackerView() {
            return imgCover;
        }


        @Override
        protected void setViewData(
                Context mContext, final FinancialProduct product, int position, int viewType) {
            imgCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FinancialJumpUtil.jumpFinancialProductWithNewTask((Activity) context,
                            null,
                            product,
                            getAdapterPosition());
                }
            });
            topPadding.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            Glide.with(context)
                    .load(product.getNewImg())
                    .into(imgCover);
        }
    }
}
