package me.suncloud.marrymemo.adpter.wallet;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.makeramen.rounded.RoundedImageView;

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

public class FinancialLoanAdapter extends RecyclerView.Adapter<BaseViewHolder<FinancialProduct>> {

    private Context context;
    private List<FinancialProduct> products;
    private int imgWidth;
    private int imgHeight;

    public FinancialLoanAdapter(
            Context context, List<FinancialProduct> products) {
        this.context = context;
        this.products = products;
        imgWidth = (CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 34)) / 2;
        imgHeight = imgWidth * 358 / 490;
    }

    @Override
    public BaseViewHolder<FinancialProduct> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.financial_loan_product_item, parent, false);
        return new FinancialLoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<FinancialProduct> holder, int position) {
        holder.setView(context, products.get(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    class FinancialLoanViewHolder extends TrackerFinancialProductViewHolder {
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;

        FinancialLoanViewHolder(View view) {
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
                Context mContext,
                final FinancialProduct product,
                int position,
                int viewType) {
            if (product == null) {
                return;
            }
            imgCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FinancialJumpUtil.jumpFinancialProductWithNewTask((Activity) context,
                            null,
                            product,
                            getAdapterPosition());
                }
            });
            Glide.with(context)
                    .load(product.getNewImg())
                    .into(imgCover);
        }
    }
}
