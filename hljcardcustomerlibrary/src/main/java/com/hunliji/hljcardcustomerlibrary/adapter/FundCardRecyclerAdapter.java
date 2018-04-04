package com.hunliji.hljcardcustomerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.models.CardBalance;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/2/7.可转入的请帖列表
 */

public class FundCardRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int ITEM_TYPE = 1;
    private final static int HEADER_TYPE = 2;

    private Context context;
    private ArrayList<CardBalance> list;
    private View headerView;
    private OnCheckClickListener onCheckClickListener;

    public FundCardRecyclerAdapter(Context context, ArrayList<CardBalance> cardBalances) {
        this.context = context;
        this.list = cardBalances;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public interface OnCheckClickListener {
        void onCheck(CardBalance item, int position);
    }

    public void setOnCheckClickListener(OnCheckClickListener onCheckClickListener) {
        this.onCheckClickListener = onCheckClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return HEADER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fund_card_list_item, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            int index = headerView == null ? position : position - 1;
            holder.setView(context, list.get(index), index, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + (headerView != null ? 1 : 0);
    }

    public class ViewHolder extends BaseViewHolder<CardBalance> {
        @BindView(R2.id.img_card_cover)
        ImageView imgCardCover;
        @BindView(R2.id.tv_card_user_name)
        TextView tvCardUserName;
        @BindView(R2.id.tv_card_cash_amount)
        TextView tvCardCashAmount;
        @BindView(R2.id.cb_card_cash)
        CheckBox cbCardCash;
        private int cardWidth;
        private int cardHeight;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cardWidth = CommonUtil.dp2px(view.getContext(), 68);
            cardHeight = CommonUtil.dp2px(view.getContext(), 110);
        }

        @Override
        protected void setViewData(
                final Context mContext, final CardBalance item, final int position, int viewType) {
            tvCardCashAmount.setText(mContext.getString(R.string.format_card_cash_amount2,
                    item.getBalance()));
            final Card card = item.getCard();
            if (card != null) {
                tvCardUserName.setText(mContext.getString(R.string
                                .format_groom_and_bride_name___card,
                        card.getGroomName(),
                        card.getBrideName()));
                String coverPath;

                File cardFile = PageImageUtil.getCardThumbFile(mContext, card.getId());
                if (cardFile == null || !cardFile.exists() || cardFile.length() == 0) {
                    coverPath = card.getTheme()
                            .getThumbPath();
                } else {
                    coverPath = cardFile.getAbsolutePath();
                }
                Glide.with(mContext)
                        .load(ImagePath.buildPath(coverPath)
                                .width(cardWidth)
                                .height(cardHeight)
                                .cropPath())
                        .apply(new RequestOptions().dontAnimate())
                        .into(imgCardCover);
            }
            cbCardCash.setChecked(item.isSelected());
            cbCardCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    item.setSelected(b);
                    if (onCheckClickListener != null) {
                        onCheckClickListener.onCheck(item, getAdapterPosition());
                    }
                }
            });
        }
    }

}
