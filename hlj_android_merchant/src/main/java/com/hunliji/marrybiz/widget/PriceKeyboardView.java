package com.hunliji.marrybiz.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.JSONUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.grantland.widget.AutofitHelper;

/**
 * Created by Suncloud on 2016/2/16.
 */
public class PriceKeyboardView extends FrameLayout implements View.OnClickListener {

    private double newTotalPrice;
    private double newDepositPrice;
    private double currentNewPrice;
    private StringBuilder newTotalPriceStr;
    private StringBuilder newDepositPriceStr;
    private StringBuilder currentNewPriceStr;
    private ConfirmOnClickListener confirmOnClickListener;
    private KBViewHolder kbViewHolder;
    private EditText currentEtNewPrice;

    public static final int MODE_TOTAL = 0;
    public static final int MODE_DEPOSIT = 1;
    public static final int MODE_BOTH = 2;

    public PriceKeyboardView(Context context) {
        this(context, null);
    }

    public PriceKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.price_keyboard_layout, this);

        init();
    }

    private void init() {
        kbViewHolder = new KBViewHolder(getRootView());
        AutofitHelper.create(kbViewHolder.etNewPrice1);
        AutofitHelper.create(kbViewHolder.etNewPrice2);

        kbViewHolder.etNewPrice1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                kbViewHolder.etNewPrice1.setSelection(s.length());
            }
        });
        kbViewHolder.etNewPrice2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                kbViewHolder.etNewPrice2.setSelection(s.length());
            }
        });

        // 默认设置
        setPriceModifyMode(MODE_TOTAL);

        findViewById(R.id.btn_key_0).setOnClickListener(this);
        findViewById(R.id.btn_key_1).setOnClickListener(this);
        findViewById(R.id.btn_key_2).setOnClickListener(this);
        findViewById(R.id.btn_key_3).setOnClickListener(this);
        findViewById(R.id.btn_key_4).setOnClickListener(this);
        findViewById(R.id.btn_key_5).setOnClickListener(this);
        findViewById(R.id.btn_key_6).setOnClickListener(this);
        findViewById(R.id.btn_key_7).setOnClickListener(this);
        findViewById(R.id.btn_key_8).setOnClickListener(this);
        findViewById(R.id.btn_key_9).setOnClickListener(this);
        findViewById(R.id.btn_key_0).setOnClickListener(this);
        findViewById(R.id.btn_key_confirm).setOnClickListener(this);
        findViewById(R.id.btn_key_dot).setOnClickListener(this);
        findViewById(R.id.btn_key_back_space).setOnClickListener(this);

        findViewById(R.id.btn_new_price_1).setOnClickListener(this);
        findViewById(R.id.btn_new_price_2).setOnClickListener(this);
    }

    public void setPriceModifyMode(int mode) {
        switch (mode) {
            case MODE_TOTAL:
                kbViewHolder.priceLayout1.setVisibility(VISIBLE);
                kbViewHolder.priceLayout2.setVisibility(GONE);
                currentEtNewPrice = kbViewHolder.etNewPrice1;
                kbViewHolder.etNewPrice1.requestFocus();
                kbViewHolder.etNewPrice2.clearFocus();
                currentEtNewPrice.setSelection(currentEtNewPrice.getText()
                        .length());
                break;
            case MODE_DEPOSIT:
                kbViewHolder.priceLayout1.setVisibility(GONE);
                kbViewHolder.priceLayout2.setVisibility(VISIBLE);
                currentEtNewPrice = kbViewHolder.etNewPrice2;
                kbViewHolder.etNewPrice2.requestFocus();
                kbViewHolder.etNewPrice1.clearFocus();
                currentEtNewPrice.setSelection(currentEtNewPrice.getText()
                        .length());
                break;
            case MODE_BOTH:
                kbViewHolder.priceLayout1.setVisibility(VISIBLE);
                kbViewHolder.priceLayout2.setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_key_0:
                if (currentEtNewPrice != null) {
                    if (newTotalPriceStr.length() == 1 && currentNewPrice == 0) {
                        // 当前已有零的话，不继续添加零
                        return;
                    } else {
                        appendToPrice("0");
                    }
                }
                break;
            case R.id.btn_key_1:
                if (currentEtNewPrice != null) {
                    appendToPrice("1");
                }
                break;
            case R.id.btn_key_2:
                if (currentEtNewPrice != null) {
                    appendToPrice("2");
                }
                break;
            case R.id.btn_key_3:
                if (currentEtNewPrice != null) {
                    appendToPrice("3");
                }
                break;
            case R.id.btn_key_4:
                if (currentEtNewPrice != null) {
                    appendToPrice("4");
                }
                break;
            case R.id.btn_key_5:
                if (currentEtNewPrice != null) {
                    appendToPrice("5");
                }
                break;
            case R.id.btn_key_6:
                if (currentEtNewPrice != null) {
                    appendToPrice("6");
                }
                break;
            case R.id.btn_key_7:
                if (currentEtNewPrice != null) {
                    appendToPrice("7");
                }
                break;
            case R.id.btn_key_8:
                if (currentEtNewPrice != null) {
                    appendToPrice("8");
                }
                break;
            case R.id.btn_key_9:
                if (currentEtNewPrice != null) {
                    appendToPrice("9");
                }
                break;
            case R.id.btn_key_dot:
                if (currentEtNewPrice != null) {
                    if (currentNewPriceStr.toString()
                            .contains(".")) {
                        // 如当前数字以.结尾就不再添加.
                        return;
                    } else if (currentNewPriceStr.length() == 0) {
                        appendToPrice("0.");
                    } else {
                        appendToPrice(".");
                    }
                }
                break;
            case R.id.btn_key_back_space:
                if (currentEtNewPrice != null && currentNewPriceStr.length() > 0) {
                    if (currentNewPriceStr.length() == 1) {
                        currentNewPriceStr = new StringBuilder("");
                        currentEtNewPrice.setText("");
                        currentNewPrice = 0;
                    } else {
                        currentNewPriceStr = new StringBuilder(currentNewPriceStr.substring(0,
                                currentNewPriceStr.length() - 1));
                        currentEtNewPrice.setText(currentNewPriceStr);
                        currentNewPrice = Double.valueOf(currentNewPriceStr.toString());
                    }
                }
                if (currentEtNewPrice == kbViewHolder.etNewPrice1) {
                    newTotalPrice = currentNewPrice;
                } else {
                    newDepositPrice = currentNewPrice;
                }
                break;
            case R.id.btn_key_confirm:
                if (JSONUtil.isEmpty(currentNewPriceStr.toString())) {
                    // 价格不能为空
                    Toast.makeText(getContext(),
                            R.string.msg_wrong_input_number,
                            Toast.LENGTH_SHORT)
                            .show();
                } else if (confirmOnClickListener != null) {
                    confirmOnClickListener.priceConfirm(newTotalPrice, newDepositPrice);
                }
                break;
            case R.id.btn_new_price_1:
                if (currentEtNewPrice != kbViewHolder.etNewPrice1) {
                    currentEtNewPrice = kbViewHolder.etNewPrice1;
                    currentNewPrice = newTotalPrice;
                    currentNewPriceStr = newTotalPriceStr;
                    kbViewHolder.etNewPrice1.requestFocus();
                    kbViewHolder.etNewPrice2.clearFocus();
                    currentEtNewPrice.setSelection(currentEtNewPrice.getText()
                            .length());
                }
                break;
            case R.id.btn_new_price_2:
                if (currentEtNewPrice != kbViewHolder.etNewPrice2) {
                    currentEtNewPrice = kbViewHolder.etNewPrice2;
                    currentNewPrice = newDepositPrice;
                    currentNewPriceStr = newDepositPriceStr;
                    kbViewHolder.etNewPrice2.requestFocus();
                    kbViewHolder.etNewPrice1.clearFocus();
                    currentEtNewPrice.setSelection(currentEtNewPrice.getText()
                            .length());
                }
                break;
        }
    }

    public void setDotEnabled(boolean enable) {
        findViewById(R.id.btn_key_dot).setEnabled(enable);
    }

    private void appendToPrice(String str) {
        // 当小数点后有两位数就不再能输入
        if (currentNewPriceStr.toString()
                .contains(".")) {
            int dotIndex = currentNewPriceStr.toString()
                    .indexOf(".");
            if (dotIndex < currentNewPriceStr.length()) {
                String suffix = currentNewPriceStr.toString()
                        .substring(dotIndex, currentNewPriceStr.length());
                if (suffix.length() >= 3) {
                    return;
                }
            }
        }
        if (currentNewPriceStr.length() == 1 && currentNewPrice == 0 && !str.equals(".")) {
            currentNewPriceStr = new StringBuilder(str);
        } else {
            currentNewPriceStr.append(str);
        }
        currentEtNewPrice.setText(currentNewPriceStr);
        if (!JSONUtil.isEmpty(currentNewPriceStr.toString())) {
            try {
                currentNewPrice = Double.valueOf(currentNewPriceStr.toString());
                if (currentEtNewPrice == kbViewHolder.etNewPrice1) {
                    newTotalPrice = currentNewPrice;
                } else {
                    newDepositPrice = currentNewPrice;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置总价
     *
     * @param newPrice 新总价
     * @param oldPrice 原总价
     */
    public void setTotalPrices(double newPrice, double oldPrice) {
        newTotalPrice = newPrice;
        newTotalPriceStr = new StringBuilder(CommonUtil.formatDouble2String(newTotalPrice));
        kbViewHolder.etNewPrice1.setText(newTotalPriceStr);
        kbViewHolder.tvOldPrice1.setText(CommonUtil.formatDouble2String(oldPrice));
        if (currentEtNewPrice == kbViewHolder.etNewPrice1) {
            // 当前聚焦修改的是总价，设置current变量
            currentNewPrice = newTotalPrice;
            currentNewPriceStr = newTotalPriceStr;
        }
    }

    /**
     * 设置定金价格
     *
     * @param newPrice 新的定金
     * @param oldPrice 原来的定金
     */
    public void setDepositPrices(double newPrice, double oldPrice) {
        newDepositPrice = newPrice;
        newDepositPriceStr = new StringBuilder(CommonUtil.formatDouble2String(newDepositPrice));
        kbViewHolder.etNewPrice2.setText(newDepositPriceStr);
        kbViewHolder.tvOldPrice2.setText(CommonUtil.formatDouble2String(oldPrice));
        if (currentEtNewPrice == kbViewHolder.etNewPrice2) {
            // 当前聚焦修改的是定金，设置current变量
            currentNewPrice = newDepositPrice;
            currentNewPriceStr = newDepositPriceStr;
        }
    }

    public void setNewTotalPriceLabel(int id) {
        kbViewHolder.newPriceLabel1.setText(id);
    }

    public void setOldTotalPriceLabel(int id) {
        kbViewHolder.oldPriceLabel1.setText(id);
    }

    public void setNewDepositPriceLabel(int id) {
        kbViewHolder.newPriceLabel2.setText(id);
    }

    public void setOldDepositPriceLabel(int id) {
        kbViewHolder.oldPriceLabel2.setText(id);
    }

    public interface ConfirmOnClickListener {
        void priceConfirm(double newTotalPrice, double newDepositPrice);
    }

    public void setConfirmOnClickListener(ConfirmOnClickListener confirmOnClickListener) {
        this.confirmOnClickListener = confirmOnClickListener;
    }

    static class KBViewHolder {
        @BindView(R.id.new_price_label_1)
        TextView newPriceLabel1;
        @BindView(R.id.et_new_price_1)
        EditText etNewPrice1;
        @BindView(R.id.btn_new_price_1)
        Button btnNewPrice1;
        @BindView(R.id.old_price_label_1)
        TextView oldPriceLabel1;
        @BindView(R.id.tv_old_price_1)
        TextView tvOldPrice1;
        @BindView(R.id.new_price_label_2)
        TextView newPriceLabel2;
        @BindView(R.id.et_new_price_2)
        EditText etNewPrice2;
        @BindView(R.id.btn_new_price_2)
        Button btnNewPrice2;
        @BindView(R.id.old_price_label_2)
        TextView oldPriceLabel2;
        @BindView(R.id.tv_old_price_2)
        TextView tvOldPrice2;
        @BindView(R.id.price_layout_2)
        LinearLayout priceLayout2;
        @BindView(R.id.price_layout_1)
        LinearLayout priceLayout1;
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.btn_key_1)
        Button btnKey1;
        @BindView(R.id.btn_key_2)
        Button btnKey2;
        @BindView(R.id.btn_key_3)
        Button btnKey3;
        @BindView(R.id.btn_key_back_space)
        ImageButton btnKeyBackSpace;
        @BindView(R.id.btn_key_4)
        Button btnKey4;
        @BindView(R.id.btn_key_5)
        Button btnKey5;
        @BindView(R.id.btn_key_6)
        Button btnKey6;
        @BindView(R.id.btn_key_7)
        Button btnKey7;
        @BindView(R.id.btn_key_8)
        Button btnKey8;
        @BindView(R.id.btn_key_9)
        Button btnKey9;
        @BindView(R.id.btn_key_confirm)
        Button btnKeyConfirm;
        @BindView(R.id.btn_key_dot)
        Button btnKeyDot;
        @BindView(R.id.btn_key_0)
        Button btnKey0;
        @BindView(R.id.btn_key_hide)
        ImageButton btnKeyHide;

        KBViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
