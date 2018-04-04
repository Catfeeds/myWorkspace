package me.suncloud.marrymemo.view.budget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.ToolsFragment;
import me.suncloud.marrymemo.fragment.user.UserFragment;
import me.suncloud.marrymemo.model.budget.BudgetCategory;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.NewBudgetUtil;
import me.suncloud.marrymemo.view.marry.MarryBookActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 结婚预算输入金额页面
 * Created by jinxin on 2016/5/26.
 */
public class NewWeddingBudgetFigureActivity extends HljBaseNoBarActivity {

    /*
        进入这个页面的入口 有ToolsFragment
          NewWeddingBudgetActivity
          MarryBookActivity
          UserFragment
          BannerUtil
          NewWeddingBudgetCategoryActivity
          NewWeddingBudgetCategoryActivity 进入以后在到NewWeddingBudgetActivity,
          会调用进入以后在到NewWeddingBudgetActivity OnNewIntent
        有ToolsFragment MarryBookActivity UserFragment BannerUtil在设置预算以后 进入预算结果页
     */
    public static final String ARG_FROM = "arg_from";
    //已经选择的类目
    public static final String ARG_SELECT_LIST = "arg_select_list";

    @BindView(R.id.budget_money)
    EditText budgetMoney;
    @BindView(R.id.action_layout)
    LinearLayout actionLayout;

    @Override
    public String pageTrackTagName() {
        return "设置预算";
    }

    private ArrayList<BudgetCategory> selects;
    private String fromClass;
    private Subscription rxBusSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wedding_figure);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        registerRxBusEvent();
    }

    private void initConstant() {
        if (getIntent() != null) {
            fromClass = getIntent().getStringExtra(ARG_FROM);
            selects = getIntent().getParcelableArrayListExtra(ARG_SELECT_LIST);
        }
    }

    private void initWidget() {
        setActionBarPadding(actionLayout);
    }

    private void registerRxBusEvent() {
        if (rxBusSubscription == null || rxBusSubscription.isUnsubscribed()) {
            rxBusSubscription = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CLOSE_BUDGET:
                                    finish();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    public void onBudget(View view) {
        String moneyStr = budgetMoney.getEditableText()
                .toString();
        if (JSONUtil.isEmpty(moneyStr)) {
            Toast.makeText(this, getString(R.string.label_budget_hint), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        double money = Double.parseDouble(moneyStr);
        if (money <= 0) {
            Toast.makeText(this, getString(R.string.label_error_money), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (money < 20000) {
            Toast.makeText(this, getString(R.string.label_budget_figure_hint), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (money < NewBudgetUtil.getInstance()
                .getMinMoney(selects) * NewBudgetUtil.MONEY_UNIT) {
            Toast.makeText(this, getString(R.string.label_budget_figure_hint), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (fromClass != null && (fromClass.equals(ToolsFragment.class.getSimpleName()) ||
                fromClass.equals(
                NewWeddingBudgetCategoryActivity.class.getSimpleName()) || fromClass.equals(
                MarryBookActivity.class.getSimpleName()) || fromClass.equals(UserFragment.class
                .getSimpleName()) || fromClass.equals(
                BannerUtil.class.getSimpleName()))) {
            Intent intent = new Intent(this, NewWeddingBudgetActivity.class);
            intent.putExtra(NewWeddingBudgetActivity.ARG_FIGURE, money);
            intent.putParcelableArrayListExtra(NewWeddingBudgetActivity.ARG_SELECTED_CATEGORY,
                    selects);
            intent.putExtra(NewWeddingBudgetActivity.ARG_FROM, fromClass);
            startActivity(intent);
            //发送关闭这个页面的通知
            RxBus.getDefault()
                    .post(new RxEvent(RxEvent.RxEventType.CLOSE_BUDGET, null));
        } else {
            Intent data = new Intent();
            data.putExtra(NewWeddingBudgetActivity.ARG_FIGURE, money);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusSubscription);
    }
}
