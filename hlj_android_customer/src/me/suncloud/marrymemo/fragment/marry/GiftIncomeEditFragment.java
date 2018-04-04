package me.suncloud.marrymemo.fragment.marry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.model.marry.GuestCount;
import me.suncloud.marrymemo.model.marry.MarryBook;
import me.suncloud.marrymemo.view.marry.IOnMarryBookEdit;
import me.suncloud.marrymemo.view.marry.ImportMoneyGiftActivity;
import me.suncloud.marrymemo.view.marry.MarryBookEditActivity;

/**
 * Created by hua_rong on 2017/12/8 礼金编辑
 */

public class GiftIncomeEditFragment extends RefreshFragment {

    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.ll_gift_account)
    LinearLayout llGiftAccount;
    @BindView(R.id.ll_delete)
    LinearLayout llDelete;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.ll_gift_income)
    LinearLayout llGiftIncome;
    @BindView(R.id.tv_gift_count)
    TextView tvGiftCount;
    Unbinder unbinder;
    private long id;
    public static final int MONEY_GIFT_CODE = 110;
    private IOnMarryBookEdit onGiftIncomeListener;
    private HljHttpSubscriber subscriber;
    private MarryBook marryBook;


    public static GiftIncomeEditFragment newInstance(MarryBook marryBook) {
        Bundle args = new Bundle();
        GiftIncomeEditFragment fragment = new GiftIncomeEditFragment();
        args.putParcelable(MarryBookEditActivity.ARG_MARRY_BOOK, marryBook);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValue();
    }

    private void initValue() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            marryBook = bundle.getParcelable(MarryBookEditActivity.ARG_MARRY_BOOK);
            if (marryBook != null) {
                id = marryBook.getId();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_gift_income, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        onNetError();
        onLoad();
    }

    private void onNetError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }

    private void onLoad() {
        CommonUtil.unSubscribeSubs(subscriber);
        subscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<GuestCount>() {
                    @Override
                    public void onNext(GuestCount guestCount) {
                        if (guestCount.getGuestCount() > 0) {
                            llGiftAccount.setVisibility(View.VISIBLE);
                            tvGiftCount.setText(getString(R.string.label_import_money_gift_count,
                                    guestCount.getGuestCount()));
                        } else {
                            llGiftAccount.setVisibility(View.GONE);
                        }
                    }
                })
                .setContentView(llGiftIncome)
                .setProgressBar(progressBar)
                .build();
        MarryApi.getGuestNameCount()
                .subscribe(subscriber);
    }

    private void initView() {
        llDelete.setVisibility(id > 0 ? View.VISIBLE : View.GONE);
        if (marryBook != null) {
            String remark = marryBook.getRemark();
            etRemark.setText(remark);
            etAmount.setText(String.format(Locale.getDefault(), "%.2f", marryBook.getMoney()));
            etAmount.setSelection(etAmount.length());
            etName.setText(marryBook.getType()
                    .getTitle());
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @OnClick(R.id.btn_cancel)
    void onCancel() {
        if (onGiftIncomeListener != null) {
            onGiftIncomeListener.onCancel();
        }
    }

    @OnClick(R.id.btn_save)
    public void onSave() {
        if (etAmount.length() == 0) {
            ToastUtil.showToast(getContext(), "金额必须大于0", 0);
            return;
        }
        double money = Double.valueOf(etAmount.getText()
                .toString()
                .trim());
        String remark = etRemark.getText()
                .toString()
                .trim();
        String guestName = etName.getText()
                .toString()
                .trim();
        if (TextUtils.isEmpty(guestName)) {
            ToastUtil.showToast(getContext(), "请输入送礼者姓名", 0);
            return;
        }
        if (onGiftIncomeListener != null) {
            onGiftIncomeListener.onSave(money, remark, MarryApi.TYPE_GIFT_ID, id, guestName, null);
        }
    }

    @OnClick(R.id.tv_delete)
    void onDelete() {
        if (onGiftIncomeListener != null) {
            onGiftIncomeListener.onDelete(id);
        }
    }

    @OnClick(R.id.ll_gift_account)
    void onGiftAccountClick() {
        hideKeyboard();
        Intent intent = new Intent(getContext(), ImportMoneyGiftActivity.class);
        startActivityForResult(intent, MONEY_GIFT_CODE);
    }

    private void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = ((InputMethodManager) getActivity().getSystemService
                    (Activity.INPUT_METHOD_SERVICE));
            if (imm != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MONEY_GIFT_CODE && resultCode == Activity.RESULT_OK) {
            if (onGiftIncomeListener != null) {
                onGiftIncomeListener.importGuestSuccess();
            }
        }
    }

    public void setOnGiftIncomeListener(IOnMarryBookEdit onGiftIncomeListener) {
        this.onGiftIncomeListener = onGiftIncomeListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(subscriber);
    }
}
