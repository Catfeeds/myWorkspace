package me.suncloud.marrymemo.adpter.merchant.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 酒店商家主页分期viewHolder
 * Created by chen_bin on 2018/2/28 0028.
 */
public class MerchantHomeInstallmentViewHolder extends ExtraBaseViewHolder {

    @BindView(R.id.btn_installment_pay)
    Button btnInstallmentPay;

    private OnInstallmentClickListener listener;

    public MerchantHomeInstallmentViewHolder(
            ViewGroup parent, OnInstallmentClickListener listener) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_home_installment_layout, parent, false));
        this.listener = listener;
    }

    public MerchantHomeInstallmentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        btnInstallmentPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onInstallmentClick();
                }
            }
        });
    }

    public interface OnInstallmentClickListener {
        void onInstallmentClick();
    }
}
