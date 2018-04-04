package com.hunliji.hljinsurancelibrary.views.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.SpanUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.InsuranceProduct;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;
import com.hunliji.hljinsurancelibrary.models.Quarantees;
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
import butterknife.Unbinder;
import rx.Observable;

/**
 * Created by hua_rong on 2017/5/25.
 * 保单详情 成功页面
 */

public class PolicySuccessFragment extends RefreshFragment {

    Unbinder unbinder;
    @BindView(R2.id.tv_project_name)
    TextView tvProjectName;
    @BindView(R2.id.tv_project_NO)
    TextView tvProjectNO;
    @BindView(R2.id.tv_company_name)
    TextView tvCompanyName;
    @BindView(R2.id.tv_project_state)
    TextView tvProjectState;
    @BindView(R2.id.tv_term)
    TextView tvTerm;
    @BindView(R2.id.tv_insurance_num)
    TextView tvInsuranceNum;
    @BindView(R2.id.tv_pay_premium)
    TextView tvPayPremium;
    @BindView(R2.id.tv_insured)
    TextView tvInsured;
    @BindView(R2.id.tv_certi_no)
    TextView tvCertiNo;
    @BindView(R2.id.tv_phone)
    TextView tvPhone;
    @BindView(R2.id.tv_spouse_name)
    TextView tvSpouseName;
    @BindView(R2.id.tv_wedding_address)
    TextView tvWeddingAddress;
    @BindView(R2.id.ll_project_protect)
    LinearLayout llProjectProtect;
    @BindView(R2.id.tv_look_insurance)
    TextView tvLookInsurance;
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;
    @BindView(R2.id.rl_company_bg)
    RelativeLayout rlCompanyBg;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_wedding_hotel)
    TextView tvWeddingHotel;
    private PolicyDetail policyDetail;
    private String[] statusArray;
    private int[] statusBgArray = {R.drawable.image_bg_to_be_effective, R.drawable
            .image_bg_in_security, R.drawable.image_bg_terminated};
    private HljHttpSubscriber downloadSubscriber;
    private Dialog dialog;
    private IWXAPI api;
    private static final int WEIXIN = 0;
    private static final int EMAIL = 1;

    private static final String ARG_POLICY_DETAIL = "policy_detail";

    public static PolicySuccessFragment newInstance(PolicyDetail policyDetail) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_POLICY_DETAIL, policyDetail);
        PolicySuccessFragment fragment = new PolicySuccessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(getActivity(), HljShare.WEIXINKEY, true);
        api.registerApp(HljShare.WEIXINKEY);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_policy_success, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Resources resources = getContext().getResources();
        if (resources != null) {
            statusArray = resources.getStringArray(R.array.status);
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            policyDetail = bundle.getParcelable(ARG_POLICY_DETAIL);
        }
        if (policyDetail != null) {
            scrollView.setVisibility(View.VISIBLE);
            initView();
            companyInfo();
            productInfo();
            insuredInfo();
            insureProject();
        }
    }

    /**
     * 保险公司相关信息
     */
    private void companyInfo() {
        //1 未提交保单 2待生效 3保障中 4已终止
        InsuranceProduct product = policyDetail.getProduct();
        int status = policyDetail.getStatus();
        if (product != null) {
            tvProjectName.setText(product.getTitle());
            tvProjectNO.setText(String.format("NO.%S", policyDetail.getPolicyNo()));
            tvCompanyName.setText(String.format("由%s承保", product.getCompany()));
            if (statusArray != null && status > 1 && status < 5) {
                tvProjectState.setText(statusArray[status - 2]);
                rlCompanyBg.setBackgroundResource(statusBgArray[status - 2]);
            }
        }
    }

    @OnClick(R2.id.rl_download)
    public void onDownload() {
        if (dialog == null) {
            dialog = new Dialog(getContext(), R.style.BubbleDialogTheme);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setContentView(R.layout.dialog_epolicy_share);
                View shareWx = window.findViewById(R.id.share_weixing);
                View shareEmail = window.findViewById(R.id.share_email);
                View cancel = window.findViewById(R.id.btn_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
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
                Point point = CommonUtil.getDeviceSize(getContext());
                params.width = point.x;
                window.setAttributes(params);
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        dialog.show();
    }


    private void onShare(final int type) {
        if (!CommonUtil.isNetworkConnected(getContext())) {
            Toast.makeText(getContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String epolicyAddress = policyDetail.getEpolicyAddress();
        if (TextUtils.isEmpty(epolicyAddress)) {
            return;
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        String filename = policyDetail.getPolicyNo() + ".pdf";
        File fileDir = FileUtil.getPolicyDir(getContext());
        String filePath = fileDir.getPath() + File.separator + filename;
        if (isHadFile(fileDir, filename)) {
            onNextView(type, filePath);
        } else {
            if (downloadSubscriber == null || downloadSubscriber.isUnsubscribed()) {
                Observable<String> observable = InsuranceApi.download(epolicyAddress, filePath);
                downloadSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                        .setOnNextListener(new SubscriberOnNextListener<String>() {
                            @Override
                            public void onNext(final String path) {
                                if (!TextUtils.isEmpty(path)) {
                                    ToastUtil.showCustomToast(getContext(),
                                            R.string.label_down_success);
                                    onNextView(type, path);
                                } else {
                                    Toast.makeText(getContext(),
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

    private void onEmailShare(String path) {
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> fileUri = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri.add(FileProvider.getUriForFile(getContext(),
                    getContext().getPackageName(),
                    file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri.add(Uri.fromFile(file));
        }
        // intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.label_electronic_policy));
        // intent.putExtra(Intent.EXTRA_TEXT, "下载的电子保单");
        intent.setType("application/octet-stream");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUri);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),
                    getString(R.string.unfind_mail___share),
                    Toast.LENGTH_SHORT)
                    .show();
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
            msg.title = "婚礼保";
            //发送请求
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            //创建唯一标识
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
        } else {
            Toast.makeText(getContext(), R.string.unfind_weixin___share, Toast.LENGTH_LONG)
                    .show();
        }
    }


    @OnClick(R2.id.rl_look_detail)
    public void onLookDetail() {
        HljWeb.startWebView(getActivity(),
                policyDetail.getProduct()
                        .getDetailUrl());
    }

    @OnClick(R2.id.rl_service_progress)
    public void onServiceProgress() {
        HljWeb.startWebView(getActivity(),
                policyDetail.getProduct()
                        .getClaimNoticeUrl());
    }

    /**
     * 基本信息
     */
    private void productInfo() {
        DateTime beginDate = policyDetail.getTransBeginDate();
        if (beginDate != null) {
            tvTerm.setText(beginDate.toString(HljTimeUtils.DATE_FORMAT_SHORT));
        }
        tvInsuranceNum.setText(String.format("%s份", policyDetail.getNum()));
        tvPayPremium.setText(String.format(Locale.getDefault(), "￥%.2f", policyDetail.getPrice()));
    }

    /**
     * 被保险人信息
     */
    private void insuredInfo() {
        tvInsured.setText(policyDetail.getFullName());
        tvCertiNo.setText(policyDetail.getCertiNo());
        tvPhone.setText(policyDetail.getPhone());
        tvSpouseName.setText(policyDetail.getSpouseName());
        tvWeddingAddress.setText(policyDetail.getWeddingAddress());
        tvWeddingHotel.setText(policyDetail.getWeddingHotel());
    }


    private void insureProject() {
        ArrayList<Quarantees> quaranteesList = policyDetail.getGuarantees();
        if (quaranteesList == null || quaranteesList.isEmpty()) {
            llProjectProtect.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < quaranteesList.size(); i++) {
                View.inflate(getContext(), R.layout.layout_quarantee_item, llProjectProtect);
                View view = llProjectProtect.getChildAt(llProjectProtect.getChildCount() - 1);
                Quarantees quarantees = quaranteesList.get(i);
                if (quarantees != null) {
                    TextView tvDesc = (TextView) view.findViewById(R.id.tv_desc);
                    TextView tvPayMoney = (TextView) view.findViewById(R.id.tv_pay_money);
                    View lineLayout = view.findViewById(R.id.line_layout);
                    tvDesc.setText(String.format("%s:", quarantees.getDesc()));
                    tvPayMoney.setText(quarantees.getMoney());
                    if (i == quaranteesList.size() - 1) {
                        lineLayout.setVisibility(View.GONE);
                    }
                }
            }
        }
    }


    private void initView() {
        NoUnderlineSpan clickableSpan = new NoUnderlineSpan() {
            @Override
            public void onClick(View widget) {
                HljWeb.startWebView(getActivity(),
                        policyDetail.getProduct()
                                .getClauseUrl());
            }
        };
        String insuranceTips = getString(R.string.label_look_insurance_tip);
        int start = insuranceTips.indexOf("《保险条款》");
        SpanUtil.setClickableSpan(tvLookInsurance,
                clickableSpan,
                insuranceTips,
                start,
                insuranceTips.length(),
                ContextCompat.getColor(getContext(), R.color.colorLink));
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(downloadSubscriber);
    }

}
