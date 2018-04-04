package me.suncloud.marrymemo.adpter.shoppingcart.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;

/**
 * Created by jinxin on 2017/11/7 0007.
 */

public class ShoppingCartEmptyViewHolder extends BaseViewHolder<Boolean> {

    @BindView(R.id.tv_go_look)
    TextView tvGoLook;
    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty;
    private Context mContext;
    private View itemView;

    private OnEmptyClickListener onEmptyClickListener;
    public ShoppingCartEmptyViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
        mContext = itemView.getContext();
    }

    @Override
    protected void setViewData(Context mContext, Boolean isEmpty, int position, int viewType) {
        int height;
        if (isEmpty) {
            height = CommonUtil.getDeviceSize(mContext).y -
                    CommonUtil.dp2px(
                            mContext,
                            45) - CommonUtil.getStatusBarHeight(mContext);
        } else {
            //emptyview 高度223+60
            height = CommonUtil.dp2px(mContext, 283);
        }
        setEmptyHeight(height);
    }

    public void setEmptyHeight(int height) {
        LinearLayout.LayoutParams emptyParams = (LinearLayout.LayoutParams) layoutEmpty
                .getLayoutParams();
        int topMargin = 0;
        if (itemView.getLayoutParams() == null) {
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT,
                    height);
            itemView.setLayoutParams(params);
            topMargin = CommonUtil.dp2px(itemView.getContext(), -25);
        } else {
            topMargin = 0;
        }
        itemView.getLayoutParams().height = height;
        emptyParams.topMargin = topMargin;
        itemView.requestLayout();
    }

    public void setOnEmptyClickListener(OnEmptyClickListener onEmptyClickListener) {
        this.onEmptyClickListener = onEmptyClickListener;
    }

    @OnClick(R.id.tv_go_look)
    void onGoLook() {
        if(onEmptyClickListener != null){
            onEmptyClickListener.onEmptyClick();
        }
    }

    public interface OnEmptyClickListener{
        void onEmptyClick();
    }
}
