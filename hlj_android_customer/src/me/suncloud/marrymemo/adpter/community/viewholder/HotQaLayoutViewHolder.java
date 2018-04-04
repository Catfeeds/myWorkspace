package me.suncloud.marrymemo.adpter.community.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljquestionanswer.activities.CreateQuestionTitleActivity;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.Indicators.CirclePageExIndicator;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.HotQaFlowAdapter;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by mo_yu on 2018/3/13.有问必答
 */

public class HotQaLayoutViewHolder extends BaseViewHolder<List<Question>> {

    @BindView(R.id.card_view)
    RelativeLayout cardView;
    @BindView(R.id.posters_view)
    SliderLayout postersView;
    @BindView(R.id.flow_indicator)
    CirclePageExIndicator flowIndicator;
    @BindView(R.id.tv_create_question)
    TextView tvCreateQuestion;
    @BindView(R.id.img_card_view_bg)
    RoundedImageView imgCardViewBg;
    List<Question> questions;
    HotQaFlowAdapter flowAdapter;
    private int itemWidth;
    private int itemHeight;
    private int imageWidth;

    public HotQaLayoutViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_hot_qa_list_layout, parent, false));
    }

    public HotQaLayoutViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(itemView
                        .getContext(),
                18);
        itemHeight = ((itemWidth - CommonUtil.dp2px(itemView.getContext(),
                14))) * 272 / 686 + CommonUtil.dp2px(itemView.getContext(), 14);
        imageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px
                (itemView.getContext(),
                32);
        cardView.getLayoutParams().height = itemHeight;
        imgCardViewBg.getLayoutParams().width = imageWidth;
        imgCardViewBg.getLayoutParams().height = imageWidth * 272 / 686;
        questions = new ArrayList<>();
        flowAdapter = new HotQaFlowAdapter(itemView.getContext(), questions);
        postersView.setPagerAdapter(flowAdapter);
        postersView.setCustomIndicator(flowIndicator);
        tvCreateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                if (Util.loginBindChecked(activity)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, CreateQuestionTitleActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_bottom,
                            R.anim.activity_anim_default);
                }
            }
        });
        postersView.startAutoCycle();
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.tagViewParentName(postersView,
                HljTaggerName.CommunityHomeFragment.COMMUNITY_HOT_QUESTION_LIST);
    }

    @Override
    protected void setViewData(
            Context mContext, List<Question> list, int position, int viewType) {
        flowAdapter.setDate(list);
    }
}
