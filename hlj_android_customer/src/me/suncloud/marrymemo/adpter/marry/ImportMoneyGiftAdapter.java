package me.suncloud.marrymemo.adpter.marry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.marry.GuestGift;

/**
 * Created by hua_rong on 2017/12/11 批量导入礼金
 */

public class ImportMoneyGiftAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        StickyRecyclerHeadersAdapter {


    private Context context;
    private List<GuestGift> guestGifts;
    private LayoutInflater inflater;
    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public ImportMoneyGiftAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setGuestGifts(List<GuestGift> guestGifts) {
        this.guestGifts = guestGifts;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.import_money_gift_item, parent, false);
        return new ImportMoneyGiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ImportMoneyGiftViewHolder) {
            ImportMoneyGiftViewHolder viewHolder = (ImportMoneyGiftViewHolder) holder;
            GuestGift guest = guestGifts.get(position);
            GuestGift lastGuest = null;
            if (position > 0) {
                lastGuest = guestGifts.get(position - 1);
            }
            viewHolder.setView(context,
                    guestGifts.get(position),
                    position,
                    getItemViewType(position));
            viewHolder.setShowTopLineView(lastGuest != null && TextUtils.equals(lastGuest
                            .getFirstChar(),
                    guest.getFirstChar()));
        }
    }

    @Override
    public long getHeaderId(int position) {
        String firstChar = guestGifts.get(position)
                .getFirstChar();
        if (TextUtils.isEmpty(firstChar)) {
            return super.getItemId(position);
        }
        return firstChar.charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.wedding_guest_contact_list_index, parent, false);
        return new ImportMoneyGiftIndexViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImportMoneyGiftIndexViewHolder) {
            ImportMoneyGiftIndexViewHolder viewHolder = (ImportMoneyGiftIndexViewHolder) holder;
            viewHolder.setView(context,
                    guestGifts.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return guestGifts == null ? 0 : guestGifts.size();
    }

    public int getPositionForSection(String str) {
        if (!TextUtils.isEmpty(str)) {
            for (int i = 0; i < getItemCount(); i++) {
                if (str.equals(guestGifts.get(i)
                        .getFirstChar())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public class ImportMoneyGiftIndexViewHolder extends BaseViewHolder<GuestGift> {

        @BindView(R.id.tv_index)
        TextView tvIndex;

        ImportMoneyGiftIndexViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, GuestGift guestName, int position, int viewType) {
            tvIndex.setText(guestName.getFirstChar());
        }

    }

    public class ImportMoneyGiftViewHolder extends BaseViewHolder<GuestGift> {

        @BindView(R.id.top_line_layout)
        View topLineLayout;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.et_gift_count)
        EditText etGiftCount;
        @BindView(R.id.iv_selected)
        ImageView ivSelected;


        @OnClick(R.id.iv_selected)
        void onSelected() {
            if (onSelectListener != null) {
                onSelectListener.onSelectClick(getAdapterPosition(), getItem(), etGiftCount);
            }
        }

        @OnClick({R.id.view_edit, R.id.ll_item})
        void onViewEdit() {
            if (onSelectListener != null) {
                onSelectListener.onItemClick(getAdapterPosition(), getItem(), etGiftCount);
            }
        }

        ImportMoneyGiftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @Override
        protected void setViewData(
                Context mContext, GuestGift guestName, final int position, int viewType) {
            tvName.setText(guestName.getGuestName());
            ivSelected.setImageResource(guestName.isSelected() ? R.mipmap
                    .icon_check_round_primary_32_32 : R.mipmap.icon_check_round_gray_32_32);
            TextWatcher textWatcher = new OnTextWatchListener(etGiftCount, guestName);
            if (etGiftCount.getTag() instanceof TextWatcher) {
                etGiftCount.removeTextChangedListener((TextWatcher) etGiftCount.getTag());
            }
            etGiftCount.setText(guestName.getMoney() <= 0 ? null : "￥" + CommonUtil
                    .formatDouble2String(
                    guestName.getMoney()));
            etGiftCount.setSelection(etGiftCount.length());
            etGiftCount.addTextChangedListener(textWatcher);
            etGiftCount.setTag(textWatcher);
        }

        public void setShowTopLineView(boolean showTopLineView) {
            topLineLayout.setVisibility(showTopLineView ? View.VISIBLE : View.GONE);
        }

        class OnTextWatchListener implements TextWatcher {

            private EditText editText;
            private GuestGift guestGift;

            OnTextWatchListener(EditText editText, GuestGift guestGift) {
                this.guestGift = guestGift;
                this.editText = editText;
            }

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) {
                    String text = s.toString();
                    if (text.endsWith(".")) {
                        return;
                    }
                    if (String.valueOf(text.charAt(0))
                            .equals("￥")) {
                        text = text.substring(1, text.length());
                    }
                    if (!TextUtils.isEmpty(text)) {
                        double money = Double.valueOf(text);
                        if (money < 0) {
                            ToastUtil.showToast(context, null, R.string.label_error_money);
                            return;
                        }
                        guestGift.setMoney(money);
                        editText.setSelection(editText.length());
                    } else {
                        guestGift.setMoney(0);
                    }
                } else {
                    guestGift.setMoney(0);
                }
                notifyItemChanged(getAdapterPosition());
            }
        }

    }

    public interface OnSelectListener {

        void onSelectClick(int position, GuestGift guestName, EditText editText);

        void onItemClick(int position, GuestGift guestName, EditText editText);
    }

}
