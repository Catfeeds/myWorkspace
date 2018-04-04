package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

public class HotQaFlowAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private List<Question> mDate;
    private Context mContext;
    private int width;
    private SliderLayout sliderLayout;

    public HotQaFlowAdapter(
            Context context, List<Question> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mDate = new ArrayList<>(list);
        width = CommonUtil.dp2px(context, 20);
    }

    public void setDate(List<Question> questions) {
        if (questions != null) {
            mDate.clear();
            mDate.addAll(questions);
            notifyDataSetChanged();
        }
    }

    public void setSliderLayout(SliderLayout sliderLayout) {
        this.sliderLayout = sliderLayout;
    }

    @Override
    public int getCount() {
        return mDate == null ? 0 : mDate.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView;
        convertView = mInflater.inflate(R.layout.community_hot_qa_flow_item, container, false);
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        final Question question = mDate.get(position);
        if (question != null) {
            HljVTTagger.buildTagger(convertView)
                    .tagName(HljTaggerName.QUESTION)
                    .atPosition(position)
                    .dataId(question.getId())
                    .dataType(VTMetaData.DATA_TYPE.DATA_TYPE_QUESTION)
                    .tag();
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), QuestionDetailActivity.class);
                    intent.putExtra(QuestionDetailActivity.ARG_QUESTION_ID, question.getId());
                    v.getContext()
                            .startActivity(intent);
                }
            });
            viewHolder.tvTotalUpCount.setText(String.valueOf(question.getTotalUpCount()));
            viewHolder.tvAnswerCount.setText(String.valueOf(question.getAnswerCount()));
            QaAuthor questionAuthor = question.getUser();
            Answer answer = question.getAnswer();
            String authPath = null;
            if (answer != null && answer.getUser() != null) {
                QaAuthor answerAuthor = answer.getUser();
                authPath = ImagePath.buildPath(answerAuthor.getAvatar())
                        .width(width)
                        .height(width)
                        .cropPath();
                viewHolder.tvAuthName.setText(answerAuthor.getName());
            }
            String questionAuthName = null;
            if (questionAuthor != null) {
                questionAuthName = questionAuthor.getName();
            }
            viewHolder.tvAuthTip.setText(mContext.getString(R.string.format_answer_tip,
                    questionAuthName));
            viewHolder.tvQuestionTitle.setText("#" + question.getTitle());
            Glide.with(mContext)
                    .load(authPath)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary))
                    .into(viewHolder.rivAuthAvatar);
        }
        container.addView(convertView);
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.riv_auth_avatar)
        RoundedImageView rivAuthAvatar;
        @BindView(R.id.tv_auth_name)
        TextView tvAuthName;
        @BindView(R.id.auth_view)
        LinearLayout authView;
        @BindView(R.id.tv_auth_tip)
        TextView tvAuthTip;
        @BindView(R.id.tv_total_up_count)
        TextView tvTotalUpCount;
        @BindView(R.id.tv_answer_count)
        TextView tvAnswerCount;
        @BindView(R.id.tv_question_title)
        TextView tvQuestionTitle;
        @BindView(R.id.img_card_view_bg)
        RoundedImageView imgCardViewBg;
        @BindView(R.id.card_view)
        RelativeLayout cardView;
        private int itemWidth;
        private int itemHeight;
        private int imageWidth;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            itemWidth = CommonUtil.getDeviceSize(view.getContext()).x - CommonUtil.dp2px(view
                            .getContext(),
                    18);
            itemHeight = ((itemWidth - CommonUtil.dp2px(view.getContext(),
                    14))) * 272 / 686 + CommonUtil.dp2px(view.getContext(), 14);
            imageWidth = CommonUtil.getDeviceSize(view.getContext()).x - CommonUtil.dp2px
                    (view.getContext(),
                            32);
            cardView.getLayoutParams().width = itemWidth;
            cardView.getLayoutParams().height = itemHeight;
            imgCardViewBg.getLayoutParams().width = imageWidth;
            imgCardViewBg.getLayoutParams().height = imageWidth * 272 / 686;
        }
    }
}