package me.suncloud.marrymemo.view.marry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.marry.MarryApi;
import me.suncloud.marrymemo.fragment.marry.GiftIncomeEditFragment;
import me.suncloud.marrymemo.fragment.marry.MarryBookEditFragment;
import me.suncloud.marrymemo.model.marry.MarryBook;


/**
 * Created by hua_rong on 2017/11/3
 * 记笔帐
 */

public class MarryBookEditActivity extends HljBaseNoBarActivity implements IOnMarryBookEdit {

    private int type;

    @Override
    public String pageTrackTagName() {
        MarryBook marryBook = getIntent().getParcelableExtra(ARG_MARRY_BOOK);
        int type = getIntent().getIntExtra(ARG_TYPE, 0);
        if (type == MarryBook.TYPE_GIFT_INCOME) {
            if (marryBook != null) {
                return "记礼金修改";
            } else {
                return "记礼金";
            }
        } else {
            if (marryBook != null) {
                return "记笔账修改";
            } else {
                return "记笔账";
            }
        }
    }

    @BindView(R.id.rb_expenditures)
    RadioButton rbExpenditures;
    @BindView(R.id.rb_gift_income)
    RadioButton rbGiftIncome;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    private FragmentTransaction ft;
    private HljHttpSubscriber deleteSubscriber;
    private HljHttpSubscriber saveSubscriber;

    private MarryBookEditFragment marryBookEditFragment;
    private GiftIncomeEditFragment giftIncomeEditFragment;
    private MarryBook marryBook;
    public static final String ARG_TYPE = "arg_type";
    public static final String ARG_MARRY_BOOK = "marryBook";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record_account);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initView();
    }

    private void initView() {
        int screenWidth = CommonUtil.getDeviceSize(this).x;
        rbExpenditures.getLayoutParams().width = (int) Math.round(screenWidth * 0.32);
        rbGiftIncome.getLayoutParams().width = (int) Math.round(screenWidth * 0.32);
        marryBook = getIntent().getParcelableExtra(ARG_MARRY_BOOK);
        type = getIntent().getIntExtra(ARG_TYPE, 0);
        if (marryBook != null) {
            actionLayout.setVisibility(View.GONE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        if (type == MarryBook.TYPE_GIFT_INCOME) {
            setSelect(1);
        } else {
            setSelect(0);
        }
    }

    private void setSelect(int position) {
        FragmentManager manager = getSupportFragmentManager();
        ft = manager.beginTransaction();
        hideFragment();
        switch (position) {
            case 0:
                if (marryBookEditFragment == null) {
                    marryBookEditFragment = MarryBookEditFragment.newInstance(marryBook);
                    marryBookEditFragment.setOnMarryBookListener(this);
                    ft.add(R.id.fl_container, marryBookEditFragment, "marryBookEditFragment");
                } else {
                    ft.show(marryBookEditFragment);
                }
                rbExpenditures.setChecked(true);
                break;
            case 1:
                if (giftIncomeEditFragment == null) {
                    giftIncomeEditFragment = GiftIncomeEditFragment.newInstance(marryBook);
                    giftIncomeEditFragment.setOnGiftIncomeListener(this);
                    ft.add(R.id.fl_container, giftIncomeEditFragment, "giftIncomeEditFragment");
                } else {
                    ft.show(giftIncomeEditFragment);
                }
                rbGiftIncome.setChecked(true);
                break;
        }
        ft.commitAllowingStateLoss();
    }

    private void hideFragment() {
        if (ft != null) {
            if (marryBookEditFragment != null && !marryBookEditFragment.isHidden()) {
                ft.hide(marryBookEditFragment);
            }
            if (giftIncomeEditFragment != null && !giftIncomeEditFragment.isHidden()) {
                ft.hide(giftIncomeEditFragment);
            }
        }
    }

    @OnClick({R.id.rb_expenditures, R.id.rb_gift_income})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_expenditures:
                setSelect(0);
                break;
            case R.id.rb_gift_income:
                setSelect(1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_down);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSave(
            double money,
            String remark,
            long typeId,
            long id,
            String guestName,
            ArrayList<Photo> photos) {
        CommonUtil.unSubscribeSubs(saveSubscriber);
        saveSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Intent intent = getIntent();
                        intent.putExtra(MarryBookActivity.ARG_IS_SCROLL_TO_GIFT,
                                rbGiftIncome.isChecked());
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }
                })
                .build();
        MarryApi.postCashBookAdd(money, remark, typeId, id, guestName, photos)
                .subscribe(saveSubscriber);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDelete(long id) {
        if (id == 0) {
            return;
        }
        CommonUtil.unSubscribeSubs(deleteSubscriber);
        deleteSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                })
                .build();
        MarryApi.deleteBook(id)
                .subscribe(deleteSubscriber);
    }

    @Override
    public void onCancel() {
        onBackPressed();
    }

    @Override
    public void importGuestSuccess() {
        Intent intent = getIntent();
        intent.putExtra(MarryBookActivity.ARG_IS_SCROLL_TO_GIFT, true);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(deleteSubscriber, saveSubscriber);
    }
}