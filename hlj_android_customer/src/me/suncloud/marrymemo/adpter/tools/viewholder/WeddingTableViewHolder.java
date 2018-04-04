package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljcommonlibrary.views.widgets.cardview.CardView;
import com.hunliji.hljimagelibrary.adapters.viewholders.BaseDraggableItemViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingGuest;
import me.suncloud.marrymemo.model.tools.WeddingTable;
import me.suncloud.marrymemo.widget.tools.WeddingTableDragHintView;

/**
 * Created by chen_bin on 2017/11/23 0023.
 */
public class WeddingTableViewHolder extends BaseDraggableItemViewHolder<WeddingTable> {

    @BindView(R.id.tv_table_name)
    TextView tvTableName;
    @BindView(R.id.tv_total_num)
    TextView tvTotalNum;
    @BindView(R.id.guests_layout)
    FlowLayout guestsLayout;
    @BindView(R.id.drag_hint_view)
    WeddingTableDragHintView dragHintView;
    @BindView(R.id.card_view)
    CardView cardView;

    private String keyword;
    private OnItemClickListener onItemClickListener;

    public WeddingTableViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, WeddingTable table, int position, int viewType) {
        if (table == null) {
            return;
        }
        int dragState = getDragStateFlags();
        if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
            cardView.setCardBackgroundColor(Color.parseColor("#f9eeee"));
        } else {
            cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        }
        if (table.getId() == 0) {
            tvTableName.setText(R.string.label_to_be_arranged);
            tvTotalNum.setText(mContext.getString(R.string.hint_to_be_arranged));
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(mContext.getString(R.string.label_table_no, table.getTableNo()));
            if (!TextUtils.isEmpty(table.getTableName())) {
                sb.append(mContext.getString(R.string.label_table_name, table.getTableName()));
            }
            tvTableName.setText(sb.toString());
            String numStr = mContext.getString(R.string.label_arranged_total_num,
                    table.getTotalNum(),
                    table.getMaxNum());
            if (table.getTotalNum() <= table.getMaxNum()) {
                tvTotalNum.setText(numStr);
            } else {
                SpannableStringBuilder builder = new SpannableStringBuilder(numStr);
                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,
                        R.color.colorPrimary)),
                        0,
                        String.valueOf(table.getTotalNum())
                                .length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvTotalNum.setText(builder);
            }
        }
        if (CommonUtil.isCollectionEmpty(table.getGuests())) {
            guestsLayout.setVisibility(View.GONE);
        } else {
            guestsLayout.setVisibility(View.VISIBLE);
            int count = guestsLayout.getChildCount();
            int size = table.getGuests()
                    .size();
            if (count > size) {
                guestsLayout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                WeddingGuest guest = table.getGuests()
                        .get(i);
                TextView tv = null;
                if (count > i) {
                    tv = (TextView) guestsLayout.getChildAt(i);
                }
                if (tv == null) {
                    View.inflate(mContext, R.layout.wedding_guest_flow_item, guestsLayout);
                    tv = (TextView) guestsLayout.getChildAt(guestsLayout.getChildCount() - 1);
                }
                String name = guest.getFullName();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(keyword) || !name.contains
                        (keyword)) {
                    tv.setText(name);
                } else {
                    int start = name.indexOf(keyword);
                    int end = start + keyword.length();
                    SpannableStringBuilder builder = new SpannableStringBuilder(name);
                    builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,
                            R.color.colorBlack3)), 0, start, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,
                            R.color.colorPrimary)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,
                            R.color.colorBlack3)),
                            end,
                            name.length(),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    tv.setText(builder);
                }
            }
        }
    }

    public void setKeyword(String searchWord) {
        this.keyword = searchWord;
    }

    public void setShowTableDragHintView(boolean showTableDragHintView) {
        if (!showTableDragHintView) {
            dragHintView.setVisibility(View.GONE);
        } else {
            dragHintView.setVisibility(View.VISIBLE);
            if (cardView.getMeasuredWidth() > 0) {
                dragHintView.setCenterPoint(cardView.getMeasuredWidth() / 2,
                        cardView.getMeasuredHeight() / 2)
                        .addRippleView();
            } else {
                cardView.getViewTreeObserver()
                        .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @Override
                            public boolean onPreDraw() {
                                cardView.getViewTreeObserver()
                                        .removeOnPreDrawListener(this);
                                dragHintView.setCenterPoint(cardView.getMeasuredWidth() / 2,
                                        cardView.getMeasuredHeight() / 2)
                                        .addRippleView();
                                return false;
                            }
                        });
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
