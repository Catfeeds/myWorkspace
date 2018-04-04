package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearButton;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.RecommendThreadViewHolder;
import com.hunliji.hljcommonviewlibrary.models.CommunityFeed;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerRecommendThreadViewHolder;
import me.suncloud.marrymemo.model.CommunityThreadMeasures;
import me.suncloud.marrymemo.util.CommunityTogglesUtil;

/**
 * Created by mo_yu on 2016/8/29.社区关注
 */
public class SocialHotRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<CommunityFeed> recommendThreads;
    private List<Long> feedIds;
    //保存在内存中，页面未销毁时使用，减少数据库读取操作
    public static final String COMMUNITY_THREAD = "CommunityThread";
    public static final String QA_QUESTION = "QaQuestion";
    public static final String QA_ANSWER = "QaAnswer";
    public static final String STORY = "Story";

    private static final int DEFAULT_TYPE = 0;
    private static final int HEADER_TYPE = 1;
    public static final int THREAD_TYPE = 3;//话题
    public static final int QUESTION_TYPE = 4;//问题
    private static final int FOOTER_TYPE = 5;
    private static final int STORY_TYPE = 6;//故事视图
    private static final int CHANNEL_LIST_TYPE = 7;//频道推荐视图
    private static final int RICH_THREAD_LIST_TYPE = 8;//精编话题推荐视图
    private static final int REFRESH_TYPE = 9;//刷新视图
    private static final int HEADER_TIP_TYPE = 10;//列表顶部视图

    private View headView;
    private View headTipView;
    private View footerView;
    private View channelListView;//推荐频道
    private View richThreadListView;//精编话题推荐
    private View refreshTipView;//刷新提示视图

    private int newCount;
    private final static int channelPosition = 2;
    public final static int richThreadPosition = 5;

    private CommunityThreadMeasures measures;

    private OnFeedItemClickListener onFeedItemClickListener;
    private OnQaItemClickListener onQaItemClickListener;
    private OnReplyItemClickListener onReplyItemClickListener;

    public SocialHotRecyclerAdapter(
            Context context) {
        this.context = context;
        this.recommendThreads = new ArrayList<>();
        measures = new CommunityThreadMeasures(context.getResources()
                .getDisplayMetrics(), CommonUtil.getDeviceSize(context));
    }

    public void setRecommendThreads(List<CommunityFeed> dataList) {
        if (dataList != null) {
            recommendThreads.clear();
            recommendThreads.addAll(dataList);
        }
    }

    public void setHeadView(View headView) {
        this.headView = headView;
    }

    public void setHeadTipView(View headTipView) {
        this.headTipView = headTipView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setChannelListView(View channelListView) {
        this.channelListView = channelListView;
    }

    public void setRichThreadListView(View richThreadListView) {
        this.richThreadListView = richThreadListView;
    }

    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }

    public void setRefreshTipView(View refreshTipView) {
        this.refreshTipView = refreshTipView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headView);
            case HEADER_TIP_TYPE:
                return new ExtraBaseViewHolder(headTipView);
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case CHANNEL_LIST_TYPE:
                return new ExtraBaseViewHolder(channelListView);
            case RICH_THREAD_LIST_TYPE:
                return new ExtraBaseViewHolder(richThreadListView);
            case REFRESH_TYPE:
                return new ExtraBaseViewHolder(refreshTipView);
            case QUESTION_TYPE:
                return new QuestionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.answer_hot_list_item, parent, false));
            case THREAD_TYPE:
                RecommendThreadViewHolder holder = new RecommendThreadViewHolder(LayoutInflater
                        .from(
                        context)
                        .inflate(R.layout.common_recommend_thread_list_item___cv, parent, false));
                holder.setOnPraiseClickListener(new RecommendThreadViewHolder
                        .OnPraiseClickListener() {
                    @Override
                    public void onPraiseClick(
                            CommunityThread thread,
                            CheckableLinearButton checkPraised,
                            ImageView imgThumbUp,
                            TextView tvPraiseCount,
                            TextView tvAdd) {
                        CommunityTogglesUtil.onNewCommunityThreadListPraise((Activity) context,
                                checkPraised,
                                imgThumbUp,
                                tvPraiseCount,
                                tvAdd,
                                thread);
                    }
                });
                holder.setOnReplyClickListener(new RecommendThreadViewHolder.OnReplyClickListener
                        () {
                    @Override
                    public void onReply(CommunityThread item, int position) {
                        if (onReplyItemClickListener != null) {
                            onReplyItemClickListener.onReply(item, position);
                        }
                    }
                });
                return holder;
            case STORY_TYPE:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
            default:
                return new ExtraBaseViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_place_holder, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (!(holder instanceof ExtraBaseViewHolder) && recommendThreads != null) {
            int currentPosition;
            if (headView != null) {
                //有顶部视图时，重新计算currentPosition值
                currentPosition = position - 1;
            } else {
                currentPosition = position;
            }
            if (headTipView != null) {
                //有列表顶部视图时，重新计算currentPosition值
                currentPosition = currentPosition - 1;
            }
            //频道固定坑位为列表第3位
            if (channelListView != null) {
                //有频道时，重新计算currentPosition值
                if (currentPosition > channelPosition) {
                    currentPosition = currentPosition - 1;
                }
            }
            //精编话题固定坑位为列表第6位
            if (richThreadListView != null) {
                //有精编话题时，重新计算currentPosition值
                if (currentPosition > richThreadPosition) {
                    currentPosition = currentPosition - 1;
                }
            }
            //刷新视图
            if (refreshTipView != null && newCount > 0) {
                //有频道时，重新计算currentPosition值
                if (currentPosition > newCount) {
                    currentPosition = currentPosition - 1;
                }
            }
            if (holder instanceof RecommendThreadViewHolder) {
                holder.setView(context,
                        recommendThreads.get(currentPosition),
                        currentPosition,
                        getItemViewType(position));
                ((RecommendThreadViewHolder) holder).setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object object) {
                        CommunityFeed item = (CommunityFeed) object;
                        CommunityThread thread = (CommunityThread) item.getEntity();
                        if (onFeedItemClickListener != null) {
                            onFeedItemClickListener.onFeedItemClickListener(position,
                                    item.getEntityId(),
                                    thread,
                                    THREAD_TYPE);
                        }
                    }
                });
            } else {
                holder.setView(context,
                        recommendThreads.get(currentPosition),
                        currentPosition,
                        getItemViewType(position));
            }
        }
    }

    /**
     * CommunityThread社区类型；QaQuestion问题类型；QaAnswer回答类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (headView != null && position == 0) {
            return HEADER_TYPE;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return FOOTER_TYPE;
        } else if (recommendThreads != null && recommendThreads.size() > 0) {
            int currentPosition;
            if (headView != null) {
                currentPosition = position - 1;
            } else {
                currentPosition = position;
            }
            if (headTipView != null) {
                if (currentPosition == 0) {
                    return HEADER_TIP_TYPE;
                } else {
                    currentPosition = currentPosition - 1;
                }
            }

            //频道固定坑位为列表第3位
            if (channelListView != null) {
                if (recommendThreads.size() >= channelPosition) {
                    if (currentPosition == channelPosition) {
                        return CHANNEL_LIST_TYPE;
                    }
                } else {
                    if (richThreadListView != null) {
                        if (position == getItemCount() - 4) {
                            return CHANNEL_LIST_TYPE;
                        }
                    } else {
                        if (position == getItemCount() - 3) {
                            return CHANNEL_LIST_TYPE;
                        }
                    }
                }
                //有频道时，重新计算currentPosition值
                if (currentPosition > channelPosition) {
                    currentPosition = currentPosition - 1;
                }
            }

            //精编话题固定坑位为列表第6位
            if (richThreadListView != null) {
                if (recommendThreads.size() >= richThreadPosition) {
                    if (currentPosition == richThreadPosition) {
                        return RICH_THREAD_LIST_TYPE;
                    }
                } else {
                    if (position == getItemCount() - 3) {
                        return RICH_THREAD_LIST_TYPE;
                    }
                }
                //有精编话题时，重新计算currentPosition值
                if (currentPosition > richThreadPosition) {
                    currentPosition = currentPosition - 1;
                }
            }
            //刷新视图，固定为刷新数据的最后一位
            if (refreshTipView != null && newCount > 0) {
                if (recommendThreads.size() >= newCount) {
                    if (currentPosition == newCount) {
                        return REFRESH_TYPE;
                    }
                } else {
                    if (position == getItemCount() - 2) {
                        return REFRESH_TYPE;
                    }
                }
                //有刷新视图时，重新计算currentPosition值
                if (currentPosition > newCount) {
                    currentPosition = currentPosition - 1;
                }
            }
            if (currentPosition < recommendThreads.size()) {
                switch (recommendThreads.get(currentPosition)
                        .getEntityType()) {
                    case COMMUNITY_THREAD:
                        return THREAD_TYPE;
                    case QA_QUESTION:
                        return QUESTION_TYPE;
                    case STORY:
                        return STORY_TYPE;
                    default:
                        break;
                }
            }
        }
        return DEFAULT_TYPE;
    }

    @Override
    public int getItemCount() {
        return recommendThreads.size() + (channelListView == null ? 0 : 1) + (headView == null ?
                0 : 1) + (headTipView == null ? 0 : 1) + (footerView == null ? 0 : 1) +
                (richThreadListView == null ? 0 : 1) + ((refreshTipView == null || newCount == 0)
                ? 0 : 1);
    }

    class QuestionViewHolder extends TrackerRecommendThreadViewHolder {
        @BindView(R.id.iv_auth)
        RoundedImageView ivAuth;
        @BindView(R.id.tv_auth_name)
        TextView tvAuthName;
        @BindView(R.id.auth_view)
        LinearLayout authView;
        @BindView(R.id.tv_question_title)
        TextView tvQuestionTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_channel_name)
        TextView tvChannelName;
        @BindView(R.id.img_thumb_up)
        ImageView imgThumbUp;
        @BindView(R.id.tv_praise_count)
        TextView tvPraiseCount;
        @BindView(R.id.praise_view)
        LinearLayout praiseView;
        @BindView(R.id.bottom_view)
        RelativeLayout bottomView;
        @BindView(R.id.question_view)
        LinearLayout questionView;

        QuestionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public View trackerView() {
            return questionView;
        }

        @Override
        protected void setViewData(
                final Context mContext,
                final CommunityFeed item,
                final int position,
                int viewType) {
            if (item != null) {
                final Question question = (Question) item.getEntity();

                if (question != null) {
                    tvQuestionTitle.setVisibility(View.VISIBLE);
                    //问题标志位
                    SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(mContext,
                            "  " + question.getTitle(),
                            measures.faceSize);
                    if (builder != null) {
                        Drawable drawable = ContextCompat.getDrawable(mContext,
                                R.mipmap.icon_question_tag_32_32);
                        drawable.setBounds(0,
                                0,
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                        builder.setSpan(new HljImageSpan(drawable),
                                0,
                                1,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    tvQuestionTitle.setText(builder);
                } else {
                    tvQuestionTitle.setVisibility(View.GONE);
                }
                if (question.getAnswer() != null) {
                    if (!TextUtils.isEmpty(question.getAnswer()
                            .getSummary())) {
                        tvContent.setVisibility(View.VISIBLE);
                        tvPraiseCount.setVisibility(View.VISIBLE);
                        imgThumbUp.setVisibility(View.VISIBLE);
                        tvContent.setText(EmojiUtil.parseEmojiByText2(context,
                                question.getAnswer()
                                        .getSummary(),
                                measures.faceSize));
                        tvPraiseCount.setText(question.getAnswer()
                                .getUpCount() == 0 ? "赞" : String.valueOf(question.getAnswer()
                                .getUpCount()));
                        tvPraiseCount.setTextColor(question.getAnswer()
                                .getLikeType() == 1 ? ContextCompat.getColor(mContext,
                                R.color.colorPrimary) : ContextCompat.getColor(mContext,
                                R.color.colorBlack2));
                        imgThumbUp.setImageResource(question.getAnswer()
                                .getLikeType() == 1 ? R.mipmap.icon_praise_primary_50_50 : R
                                .mipmap.icon_praise_black_50_50);
                    } else {
                        tvPraiseCount.setVisibility(View.GONE);
                        imgThumbUp.setVisibility(View.GONE);
                        tvContent.setVisibility(View.GONE);
                    }
                    //回答者头像
                    if (question.getAnswer()
                            .getUser() != null) {
                        authView.setVisibility(View.VISIBLE);
                        String imageUrl = ImageUtil.getImagePath(question.getAnswer()
                                .getUser()
                                .getAvatar(), CommonUtil.dp2px(mContext, 20));
                        Glide.with(mContext)
                                .load(imageUrl)
                                .apply(new RequestOptions()
                                .dontAnimate())
                                .into(ivAuth);
                        tvAuthName.setText(question.getAnswer()
                                .getUser()
                                .getName());
                    } else {
                        authView.setVisibility(View.GONE);
                    }

                } else {
                    authView.setVisibility(View.GONE);
                    tvContent.setVisibility(View.GONE);
                    praiseView.setVisibility(View.GONE);
                }
                tvChannelName.setText(Html.fromHtml(mContext.getString(R.string
                                .label_thread_channel___cm,
                        "婚嫁问答")));
                tvChannelName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onQaItemClickListener != null)
                            onQaItemClickListener.onQaItemClickListener();
                    }
                });
                questionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (question.getId() != 0) {
                            if (onFeedItemClickListener != null) {
                                onFeedItemClickListener.onFeedItemClickListener(position,
                                        item.getEntityId(),
                                        question,
                                        QUESTION_TYPE);
                                //                                tvQuestionTitle.setTextColor
                                // (ContextCompat.getColor(mContext,
                                //                                        R.color.colorGray));
                                //                                tvContent.setTextColor
                                // (ContextCompat.getColor(mContext,
                                //                                        R.color.colorGray));
                            }
                        }
                    }
                });
                //历史点击中包含item的id，表示点击过
                //                if (feedIds.contains(item.getEntityId())) {
                //                    tvQuestionTitle.setTextColor(ContextCompat.getColor
                // (mContext, R.color.colorGray));
                //                    tvContent.setTextColor(ContextCompat.getColor(mContext, R
                // .color.gray1));
                //                } else {
                //                    tvQuestionTitle.setTextColor(ContextCompat.getColor
                // (mContext, R.color.colorBlack2));
                //                    tvContent.setTextColor(ContextCompat.getColor(mContext, R
                // .color.black3));
                //                }
            }
        }
    }

    public void setOnReplyItemClickListener(OnReplyItemClickListener onReplyItemClickListener) {
        this.onReplyItemClickListener = onReplyItemClickListener;
    }

    public interface OnReplyItemClickListener {
        void onReply(
                CommunityThread item, int position);
    }

    public void setOnFeedItemClickListener(
            OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public interface OnFeedItemClickListener {
        void onFeedItemClickListener(
                int position, long id, Object item, int type);
    }

    public void setOnQaItemClickListener(
            OnQaItemClickListener onQaItemClickListener) {
        this.onQaItemClickListener = onQaItemClickListener;
    }

    public interface OnQaItemClickListener {
        void onQaItemClickListener();
    }
}
