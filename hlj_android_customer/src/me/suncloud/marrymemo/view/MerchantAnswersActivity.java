package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.MerchantAnswerListAdapter;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func2;

/**
 * Created by wangtao on 2017/1/6.
 */

public class MerchantAnswersActivity extends HljBaseActivity {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View endView;
    private View loadView;
    private View headerView;
    private MerchantAnswerListAdapter adapter;
    private long id;
    private Subscription infoSubscription;
    private Subscription pageSubscription;
    private Subscriber followSubscriber;
    private Merchant merchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getIntent().getLongExtra("id", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_answers);
        ButterKnife.bind(this);
        initView();
        initLoad();
    }

    private void initView() {
        headerView = View.inflate(this, R.layout.merchant_answers_header, null);
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(com.hunliji.hljquestionanswer.R.id.no_more_hint);
        loadView = footerView.findViewById(com.hunliji.hljquestionanswer.R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MerchantAnswerListAdapter(this);
        adapter.setHeaderView(headerView);
        adapter.setFooterView(footerView);
        recyclerView.setAdapter(adapter);

    }

    private void initLoad() {
        infoSubscription = Observable.zip(MerchantApi.getQaMerchantInfoObb(id),
                QuestionAnswerApi.getMerchantAnswersObb(id, 1),
                new Func2<Merchant, HljHttpData<List<Answer>>, MerchantAnswersZip>() {
                    @Override
                    public MerchantAnswersZip call(
                            Merchant merchant, HljHttpData<List<Answer>> listHljHttpData) {
                        return new MerchantAnswersZip(merchant, listHljHttpData);
                    }
                })
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setProgressBar(progressBar)
                        .setContentView(recyclerView)
                        .setEmptyView(emptyView)
                        .setOnNextListener(new SubscriberOnNextListener<MerchantAnswersZip>() {
                            @Override
                            public void onNext(MerchantAnswersZip zip) {
                                if (zip.answersData != null) {
                                    adapter.setAnswers(zip.answersData.getData());
                                    initPagination(zip.answersData.getPageCount());
                                }
                                initMerchantInfo(zip);
                            }
                        })
                        .build());
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscription);
        pageSubscription = PaginationTool.buildPagingObservable(recyclerView,
                pageCount,
                new PagingListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public Observable<HljHttpData<List<Answer>>> onNextPage(int page) {
                        return QuestionAnswerApi.getMerchantAnswersObb(id, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .subscribe(HljHttpSubscriber.buildSubscriber(this)
                        .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Answer>>>() {
                            @Override
                            public void onNext(HljHttpData<List<Answer>> listHljHttpData) {
                                adapter.addAnswers(listHljHttpData.getData());
                            }
                        })
                        .build());
    }

    private void initMerchantInfo(MerchantAnswersZip zip) {
        MerchantAnswersActivity.this.merchant = zip.merchant;
        int answerCount = 0;
        if (zip.answersData != null) {
            answerCount = zip.answersData.getTotalCount();
        }
        if (headerView == null) {
            return;
        }
        HeaderViewHolder holder = (HeaderViewHolder) headerView.getTag();
        if (holder == null) {
            holder = new HeaderViewHolder(headerView);
            headerView.setTag(holder);
        }
        holder.tvName.setText(merchant.getName());
        holder.tvAnswerCount.setText(String.valueOf(answerCount));
        holder.tvPrisedCount.setText(String.valueOf(merchant.getPraisedCount()));
        holder.tvFansCount.setText(String.valueOf(merchant.getFansCount()));

        holder.btnFollow.setText(merchant.isCollected() ? R.string.label_followed : R.string
                .label_follow3);
        holder.btnFollow.setTextColor(ActivityCompat.getColor(MerchantAnswersActivity.this,
                merchant.isCollected() ? R.color.colorPrimary : R.color.colorBlack3));

        Glide.with(this)
                .asBitmap()
                .load(ImageUtil.getAvatar(merchant.getLogoPath()))
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                .into(holder.ivAvatar);

        final HeaderViewHolder finalHolder = holder;
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (merchant == null) {
                    return;
                }
                if (!AuthUtil.loginBindCheck(MerchantAnswersActivity.this)) {
                    return;
                }
                if (followSubscriber != null && !followSubscriber.isUnsubscribed()) {
                    return;
                }
                followSubscriber = HljHttpSubscriber.buildSubscriber(MerchantAnswersActivity
                        .this)
                        .setOnNextListener(new SubscriberOnNextListener() {
                            @Override
                            public void onNext(Object o) {
                                ToastUtil.showCustomToast(MerchantAnswersActivity.this,
                                        merchant.isCollected() ? R.string.hint_collect_complete2
                                                : R.string.hint_discollect_complete2);
                            }
                        })
                        .build();
                if (merchant.isCollected()) {
                    finalHolder.btnFollow.setText(R.string.label_follow3);
                    finalHolder.btnFollow.setTextColor(ActivityCompat.getColor(
                            MerchantAnswersActivity.this,
                            R.color.colorBlack3));
                    merchant.setCollected(false);
                    CommonApi.deleteMerchantFollowObb(merchant.getId())
                            .subscribe(followSubscriber);
                } else {
                    finalHolder.btnFollow.setText(R.string.label_followed);
                    finalHolder.btnFollow.setTextColor(ActivityCompat.getColor(
                            MerchantAnswersActivity.this,
                            R.color.colorPrimary));
                    merchant.setCollected(true);
                    CommonApi.postMerchantFollowObb(merchant.getId())
                            .subscribe(followSubscriber);
                }
            }
        });

        holder.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (merchant == null) {
                    return;
                }
                Intent intent = new Intent(MerchantAnswersActivity.this, MerchantDetailActivity.class);
                intent.putExtra("id", merchant.getId());
                startActivity(intent);
            }
        });


    }

    static class HeaderViewHolder {
        @BindView(R.id.iv_avatar)
        RoundedImageView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_answer_count)
        TextView tvAnswerCount;
        @BindView(R.id.tv_prised_count)
        TextView tvPrisedCount;
        @BindView(R.id.tv_fans_count)
        TextView tvFansCount;
        @BindView(R.id.btn_follow)
        Button btnFollow;
        @BindView(R.id.btn_home)
        Button btnHome;

        HeaderViewHolder(View view) {ButterKnife.bind(this, view);}
    }


    private class MerchantAnswersZip {
        Merchant merchant;
        HljHttpData<List<Answer>> answersData;

        MerchantAnswersZip(
                Merchant merchant, HljHttpData<List<Answer>> answersData) {
            this.merchant = merchant;
            this.answersData = answersData;
        }
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(infoSubscription, pageSubscription, followSubscriber);
        super.onFinish();
    }
}
