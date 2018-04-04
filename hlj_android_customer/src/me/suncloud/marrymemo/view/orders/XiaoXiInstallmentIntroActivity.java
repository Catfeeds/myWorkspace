package me.suncloud.marrymemo.view.orders;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Installment2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentDetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.widget.FlowLayout;

public class XiaoXiInstallmentIntroActivity extends HljBaseActivity {


    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.img_installment_intro)
    ImageView imgInstallmentIntro;

    public static final String ARG_PRICE = "price";
    public static final String ARG_WORK = "work";
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R.id.tv_installment_hint)
    TextView tvInstallmentHint;
    @BindView(R.id.xiaoxi_installment_layout)
    LinearLayout xiaoxiInstallmentLayout;
    @BindView(R.id.tv_option_hint)
    TextView tvOptionHint;
    private double price;
    private HljHttpSubscriber initSub;
    private Installment2 installment;
    private Work work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaoxi_installment_intro);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        price = getIntent().getDoubleExtra(ARG_PRICE, 0);
        work = (Work) getIntent().getSerializableExtra(ARG_WORK);
    }

    private void initViews() {
        tvOptionHint.setText("选择分期期数");
        tvInstallmentHint.setText(R.string.label_xiaoxi_installment_hint3);
    }

    private void initLoad() {
        initSub = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(contentLayout)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<InstallmentData>() {
                    @Override
                    public void onNext(InstallmentData installmentData) {
                        installment = installmentData.getInstallment();
                        setViews();
                    }
                })
                .build();
        XiaoxiInstallmentApi.getInstallmentInfo(price)
                .subscribe(initSub);
    }

    private void setViews() {
        Glide.with(this)
                .load(installment.getImage()
                        .getImagePath())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(
                            @Nullable GlideException e,
                            Object model,
                            Target<Drawable> target,
                            boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(
                            Drawable resource,
                            Object model,
                            Target<Drawable> target,
                            DataSource dataSource,
                            boolean isFirstResource) {
                        int height = resource.getIntrinsicHeight();
                        int width = resource.getIntrinsicWidth();
                        imgInstallmentIntro.getLayoutParams().height = CommonUtil.getDeviceSize(
                                XiaoXiInstallmentIntroActivity.this).x * height / width;
                        return false;
                    }
                })
                .into(imgInstallmentIntro);
        int installmentWidth = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this,
                16 * 3)) / 2;
        ArrayList<InstallmentDetail> details = work.getInstallment()
                .getDetails();
        flowLayout.removeAllViews();
        for (InstallmentDetail detail : details) {
            final CheckBox cbDetail = (CheckBox) LayoutInflater.from(this)
                    .inflate(R.layout.installment_check_box, null, false);
            cbDetail.setText(detail.getShowText());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(installmentWidth,
                    CommonUtil.dp2px(this, 44));
            if (details.indexOf(detail) == 0) {
                cbDetail.setChecked(true);
            }
            flowLayout.addView3(cbDetail, params);
        }
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        int stageNum = -1;
        if (flowLayout.getCheckedIndex() >= 0) {
            stageNum = installment.getDetails()
                    .get(flowLayout.getCheckedIndex())
                    .getStageNum();
        }
        Intent intent = new Intent(this, ServiceOrderConfirmActivity.class);
        intent.putExtra(ServiceOrderConfirmActivity.ARG_WORK, work);
        intent.putExtra(ServiceOrderConfirmActivity.ARG_INSTALLMENT_STAGE_NUM, stageNum);
        startActivity(intent);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub);
    }

}
