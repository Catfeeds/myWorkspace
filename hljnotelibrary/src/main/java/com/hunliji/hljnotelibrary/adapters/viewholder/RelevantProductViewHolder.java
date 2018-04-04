package com.hunliji.hljnotelibrary.adapters.viewholder;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.adapters.NoteProductRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/6/29.笔记相关婚品
 */
public class RelevantProductViewHolder extends BaseViewHolder<ArrayList<ShopProduct>> {

    @BindView(R2.id.tv_relevant_header)
    TextView tvRelevantHeader;
    @BindView(R2.id.relevant_header_layout)
    LinearLayout relevantHeaderLayout;
    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    public NoteProductRecyclerAdapter adapter;
    private ArrayList<ShopProduct> shopProducts;

    public RelevantProductViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        shopProducts = new ArrayList<>();
        adapter = new NoteProductRecyclerAdapter(itemView.getContext(), shopProducts);
        recyclerView.addItemDecoration(new SpacesItemDecoration(itemView.getContext()));
        recyclerView.setAdapter(adapter);
        relevantHeaderLayout.setVisibility(View.VISIBLE);
        tvRelevantHeader.setText(itemView.getContext()
                .getString(R.string.label_relevant_product___note));
    }

    @Override
    protected void setViewData(
            Context mContext, ArrayList<ShopProduct> products, int position, int viewType) {
        if (products != null) {
            shopProducts.clear();
            shopProducts.addAll(products);
        }
        adapter.notifyDataSetChanged();
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;
        private int middleSpace;

        SpacesItemDecoration(Context context) {
            this.space = CommonUtil.dp2px(context, 16);
            this.middleSpace = CommonUtil.dp2px(context, 8);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int left = 0;
            int right ;
            int position = parent.getChildAdapterPosition(view);
            if (position==0){
                left = space;
                right = middleSpace;
            }else if (position == parent.getAdapter().getItemCount()-1){
                right = space;
            }else {
                right = middleSpace;
            }
            outRect.set(left, 0, right, 0);
        }
    }
}