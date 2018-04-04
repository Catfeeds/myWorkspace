package me.suncloud.marrymemo.adpter.tools.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingGuest;

/**
 * Created by chen_bin on 2017/11/23 0023.
 */
public class WeddingGuestViewHolder extends BaseViewHolder<WeddingGuest> {

    @BindView(R.id.btn_delete)
    ImageButton btnDelete;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.btn_subtract)
    ImageButton btnSubtract;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.btn_plus)
    ImageButton btnPlus;

    private OnDeleteGuestListener onDeleteGuestListener;
    private OnSubtractGuestLister onSubtractGuestLister;
    private OnPlusGuestListener onPlusGuestListener;

    public WeddingGuestViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteGuestListener != null) {
                    onDeleteGuestListener.onDeleteGuest(getAdapterPosition(), getItem());
                }
            }
        });
        btnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSubtractGuestLister != null) {
                    onSubtractGuestLister.onSubtractGuest(getAdapterPosition(), getItem());
                }
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlusGuestListener != null) {
                    onPlusGuestListener.onPlusGuest(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, WeddingGuest guest, int position, int viewType) {
        if (guest == null) {
            return;
        }
        tvName.setText(guest.getFullName());
        tvNum.setText(String.valueOf(guest.getNum()));
    }

    public void setOnDeleteGuestListener(OnDeleteGuestListener onDeleteGuestListener) {
        this.onDeleteGuestListener = onDeleteGuestListener;
    }

    public void setOnSubtractGuestLister(OnSubtractGuestLister onSubtractGuestLister) {
        this.onSubtractGuestLister = onSubtractGuestLister;
    }

    public void setOnPlusGuestListener(OnPlusGuestListener onPlusGuestListener) {
        this.onPlusGuestListener = onPlusGuestListener;
    }

    public interface OnDeleteGuestListener {
        void onDeleteGuest(int position, WeddingGuest guest);
    }

    public interface OnSubtractGuestLister {
        void onSubtractGuest(int position, WeddingGuest guest);
    }

    public interface OnPlusGuestListener {
        void onPlusGuest(int position, WeddingGuest guest);
    }

}
