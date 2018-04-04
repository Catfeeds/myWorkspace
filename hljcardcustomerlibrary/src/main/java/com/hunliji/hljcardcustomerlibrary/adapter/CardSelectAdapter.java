package com.hunliji.hljcardcustomerlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.Theme;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/6/20.
 * 宾客回复 请帖列表
 */

public class CardSelectAdapter extends RecyclerView.Adapter<BaseViewHolder<Card>> {

    private Context context;
    private List<Card> cardList;
    private LayoutInflater layoutInflater;
    private SparseBooleanArray sparseArray;

    public CardSelectAdapter(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
        layoutInflater = LayoutInflater.from(context);
        sparseArray = new SparseBooleanArray();
        sparseArray.put(0, true);
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<Card> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardSelectViewHolder(layoutInflater.inflate(R.layout
                        .item_card_reply_select___card,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Card> holder, int position) {
        holder.setView(context, cardList.get(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return cardList == null ? 0 : cardList.size();
    }

    public class CardSelectViewHolder extends BaseViewHolder<Card> {

        @BindView(R2.id.iv_thumb)
        RoundedImageView ivThumb;
        @BindView(R2.id.iv_selected)
        ImageView ivSelected;
        @BindView(R2.id.theme_layout)
        RelativeLayout themeLayout;
        @BindView(R2.id.view_selected)
        View viewSelected;
        @BindView(R2.id.tv_partner)
        TextView tvPartner;
        private int imageWidth;
        private int height;
        private long userId;

        public CardSelectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Context context = itemView.getContext();
            imageWidth = CommonUtil.dp2px(context, 80);
            height = Math.round((imageWidth * 122) / 75 + CommonUtil.dp2px(context, 1));
            themeLayout.getLayoutParams().width = imageWidth;
            themeLayout.getLayoutParams().height = height;
            User user = UserSession.getInstance()
                    .getUser(context);
            if (user != null) {
                userId = user.getId();
            }
        }

        @Override
        protected void setViewData(
                Context mContext, Card card, final int position, int viewType) {
            if (card != null) {
                final boolean selected = sparseArray.get(position);
                viewSelected.setSelected(selected);
                ivSelected.setVisibility(selected ? View.VISIBLE : View.GONE);
                final String path = showThumbPath(position, card);
                themeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!selected) {
                            sparseArray.clear();
                            sparseArray.put(position, true);
                            if (listener != null) {
                                listener.onCardSelect(getItem(), position, path);
                            }
                            notifyDataSetChanged();
                        }
                    }
                });
                if (card.getUserId() > 0 && card.getUserId() != userId && userId > 0) {
                    tvPartner.setVisibility(View.VISIBLE);
                } else {
                    tvPartner.setVisibility(View.GONE);
                }
            }
        }

        private String showThumbPath(int position, Card card) {
            if (position == 0) {
                ivThumb.setImageResource(R.mipmap.icon_card_select_all___card);
                return null;
            } else {
                String path = null;
                File cardFile = PageImageUtil.getCardThumbFile(context, card.getId());
                if (cardFile == null || !cardFile.exists() || cardFile.length() == 0) {
                    Theme theme = card.getTheme();
                    if (theme != null && theme.getId() > 0) {
                        path = theme.getThumbPath();
                    }
                } else {
                    path = cardFile.getAbsolutePath();
                }
                Glide.with(context)
                        .load(ImagePath.buildPath(path)
                                .width(imageWidth)
                                .height(height)
                                .cropPath())
                        .into(ivThumb);
                return path;
            }
        }
    }

    public void setOnCardSelectListener(OnCardSelectListener listener) {
        this.listener = listener;
    }

    private OnCardSelectListener listener;

    public interface OnCardSelectListener {
        void onCardSelect(Card card, int position, String path);
    }

}
