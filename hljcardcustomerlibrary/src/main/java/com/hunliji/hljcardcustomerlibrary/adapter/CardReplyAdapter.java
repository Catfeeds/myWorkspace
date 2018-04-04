package com.hunliji.hljcardcustomerlibrary.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.models.CardReply;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;

import org.joda.time.DateTime;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/6/20.
 * 请帖回复-祝福、赴宴
 */

public class CardReplyAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<CardReply> cardReplyList;
    private View footerView;
    private LayoutInflater layoutInflater;
    private Context context;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private long lastReplyId;
    private long userId;

    public void setLastReplyId(long lastReplyId) {
        this.lastReplyId = lastReplyId;
    }

    public CardReplyAdapter(Context context, List<CardReply> cardReplyList) {
        this.context = context;
        this.cardReplyList = cardReplyList;
        layoutInflater = LayoutInflater.from(context);
        User user = UserSession.getInstance()
                .getUser(context);
        if (user != null) {
            userId = user.getId();
        }
    }

    public void setCardReplyList(List<CardReply> cardReplyList) {
        this.cardReplyList = cardReplyList;
        notifyDataSetChanged();
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                View view = layoutInflater.inflate(R.layout.item_card_reply___card, parent, false);
                return new CardReplyViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == cardReplyList.size()) {
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_LIST;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof CardReplyViewHolder) {
                    CardReplyViewHolder viewHolder = (CardReplyViewHolder) holder;
                    viewHolder.setView(context, getItem(position), position, viewType);
                }
                break;
        }
    }

    public CardReply getItem(int position) {
        return cardReplyList.get(position);
    }


    @Override
    public int getItemCount() {
        return cardReplyList == null ? 0 : cardReplyList.size() + (footerView != null &&
                !cardReplyList.isEmpty() ? 1 : 0);
    }

    public class CardReplyViewHolder extends BaseViewHolder<CardReply> {

        @BindView(R2.id.tv_wish_name)
        TextView tvWishName;
        @BindView(R2.id.tv_wish_time)
        TextView tvWishTime;
        @BindView(R2.id.tv_wish_content)
        TextView tvWishContent;
        @BindView(R2.id.rl_wish)
        RelativeLayout rlWish;
        @BindView(R2.id.tv_feast_name)
        TextView tvFeastName;
        @BindView(R2.id.tv_feast_time)
        TextView tvFeastTime;
        @BindView(R2.id.tv_feast_info)
        TextView tvFeastInfo;
        @BindView(R2.id.rl_feast)
        RelativeLayout rlFeast;
        @BindView(R2.id.tv_feast_number)
        TextView tvFeastNumber;
        @BindView(R2.id.view_line)
        View viewLine;
        @BindView(R2.id.fl_itemView)
        View flItemView;
        @BindView(R2.id.tv_wish_from)
        TextView tvWishFrom;
        @BindView(R2.id.tv_feast_from)
        TextView tvFeastFrom;

        public CardReplyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                final Context mContext,
                final CardReply cardReply,
                final int position,
                int viewType) {
            if (cardReply != null) {
                //0：赴宴、1：待定、2：有事 3：祝福
                int state = cardReply.getState();
                itemView.setVisibility(View.VISIBLE);
                DateTime dateTime = cardReply.getCreatedAt();
                if (cardReply.getId() > lastReplyId) {
                    flItemView.setBackgroundResource(R.drawable
                            .sl_color_transparent_primary5_background2);
                } else {
                    flItemView.setBackgroundResource(R.drawable.sl_color_white_2_background2);
                }
                if (state == CardReply.TYPE_WISH) {
                    rlFeast.setVisibility(View.GONE);
                    rlWish.setVisibility(View.VISIBLE);
                    tvWishName.setText(cardReply.getName());
                    tvWishContent.setText(cardReply.getWishLanguage());
                    if (dateTime != null) {
                        tvWishTime.setText(dateTime.toString("yyyy-MM-dd"));
                    }
                } else {
                    rlFeast.setVisibility(View.VISIBLE);
                    rlWish.setVisibility(View.GONE);
                    tvFeastName.setText(cardReply.getName());
                    if (dateTime != null) {
                        tvFeastTime.setText(dateTime.toString("yyyy-MM-dd"));
                    }
                    switch (state) {
                        case CardReply.TYPE_FEAST:
                            tvFeastNumber.setText(String.valueOf(cardReply.getCount()));
                            tvFeastNumber.setVisibility(View.VISIBLE);
                            tvFeastInfo.setText("人赴宴");
                            break;
                        case CardReply.TYPE_TO_BE_DETERMINED:
                            tvFeastInfo.setText("待定");
                            tvFeastNumber.setVisibility(View.GONE);
                            break;
                        case CardReply.TYPE_BUSY:
                            tvFeastInfo.setText("有事");
                            tvFeastNumber.setVisibility(View.GONE);
                            break;
                    }
                }

                flItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMenu(mContext, cardReply, position);
                    }
                });
                viewLine.setVisibility(position == cardReplyList.size() - 1 ? View.GONE : View
                        .VISIBLE);
                if (userId != 0 && cardReply.getUserId() != userId) {
                    tvWishFrom.setVisibility(View.VISIBLE);
                    tvFeastFrom.setVisibility(View.VISIBLE);
                } else {
                    tvWishFrom.setVisibility(View.GONE);
                    tvFeastFrom.setVisibility(View.GONE);
                }
            }
        }

        private void showMenu(
                final Context mContext, final CardReply item, final int position) {
            Dialog dialog = null;
            if (item.getState() == CardReply.TYPE_WISH) {
                dialog = DialogUtil.createBottomMenuDialog(mContext,
                        new LinkedHashMap<String, View.OnClickListener>() {
                            {
                                put("回复", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onReplyItem(mContext, item, position);
                                    }
                                });
                                put("删除", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onDelete(mContext, item, position);
                                    }
                                });
                            }
                        },
                        null);
            } else {
                dialog = DialogUtil.createBottomMenuDialog(mContext,
                        new LinkedHashMap<String, View.OnClickListener>() {
                            {
                                put("删除", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onDelete(mContext, item, position);
                                    }
                                });
                            }
                        },
                        null);
            }
            dialog.show();
        }

        private void onReplyItem(
                final Context mContext, final CardReply item, final int position) {
            if (item.getCardUserReply() == null) {
                ToastUtil.showToast(context, "抱歉，该宾客未允许接收消息", 0);
            } else if (item.getCardUserReply()
                    .getStatus() == 1 || item.getCardUserReply()
                    .getStatus() == 2) {
                ToastUtil.showToast(context, "抱歉，您已给该宾客发过了", 0);
            } else if (onCardReplyListener != null) {
                onCardReplyListener.onCardReply(item, position);
            }
        }

        private void onDelete(final Context mContext, final CardReply item, final int position) {
            String msg;
            if (item.getState() == CardReply.TYPE_WISH) {
                msg = "确认删除此条宾客回复";
            } else {
                msg = "确认删除此条赴宴消息";
            }
            DialogUtil.createDoubleButtonDialog(mContext, msg, "", "", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCardReplyListener.onCardDelete(item, position);
                }
            }, null)
                    .show();
        }

    }

    public void setOnCardReplyListener(
            OnCardReplyListener onCardReplyListener) {
        this.onCardReplyListener = onCardReplyListener;
    }

    private OnCardReplyListener onCardReplyListener;

    public interface OnCardReplyListener {

        void onCardReplyLongClick(CardReply cardReply, int position);

        void onCardReply(CardReply cardReply, int position);

        void onCardDelete(CardReply cardReply, int position);

    }

}
