package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.MerchantShopImageRecyclerAdapter;

/**
 * Created by wangtao on 2017/9/29.
 */

public class MerchantHomeDescribeViewHolder extends BaseViewHolder<Merchant> {

    @BindView(R.id.tv_merchant_describe)
    TextView tvMerchantDescribe;
    @BindView(R.id.shop_images_recycler_view)
    RecyclerView shopImagesRecyclerView;

    private OnItemClickListener onItemClickListener;

    public MerchantHomeDescribeViewHolder(
            ViewGroup parent, OnItemClickListener onItemClickListener) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_describe_layout, parent, false));
        this.onItemClickListener = onItemClickListener;
    }

    private MerchantHomeDescribeViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });

        shopImagesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        shopImagesRecyclerView.addItemDecoration(new SpacesItemDecoration(view.getContext()));
        MerchantShopImageRecyclerAdapter adapter = new MerchantShopImageRecyclerAdapter(view
                .getContext(),
                null);
        shopImagesRecyclerView.setFocusable(false);
        shopImagesRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void setViewData(
            Context mContext, Merchant merchant, int position, int viewType) {
        if (!TextUtils.isEmpty(merchant.getDesc())) {
            tvMerchantDescribe.setVisibility(View.VISIBLE);
            tvMerchantDescribe.setText(merchant.getDesc());
        } else {
            tvMerchantDescribe.setVisibility(View.GONE);
        }
        if (CommonUtil.isCollectionEmpty(merchant.getRealPhotos())) {
            shopImagesRecyclerView.setVisibility(View.GONE);
        } else {
            shopImagesRecyclerView.setVisibility(View.VISIBLE);
            if (shopImagesRecyclerView.getAdapter() != null && shopImagesRecyclerView.getAdapter
                    () instanceof MerchantShopImageRecyclerAdapter) {
                ((MerchantShopImageRecyclerAdapter) shopImagesRecyclerView.getAdapter()).setPhotos(
                        merchant.getRealPhotos());
            }
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        private int middleSpace;

        SpacesItemDecoration(Context context) {
            this.space = CommonUtil.dp2px(context, 16);
            this.middleSpace = CommonUtil.dp2px(context, 4);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int left = middleSpace;
            int right = 0;
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                left = space;
            } else if (position == parent.getAdapter()
                    .getItemCount() - 1) {
                right = space;
            }
            outRect.set(left, 0, right, 0);
        }
    }
}
