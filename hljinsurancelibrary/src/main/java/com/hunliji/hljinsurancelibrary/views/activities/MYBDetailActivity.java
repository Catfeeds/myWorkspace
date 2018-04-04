package com.hunliji.hljinsurancelibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;
import com.hunliji.hljsharelibrary.HljShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXFileObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hunliji.hljinsurancelibrary.models.InsuranceProduct;
import rx.Observable;

/**
 * 蜜月保保单详情（投保成功）
 * Created by jinxin on 2017/8/18 0018.
 */

public class MYBDetailActivity extends HljBaseActivity {

    private static final int WEIXIN = 0;
    private static final int EMAIL = 1;

    @BindView(R2.id.tv_project_name)
    TextView tvProjectName;
    @BindView(R2.id.tv_project_NO)
    TextView tvProjectNO;
    @BindView(R2.id.tv_company_name)
    TextView tvCompanyName;
    @BindView(R2.id.tv_project_state)
    TextView tvProjectState;
    @BindView(R2.id.rl_company_bg)
    RelativeLayout rlCompanyBg;
    @BindView(R2.id.tv_insurance_time)
    TextView tvInsuranceTime;
    @BindView(R2.id.tv_count)
    TextView tvCount;
    @BindView(R2.id.tv_insurance_money)
    TextView tvInsuranceMoney;
    @BindView(R2.id.tv_insurance_way)
    TextView tvInsuranceWay;
    @BindView(R2.id.tv_insurance_download)
    TextView tvInsuranceDownload;
    @BindView(R2.id.tv_groom_identify)
    TextView tvGroomIdentify;
    @BindView(R2.id.tv_groom_phone)
    TextView tvGroomPhone;
    @BindView(R2.id.tv_bride_identify)
    TextView tvBrideIdentify;
    @BindView(R2.id.tv_bride_phone)
    TextView tvBridePhone;
    @BindView(R2.id.tv_look_rule)
    TextView tvLookRule;
    @BindView(R2.id.tv_look_clause_out)
    TextView tvLookClauseOut;
    @BindView(R2.id.tv_look_clause_in)
    TextView tvLookClauseIn;
    @BindView(R2.id.tv_insurance_from)
    TextView tvInsuranceFrom;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.layout_insurance_download)
    LinearLayout layoutInsuranceDownload;
    @BindView(R2.id.tv_service_progress)
    TextView tvServiceProgress;
    @BindView(R2.id.tv_groom_name)
    TextView tvGroomName;
    @BindView(R2.id.tv_bride_name)
    TextView tvBrideName;

    private int[] statusBgArray = {R.drawable.image_bg_to_be_effective, R.drawable
            .image_bg_in_security, R.drawable.image_bg_terminated};
    private String[] statusArray;
    private String id;
    private PolicyDetail policyDetail;
    private Dialog downLoadDialog;
    private IWXAPI api;
    private HljHttpSubscriber downloadSubscriber;
    private HljHttpSubscriber loadSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_yue_bao_detail);
        ButterKnife.bind(this);

        initConstant();
        initLoad();
    }

    private void initConstant() {
        api = WXAPIFactory.createWXAPI(this, HljShare.WEIXINKEY, true);
        api.registerApp(HljShare.WEIXINKEY);
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
        }
        statusArray = getResources().getStringArray(R.array.status);
    }

    private void initLoad() {
        loadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<PolicyDetail>() {
                    @Override
                    public void onNext(PolicyDetail policyDetail) {
                        setPolicyDetail(policyDetail);
                    }
                })
                .build();
        InsuranceApi.getMybDetail(id)
                .subscribe(loadSubscriber);
    }

    private void setPolicyDetail(PolicyDetail policyDetail) {
        if (policyDetail == null) {
            return;
        }
        this.policyDetail = policyDetail;
        InsuranceProduct product = policyDetail.getProduct();
        int status = policyDetail.getStatus();
        if (status == 3 || status == 4) {
            tvInsuranceDownload.setVisibility(View.VISIBLE);
        } else {
            tvInsuranceDownload.setVisibility(View.GONE);
        }
        if (product != null) {
            tvProjectName.setText(product.getTitle());
            if (!TextUtils.isEmpty(policyDetail.getPolicyNo())) {
                tvProjectNO.setText(String.format("NO.%S", policyDetail.getPolicyNo()));
            } else {
                tvProjectNO.setText(null);
            }
            tvCompanyName.setText(String.format("由%s承保", product.getCompany()));
            if (statusArray != null && status > 1 && status < 5) {
                tvProjectState.setText(statusArray[status - 2]);
                rlCompanyBg.setBackgroundResource(statusBgArray[status - 2]);
            }
        }
        DateTime beginDate = policyDetail.getTransBeginDate();
        if (beginDate != null) {
            tvInsuranceTime.setText(beginDate.toString(HljTimeUtils.DATE_FORMAT_SHORT));
        }
        tvCount.setText(String.format("%s份", policyDetail.getNum()));
        String rawGiverName = policyDetail.getGiverName();
        if (!TextUtils.isEmpty(rawGiverName)) {
            String giverName = "(" + rawGiverName + "赠)";
            SpannableStringBuilder ssb = new SpannableStringBuilder(giverName);
            ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color
                    .colorLink));
            ssb.setSpan(span, 1, rawGiverName.length() + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tvInsuranceFrom.setText(ssb);
        } else {
            tvInsuranceFrom.setText(null);
        }

        tvInsuranceMoney.setText(String.format(Locale.getDefault(),
                "￥%.2f",
                policyDetail.getPrice()));
        tvGroomName.setText(policyDetail.getFullName());
        tvGroomIdentify.setText(policyDetail.getCertiNo());
        tvGroomPhone.setText(policyDetail.getPhone());
        tvBrideName.setText(policyDetail.getSpouseName());
        tvBrideIdentify.setText(policyDetail.getSpouseCertiNo());
        tvBridePhone.setText(policyDetail.getSpousePhone());
    }

    @OnClick(R2.id.tv_look_rule)
    void onRule() {
        if (policyDetail == null || policyDetail.getProduct() == null || TextUtils.isEmpty(
                policyDetail.getProduct()
                        .getDetailUrl())) {
            return;
        }
        HljWeb.startWebView(this,
                policyDetail.getProduct()
                        .getDetailUrl());
    }

    @OnClick(R2.id.tv_look_clause_out)
    void onClauseOut() {
        if (policyDetail == null || policyDetail.getProduct() == null || TextUtils.isEmpty(
                policyDetail.getProduct()
                        .getClauseUrlAbroad())) {
            return;
        }
        HljWeb.startWebView(this,
                policyDetail.getProduct()
                        .getClauseUrlAbroad());
    }

    @OnClick(R2.id.tv_look_clause_in)
    void onClauseIn() {
        if (policyDetail == null || policyDetail.getProduct() == null || TextUtils.isEmpty(
                policyDetail.getProduct()
                        .getClauseUrl())) {
            return;
        }
        HljWeb.startWebView(this,
                policyDetail.getProduct()
                        .getClauseUrl());
    }

    @OnClick(R2.id.tv_service_progress)
    void onServiceProgress() {
        HljWeb.startWebView(this,
                policyDetail.getProduct()
                        .getClaimNoticeUrl());
    }

    @OnClick(R2.id.tv_insurance_download)
    void onDownload() {
        if (downLoadDialog != null && downLoadDialog.isShowing()) {
            return;
        }
        if (downLoadDialog == null) {
            downLoadDialog = new Dialog(this, R.style.BubbleDialogTheme);
            Window window = downLoadDialog.getWindow();
            if (window != null) {
                window.setContentView(R.layout.dialog_epolicy_share);
                View shareWx = window.findViewById(R.id.share_weixing);
                View shareEmail = window.findViewById(R.id.share_email);
                View cancel = window.findViewById(R.id.btn_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downLoadDialog.cancel();
                    }
                });
                shareWx.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShare(WEIXIN);
                    }
                });
                shareEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShare(EMAIL);
                    }
                });
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = CommonUtil.getDeviceSize(this).x;
                window.setAttributes(params);
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        downLoadDialog.show();
    }

    private void onShare(final int type) {
        if (!CommonUtil.isNetworkConnected(this)) {
            Toast.makeText(this, "网络异常，请检查网络！", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String epolicyAddress = policyDetail.getEpolicyAddress();
        if (TextUtils.isEmpty(epolicyAddress)) {
            return;
        }
        if (downLoadDialog != null) {
            downLoadDialog.dismiss();
        }
        String filename = policyDetail.getPolicyNo() + ".pdf";
        File fileDir = FileUtil.getPolicyDir(this);
        String filePath = fileDir.getPath() + File.separator + filename;
        if (isHadFile(fileDir, filename)) {
            onNextView(type, filePath);
        } else {
            if (downloadSubscriber == null || downloadSubscriber.isUnsubscribed()) {
                Observable<String> observable = InsuranceApi.download(epolicyAddress, filePath);
                downloadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener<String>() {
                            @Override
                            public void onNext(final String path) {
                                if (!TextUtils.isEmpty(path)) {
                                    ToastUtil.showCustomToast(MYBDetailActivity.this,
                                            R.string.label_down_success);
                                    onNextView(type, path);
                                } else {
                                    Toast.makeText(MYBDetailActivity.this,
                                            getString(R.string.label_policy_generate),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        })
                        .setProgressBar(progressBar)
                        .build();
                observable.subscribe(downloadSubscriber);
            }
        }
    }

    private boolean isHadFile(File fileDir, String fileName) {
        String[] fileList = fileDir.list();
        for (String path : fileList) {
            if (path.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    private void onNextView(int type, String path) {
        switch (type) {
            case WEIXIN:
                onWxShare(path);
                break;
            case EMAIL:
                onEmailShare(path);
                break;
        }
    }

    private void onWxShare(String path) {
        if (api == null) {
            return;
        }
        if (api.getWXAppSupportAPI() > 0) {
            WXFileObject fileObj = new WXFileObject();
            // fileObj.fileData = bytes;
            fileObj.filePath = path;
            //使用媒体消息分享
            WXMediaMessage msg = new WXMediaMessage(fileObj);
            msg.title = "蜜月保";
            //发送请求
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            //创建唯一标识
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
        } else {
            Toast.makeText(this, R.string.unfind_weixin___share, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void onEmailShare(String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> fileUri = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri.add(FileProvider.getUriForFile(this, getPackageName(), file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri.add(Uri.fromFile(file));
        }
        intent.setType("application/octet-stream");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUri);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.unfind_mail___share), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSubscriber, downloadSubscriber);
    }
}
