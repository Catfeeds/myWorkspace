package me.suncloud.marrymemo.adpter.merchant;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.merchant.HotelHall;
import com.hunliji.hljcommonlibrary.models.merchant.HotelMenu;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantRecommendPosterItem;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.GroupRecyclerAdapter;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonGroupHeaderViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallEventViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;
import com.hunliji.hljcommonviewlibrary.models.GroupAdapterHeader;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljquestionanswer.adapters.viewholder.AskQuestionViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentMarksViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeCaseListViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeCommentMarksHeaderViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeDescribeViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeHotelHallViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeHotelMenuViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeInstallmentViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeNoteListViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeNoticeViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeQuestionEmptyViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantRecommendPosterViewHolder;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentViewHolder;
import me.suncloud.marrymemo.model.wrappers.HljHttpCommentsData;

/**
 * Created by wangtao on 2017/9/27.
 */

public class MerchantHomeAdapter extends GroupRecyclerAdapter<BaseViewHolder> {

    public static class GroupIndex {
        public static final int Info = 0;
        public static final int Notice = 1;
        private static final int Poster = 2;
        private static final int Event = 3;
        public static final int Work = 4;
        public static final int Case = 5;
        private static final int Note = 6;
        private static final int Describe = 7;
        public static final int Question = 8;
        public static final int Comment = 9;
        public static final int Installment = 3;
        public static final int Hall = 4;
        public static final int Menu = 5;
    }

    public static class GroupType {
        public static final int Notice = 1;
        public static final int Poster = 2;
        public static final int Event = 3;
        public static final int Work = 4;
        public static final int Case = 5;
        public static final int Note = 6;
        public static final int Describe = 7;
        public static final int Question = 8;
        public static final int Comment = 9;
        public static final int Hall = 10;
        public static final int Menu = 11;
        public static final int Info = 12;
        public static final int Installment = 13;
    }

    public static class ItemType {
        private static final int CommentFooterEmpty = -4;
        private static final int CommentFooter = -3;
        private static final int CommentMarksGroupHeader = -2;
        private static final int CommonGroupHeader = -1;
        private static final int Notice = 1;
        private static final int PosterDouble = 2;
        private static final int PosterVertical = 3;
        private static final int PosterHorizontal = 4;
        private static final int Event = 5;
        private static final int Work = 6;
        private static final int Case = 7;
        private static final int Note = 8;
        private static final int Describe = 9;
        private static final int Question = 10;
        private static final int QuestionEmpty = 11;
        public static final int Comment = 12;
        private static final int Hall = 13;
        private static final int Menu = 14;
        private static final int Info = 15;
        private static final int Installment = 16;
    }

    private Context context;
    private Merchant merchant;
    private EventInfo eventInfo;
    private Question question;
    private List<MerchantRecommendPosterItem> recommendPosterItems;
    private List<Work> works;
    private List<Work> cases;
    private List<Note> notes;
    private List<ServiceComment> comments;
    private List<ServiceCommentMark> marks;
    private List<HotelHall> halls;
    private List<HotelMenu> menus;

    private int caseCount;
    private int workCount;
    private int noteCount;
    private int questionCount;
    private int hallCount;
    private int menuCount;
    private int commentCount;
    private long currentMarkId;

    private int showCommentCount;

    private View commentFooterView;
    private View merchantInfoView;

    private OnItemClickListener onItemClickListener;
    private MerchantHomeNoticeViewHolder.NoticeClickListener noticeClickListener;
    private CommonGroupHeaderViewHolder.GroupHeaderClickListener headerClickListener;
    private ServiceCommentViewHolder.OnPraiseListener onPraiseListener;
    private ServiceCommentViewHolder.OnCommentListener onCommentListener;
    private ServiceCommentMarksViewHolder.OnCommentFilterListener onCommentFilterListener;
    private MerchantHomeCommentMarksHeaderViewHolder.OnCommentEmptyClickListener
            onCommentEmptyClickListener;
    private MerchantHomeInstallmentViewHolder.OnInstallmentClickListener onInstallmentClickListener;

    public MerchantHomeAdapter(Context context) {
        this.context = context;
    }

    public void setCommentFooterView(View commentFooterView) {
        this.commentFooterView = commentFooterView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setNoticeClickListener(
            MerchantHomeNoticeViewHolder.NoticeClickListener noticeClickListener) {
        this.noticeClickListener = noticeClickListener;
    }

    public void setHeaderClickListener(
            CommonGroupHeaderViewHolder.GroupHeaderClickListener headerClickListener) {
        this.headerClickListener = headerClickListener;
    }

    public void setOnPraiseListener(
            ServiceCommentViewHolder.OnPraiseListener onPraiseListener) {
        this.onPraiseListener = onPraiseListener;
    }

    public void setOnCommentListener(
            ServiceCommentViewHolder.OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
    }

    public void setOnCommentFilterListener(
            ServiceCommentMarksViewHolder.OnCommentFilterListener onCommentFilterListener) {
        this.onCommentFilterListener = onCommentFilterListener;
    }

    public void setOnCommentEmptyClickListener(
            MerchantHomeCommentMarksHeaderViewHolder.OnCommentEmptyClickListener
                    onCommentEmptyClickListener) {
        this.onCommentEmptyClickListener = onCommentEmptyClickListener;
    }

    public void setOnInstallmentClickListener(
            MerchantHomeInstallmentViewHolder.OnInstallmentClickListener
                    onInstallmentClickListener) {
        this.onInstallmentClickListener = onInstallmentClickListener;
    }

    public void setMerchantInfoView(View merchantInfoView) {
        this.merchantInfoView = merchantInfoView;
        setGroup(GroupIndex.Info, GroupType.Info, 1);
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
        if (!TextUtils.isEmpty(merchant.getNoticeStr()) || !TextUtils.isEmpty(merchant
                .getShopGift()) || (merchant.isCoupon() && !CommonUtil.isCollectionEmpty(
                merchant.getCoupons())) || !TextUtils.isEmpty(merchant.getExclusiveOffer()) ||
                merchant.getShopType() == Merchant.SHOP_TYPE_HOTEL) {
            setGroup(GroupIndex.Notice, GroupType.Notice, 1);
        }
        if (merchant.getEventInfo() != null) {
            setEventInfo(merchant.getEventInfo());
        }

        if (!TextUtils.isEmpty(merchant.getDesc()) || !CommonUtil.isCollectionEmpty(merchant
                .getRealPhotos())) {
            setGroup(GroupIndex.Describe, GroupType.Describe, 1);
        }

        //是否支持分期
        if (merchant.isInstallment()) {
            setGroup(GroupIndex.Installment, GroupType.Installment, 1);
        }
    }

    public void setEventInfo(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
        setGroup(GroupIndex.Event, GroupType.Event, 1);
    }

    public void setRecommendPosterItems(List<MerchantRecommendPosterItem> recommendPosterItems) {
        this.recommendPosterItems = recommendPosterItems;
        setGroup(GroupIndex.Poster, GroupType.Poster, recommendPosterItems.size());
    }

    public void setWorks(HljHttpData<List<Work>> workListData) {
        this.works = workListData.getData();
        this.workCount = workListData.getTotalCount();
        setGroup(GroupIndex.Work, GroupType.Work, works.size());
    }

    public void setCases(HljHttpData<List<Work>> caseListData) {
        this.cases = caseListData.getData();
        this.caseCount = caseListData.getTotalCount();
        setGroup(GroupIndex.Case, GroupType.Case, 1);
    }

    public void setNotes(HljHttpData<List<Note>> noteListData) {
        this.notes = noteListData.getData();
        this.noteCount = noteListData.getTotalCount();
        setGroup(GroupIndex.Note, GroupType.Note, 1);
    }

    public void setQuestion(HljHttpData<List<Question>> questionListData) {
        if (questionListData == null) {
            this.questionCount = 0;
            this.question = null;
        } else {
            this.questionCount = questionListData.getTotalCount();
            if (CommonUtil.isCollectionEmpty(questionListData.getData())) {
                this.question = null;
            } else {
                this.question = questionListData.getData()
                        .get(0);
            }
        }
        setGroup(GroupIndex.Question, GroupType.Question, 1);
    }

    public void initComments(
            List<ServiceCommentMark> marks, HljHttpCommentsData commentListData) {
        this.marks = marks;
        if (!CommonUtil.isCollectionEmpty(marks)) {
            setComments(commentListData);
        } else {
            setComments(null);
        }
    }

    public void setComments(HljHttpCommentsData commentListData) {
        if (currentMarkId == 0) {
            commentCount = commentListData == null ? 0 : commentListData.getTotalCount();
        }
        if (commentListData == null) {
            this.comments = new ArrayList<>();
        } else if (CommonUtil.isCollectionEmpty(commentListData.getData())) {
            this.comments = new ArrayList<>();
        } else {
            this.comments = commentListData.getData();
        }
        showCommentCount = comments.size();
        if (commentListData != null && commentListData.getFirstSixMonthAgoIndex() > 0) {
            showCommentCount = Math.min(commentListData.getFirstSixMonthAgoIndex(),
                    showCommentCount);
        }
        setGroup(GroupIndex.Comment, GroupType.Comment, showCommentCount);
    }

    public void clearComments() {
        if (this.comments != null) {
            comments.clear();
        }
        setGroup(GroupIndex.Comment, GroupType.Comment, 0);
    }

    public void addComments(HljHttpCommentsData commentListData) {
        if (commentListData == null || CommonUtil.isCollectionEmpty(commentListData.getData())) {
            return;
        }
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.addAll(commentListData.getData());
        int addCount = commentListData.getData()
                .size();
        if (commentListData.getFirstSixMonthAgoIndex() >= 0) {
            addCount = Math.min(commentListData.getFirstSixMonthAgoIndex(), addCount);
        }
        if (addCount == 0) {
            return;
        }
        showCommentCount += addCount;
        addGroupChild(GroupIndex.Comment, GroupType.Comment, addCount);
    }


    public void showOtherComments() {
        if (comments == null || showCommentCount >= comments.size()) {
            return;
        }
        addGroupChild(GroupIndex.Comment, GroupType.Comment, comments.size() - showCommentCount);
        showCommentCount = comments.size();
    }


    public void setHalls(List<HotelHall> halls) {
        this.halls = halls;
        this.hallCount = halls != null ? halls.size() : 0;
        setGroup(GroupIndex.Hall, GroupType.Hall, Math.min(hallCount, 3));
    }

    public void setMenus(List<HotelMenu> menus) {
        this.menus = menus;
        this.menuCount = menus != null ? menus.size() : 0;
        setGroup(GroupIndex.Menu, GroupType.Menu, Math.min(menuCount, 3));
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemType.CommentFooterEmpty:
                View emptyView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty_placeholder, parent, false);
                emptyView.getLayoutParams().height = CommonUtil.dp2px(parent.getContext(), 200);
                return new ExtraBaseViewHolder(emptyView);
            case ItemType.CommentFooter:
                return new ExtraBaseViewHolder(commentFooterView);
            case ItemType.CommentMarksGroupHeader:
                MerchantHomeCommentMarksHeaderViewHolder marksHeaderViewHolder = new
                        MerchantHomeCommentMarksHeaderViewHolder(
                        parent);
                marksHeaderViewHolder.setOnCommentFilterListener(new ServiceCommentMarksViewHolder.OnCommentFilterListener() {

                    @Override
                    public void onCommentFilter(long markId) {
                        currentMarkId = markId;
                        onCommentFilterListener.onCommentFilter(markId);

                    }
                });
                marksHeaderViewHolder.setOnCommentClickListener(onCommentEmptyClickListener);
                return marksHeaderViewHolder;
            case ItemType.CommonGroupHeader:
                return new CommonGroupHeaderViewHolder(parent, headerClickListener);
            case ItemType.Notice:
                return new MerchantHomeNoticeViewHolder(parent, noticeClickListener);
            case ItemType.PosterDouble:
                return new MerchantRecommendPosterViewHolder(parent,
                        MerchantRecommendPosterItem.DOUBLE_IMAGE,
                        merchant);
            case ItemType.PosterVertical:
                return new MerchantRecommendPosterViewHolder(parent,
                        MerchantRecommendPosterItem.SIMPLE_VERTICAL_IMAGE,
                        merchant);
            case ItemType.PosterHorizontal:
                return new MerchantRecommendPosterViewHolder(parent,
                        MerchantRecommendPosterItem.SIMPLE_HORIZONTAL_IMAGE,
                        merchant);
            case ItemType.Event:
                return new SmallEventViewHolder(parent);
            case ItemType.Work:
                SmallWorkViewHolder workViewHolder = new SmallWorkViewHolder(parent);
                workViewHolder.setOnItemClickListener(onItemClickListener);
                workViewHolder.setStyle(SmallWorkViewHolder.STYLE_MERCHANT_HOME_PAGE);
                return workViewHolder;
            case ItemType.Case:
                return new MerchantHomeCaseListViewHolder(parent);
            case ItemType.Note:
                return new MerchantHomeNoteListViewHolder(parent);
            case ItemType.Describe:
                return new MerchantHomeDescribeViewHolder(parent, onItemClickListener);
            case ItemType.Question:
                AskQuestionViewHolder questionViewHolder = new AskQuestionViewHolder(parent);
                questionViewHolder.setWorkWithMerchantVisibility(true);
                questionViewHolder.setOnItemClickListener(onItemClickListener);
                return questionViewHolder;
            case ItemType.QuestionEmpty:
                return new MerchantHomeQuestionEmptyViewHolder(parent, merchant);
            case ItemType.Comment:
                ServiceCommentViewHolder commentViewHolder = new ServiceCommentViewHolder(parent);
                commentViewHolder.setOnCommentListener(onCommentListener);
                commentViewHolder.setOnPraiseListener(onPraiseListener);
                commentViewHolder.setOnItemClickListener(onItemClickListener);
                return commentViewHolder;
            case ItemType.Hall:
                return new MerchantHomeHotelHallViewHolder(parent,
                        onItemClickListener,
                        merchant.getId());
            case ItemType.Menu:
                return new MerchantHomeHotelMenuViewHolder(parent, onItemClickListener);
            case ItemType.Info:
                return new ExtraBaseViewHolder(merchantInfoView);
            case ItemType.Installment:
                return new MerchantHomeInstallmentViewHolder(parent, onInstallmentClickListener);
        }
        return null;
    }

    @Override
    public boolean hasGroupHeader(int groupType) {
        switch (groupType) {
            case GroupType.Event:
            case GroupType.Work:
            case GroupType.Case:
            case GroupType.Note:
            case GroupType.Describe:
            case GroupType.Question:
            case GroupType.Comment:
            case GroupType.Hall:
            case GroupType.Menu:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean hasGroupFooter(int groupType) {
        switch (groupType) {
            case GroupType.Comment:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getGroupHeaderType(int groupType) {
        switch (groupType) {
            case GroupType.Comment:
                return ItemType.CommentMarksGroupHeader;
            default:
                return ItemType.CommonGroupHeader;
        }
    }

    @Override
    public int getGroupFooterType(int groupType) {
        switch (groupType) {
            case GroupType.Comment:
                return CommonUtil.isCollectionEmpty(comments) ? ItemType.CommentFooterEmpty :
                        ItemType.CommentFooter;
            default:
                return 0;
        }
    }

    @Override
    public int getItemViewType(int groupType, int childPosition) {
        switch (groupType) {
            case GroupType.Notice:
                return ItemType.Notice;
            case GroupType.Poster:
                switch (recommendPosterItems.get(childPosition)
                        .getShowType()) {
                    case MerchantRecommendPosterItem.DOUBLE_IMAGE:
                        return ItemType.PosterDouble;
                    case MerchantRecommendPosterItem.SIMPLE_VERTICAL_IMAGE:
                        return ItemType.PosterVertical;
                    default:
                        return ItemType.PosterHorizontal;
                }
            case GroupType.Event:
                return ItemType.Event;
            case GroupType.Work:
                return ItemType.Work;
            case GroupType.Case:
                return ItemType.Case;
            case GroupType.Note:
                return ItemType.Note;
            case GroupType.Describe:
                return ItemType.Describe;
            case GroupType.Question:
                if (question != null) {
                    return ItemType.Question;
                } else {
                    return ItemType.QuestionEmpty;
                }
            case GroupType.Comment:
                return ItemType.Comment;
            case GroupType.Hall:
                return ItemType.Hall;
            case GroupType.Menu:
                return ItemType.Menu;
            case GroupType.Info:
                return ItemType.Info;
            case GroupType.Installment:
                return ItemType.Installment;
        }
        return 0;
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupType, int childPosition) {
        if (holder instanceof MerchantHomeNoticeViewHolder) {
            ((MerchantHomeNoticeViewHolder) holder).setView(context,
                    merchant,
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof MerchantRecommendPosterViewHolder) {
            ((MerchantRecommendPosterViewHolder) holder).setView(context,
                    recommendPosterItems.get(childPosition),
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof SmallEventViewHolder) {
            ((SmallEventViewHolder) holder).setView(context,
                    eventInfo,
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof SmallWorkViewHolder) {
            ((SmallWorkViewHolder) holder).setView(context,
                    works.get(childPosition),
                    childPosition,
                    getItemViewType(groupType, childPosition));
            ((SmallWorkViewHolder) holder).setShowBottomThinLineView(childPosition < works.size()
                    - 1);
        } else if (holder instanceof MerchantHomeCaseListViewHolder) {
            ((MerchantHomeCaseListViewHolder) holder).setView(context,
                    cases,
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof MerchantHomeNoteListViewHolder) {
            ((MerchantHomeNoteListViewHolder) holder).setView(context,
                    notes,
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof MerchantHomeDescribeViewHolder) {
            ((MerchantHomeDescribeViewHolder) holder).setView(context,
                    merchant,
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof AskQuestionViewHolder) {
            ((AskQuestionViewHolder) holder).setView(context,
                    question,
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof ServiceCommentViewHolder) {
            ((ServiceCommentViewHolder) holder).setShowTopLineView(childPosition > 0);
            ((ServiceCommentViewHolder) holder).setView(context,
                    comments.get(childPosition),
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof MerchantHomeHotelHallViewHolder) {
            ((MerchantHomeHotelHallViewHolder) holder).setView(context,
                    halls.get(childPosition),
                    childPosition,
                    getItemViewType(groupType, childPosition));
        } else if (holder instanceof MerchantHomeHotelMenuViewHolder) {
            ((MerchantHomeHotelMenuViewHolder) holder).setView(context,
                    menus.get(childPosition),
                    childPosition,
                    getItemViewType(groupType, childPosition));
        }
    }

    @Override
    public void onBindGroupHeaderViewHolder(BaseViewHolder holder, int groupType) {
        if (holder instanceof CommonGroupHeaderViewHolder) {
            GroupAdapterHeader header = null;
            switch (groupType) {
                case GroupType.Event:
                    header = new GroupAdapterHeader(groupType,
                            context.getString(R.string.label_merchant_event),
                            null,
                            false,
                            16,
                            18,
                            16,
                            8,
                            false);
                    break;
                case GroupType.Work:
                    header = new GroupAdapterHeader(groupType,
                            context.getString(R.string.label_recommend_work),
                            context.getString(R.string.label_work_count2, workCount),
                            true,
                            16,
                            18,
                            16,
                            4,
                            false);
                    break;
                case GroupType.Case:
                    header = new GroupAdapterHeader(groupType,
                            context.getString(R.string.label_recommend_case),
                            context.getString(R.string.label_case_count2, caseCount),
                            true,
                            16,
                            18,
                            16,
                            18,
                            false);
                    break;
                case GroupType.Note:
                    header = new GroupAdapterHeader(groupType,
                            context.getString(R.string.label_merchant_note),
                            context.getString(R.string.label_merchant_note_count, noteCount),
                            true,
                            16,
                            18,
                            16,
                            18,
                            false);
                    break;
                case GroupType.Describe:
                    header = new GroupAdapterHeader(groupType,
                            context.getString(R.string.title_activity_merchant_description),
                            null,
                            true,
                            16,
                            18,
                            16,
                            18,
                            true);
                    break;
                case GroupType.Question:
                    header = new GroupAdapterHeader(groupType,
                            context.getString(R.string.label_ask_question),
                            questionCount > 0 ? context.getString(R.string
                                            .label_merchant_question_count,
                                    questionCount) : context.getString(R.string.label_no_question),
                            questionCount > 0,
                            16,
                            18,
                            16,
                            18,
                            true);
                    break;
                case GroupType.Hall:
                    header = new GroupAdapterHeader(groupType,
                            context.getString(R.string.label_hall),
                            context.getString(R.string.label_hall_count, hallCount),
                            true,
                            16,
                            18,
                            16,
                            4,
                            false);
                    break;
                case GroupType.Menu:
                    header = new GroupAdapterHeader(groupType,
                            context.getString(R.string.label_menu),
                            context.getString(R.string.label_menu_count, menuCount),
                            true,
                            16,
                            18,
                            16,
                            18,
                            true);
                    break;
            }

            if (header == null) {
                return;
            }
            ((CommonGroupHeaderViewHolder) holder).setView(context, header, 0, groupType);
        } else if (holder instanceof MerchantHomeCommentMarksHeaderViewHolder) {
            ((MerchantHomeCommentMarksHeaderViewHolder) holder).setView(context,
                    marks,
                    0,
                    groupType);
            ((MerchantHomeCommentMarksHeaderViewHolder) holder).setCommentCount(commentCount,
                    merchant);
            ((MerchantHomeCommentMarksHeaderViewHolder) holder).setMarkId(currentMarkId);
        }
    }

    @Override
    public void onBindGroupFooterViewHolder(BaseViewHolder holder, int groupType) {

    }
}
