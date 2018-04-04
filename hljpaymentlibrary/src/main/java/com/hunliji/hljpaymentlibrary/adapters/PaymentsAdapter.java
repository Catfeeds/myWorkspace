package com.hunliji.hljpaymentlibrary.adapters;

import android.app.Dialog;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.BankCard;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljpaymentlibrary.PayAgent;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.models.LLPayment;
import com.hunliji.hljpaymentlibrary.models.Payment;
import com.hunliji.hljpaymentlibrary.models.WalletPayment;
import com.hunliji.hljpaymentlibrary.models.XiaoxiPayment;

import java.util.List;

/**
 * Created by wangtao on 2017/2/9.
 */

public class PaymentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Payment> payments;
    private View headerView;
    private final int HEADER_TYPE = -1;
    private final int COMMENT_TYPE = 1;
    private final int LL_TYPE = 2;
    private final int WALLET_TYPE = 3;
    private final int INSTALLMENT_TYPE = 4;
    private Dialog cardListDialog;
    private PaymentActionListener paymentActionListener;

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setPaymentActionListener(PaymentActionListener paymentActionListener) {
        this.paymentActionListener = paymentActionListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new RecyclerView.ViewHolder(headerView) {};
            case COMMENT_TYPE:
                return new CommentPaymentViewHolder(getView(parent, viewType));
            case LL_TYPE:
                return new LLPaymentViewHolder(getView(parent, viewType));
            case WALLET_TYPE:
                return new WalletPaymentViewHolder(getView(parent, viewType));
            case INSTALLMENT_TYPE:
                return new InstallmentPaymentViewHolder(getView(parent, viewType));
        }
        return null;
    }


    private View getView(ViewGroup parent, int viewType) {
        switch (viewType) {
            case COMMENT_TYPE:
                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pay_agent_comment___pay, parent, false);
            case LL_TYPE:
                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pay_agent_quick___pay, parent, false);
            case WALLET_TYPE:
                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pay_agent_wallet___pay, parent, false);
            case INSTALLMENT_TYPE:
                return LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pay_agent_installment___pay, parent, false);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentPaymentViewHolder) {
            ((CommentPaymentViewHolder) holder).setViewValue(getItem(position), position);
        } else if (holder instanceof LLPaymentViewHolder) {
            ((LLPaymentViewHolder) holder).setViewValue(getItem(position), position);
        } else if (holder instanceof WalletPaymentViewHolder) {
            ((WalletPaymentViewHolder) holder).setViewValue(getItem(position), position);
        } else if (holder instanceof InstallmentPaymentViewHolder) {
            ((InstallmentPaymentViewHolder) holder).setViewValue(getItem(position), position);
        }
    }

    public Payment getItem(int position) {
        if (headerView != null) {
            position--;
        }
        if (position >= 0 && position < payments.size()) {
            return payments.get(position);
        }
        return null;
    }

    public Payment getSelectPayment() {
        for (Payment payment : payments) {
            if (payment.isSelected()) {
                return payment;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (payments == null || payments.isEmpty()) {
            return 0;
        }
        int size = payments.size();
        if (headerView != null) {
            size++;
        }
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return HEADER_TYPE;
        }
        if (headerView != null) {
            position--;
        }
        Payment payment = payments.get(position);
        if (payment instanceof LLPayment) {
            return LL_TYPE;
        } else if (payment instanceof WalletPayment) {
            return WALLET_TYPE;
        } else if (payment instanceof XiaoxiPayment) {
            return INSTALLMENT_TYPE;
        } else {
            return COMMENT_TYPE;
        }
    }

    private void onPaymentSelectedChange(Payment payment) {
        int index = payments.indexOf(payment);
        if (index >= 0) {
            if (headerView != null) {
                index++;
            }
            notifyItemChanged(index);
        } else {
            notifyDataSetChanged();
        }
    }

    private class PaymentViewHolder extends RecyclerView.ViewHolder {

        private View line;
        private Payment payment;
        CheckableLinearButton checkableView;

        private PaymentViewHolder(View itemView) {
            super(itemView);
            checkableView = (CheckableLinearButton) itemView;
            line = itemView.findViewById(R.id.line);
            checkableView.setOnCheckedChangeListener(new CheckableLinearLayout
                    .OnCheckedChangeListener() {
                @Override
                public void onCheckedChange(View view, boolean checked) {
                    if (!checked || getPayment() == null) {
                        return;
                    }
                    for (Payment payment : payments) {
                        if (payment == getPayment()) {
                            if (!payment.isSelected()) {
                                payment.setSelected(true);
                                onPaymentSelectedChange(payment);
                            }
                        } else if (payment.isSelected()) {
                            payment.setSelected(false);
                            onPaymentSelectedChange(payment);
                        }
                    }
                }
            });

        }

        public void setViewValue(Payment payment, int position) {
            this.payment = payment;
            line.setVisibility(position == (headerView == null ? 0 : 1) ? View.GONE : View.VISIBLE);
            checkableView.setChecked(payment.isSelected());
        }

        Payment getPayment() {
            return payment;
        }
    }

    private class CommentPaymentViewHolder extends PaymentViewHolder {
        private ImageView ivIcon;
        private TextView tvName;

        private CommentPaymentViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }

        public void setViewValue(Payment payment, int position) {
            super.setViewValue(payment, position);
            switch (payment.getPayAgent()) {
                case PayAgent.ALI_PAY:
                    ivIcon.setImageResource(R.mipmap.ic_alipay___pay);
                    tvName.setText(R.string.label_alipay_pay___pay);
                    break;
                case PayAgent.UNION_PAY:
                    ivIcon.setImageResource(R.mipmap.ic_union_pay___pay);
                    tvName.setText(R.string.label_union_pay___pay);
                    break;
                case PayAgent.CMB_PAY:
                    ivIcon.setImageResource(R.mipmap.ic_cmb_pay___pay);
                    tvName.setText(R.string.label_cmb_pay___pay);
                    break;
                case PayAgent.WEIXIN_PAY:
                    ivIcon.setImageResource(R.mipmap.ic_weixin_pay___pay);
                    tvName.setText(R.string.label_weixin_pay___pay);
                    break;
                case PayAgent.XIAO_XI_PAY:
                    ivIcon.setImageResource(R.mipmap.icon_xiaoxi_installment_68_68___pay);
                    tvName.setText(R.string.label_installment___pay);
                    break;
            }

        }
    }

    private class LLPaymentViewHolder extends PaymentViewHolder {
        private TextView tvCurrentCard;
        private View cardLayout;

        private LLPaymentViewHolder(View itemView) {
            super(itemView);
            tvCurrentCard = (TextView) itemView.findViewById(R.id.tv_current_card);
            cardLayout = itemView.findViewById(R.id.card_layout);
            cardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //银行卡列表
                    if (getPayment() == null || !(getPayment() instanceof LLPayment)) {
                        return;
                    }
                    final LLPayment llPayment = (LLPayment) getPayment();
                    List<BankCard> cards = llPayment.getCards();
                    if (cards == null || cards.isEmpty()) {
                        return;
                    }
                    checkableView.setChecked(true);
                    if (cardListDialog != null && cardListDialog.isShowing()) {
                        return;
                    }
                    if (cardListDialog == null) {
                        cardListDialog = new Dialog(v.getContext(), R.style.BubbleDialogTheme);
                        cardListDialog.setContentView(R.layout.dialog_bank_card_list___pay);
                        cardListDialog.findViewById(R.id.layout)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cardListDialog.dismiss();
                                    }
                                });
                        cardListDialog.findViewById(R.id.close)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cardListDialog.dismiss();
                                    }
                                });
                        View footer = View.inflate(v.getContext(),
                                R.layout.footer_new_bank_card___pay,
                                null);
                        footer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cardListDialog.dismiss();
                                if (paymentActionListener != null) {
                                    paymentActionListener.llpayNewCard(!llPayment.getCards()
                                            .isEmpty());
                                }
                            }
                        });
                        ListView listView = (ListView) cardListDialog.findViewById(R.id.card_list);
                        listView.addFooterView(footer, null, false);
                        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        listView.setAdapter(new BankCardsAdapter(cards));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(
                                    AdapterView<?> parent, View view, int position, long id) {
                                BankCard card = (BankCard) parent.getAdapter()
                                        .getItem(position);
                                if (card != null) {
                                    llPayment.setCurrentCard(card);
                                    tvCurrentCard.setText(tvCurrentCard.getContext()
                                            .getString(R.string.fmt_bank_card_info___pay,
                                                    card.getBankName(),
                                                    card.getCardType(),
                                                    card.getAccount()));
                                    cardListDialog.dismiss();
                                }
                            }
                        });
                        listView.setItemChecked(0, true);
                        Window win = cardListDialog.getWindow();
                        if (win != null) {
                            WindowManager.LayoutParams params = win.getAttributes();
                            Point point = CommonUtil.getDeviceSize(v.getContext());
                            params.width = point.x;
                            params.height = point.y / 2;
                            win.setGravity(Gravity.BOTTOM);
                            win.setWindowAnimations(R.style.dialog_anim_rise_style);
                        }
                    }
                    cardListDialog.show();
                }
            });
        }

        public void setViewValue(Payment payment, int position) {
            super.setViewValue(payment, position);
            if (!(payment instanceof LLPayment)) {
                return;
            }
            BankCard currentCard = ((LLPayment) payment).getCurrentCard();
            if (currentCard != null) {
                cardLayout.setVisibility(View.VISIBLE);
                tvCurrentCard.setText(tvCurrentCard.getContext()
                        .getString(R.string.fmt_bank_card_info___pay,
                                currentCard.getBankName(),
                                currentCard.getCardType(),
                                currentCard.getAccount()));
            } else {
                cardLayout.setVisibility(View.GONE);
            }
        }
    }

    private class WalletPaymentViewHolder extends PaymentViewHolder {
        private TextView tvWalletMoney;
        private TextView tvWalletHint;

        private WalletPaymentViewHolder(View itemView) {
            super(itemView);
            tvWalletHint = itemView.findViewById(R.id.tv_wallet_hint);
            tvWalletMoney = itemView.findViewById(R.id.tv_wallet_money);
        }

        public void setViewValue(Payment payment, int position) {
            super.setViewValue(payment, position);
            if (!(payment instanceof WalletPayment)) {
                return;
            }
            double walletPrice = ((WalletPayment) payment).getWalletPrice();
            if (((WalletPayment) payment).isEnable()) {
                checkableView.setEnabled(true);
                tvWalletHint.setText(R.string.label_wallet_price___pay);
                tvWalletMoney.setVisibility(View.VISIBLE);
                tvWalletMoney.setText(NumberFormatUtil.formatDouble2String(walletPrice));
            } else {
                checkableView.setEnabled(false);
                tvWalletHint.setText(R.string.label_wallet_not_sufficient___pay);
                tvWalletMoney.setVisibility(View.GONE);
            }
        }
    }

    private class InstallmentPaymentViewHolder extends PaymentViewHolder {
        TextView tvHint;

        private InstallmentPaymentViewHolder(View itemView) {
            super(itemView);
            tvHint = itemView.findViewById(R.id.tv_installment_hint);
        }

        public void setViewValue(Payment payment, int position) {
            super.setViewValue(payment, position);
            if (payment instanceof XiaoxiPayment) {
                XiaoxiPayment xiaoxiPayment = (XiaoxiPayment) payment;
                if (xiaoxiPayment.getInstallmentMoneyStartAt() > 0) {
                    tvHint.setText("月供￥" + CommonUtil.formatDouble2String(xiaoxiPayment
                            .getInstallmentMoneyStartAt()) + "起");
                    tvHint.setVisibility(View.VISIBLE);
                } else {
                    tvHint.setVisibility(View.GONE);
                }
            }
        }
    }

    public interface PaymentActionListener {
        void llpayNewCard(boolean needVerify);
    }
}
