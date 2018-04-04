package com.hunliji.posclient.views.activites;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljupdatelibrary.HljUpdate;
import com.hunliji.posclient.Constants;
import com.hunliji.posclient.R;
import com.hunliji.posclient.api.order.OrderApi;
import com.hunliji.posclient.models.relam.PosPayResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

@Route(path = RouterPath.IntentPath.Customer.MAIN)
@RuntimePermissions
public class MainActivity extends HljBaseNoBarActivity {

    private Realm realm;
    private boolean isExit;

    private Subscription initSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        setDefaultStatusBarPadding();
        initValues();
        initLoad();
    }

    private void initValues() {
        onNewIntent(getIntent());
        realm = Realm.getDefaultInstance();
    }

    private void initLoad() {
        HljUpdate.getInstance()
                .updateCheck(this);
        initSub = realm.where(PosPayResult.class)
                .isNotNull("traceNo")
                .findAllAsync()
                .asObservable()
                .filter(new Func1<RealmResults<PosPayResult>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<PosPayResult> payResults) {
                        return payResults.isLoaded();
                    }
                })
                .first()
                .concatMapDelayError(new Func1<RealmResults<PosPayResult>, Observable<?>>() {
                    @Override
                    public Observable<?> call(RealmResults<PosPayResult> payResults) {
                        return Observable.from(payResults)
                                .concatMap(new Func1<PosPayResult, Observable<?>>() {
                                    @Override
                                    public Observable<?> call(final PosPayResult payResult) {
                                        return OrderApi.submitPosPayResultObb(payResult)
                                                .map(new Func1() {
                                                    @Override
                                                    public Object call(Object o) {
                                                        realm.beginTransaction();
                                                        payResult.deleteFromRealm();
                                                        realm.commitTransaction();
                                                        return o;
                                                    }
                                                });
                                    }
                                });
                    }
                })
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }

    @OnClick(R.id.btn_scan)
    void onScanWithCheck() {
        MainActivityPermissionsDispatcher.onScanWithCheck(this);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void onScan() {
        startActivity(new Intent(this, ScanQRCodeActivity.class));
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void onRationale(PermissionRequest request) {
        DialogUtil.showRationalePermissionsDialog(this,
                request,
                getString(R.string.msg_permission_r_for_camera___cm));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,
                requestCode,
                grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        exitBy2Click();
    }

    private void exitBy2Click() {
        Timer timer;
        if (!isExit) {
            isExit = true;
            ToastUtil.showToast(this, null, R.string.msg_exit_app);
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getStringExtra("action");
        if (!TextUtils.isEmpty(action)) {
            switch (action) {
                case "exit":
                    finish();
                    break;
            }
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub);
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HljUpdate.getInstance()
                .update(this);
    }
}