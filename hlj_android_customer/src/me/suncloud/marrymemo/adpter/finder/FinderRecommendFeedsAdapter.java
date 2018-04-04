package me.suncloud.marrymemo.adpter.finder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.interfaces.OnGetSimilarListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.models.note.NoteMedia;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljnotelibrary.adapters.viewholder.CommonNoteViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.viewholder.FinderRecommendMarkViewHolder;
import me.suncloud.marrymemo.adpter.finder.viewholder.FinderRecommendMerchantViewHolder;
import me.suncloud.marrymemo.adpter.finder.viewholder.FinderRecommendPosterViewHolder;
import me.suncloud.marrymemo.adpter.finder.viewholder.FinderRecommendProductViewHolder;
import me.suncloud.marrymemo.adpter.finder.viewholder.FinderRecommendWorkAndCaseViewHolder;
import me.suncloud.marrymemo.model.finder.CPMFeed;
import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.util.finder.FinderPrefUtil;
import me.suncloud.marrymemo.util.finder.FinderRecommendFeedsPaginationTool;

/**
 * 发现页feeds流adapter
 * Created by chen_bin on 2018/2/5 0005.
 */
public class FinderRecommendFeedsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private LayoutInflater inflater;

    private List<Object> data;
    private List<FinderFeed> finderFeeds;
    private Poster poster;

    private boolean isCanShowNoteClickHint; //是否能否显示点击进入笔记详情的提示
    private boolean isCanShowSimilarClickHint;//是否能够显示“找相似”的提示

    private boolean isShowedNoteClickHint;
    private boolean isShowedSimilarClickHint;

    private int newDataCount;
    private int noteClickHintPosition = -1;
    private int similarClickHintPosition = -1;

    private OnGetSimilarListener onGetSimilarListener;
    private OnItemClickListener onItemClickListener;

    private final static int ITEM_TYPE_POSTER = 0; //poster
    private final static int ITEM_TYPE_MERCHANT = 1; //商家
    private final static int ITEM_TYPE_PRODUCT_RATIO_1_TO_1 = 2; //婚品图片比例1:1
    private final static int ITEM_TYPE_PRODUCT_RATIO_2_TO_3 = 3; //婚品图片笔记2:3
    private final static int ITEM_TYPE_MARK = 4; //标签
    private final static int ITEM_TYPE_WORK_AND_CASE_RATIO_1_TO_1 = 5;  //套餐(案例)图片比例1:1
    private final static int ITEM_TYPE_WORK_AND_CASE_RATIO_3_TO_4 = 6; //套餐(案例) 图片比例3:4
    private final static int ITEM_TYPE_WORK_AND_CASE_RATIO_4_TO_3 = 7; //套餐(案例) 图片比例4:3
    private final static int ITEM_TYPE_NOTE_RATIO_1_TO_1 = 8; //笔记图片比例1:1
    private final static int ITEM_TYPE_NOTE_RATIO_3_TO_4 = 9; //笔记图片比例3:4
    private final static int ITEM_TYPE_NOTE_RATIO_4_TO_3 = 10; //笔记图片比例4:3
    private final static int ITEM_TYPE_FOOTER = 11; //footerView
    private final static int ITEM_TYPE_EMPTY = 12; //空视图

    public FinderRecommendFeedsAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        this.finderFeeds = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<FinderFeed> finderFeeds) {
        setData(finderFeeds, null, null, true);
    }

    public void setData(
            List<FinderFeed> finderFeeds, List<CPMFeed> cpmFeeds, Poster poster, boolean isInit) {
        if (isInit) {
            this.data.clear();
            this.finderFeeds.clear();
        }
        this.newDataCount = 0;
        this.noteClickHintPosition = -1;
        this.similarClickHintPosition = -1;
        this.poster = poster;

        int finderFeedsCount = CommonUtil.getCollectionSize(finderFeeds);
        int cpmFeedsCount = CommonUtil.getCollectionSize(cpmFeeds);
        if (finderFeedsCount > 0 || cpmFeedsCount > 0) {
            this.data.clear();
            if (finderFeedsCount > 0) {
                this.newDataCount += finderFeedsCount;
                this.finderFeeds.addAll(0, finderFeeds);
            }
            this.data.addAll(0, this.finderFeeds);
            if (cpmFeedsCount > 0) {
                this.newDataCount += cpmFeedsCount;
                for (int i = 0; i < cpmFeedsCount; i++) {
                    CPMFeed cpmFeed = cpmFeeds.get(i);
                    int random = (3 * i) + (int) (Math.random() * 3);
                    random = random < this.data.size() ? random : this.data.size();
                    this.data.add(random, cpmFeed);
                }
            }
        }
        if (!CommonUtil.isCollectionEmpty(this.data)) {
            Object obj = this.data.get(0);
            if (obj instanceof Poster) {
                this.data.remove(0);
            }
        }
        if (poster != null) {
            this.newDataCount += 1;
            this.data.add(0, poster);
        }
        notifyDataSetChanged();
    }

    public void addData(List<FinderFeed> finderFeeds) {
        addData(this.finderFeeds.size(), this.data.size(), finderFeeds);
    }

    public void addData(int feedPosition, int dataPosition, List<FinderFeed> finderFeeds) {
        if (!CommonUtil.isCollectionEmpty(finderFeeds)) {
            this.finderFeeds.addAll(feedPosition, finderFeeds);
            this.data.addAll(dataPosition, finderFeeds);
            notifyItemRangeInserted(dataPosition, finderFeeds.size());
        }
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public int getDataPosition(Object obj) {
        return data.indexOf(obj);
    }

    /**
     * 获取前30条FinderFeeds数据
     *
     * @return
     */
    public List<FinderFeed> getFirst30FinderFeeds() {
        List<FinderFeed> feeds = new ArrayList<>();
        for (Object obj : data) {
            if (obj instanceof FinderFeed) {
                FinderFeed feed = (FinderFeed) obj;
                feeds.add(feed);
                if (feeds.size() == FinderRecommendFeedsPaginationTool.PER_PAGE) {
                    break;
                }
            }
        }
        return feeds;
    }

    /**
     * 获取指定feed在feeds中的position
     *
     * @param feed
     * @return
     */
    public int getFinderFeedPosition(FinderFeed feed) {
        return finderFeeds.indexOf(feed);
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public Poster getPoster() {
        return poster;
    }

    public void setCanShowNoteClickHint(boolean canShowNoteClickHint) {
        isCanShowNoteClickHint = canShowNoteClickHint;
    }

    public void setCanShowSimilarClickHint(boolean canShowSimilarClickHint) {
        isCanShowSimilarClickHint = canShowSimilarClickHint;
    }

    public int getNewDataCount() {
        return newDataCount;
    }

    public void setSimilarClickHintPosition(int similarClickHintPosition) {
        if (!isShowedSimilarClickHint && isCanShowSimilarClickHint) {
            this.similarClickHintPosition = similarClickHintPosition;
        }
    }

    public void setOnGetSimilarListener(OnGetSimilarListener onGetSimilarListener) {
        this.onGetSimilarListener = onGetSimilarListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(data);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - getFooterViewCount()) {
            return ITEM_TYPE_FOOTER;
        } else {
            Object obj = getItem(position);
            if (obj == null) {
                return ITEM_TYPE_EMPTY;
            } else if (obj instanceof Poster) {
                return ITEM_TYPE_POSTER;
            } else if (obj instanceof CPMFeed) {
                return getCPMFeedItemType((CPMFeed) obj);
            } else if (obj instanceof FinderFeed) {
                return getFinderFeedItemType((FinderFeed) obj);
            } else {
                return ITEM_TYPE_EMPTY;
            }
        }
    }

    private int getCPMFeedItemType(CPMFeed feed) {
        Object obj = feed.getEntityObj();
        if (obj == null) {
            return ITEM_TYPE_EMPTY;
        }
        if (obj instanceof Merchant) {
            return ITEM_TYPE_MERCHANT;
        } else if (obj instanceof Work) {
            return getWorkAndCaseItemType((Work) obj);
        } else {
            return ITEM_TYPE_EMPTY;
        }
    }

    private int getFinderFeedItemType(FinderFeed feed) {
        Object obj = feed.getEntityObj();
        if (obj == null) {
            return ITEM_TYPE_EMPTY;
        }
        if (obj instanceof Merchant) {
            return ITEM_TYPE_MERCHANT;
        }
        if (obj instanceof NoteMark) {
            return ITEM_TYPE_MARK;
        } else if (obj instanceof ShopProduct) {
            return getProductItemType((ShopProduct) obj);
        } else if (obj instanceof Work) {
            return getWorkAndCaseItemType((Work) obj);
        } else if (obj instanceof Note) {
            return getNoteItemType((Note) obj);
        } else {
            return ITEM_TYPE_EMPTY;
        }
    }

    private int getProductItemType(ShopProduct product) {
        if (product == null) {
            return ITEM_TYPE_EMPTY;
        }
        Photo photo = product.getCoverImage();
        if (photo == null) {
            return ITEM_TYPE_PRODUCT_RATIO_1_TO_1;
        }
        int width = photo.getWidth();
        int height = photo.getHeight();
        if (width == 0 || height * 1.0f / width < 1.2f) {
            return ITEM_TYPE_PRODUCT_RATIO_1_TO_1;
        }
        return ITEM_TYPE_PRODUCT_RATIO_2_TO_3;
    }

    private int getWorkAndCaseItemType(Work work) {
        if (work == null) {
            return ITEM_TYPE_EMPTY;
        }
        Merchant merchant = work.getMerchant();
        if (merchant == null) {
            return ITEM_TYPE_WORK_AND_CASE_RATIO_1_TO_1;
        }
        switch ((int) merchant.getPropertyId()) {
            case Merchant.PROPERTY_WEDDING_PLAN:
            case Merchant.PROPERTY_WEDDING_DRESS_PHOTO:
            case Merchant.PROPERTY_WEDDING_MAKEUP:
            case Merchant.PROPERTY_WEDDING_SHOOTING:
            case Merchant.PROPERTY_FLORAL_DESSERT:
                return ITEM_TYPE_WORK_AND_CASE_RATIO_4_TO_3;
            case Merchant.PROPERTY_WEDDING_DRESS:
                return ITEM_TYPE_WORK_AND_CASE_RATIO_3_TO_4;
            default:
                return ITEM_TYPE_WORK_AND_CASE_RATIO_1_TO_1;
        }
    }

    private int getNoteItemType(Note note) {
        if (note == null) {
            return ITEM_TYPE_EMPTY;
        }
        NoteMedia media = note.getCover();
        if (media == null) {
            return ITEM_TYPE_NOTE_RATIO_1_TO_1;
        }
        float ratio = media.getRatio();
        if (ratio == NoteMedia.RATIO_4_TO_3) {
            return ITEM_TYPE_NOTE_RATIO_3_TO_4;
        } else if (ratio == NoteMedia.RATIO_3_TO_4) {
            return ITEM_TYPE_NOTE_RATIO_4_TO_3;
        } else {
            return ITEM_TYPE_NOTE_RATIO_1_TO_1;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                ExtraBaseViewHolder footerViewHolder = new ExtraBaseViewHolder(footerView);
                footerViewHolder.itemView.setLayoutParams(new StaggeredGridLayoutManager
                        .LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
                return footerViewHolder;
            case ITEM_TYPE_POSTER:
                return new FinderRecommendPosterViewHolder(inflater.inflate(R.layout
                                .finder_recommend_poster_list_item,
                        parent,
                        false));
            case ITEM_TYPE_MERCHANT:
                FinderRecommendMerchantViewHolder merchantViewHolder = new
                        FinderRecommendMerchantViewHolder(
                        inflater.inflate(R.layout.finder_recommend_merchant_list_item,
                                parent,
                                false));
                merchantViewHolder.setOnGetSimilarListener(onGetSimilarListener);
                return merchantViewHolder;
            case ITEM_TYPE_MARK:
                return new FinderRecommendMarkViewHolder(inflater.inflate(R.layout
                                .finder_recommend_mark_list_item,
                        parent,
                        false));
            case ITEM_TYPE_PRODUCT_RATIO_1_TO_1:
            case ITEM_TYPE_PRODUCT_RATIO_2_TO_3:
                FinderRecommendProductViewHolder productViewHolder = new
                        FinderRecommendProductViewHolder(
                        inflater.inflate(R.layout.finder_recommend_product_list_item,
                                parent,
                                false),
                        getProductItemStyle(viewType));
                productViewHolder.setOnGetSimilarListener(onGetSimilarListener);
                return productViewHolder;
            case ITEM_TYPE_WORK_AND_CASE_RATIO_1_TO_1:
            case ITEM_TYPE_WORK_AND_CASE_RATIO_3_TO_4:
            case ITEM_TYPE_WORK_AND_CASE_RATIO_4_TO_3:
                FinderRecommendWorkAndCaseViewHolder workAndCaseViewHolder = new
                        FinderRecommendWorkAndCaseViewHolder(
                        inflater.inflate(R.layout.finder_recommend_work_and_case_list_item,
                                parent,
                                false),
                        getWorkAndCaseItemStyle(viewType));
                workAndCaseViewHolder.setOnGetSimilarListener(onGetSimilarListener);
                return workAndCaseViewHolder;
            case ITEM_TYPE_NOTE_RATIO_1_TO_1:
            case ITEM_TYPE_NOTE_RATIO_3_TO_4:
            case ITEM_TYPE_NOTE_RATIO_4_TO_3:
                CommonNoteViewHolder noteViewHolder = new CommonNoteViewHolder(inflater.inflate(R
                                .layout.common_note_list_item___note,
                        parent,
                        false), getNoteItemStyle(viewType));
                noteViewHolder.setOnGetSimilarListener(onGetSimilarListener);
                noteViewHolder.setOnItemClickListener(new OnItemClickListener<Note>() {
                    @Override
                    public void onItemClick(int position, Note note) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position, note);
                        }
                    }
                });
                return noteViewHolder;
            default:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout.empty_place_holder___cm,
                        parent,
                        false));
        }
    }

    /**
     * 婚品item的封面图片style
     *
     * @param viewType
     * @return
     */
    private int getProductItemStyle(int viewType) {
        switch (viewType) {
            case ITEM_TYPE_PRODUCT_RATIO_2_TO_3:
                return FinderRecommendProductViewHolder.STYLE_RATIO_2_TO_3;
            default:
                return FinderRecommendProductViewHolder.STYLE_RATIO_1_TO_1;
        }
    }

    /**
     * 套餐案例item的封面图片style
     *
     * @param viewType
     * @return
     */
    private int getWorkAndCaseItemStyle(int viewType) {
        switch (viewType) {
            case ITEM_TYPE_WORK_AND_CASE_RATIO_3_TO_4:
                return FinderRecommendWorkAndCaseViewHolder.STYLE_RATIO_3_TO_4;
            case ITEM_TYPE_WORK_AND_CASE_RATIO_4_TO_3:
                return FinderRecommendWorkAndCaseViewHolder.STYLE_RATIO_4_TO_3;
            default:
                return FinderRecommendWorkAndCaseViewHolder.STYLE_RATIO_1_TO_1;
        }
    }

    /**
     * 笔记item的封面图片style
     *
     * @param viewType
     * @return
     */
    private int getNoteItemStyle(int viewType) {
        switch (viewType) {
            case ITEM_TYPE_NOTE_RATIO_3_TO_4:
                return CommonNoteViewHolder.STYLE_RATIO_3_TO_4;
            case ITEM_TYPE_NOTE_RATIO_4_TO_3:
                return CommonNoteViewHolder.STYLE_RATIO_4_TO_3;
            default:
                return CommonNoteViewHolder.STYLE_RATIO_1_TO_1;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_TYPE_EMPTY) {
            return;
        }
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    ((StaggeredGridLayoutManager.LayoutParams) params).setFullSpan(true);
                }
                break;
            default:
                Object obj = getItem(position);
                Object o;
                boolean isShowSimilarIcon = false;
                boolean isShowRelevantHint = false;
                if (obj instanceof FinderFeed) {
                    FinderFeed finderFeed = (FinderFeed) obj;
                    o = finderFeed.getEntityObj();
                    isShowSimilarIcon = finderFeed.isShowSimilarIcon();
                    isShowRelevantHint = finderFeed.isShowRelevantHint();
                } else if (obj instanceof CPMFeed) {
                    CPMFeed cpmFeed = (CPMFeed) obj;
                    isShowSimilarIcon = cpmFeed.isShowSimilarIcon();
                    o = cpmFeed.getEntityObj();
                } else {
                    o = obj;
                }
                holder.setView(context, o, position, viewType);

                //商家cell
                if (holder instanceof FinderRecommendMerchantViewHolder) {
                    FinderRecommendMerchantViewHolder merchantViewHolder =
                            (FinderRecommendMerchantViewHolder) holder;
                    merchantViewHolder.setShowSimilarIcon(isShowSimilarIcon);
                    merchantViewHolder.setShowRelevantHint(isShowRelevantHint);
                }
                //婚品cell
                else if (holder instanceof FinderRecommendProductViewHolder) {
                    FinderRecommendProductViewHolder productViewHolder =
                            (FinderRecommendProductViewHolder) holder;
                    productViewHolder.setShowSimilarIcon(isShowSimilarIcon);
                    productViewHolder.setShowRelevantHint(isShowRelevantHint);
                }
                //套餐/案例cell
                else if (holder instanceof FinderRecommendWorkAndCaseViewHolder) {
                    FinderRecommendWorkAndCaseViewHolder workAndCaseViewHolder =
                            (FinderRecommendWorkAndCaseViewHolder) holder;
                    workAndCaseViewHolder.setShowSimilarIcon(isShowSimilarIcon);
                    workAndCaseViewHolder.setShowRelevantHint(isShowRelevantHint);
                }
                //笔记cell
                else if (holder instanceof CommonNoteViewHolder) {
                    CommonNoteViewHolder noteViewHolder = (CommonNoteViewHolder) holder;
                    noteViewHolder.setShowSimilarIcon(isShowSimilarIcon);
                    setShowNoteClickHint(noteViewHolder, position);
                    setShowSimilarClickHint(noteViewHolder, position);
                }
                break;
        }
    }

    /**
     * 设置笔记详情点击的提示
     *
     * @param noteViewHolder
     * @param position
     */
    private void setShowNoteClickHint(CommonNoteViewHolder noteViewHolder, int position) {
        if (!isShowedNoteClickHint && isCanShowNoteClickHint && noteClickHintPosition == -1) {
            noteClickHintPosition = position;
        }
        if (noteClickHintPosition == position) {
            isShowedNoteClickHint = true;
        }
        noteViewHolder.setShowNoteClickHint(noteClickHintPosition == position);
    }

    /**
     * 设置显示找相似的提示
     *
     * @param noteViewHolder
     * @param position
     */
    private void setShowSimilarClickHint(CommonNoteViewHolder noteViewHolder, int position) {
        if (!isShowedSimilarClickHint && isCanShowSimilarClickHint && similarClickHintPosition ==
                -1) {
            similarClickHintPosition = position;
        }
        if (similarClickHintPosition == position) {
            isShowedSimilarClickHint = true;
        }
        noteViewHolder.setShowSimilarClickHint(similarClickHintPosition == position);
    }

    /**
     * 隐藏点击进入笔记详情的提示
     *
     * @param
     */
    public void hideNoteClickHint() {
        if (!FinderPrefUtil.getInstance(context)
                .isNoteClickHintClicked()) {
            FinderPrefUtil.getInstance(context)
                    .setNoteClickHintClicked(true);
        }
        if (isCanShowNoteClickHint && noteClickHintPosition > -1) {
            isCanShowNoteClickHint = false;
            notifyItemChanged(noteClickHintPosition);
            noteClickHintPosition = -1;
        }
    }

    /**
     * 隐藏找相似的提示
     */
    public void hideSimilarClickHint() {
        if (!FinderPrefUtil.getInstance(context)
                .isSimilarClickHintClicked()) {
            FinderPrefUtil.getInstance(context)
                    .setSimilarClickHintClicked(true);
        }
        if (isCanShowSimilarClickHint && similarClickHintPosition > -1) {
            isCanShowSimilarClickHint = false;
            notifyItemChanged(similarClickHintPosition);
            similarClickHintPosition = -1;
        }
    }

}