package me.suncloud.marrymemo.adpter.shoppingcart.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;

/**
 * Created by jinxin on 2017/11/7 0007.
 */

public class ShoppingCartDividerViewHolder extends BaseViewHolder {

    @BindView(R.id.tv_clear)
    TextView tvClear;
    @BindView(R.id.layout_clear)
    LinearLayout layoutClear;
    @BindView(R.id.cart_footer_divider)
    View cartFooterDivider;
    @BindView(R.id.layout_recommend)
    LinearLayout layoutRecommend;

    private OnCartDividerClickListener onCartDividerClickListener;

    public ShoppingCartDividerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, Object item, int position, int viewType) {

    }

    public void setDivider(boolean showClear, boolean showRecommend) {
        layoutClear.setVisibility(showClear ? View.VISIBLE : View.GONE);
        layoutRecommend.setVisibility(showRecommend ? View.GONE : View.VISIBLE);
        cartFooterDivider.setVisibility(showRecommend?View.GONE:View.VISIBLE);
    }

    @OnClick(R.id.tv_clear)
    void onClear() {
        if (onCartDividerClickListener != null) {
            onCartDividerClickListener.onClear();
        }
    }

    public void setOnCartDividerClickListener(
            OnCartDividerClickListener onCartDividerClickListener) {
        this.onCartDividerClickListener = onCartDividerClickListener;
    }

    public interface OnCartDividerClickListener {
        void onClear();
    }
}
