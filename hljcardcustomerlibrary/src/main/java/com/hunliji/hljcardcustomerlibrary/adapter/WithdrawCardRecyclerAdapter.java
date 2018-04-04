package com.hunliji.hljcardcustomerlibrary.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.models.CardBalance;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.BindInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/2/7.可提现的请帖列表
 */

public class WithdrawCardRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int ITEM_TYPE = 1;
    private final static int HEADER_TYPE = 2;

    private Context context;
    private ArrayList<CardBalance> list;
    private View headerView;
    private OnWithdrawClickListener onWithdrawClickListener;
    private OnUnbindClickListener onUnbindClickListener;

    public WithdrawCardRecyclerAdapter(Context context, ArrayList<CardBalance> cardGifts) {
        this.context = context;
        this.list = cardGifts;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public interface OnWithdrawClickListener {
        void onWithdraw(CardBalance item, int position);
    }

    public void setOnWithdrawClickListener(OnWithdrawClickListener onWithdrawClickListener) {
        this.onWithdrawClickListener = onWithdrawClickListener;
    }

    public void setOnUnbindClickListener(OnUnbindClickListener onUnbindClickListener) {
        this.onUnbindClickListener = onUnbindClickListener;
    }

    public interface OnUnbindClickListener {
        void onUnbind(CardBalance item, int position);
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
                        .inflate(R.layout.withdraw_card_list_item, parent, false));
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
        @BindView(R2.id.tv_card_cash)
        TextView tvCardCash;
        @BindView(R2.id.img_bind_logo)
        ImageView imgBindLogo;
        @BindView(R2.id.tv_bind_name)
        TextView tvBindName;
        @BindView(R2.id.btn_withdraw_cash)
        Button btnWithdrawCash;
        @BindView(R2.id.img_unbind_arrow)
        ImageView imgUnbindArrow;
        @BindView(R2.id.tv_unbind)
        TextView tvUnbind;
        private int cardWidth;
        private int cardHeight;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cardWidth = CommonUtil.dp2px(view.getContext(), 110);
            cardHeight = CommonUtil.dp2px(view.getContext(), 180);
        }

        @Override
        protected void setViewData(
                final Context mContext, final CardBalance item, final int position, int viewType) {

            final BindInfo bindInfo = item.getBindInfo();
            final Card card = item.getCard();
            if (item.getVersion() == CardBalance.NEW_CARD) {
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
            } else {
                if (bindInfo != null && bindInfo.getType() == BindInfo.BIND_BANK) {
                    tvCardUserName.setText("旧版请帖礼金收入");
                    imgCardCover.setImageResource(R.mipmap.icon_card_cash___card);
                } else {
                    tvCardUserName.setText("旧版请帖礼物收入");
                    imgCardCover.setImageResource(R.mipmap.icon_card_gift___card);
                }
            }

            tvUnbind.setVisibility(View.GONE);
            if (bindInfo != null) {
                imgBindLogo.setVisibility(View.VISIBLE);
                imgUnbindArrow.setVisibility(View.GONE);
                tvBindName.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack2));
                if (bindInfo.getType() == BindInfo.BIND_BANK) {
                    tvBindName.setText(mContext.getString(R.string.format_bind_info___card,
                            bindInfo.getBankDesc(),
                            bindInfo.getAccNo()));
                    if (item.getVersion() == CardBalance.NEW_CARD) {
                        tvUnbind.setVisibility(View.VISIBLE);
                        tvUnbind.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (onUnbindClickListener != null) {
                                    onUnbindClickListener.onUnbind(item, position);
                                }
                            }
                        });
                    }
                    String imgUrl = ImagePath.buildPath(bindInfo.getBankLogo())
                            .height(CommonUtil.dp2px(mContext, 16))
                            .width(CommonUtil.dp2px(mContext, 16))
                            .cropPath();
                    Glide.with(mContext)
                            .load(imgUrl)
                            .apply(new RequestOptions().dontAnimate())
                            .into(imgBindLogo);
                } else {
                    imgBindLogo.setImageResource(R.mipmap.icon_wx_63_63___card);
                    if (item.getVersion() == CardBalance.NEW_CARD) {
                        tvBindName.setText(mContext.getString(R.string.format_bind_info___card,
                                "微信钱包",
                                bindInfo.getIdHolder()));
                        tvUnbind.setVisibility(View.VISIBLE);
                        tvUnbind.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (onUnbindClickListener != null) {
                                    onUnbindClickListener.onUnbind(item, position);
                                }
                            }
                        });
                    } else {
                        tvBindName.setText("微信钱包");
                    }
                }
            } else {
                imgBindLogo.setVisibility(View.GONE);
                tvBindName.setTextColor(ContextCompat.getColor(mContext, R.color.colorLink));
                tvBindName.setText("未设置提现账号");
                imgUnbindArrow.setVisibility(View.VISIBLE);
                tvBindName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onWithdrawClickListener != null) {
                            onWithdrawClickListener.onWithdraw(item, position);
                        }
                    }
                });
            }
            tvCardCash.setText(mContext.getString(R.string.label_price___cm,
                    String.valueOf(item.getBalance())));
            btnWithdrawCash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onWithdrawClickListener != null) {
                        if (!HljCard.isCustomer(mContext) && item.getVersion() != CardBalance
                                .NEW_CARD) {
                            //非婚礼纪客户端，旧版提现不可使用，提示下载婚礼纪
                            DialogUtil.createDoubleButtonDialog(mContext,
                                    "请下载婚礼纪APP进行提现！",
                                    null,
                                    null,
                                    null,
                                    null).show();
                        } else {
                            onWithdrawClickListener.onWithdraw(item, position);
                        }
                    }
                }
            });
        }
    }
}
