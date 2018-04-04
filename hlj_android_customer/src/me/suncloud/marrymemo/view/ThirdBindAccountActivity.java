package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.login.LoginApi;
import me.suncloud.marrymemo.model.ThirdBind;
import me.suncloud.marrymemo.model.login.ThirdBindPostBody;
import me.suncloud.marrymemo.util.LoginHelper;
import com.hunliji.hljsharelibrary.HljThirdLogin;
import com.hunliji.hljsharelibrary.models.ThirdLoginParameter;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 第三方账号绑定activity
 * Created by jinxin on 2017/1/6 0006.
 */

public class ThirdBindAccountActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener {

    @BindView(R.id.tv_qq_bind)
    TextView tvQqBind;
    @BindView(R.id.qq_layout)
    LinearLayout qqLayout;
    @BindView(R.id.tv_weibo_bind)
    TextView tvWeiboBind;
    @BindView(R.id.weibo_layout)
    LinearLayout weiboLayout;
    @BindView(R.id.tv_wechat_bind)
    TextView tvWechatBind;
    @BindView(R.id.wechat_layout)
    LinearLayout wechatLayout;
    @BindView(R.id.scroll_view)
    PullToRefreshScrollView scrollView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;

    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber unBindSubscriber;
    private HljHttpSubscriber bindSubscriber;
    private Subscription rxSubscription;
    private int unBindColor;//绑定颜色
    private int bindColor;//解绑颜色
    private String bindString;//解绑
    private String unBindString;//绑定
    private boolean isBind;//绑定true 解绑false
    private TextView tvClicked;//
    private Dialog unBindDialog;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_account);
        ButterKnife.bind(this);
        unBindColor = ContextCompat.getColor(this, R.color.green);
        bindColor = ContextCompat.getColor(this, R.color.colorPrimary);
        bindString = getString(R.string.label_unbind1);
        unBindString = getString(R.string.label_bind);
        initUnbind();
        scrollView.setOnRefreshListener(this);
        onRefresh(scrollView);
        registerRxBus();
    }

    private void registerRxBus() {
        rxSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .filter(new Func1<RxEvent, Boolean>() {
                    @Override
                    public Boolean call(RxEvent rxEvent) {
                        return rxEvent.getType() == RxEvent.RxEventType.THIRD_BIND_CALLBACK;
                    }
                })
                .map(new Func1<RxEvent, ThirdLoginParameter>() {
                    @Override
                    public ThirdLoginParameter call(RxEvent rxEvent) {
                        return (ThirdLoginParameter) rxEvent.getObject();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxBusSubscriber<ThirdLoginParameter>() {
                    @Override
                    protected void onEvent(ThirdLoginParameter thirdLoginParameter) {
                        onBind(thirdLoginParameter);
                    }
                });
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(refreshView.isRefreshing() ? null : progressBar)
                    .setPullToRefreshBase(refreshView)
                    .setEmptyView(emptyView)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ThirdBind>>>
                            () {
                        @Override
                        public void onNext(HljHttpData<List<ThirdBind>> listHljHttpData) {
                            if (listHljHttpData != null) {
                                scrollView.setVisibility(View.VISIBLE);
                                initBind(listHljHttpData.getData());
                            } else {
                                scrollView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .setDataNullable(true)
                    .build();
        }
        Observable observable = LoginApi.getThirdBind();
        observable.subscribe(refreshSubscriber);
    }

    private void initUnbind() {
        tvQqBind.setTextColor(unBindColor);
        tvQqBind.setText(unBindString);
        tvQqBind.setTag(false);
        tvWeiboBind.setTextColor(unBindColor);
        tvWeiboBind.setText(unBindString);
        tvWeiboBind.setTag(false);
        tvWechatBind.setTextColor(unBindColor);
        tvWechatBind.setText(unBindString);
        tvWechatBind.setTag(false);
    }

    private void initBind(List<ThirdBind> binds) {
        if (binds == null || binds.isEmpty()) {
            return;
        }
        for (ThirdBind bind : binds) {
            if (bind.getId() <= 0) {
                continue;
            }
            String type = bind.getType();
            if (TextUtils.isEmpty(type)) {
                continue;
            }
            //qq,sina,weixin
            switch (type) {
                case "qq":
                    tvQqBind.setTextColor(bindColor);
                    tvQqBind.setText(bindString);
                    tvQqBind.setTag(true);
                    break;
                case "sina":
                    tvWeiboBind.setTextColor(bindColor);
                    tvWeiboBind.setText(bindString);
                    tvWeiboBind.setTag(true);
                    break;
                case "weixin":
                    tvWechatBind.setTextColor(bindColor);
                    tvWechatBind.setText(bindString);
                    tvWechatBind.setTag(true);
                    break;
                default:
                    break;
            }

        }
    }

    @OnClick({R.id.tv_qq_bind, R.id.tv_weibo_bind, R.id.tv_wechat_bind})
    public void onClick(View view) {
        tvClicked = (TextView) view;
        int id = tvClicked.getId();
        if (tvClicked.getTag() == null) {
            return;
        }
        boolean tag = (boolean) tvClicked.getTag();
        switch (id) {
            case R.id.tv_qq_bind:
                if (tag) {
                    //解绑
                    unBind(LoginHelper.QQ_TAG);
                } else {
                    HljThirdLogin.qqLogin(this)
                            .bind();
                }
                break;
            case R.id.tv_weibo_bind:
                if (tag) {
                    //解绑
                    unBind(LoginHelper.WEIBO_TAG);
                } else {
                    HljThirdLogin.weiboLogin(this)
                            .bind();
                }
                break;
            case R.id.tv_wechat_bind:
                if (tag) {
                    //解绑
                    unBind(LoginHelper.WEIXIN_TAG);
                } else {
                    HljThirdLogin.weixinLogin(this)
                            .bind();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(refreshSubscriber,
                bindSubscriber,
                unBindSubscriber,
                rxSubscription);
        if (unBindDialog != null && unBindDialog.isShowing()) {
            unBindDialog.dismiss();
        }
        super.onFinish();
    }

    public void onBind(ThirdLoginParameter parameter) {
        if (parameter == null) {
            return;
        }
        if (bindSubscriber == null || bindSubscriber.isUnsubscribed()) {
            bindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(HljHttpResult result) {
                            if (result != null) {
                                if (result.getStatus()
                                        .getRetCode() == 0) {
                                    //绑定成功
                                    tvClicked.setTextColor(bindColor);
                                    tvClicked.setText(bindString);
                                    tvClicked.setTag(true);
                                    Toast.makeText(ThirdBindAccountActivity.this,
                                            getString(R.string.label_bind_success),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(ThirdBindAccountActivity.this,
                                            result.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    })
                    .build();
        }
        Observable<HljHttpResult> observable = LoginApi.thirdBind(parameter);
        observable.subscribe(bindSubscriber);
    }

    public void unBind(String type) {
        if (TextUtils.isEmpty(type)) {
            return;
        }

        String typeName = null;
        switch (type) {
            case LoginHelper.QQ_TAG:
                typeName = "QQ";
                break;
            case LoginHelper.WEIBO_TAG:
                typeName = "微博";
                break;
            case LoginHelper.WEIXIN_TAG:
                typeName = "微信";
                break;
            default:
                break;
        }

        this.type = type;
        if (unBindDialog == null) {
            unBindDialog = DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.label_third_unbind_hint3),
                    getString(R.string.label_third_unbind_hint1, typeName),
                    getString(R.string.label_wrong_action),
                    getString(R.string.label_third_unbind_hint2),
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unBindPostRequest(ThirdBindAccountActivity.this.type);
                        }
                    });

        }

        if (unBindDialog.isShowing()) {
            return;
        }
        unBindDialog.show();
    }

    private void unBindPostRequest(String type) {
        if (unBindSubscriber == null || unBindSubscriber.isUnsubscribed()) {
            unBindSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(HljHttpResult result) {
                            if (result != null) {
                                if (result.getStatus()
                                        .getRetCode() == 0) {
                                    //解绑成功
                                    tvClicked.setTextColor(unBindColor);
                                    tvClicked.setText(unBindString);
                                    tvClicked.setTag(false);
                                    Toast.makeText(ThirdBindAccountActivity.this,
                                            getString(R.string.label_unbind_success),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(ThirdBindAccountActivity.this,
                                            result.getStatus()
                                                    .getMsg(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    })
                    .build();
        }
        ThirdBindPostBody body = new ThirdBindPostBody();
        body.setType(type);
        Observable<HljHttpResult> observable = LoginApi.thirdUnBind(body);
        observable.subscribe(unBindSubscriber);
    }
}
