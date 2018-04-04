package com.hunliji.hljnotelibrary.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.MerchantBriefInfoViewHolder;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder.CommonNoteCommentViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.CommonNoteViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.NoteBookItemViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.RelevantProductViewHolder;
import com.hunliji.hljnotelibrary.adapters.viewholder.UserRelevantNoteViewHolder;
import com.hunliji.hljnotelibrary.views.activities.NoteDetailActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mo_yu on 2017/6/26.笔记和笔记详情通用adapter
 */

public class NoteDetailRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int DEFAULT_TYPE = 1;//空视图，防止出现无法解析的数据时，显示异常
    private static final int FOOTER_TYPE = 2;
    private static final int NOTE_HEADER_TYPE = 3;//笔记(本)详情
    private static final int MERCHANT_TYPE = 4;//相关商家
    private static final int PRODUCT_TYPE = 5;//相关婚品
    private static final int USER_RELEVANT_NOTE_TYPE = 6;//他的相关笔记
    private static final int COMMENT_HEADER_TYPE = 7;//笔记评论头部视图
    private static final int COMMENT_TYPE = 8;//笔记评论
    private static final int COMMENT_FOOTER_TYPE = 9;//笔记评论底部视图
    private static final int RELATIVE_NOTE_TYPE_1_TO_1 = 10;//推荐笔记1:1
    private static final int RELATIVE_NOTE_TYPE_3_TO_4 = 11;//推荐笔记3:4
    private static final int RELATIVE_NOTE_TYPE_4_TO_3 = 12;//推荐笔记4:3
    private static final int NOTE_BOOK_ITEM_TYPE = 13;//笔记详情
    private static final int RELATIVE_NOTE_HEADER_TYPE = 14;//相关笔记列表（看了又看）头部视图

    public int maxCommentCount = 3;
    private Context context;
    private ArrayList<Note> relativeNotes;
    private ArrayList<Note> userNotes;
    private ArrayList<Note> notebookNotes;
    private ArrayList<Merchant> merchants;
    private ArrayList<ShopProduct> products;
    private ArrayList<RepliedComment> comments;
    private View footerView;
    private View noteHeaderView;
    private View commentHeaderView;
    private View commentFooterView;
    private View relevantNoteHeaderView;
    private LayoutInflater inflater;
    private int notebookType;
    private int totalUserNoteCount;
    private boolean gender;
    private String entityType;
    private OnUserNoteListener onUserNoteListener;
    private OnCommentReplyListener onCommentReplyListener;

    public NoteDetailRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setRelativeNotes(ArrayList<Note> relativeNotes) {
        this.relativeNotes = relativeNotes;
    }

    public void setNotebookNotes(ArrayList<Note> notebookNotes) {
        this.notebookNotes = notebookNotes;
    }

    public void setMerchants(ArrayList<Merchant> merchants) {
        this.merchants = merchants;
    }

    public void setProducts(ArrayList<ShopProduct> products) {
        this.products = products;
    }

    public void setComments(ArrayList<RepliedComment> comments) {
        this.comments = comments;
    }

    public void setUserNotes(ArrayList<Note> userNotes) {
        this.userNotes = userNotes;
    }

    public void setNoteHeaderView(View noteHeaderView) {
        this.noteHeaderView = noteHeaderView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setCommentHeaderView(View commentHeaderView) {
        this.commentHeaderView = commentHeaderView;
    }

    public void setCommentFooterView(View commentFooterView) {
        this.commentFooterView = commentFooterView;
    }

    public void setRelevantNoteHeaderView(View relevantNoteHeaderView) {
        this.relevantNoteHeaderView = relevantNoteHeaderView;
    }

    public void setMaxCommentCount(int maxCommentCount) {
        this.maxCommentCount = maxCommentCount;
    }

    public int getMaxCommentCount() {
        return maxCommentCount;
    }

    public void setTotalUserNoteCount(int totalUserNoteCount) {
        this.totalUserNoteCount = totalUserNoteCount;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public void setNotebookType(int notebookType) {
        this.notebookType = notebookType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public void addItems(List<Note> items) {
        if (!CommonUtil.isCollectionEmpty(items)) {
            int start = getItemCount() - (footerView != null ? 1 : 0);
            this.relativeNotes.addAll(items);
            notifyItemRangeInserted(start, items.size());
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                ExtraBaseViewHolder footerViewHolder = new ExtraBaseViewHolder(footerView);
                footerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return footerViewHolder;
            case NOTE_HEADER_TYPE:
                ExtraBaseViewHolder noteDetailViewHolder = new ExtraBaseViewHolder(noteHeaderView);
                noteDetailViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return noteDetailViewHolder;
            case USER_RELEVANT_NOTE_TYPE:
                UserRelevantNoteViewHolder userRelevantNoteViewHolder = new
                        UserRelevantNoteViewHolder(
                        inflater.inflate(R.layout.user_relevant_note_layout___note, parent, false));
                userRelevantNoteViewHolder.itemView.setLayoutParams(new
                        StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                userRelevantNoteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onUserNoteListener != null) {
                            onUserNoteListener.onUserNoteClick();
                        }
                    }
                });
                userRelevantNoteViewHolder.setOnItemClickListener(new OnItemClickListener<Note>() {
                    @Override
                    public void onItemClick(int position, Note object) {
                        if (onUserNoteListener != null) {
                            onUserNoteListener.onUserNoteClick();
                        }
                    }
                });
                userRelevantNoteViewHolder.setGender(gender);
                return userRelevantNoteViewHolder;
            case MERCHANT_TYPE:
                MerchantBriefInfoViewHolder merchantBriefInfoViewHolder = new
                        MerchantBriefInfoViewHolder(
                        inflater.inflate(R.layout.merchant_brief_info_list_item___cv,
                                parent,
                                false));
                merchantBriefInfoViewHolder.setOnItemClickListener(new OnItemClickListener<Merchant>() {
                    @Override
                    public void onItemClick(int position, Merchant merchant) {
                        if (merchant != null && merchant.getId() > 0) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                    .withLong("id", merchant.getId())
                                    .withTransition(R.anim.slide_in_right,
                                            R.anim.activity_anim_default)
                                    .navigation(context);
                        }
                    }
                });
                merchantBriefInfoViewHolder.itemView.setLayoutParams(new
                        StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return merchantBriefInfoViewHolder;
            case PRODUCT_TYPE:
                RelevantProductViewHolder relevantProductViewHolder = new RelevantProductViewHolder(
                        inflater.inflate(R.layout.relevant_product_layout___note, parent, false));
                relevantProductViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return relevantProductViewHolder;
            case COMMENT_HEADER_TYPE:
                ExtraBaseViewHolder commentHeaderViewHolder = new ExtraBaseViewHolder(
                        commentHeaderView);
                commentHeaderViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return commentHeaderViewHolder;
            case COMMENT_FOOTER_TYPE:
                ExtraBaseViewHolder commentFooterViewHolder = new ExtraBaseViewHolder(
                        commentFooterView);
                commentFooterViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return commentFooterViewHolder;
            case COMMENT_TYPE:
                CommonNoteCommentViewHolder commonNoteCommentViewHolder = new
                        CommonNoteCommentViewHolder(
                        inflater.inflate(R.layout.note_comment_list_item___note, parent, false),
                        entityType);
                commonNoteCommentViewHolder.itemView.setLayoutParams(new
                        StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                commonNoteCommentViewHolder.setOnCommentListener(new CommonNoteCommentViewHolder
                        .OnCommentListener() {
                    @Override
                    public void onComment(RepliedComment comment) {
                        if (onCommentReplyListener != null) {
                            onCommentReplyListener.onCommentItemClick(comment);
                        }
                    }
                });
                return commonNoteCommentViewHolder;
            case RELATIVE_NOTE_HEADER_TYPE:
                ExtraBaseViewHolder relevantNoteHeaderViewHolder = new ExtraBaseViewHolder(
                        relevantNoteHeaderView);
                relevantNoteHeaderViewHolder.itemView.setLayoutParams(new
                        StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return relevantNoteHeaderViewHolder;
            case RELATIVE_NOTE_TYPE_3_TO_4:
            case RELATIVE_NOTE_TYPE_4_TO_3:
            case RELATIVE_NOTE_TYPE_1_TO_1:
                int style;
                if (viewType == RELATIVE_NOTE_TYPE_3_TO_4) {
                    style = CommonNoteViewHolder.STYLE_RATIO_3_TO_4;
                } else if (viewType == RELATIVE_NOTE_TYPE_4_TO_3) {
                    style = CommonNoteViewHolder.STYLE_RATIO_4_TO_3;
                } else {
                    style = CommonNoteViewHolder.STYLE_RATIO_1_TO_1;
                }
                CommonNoteViewHolder noteViewHolder = new CommonNoteViewHolder(inflater.inflate(R
                                .layout.common_note_list_item___note,
                        parent,
                        false), style);
                noteViewHolder.setOnItemClickListener(new OnItemClickListener<Note>() {
                    @Override
                    public void onItemClick(int position, Note note) {
                        if (note != null && note.getId() > 0) {
                            Activity activity = (Activity) context;
                            Intent intent = new Intent(context, NoteDetailActivity.class);
                            intent.putExtra("note_id", note.getId());
                            intent.putExtra("url", note.getUrl());
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                return noteViewHolder;
            case NOTE_BOOK_ITEM_TYPE:
                NoteBookItemViewHolder noteBookItemViewHolder = new NoteBookItemViewHolder
                        (inflater.inflate(
                        R.layout.note_book_item___note,
                        parent,
                        false));
                noteBookItemViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                noteBookItemViewHolder.setOnNotebookItemClickListener(new NoteBookItemViewHolder
                        .OnNotebookItemClickListener() {
                    @Override
                    public void onNotebookItemClick(int notePosition, int inspirationPosition) {
                        long[] noteIds = new long[notebookNotes.size()];
                        for (int i = 0; i < notebookNotes.size(); i++) {
                            noteIds[i] = notebookNotes.get(i)
                                    .getId();
                        }
                        Activity activity = (Activity) context;
                        Intent intent = new Intent(context, NoteDetailActivity.class);
                        intent.putExtra("note_position", notePosition);
                        intent.putExtra("inspiration_position", inspirationPosition);
                        intent.putExtra(NoteDetailActivity.ARG_NOTE_IDS, noteIds);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                return noteBookItemViewHolder;
            default:
                ExtraBaseViewHolder emptyViewHolder = new ExtraBaseViewHolder(LayoutInflater.from(
                        parent.getContext())
                        .inflate(R.layout.empty_place_holder___cm, parent, false));
                emptyViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return emptyViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int currentPosition = getCurrentPosition(position);
        int viewType = getItemViewType(position);
        switch (viewType) {
            case NOTE_BOOK_ITEM_TYPE:
                NoteBookItemViewHolder noteBookItemViewHolder = (NoteBookItemViewHolder) holder;
                //笔记本列表的笔记,每一项都是一个item
                if (currentPosition == notebookNotes.size() - 1) {
                    noteBookItemViewHolder.showOrHideBottomLineView(true);
                } else {
                    noteBookItemViewHolder.showOrHideBottomLineView(false);
                }
                noteBookItemViewHolder.setView(context,
                        notebookNotes.get(currentPosition),
                        currentPosition,
                        viewType);
                break;
            case USER_RELEVANT_NOTE_TYPE:
                //用户相关笔记为单项列表，把整个list传入
                UserRelevantNoteViewHolder relevantUserNoteViewHolder =
                        (UserRelevantNoteViewHolder) holder;
                relevantUserNoteViewHolder.setNoteType(notebookType);
                relevantUserNoteViewHolder.setTotalUserNoteCount(totalUserNoteCount);
                relevantUserNoteViewHolder.setView(context, userNotes, position, viewType);
                break;
            case PRODUCT_TYPE:
                //婚品为单项列表，把整个list传入
                holder.setView(context, products, position, viewType);
                break;
            case MERCHANT_TYPE:
                MerchantBriefInfoViewHolder merchantBriefInfoViewHolder =
                        (MerchantBriefInfoViewHolder) holder;
                merchantBriefInfoViewHolder.showHeaderTag(true);
                merchantBriefInfoViewHolder.setShowMerchantHeaderView(currentPosition == 0);
                merchantBriefInfoViewHolder.setMerchantHeaderTitle(context.getString(R.string
                        .label_serve_merchant___note));
                if (currentPosition == merchants.size() - 1) {
                    merchantBriefInfoViewHolder.setShowBottomThickLineView(true);
                } else {
                    merchantBriefInfoViewHolder.setShowBottomThinLineView(true);
                }
                merchantBriefInfoViewHolder.setView(context,
                        merchants.get(currentPosition),
                        position,
                        viewType);
                break;
            case COMMENT_TYPE:
                CommonNoteCommentViewHolder commonNoteCommentViewHolder =
                        (CommonNoteCommentViewHolder) holder;
                commonNoteCommentViewHolder.setView(context,
                        comments.get(currentPosition),
                        position,
                        viewType);
                if (currentPosition == getCommentsCount() - 1) {
                    commonNoteCommentViewHolder.hideBottomLine();
                }
                break;
            case RELATIVE_NOTE_TYPE_1_TO_1:
            case RELATIVE_NOTE_TYPE_3_TO_4:
            case RELATIVE_NOTE_TYPE_4_TO_3:
                holder.setView(context, relativeNotes.get(currentPosition), position, viewType);
                break;
        }
        if (viewType != RELATIVE_NOTE_TYPE_1_TO_1 && viewType != RELATIVE_NOTE_TYPE_3_TO_4 &&
                viewType != RELATIVE_NOTE_TYPE_4_TO_3) {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
            }
        }
    }

    private int getCurrentPosition(int position) {
        int currentPosition = noteHeaderView != null ? position - 1 : position;
        if (!CommonUtil.isCollectionEmpty(notebookNotes)) {
            if (currentPosition < notebookNotes.size()) {
                return currentPosition;
            }
            currentPosition -= notebookNotes.size();
        }
        if (!CommonUtil.isCollectionEmpty(merchants)) {
            if (currentPosition < merchants.size()) {
                return currentPosition;
            }
            currentPosition -= merchants.size();
        }
        //婚品为水平列表，只占一个视图位
        if (!CommonUtil.isCollectionEmpty(products)) {
            if (currentPosition == 0) {
                return currentPosition;
            }
            currentPosition = currentPosition - 1;
        }
        if (commentHeaderView != null) {
            if (currentPosition == 0) {
                return currentPosition;
            }
            currentPosition = currentPosition - 1;
        }
        if (!CommonUtil.isCollectionEmpty(comments)) {
            if (currentPosition < getCommentsCount()) {
                return currentPosition;
            }
            currentPosition -= getCommentsCount();
        }
        if (commentFooterView != null) {
            if (currentPosition == 0) {
                return currentPosition;
            }
            currentPosition = currentPosition - 1;
        }
        if (relevantNoteHeaderView != null) {
            if (currentPosition == 0) {
                return currentPosition;
            }
            currentPosition = currentPosition - 1;
        }
        //用户相关笔记为水平列表，只占一个视图位
        if (!CommonUtil.isCollectionEmpty(userNotes)) {
            if (currentPosition == 0) {
                return currentPosition;
            }
            currentPosition = currentPosition - 1;
        }
        if (!CommonUtil.isCollectionEmpty(relativeNotes)) {
            if (currentPosition < relativeNotes.size()) {
                return currentPosition;
            }
            currentPosition -= relativeNotes.size();
        }
        return currentPosition;
    }

    private int getCommentsCount() {
        if (!CommonUtil.isCollectionEmpty(comments)) {
            return comments.size() > maxCommentCount ? maxCommentCount : comments.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (noteHeaderView != null && position == 0) {
            return NOTE_HEADER_TYPE;
        } else if (footerView != null && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            int currentPosition = noteHeaderView != null ? position - 1 : position;
            if (!CommonUtil.isCollectionEmpty(notebookNotes)) {
                if (currentPosition < notebookNotes.size()) {
                    return NOTE_BOOK_ITEM_TYPE;
                }
                currentPosition -= notebookNotes.size();
            }
            if (!CommonUtil.isCollectionEmpty(merchants)) {
                if (currentPosition < merchants.size()) {
                    return MERCHANT_TYPE;
                }
                currentPosition -= merchants.size();
            }
            //婚品为水平列表，只占一个视图位
            if (!CommonUtil.isCollectionEmpty(products)) {
                if (currentPosition == 0) {
                    return PRODUCT_TYPE;
                }
                currentPosition = currentPosition - 1;
            }
            if (commentHeaderView != null) {
                if (currentPosition == 0) {
                    return COMMENT_HEADER_TYPE;
                }
                currentPosition = currentPosition - 1;
            }
            if (!CommonUtil.isCollectionEmpty(comments)) {
                if (currentPosition < getCommentsCount()) {
                    return COMMENT_TYPE;
                }
                currentPosition -= getCommentsCount();
            }
            if (commentFooterView != null) {
                if (currentPosition == 0) {
                    return COMMENT_FOOTER_TYPE;
                }
                currentPosition = currentPosition - 1;
            }
            //用户相关笔记为水平列表，只占一个视图位
            if (!CommonUtil.isCollectionEmpty(userNotes)) {
                if (currentPosition == 0) {
                    return USER_RELEVANT_NOTE_TYPE;
                }
                currentPosition = currentPosition - 1;
            }
            if (relevantNoteHeaderView != null) {
                if (currentPosition == 0) {
                    return RELATIVE_NOTE_HEADER_TYPE;
                }
                currentPosition = currentPosition - 1;
            }
            if (!CommonUtil.isCollectionEmpty(relativeNotes)) {
                if (currentPosition < relativeNotes.size()) {
                    float ratio = relativeNotes.get(currentPosition)
                            .getCover()
                            .getRatio();
                    if (ratio == NoteMedia.RATIO_1_TO_1) {
                        return RELATIVE_NOTE_TYPE_1_TO_1;
                    } else if (ratio == NoteMedia.RATIO_4_TO_3) {
                        return RELATIVE_NOTE_TYPE_3_TO_4;
                    } else {
                        return RELATIVE_NOTE_TYPE_4_TO_3;
                    }
                }
            }
        }
        return DEFAULT_TYPE;
    }


    //顺序依次为笔记详情，（笔记本列表），服务商家，相关商品，她的婚纱照，用户评论，看了又看
    @Override
    public int getItemCount() {
        return getHeaderCount() + (relativeNotes != null ? relativeNotes.size() : 0) +
                (footerView != null ? 1 : 0);
    }

    public int getHeaderCount() {
        return (noteHeaderView != null ? 1 : 0) + (notebookNotes != null ? notebookNotes.size() :
                0) + (!CommonUtil.isCollectionEmpty(
                merchants) ? merchants.size() : 0) + (!CommonUtil.isCollectionEmpty(products) ? 1
                : 0) + (!CommonUtil.isCollectionEmpty(
                userNotes) ? 1 : 0) + (commentHeaderView != null ? 1 : 0) + (commentFooterView !=
                null ? 1 : 0) + getCommentsCount() + (relevantNoteHeaderView != null ? 1 : 0);
    }

    public interface OnUserNoteListener {
        void onUserNoteClick();
    }

    public void setOnUserNoteListener(OnUserNoteListener onUserNoteListener) {
        this.onUserNoteListener = onUserNoteListener;
    }

    public interface OnCommentReplyListener {
        void onCommentItemClick(RepliedComment comment);
    }


    public void setOnCommentReplyListener(OnCommentReplyListener onCommentReplyListener) {
        this.onCommentReplyListener = onCommentReplyListener;
    }
}
