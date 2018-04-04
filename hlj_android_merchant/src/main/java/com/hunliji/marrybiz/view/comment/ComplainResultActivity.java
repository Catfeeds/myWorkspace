package com.hunliji.marrybiz.view.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljcommonviewlibrary.utils.FixedColumnGridInterface;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;
import com.hunliji.hljquestionanswer.adapters.viewholder.QADetailHeaderViewHolder;
import com.hunliji.hljquestionanswer.adapters.viewholder.QADetailViewHolder;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.viewholder.CommentDetailHeaderViewHolder;
import com.hunliji.marrybiz.adapter.comment.viewholder.CommentDetailViewHolder;
import com.hunliji.marrybiz.api.comment.CommentApi;
import com.hunliji.marrybiz.model.comment.ComplainDetail;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by hua_rong on 2017/6/13.
 * 申诉结果
 */

public class ComplainResultActivity extends HljBaseActivity {


    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.fl_content)
    FrameLayout flContent;
    @BindView(R.id.tv_reason)
    TextView tvReason;
    @BindView(R.id.hlj_grid_view)
    HljGridView hljGridView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    private long id;
    private HljHttpSubscriber subscriber;
    private View itemView;
    private View headerView;
    private CommentDetailViewHolder commentDetailViewHolder;
    private CommentDetailHeaderViewHolder commentDetailHeaderViewHolder;
    private int complainType;//区分 评论和问答
    private QADetailHeaderViewHolder qaDetailHeaderViewHolder;
    private QADetailViewHolder qaDetailViewHolder;

    private View qaHeaderView;
    private View qaItemView;
    private static final String reasons[] = {"恶意诋毁", "广告", "色情", "脏话", "其他"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_result);
        ButterKnife.bind(this);
        id = getIntent().getLongExtra("id", 0);
        complainType = getIntent().getIntExtra(ComplainDetail.TYPE, 0);
        initView();
        onError();
        onLoad();
    }

    private void initView() {
        if (complainType == ComplainDetail.TYPE_COMMENT) {
            itemView = View.inflate(this, R.layout.item_comment_detail, null);
            headerView = View.inflate(this, R.layout.header_comment_detail, null);
            commentDetailViewHolder = new CommentDetailViewHolder(itemView);
            commentDetailViewHolder.hideLine();
            commentDetailHeaderViewHolder = new CommentDetailHeaderViewHolder(headerView);
        }
        if (complainType == ComplainDetail.TYPE_QUESTION) {
            qaHeaderView = View.inflate(this, R.layout.qa_detail_header___qa, null);
            qaDetailHeaderViewHolder = new QADetailHeaderViewHolder(qaHeaderView);
            qaItemView = View.inflate(this, R.layout.qa_detail_item___qa, null);
            qaDetailViewHolder = new QADetailViewHolder(qaItemView);
        }
    }


    private void onError() {
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }


    private void onLoad() {
        CommonUtil.unSubscribeSubs(subscriber);
        Observable<ComplainDetail> observable = CommentApi.getComplainDetail(id, complainType);
        subscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<ComplainDetail>() {
                    @Override
                    public void onNext(ComplainDetail complainDetail) {
                        showView(complainDetail);
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(scrollView)
                .setProgressBar(progressBar)
                .build();
        observable.subscribe(subscriber);
    }

    private void showView(ComplainDetail complainDetail) {
        int status = complainDetail.getAppealStatus();
        if (status == 1) {
            setTitle(getString(R.string.title_complain_success));
            tvResult.setText(R.string.label_complain_result_success);
        } else {
            setTitle(getString(R.string.title_complain_fail));
            tvResult.setText(R.string.label_complain_result_fail);
        }
        DateTime dateTime = complainDetail.getCreatedAt();
        if (dateTime != null) {
            tvTime.setText(getString(R.string.label_complain_time,
                    dateTime.toString(Constants.DATE_FORMAT_LONG)));
        }
        String reason = complainDetail.getReason();
        String content = complainDetail.getContent();
        if (complainType == ComplainDetail.TYPE_QUESTION) {
            //如果是问答 reason返回的是 1 - 5
            if (Integer.valueOf(reason) > 0 && Integer.valueOf(reason) - 1 < reasons.length) {
                reason = reasons[Integer.valueOf(reason) - 1];
            }
        }
        if (TextUtils.isEmpty(content)) {
            tvReason.setText(reason);
            tvReason.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        } else {
            content = reason + "：" + complainDetail.getContent();
            ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(this,
                    R.color.colorAccent));
            SpannableStringBuilder builder = new SpannableStringBuilder(content);
            builder.setSpan(span, 0, reason.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvReason.setText(builder);
        }
        FixedColumnGridInterface gridInterface = new FixedColumnGridInterface(CommonUtil.dp2px(this,
                10));
        gridInterface.setColumnCount(4);
        hljGridView.setGridInterface(gridInterface);
        final ArrayList<Photo> photoList = complainDetail.getPhotos();
        if (photoList != null && !photoList.isEmpty()) {
            hljGridView.setVisibility(View.VISIBLE);
            hljGridView.setDate(photoList);
            hljGridView.setItemClickListener(new HljGridView.GridItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    Intent intent = new Intent(ComplainResultActivity.this,
                            PicsPageViewActivity.class);
                    intent.putExtra("position", position);
                    intent.putParcelableArrayListExtra("photos", photoList);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
        } else {
            hljGridView.setVisibility(View.GONE);
        }
        if (complainType == ComplainDetail.TYPE_COMMENT) {
            showContent(complainDetail);
        } else {
            int type = complainDetail.getType();//申诉类型 1问题申诉 2回答申诉
            if (type == 1) {
                Question question = complainDetail.getQaQuestion();
                if (question != null && question.getId() > 0) {
                    flContent.addView(qaHeaderView);
                    qaDetailHeaderViewHolder.setComplainHidden(true);
                    qaDetailHeaderViewHolder.setView(this, question, -1, 0);
                }
            } else {
                Answer answer = complainDetail.getQaAnswer();
                if (answer != null && answer.getId() > 0) {
                    flContent.addView(qaItemView);
                    qaDetailViewHolder.setView(this, answer, -1, 0);
                    qaDetailViewHolder.setComplainView();
                }
            }
        }
    }

    private void showContent(ComplainDetail complainDetail) {
        RepliedComment repliedComment = complainDetail.getRepliedComment();
        ServiceComment orderComment = complainDetail.getOrderComment();
        if (orderComment != null && orderComment.getId() > 0) {
            flContent.addView(headerView);
            orderComment.setWork(null);
            commentDetailHeaderViewHolder.setHeaderView(orderComment);
        } else if (repliedComment != null && repliedComment.getId() > 0) {
            flContent.addView(itemView);
            commentDetailViewHolder.hideComplainIcon();
            commentDetailViewHolder.setView(this, repliedComment, -1, 0);
        }
    }


    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(subscriber);
    }
}
