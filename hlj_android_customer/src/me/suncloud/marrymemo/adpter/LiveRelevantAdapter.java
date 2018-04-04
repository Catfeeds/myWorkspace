package me.suncloud.marrymemo.adpter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.BigEventViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.MerchantAnswerViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.MerchantBriefInfoViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallWorkViewHolder;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljlivelibrary.models.LiveMeasures;
import com.hunliji.hljlivelibrary.models.LiveRelevantWrapper;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.ProductMerchantActivity;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.community.CommunityThreadDetailActivity;
import me.suncloud.marrymemo.view.event.EventDetailActivity;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import me.suncloud.marrymemo.view.product.ShopProductDetailActivity;

/**
 * Created by mo_yu on 2016/10/24.直播详情相关列表Adapter
 */

public class LiveRelevantAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private ArrayList<LiveRelevantWrapper> dataList;
    private View footerView;
    private View headerView;
    private LayoutInflater inflater;
    private LiveMeasures measures;
    private static final int ITEM_TYPE_FOOTER = 1;
    private static final int ITEM_TYPE_HEADER = 2;
    private static final int ITEM_TYPE_MERCHANT = 3;
    private static final int ITEM_TYPE_THREAD = 4;
    private static final int ITEM_TYPE_QA = 5;
    private static final int ITEM_TYPE_WORK = 6;
    private static final int ITEM_TYPE_PRODUCT = 7;
    private static final int ITEM_TYPE_EVENT = 8;
    private static final int ITEM_TYPE_EMPTY = 9;

    public LiveRelevantAdapter(Context context, ArrayList<LiveRelevantWrapper> list) {
        this.context = context;
        this.dataList = list;
        this.inflater = LayoutInflater.from(context);
        this.measures = new LiveMeasures(context.getResources()
                .getDisplayMetrics(), CommonUtil.getDeviceSize(context));
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setData(ArrayList<LiveRelevantWrapper> list) {
        if (list != null) {
            int oldSize = this.dataList.size();
            int size = list.size();
            this.dataList.clear();
            this.dataList.addAll(list);
            if (Math.min(oldSize, size) > 0) {
                notifyItemRangeChanged(0, Math.min(oldSize, size) - 1);
            }
            if (oldSize > size) {
                notifyItemRangeRemoved(size, oldSize - size - 1);
            } else if (oldSize < size) {
                notifyItemRangeInserted(oldSize, size - oldSize - 1);
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_MERCHANT:
                MerchantBriefInfoViewHolder merchantBriefInfoViewHolder = new
                        MerchantBriefInfoViewHolder(
                        inflater.inflate(R.layout.merchant_brief_info_list_item___cv,
                                parent,
                                false));
                merchantBriefInfoViewHolder.setOnItemClickListener(new OnItemClickListener<Merchant>() {
                    @Override
                    public void onItemClick(int position, Merchant merchant) {
                        if (merchant != null && merchant.getId() > 0) {
                            Intent intent = new Intent(context,
                                    merchant.getShopType() == Merchant.SHOP_TYPE_PRODUCT ?
                                            ProductMerchantActivity.class :
                                            MerchantDetailActivity.class);
                            intent.putExtra("id", merchant.getId());
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                return merchantBriefInfoViewHolder;
            case ITEM_TYPE_THREAD:
                return new ThreadViewHolder(inflater.inflate(R.layout.live_thread_list_item___live,
                        parent,
                        false));
            case ITEM_TYPE_QA:
                MerchantAnswerViewHolder answerViewHolder = new MerchantAnswerViewHolder(inflater
                        .inflate(
                        R.layout.merchant_answer_item___cv,
                        parent,
                        false));
                answerViewHolder.setOnItemClickListener(new OnItemClickListener<Answer>() {
                    @Override
                    public void onItemClick(int position, Answer answer) {
                        if (answer != null && answer.getId() > 0) {
                            Intent intent = new Intent(context, QuestionDetailActivity.class);
                            intent.putExtra("questionId",
                                    answer.getQuestion()
                                            .getId());
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                return answerViewHolder;
            case ITEM_TYPE_WORK:
                SmallWorkViewHolder workHolder = new SmallWorkViewHolder(inflater.inflate(R
                                .layout.small_common_work_item___cv,
                        parent,
                        false));
                workHolder.setStyle(SmallWorkViewHolder.STYLE_MERCHANT_HOME_PAGE);
                workHolder.setOnItemClickListener(new OnItemClickListener<Work>() {
                    @Override
                    public void onItemClick(int position, Work work) {
                        if (work != null && work.getId() > 0) {
                            Intent intent = new Intent(context, WorkActivity.class);
                            intent.putExtra("id", work.getId());
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                return workHolder;
            case ITEM_TYPE_PRODUCT:
                return new ProductViewHolder(inflater.inflate(R.layout
                                .live_product_list_item___live,
                        parent,
                        false));
            case ITEM_TYPE_EVENT:
                BigEventViewHolder eventViewHolder = new BigEventViewHolder(inflater.inflate(R
                                .layout.big_event_list_item___cv,
                        parent,
                        false), BigEventViewHolder.STYLE_LIVE);
                eventViewHolder.setOnItemClickListener(new OnItemClickListener<EventInfo>() {
                    @Override
                    public void onItemClick(int position, EventInfo eventInfo) {
                        if (eventInfo != null && eventInfo.getId() > 0) {
                            Intent intent = new Intent(context, EventDetailActivity.class);
                            intent.putExtra("id", eventInfo.getId());
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                return eventViewHolder;
            default:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout.empty_place_holder___cm,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ExtraBaseViewHolder) {
            return;
        }
        int currentPosition = position;
        int itemPosition = 0;//info列表中item的真实位置
        int listCount = 0;
        if (headerView != null) {
            currentPosition = currentPosition - 1;
        }
        for (int i = 0; i < dataList.size(); i++) {
            listCount = listCount + dataList.get(i)
                    .getInfoList()
                    .size();
            if (currentPosition < listCount) {
                itemPosition = currentPosition - (listCount - dataList.get(i)
                        .getInfoList()
                        .size());
                currentPosition = i;
                break;
            }
        }
        int viewType = getItemViewType(position);
        int size = dataList.get(currentPosition)
                .getInfoList()
                .size();
        holder.setView(context,
                dataList.get(currentPosition)
                        .getInfoList()
                        .get(itemPosition),
                itemPosition,
                viewType);
        switch (viewType) {
            case ITEM_TYPE_MERCHANT:
                MerchantBriefInfoViewHolder merchantBriefInfoViewHolder =
                        (MerchantBriefInfoViewHolder) holder;
                merchantBriefInfoViewHolder.setShowMerchantHeaderView(itemPosition == 0);
                merchantBriefInfoViewHolder.setMerchantHeaderTitle(context.getString(R.string
                        .label_relevant_merchant___cv));
                if (itemPosition == size - 1) {
                    merchantBriefInfoViewHolder.setShowBottomThickLineView(true);
                } else {
                    merchantBriefInfoViewHolder.setShowBottomThinLineView(true);
                }
                break;
            case ITEM_TYPE_THREAD:
                ThreadViewHolder threadViewHolder = (ThreadViewHolder) holder;
                threadViewHolder.setShowThreadHeaderView(itemPosition == 0);
                threadViewHolder.setThreadHeaderTitle(context.getString(R.string
                        .label_relevant_thread___cv));
                if (itemPosition == size - 1) {
                    threadViewHolder.setShowBottomThickLineView(true);
                    threadViewHolder.setItemBottomMargin(CommonUtil.dp2px(context, 6));
                } else {
                    threadViewHolder.setShowBottomThinLineView(true);
                    threadViewHolder.setItemBottomMargin(0);
                }
                break;
            case ITEM_TYPE_QA:
                MerchantAnswerViewHolder answerViewHolder = (MerchantAnswerViewHolder) holder;
                answerViewHolder.setShowAnswerHeaderView(itemPosition == 0);
                answerViewHolder.setAnswerHeaderTitle(context.getString(R.string
                        .label_relevant_answer___cv));
                if (itemPosition == size - 1) {
                    answerViewHolder.setShowBottomThickLineView(true);
                } else {
                    answerViewHolder.setShowBottomThinLineView(true);
                }
                break;
            case ITEM_TYPE_WORK:
                SmallWorkViewHolder workViewHolder = (SmallWorkViewHolder) holder;
                workViewHolder.setShowWorkHeaderView(itemPosition == 0);
                workViewHolder.setWorkHeaderTitle(context.getString(R.string
                        .label_relevant_work___cv));
                if (itemPosition == size - 1) {
                    workViewHolder.setItemBottomMargin(CommonUtil.dp2px(context, 6));
                    workViewHolder.setShowBottomThickLineView(true);
                } else {
                    workViewHolder.setItemBottomMargin(0);
                    workViewHolder.setShowBottomThinLineView(true);
                }
                break;
            case ITEM_TYPE_PRODUCT:
                ProductViewHolder productViewHolder = (ProductViewHolder) holder;
                productViewHolder.setShowProductHeaderView(itemPosition == 0);
                productViewHolder.setProductHeaderTitle(context.getString(R.string
                        .label_relevant_product___cv));
                if (itemPosition == size - 1) {
                    productViewHolder.setShowBottomThickLineView(true);
                    productViewHolder.setItemBottomMargin(CommonUtil.dp2px(context, 6));
                } else {
                    productViewHolder.setShowBottomThinLineView(true);
                    productViewHolder.setItemBottomMargin(0);
                }
                break;
            case ITEM_TYPE_EVENT:
                BigEventViewHolder eventViewHolder = (BigEventViewHolder) holder;
                eventViewHolder.setShowEventHeaderView(itemPosition == 0);
                eventViewHolder.setEventHeaderTitle(context.getString(R.string
                        .label_relevant_event___cv));
                if (itemPosition == size - 1) {
                    eventViewHolder.setShowBottomThickLineView(true);
                } else {
                    eventViewHolder.setShowBottomThinLineView(true);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        int listSize = 0;
        for (int i = 0; i < dataList.size(); i++) {
            listSize = listSize + dataList.get(i)
                    .getInfoList()
                    .size();
        }
        return listSize + (footerView == null ? 0 : 1) + (headerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            int currentPosition = position;
            if (headerView != null) {
                currentPosition = currentPosition - 1;
            }
            int listCount = 0;
            /**
             * 两个列表嵌套，listCount为列表item的个数之和
             * 当前位置比listCount小时，表示当前位置在dataList的第i个子项中
             */
            for (int i = 0; i < dataList.size(); i++) {
                listCount = listCount + dataList.get(i)
                        .getInfoList()
                        .size();
                if (currentPosition < listCount) {
                    currentPosition = i;
                    break;
                }
            }
            switch (dataList.get(currentPosition)
                    .getType()) {
                case 1:
                    return ITEM_TYPE_MERCHANT;
                case 2:
                    return ITEM_TYPE_THREAD;
                case 3:
                    return ITEM_TYPE_QA;
                case 4:
                    return ITEM_TYPE_WORK;
                case 5:
                    return ITEM_TYPE_PRODUCT;
                case 6:
                    return ITEM_TYPE_EVENT;
                default:
                    return ITEM_TYPE_EMPTY;
            }

        }
    }

    public class ThreadViewHolder extends BaseViewHolder<CommunityThread> {
        @BindView(R.id.thread_header_view)
        View threadHeaderView;
        @BindView(R.id.tv_thread_header_title)
        TextView tvThreadHeaderTitle;
        @BindView(R.id.thread_view)
        RelativeLayout threadView;
        @BindView(R.id.iv_live_thread)
        ImageView ivLiveThread;
        @BindView(R.id.tv_live_thread)
        TextView tvLiveThread;
        @BindView(R.id.bottom_thin_line_layout)
        View bottomThinLineLayout;
        @BindView(R.id.bottom_thick_line_layout)
        View bottomThickLineLayout;

        ThreadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            threadView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommunityThread thread = getItem();
                    if (thread != null && thread.getId() > 0) {
                        Intent intent = new Intent(context, CommunityThreadDetailActivity.class);
                        intent.putExtra("id", thread.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, final CommunityThread thread, int position, int currentPosition) {
            if (thread == null) {
                return;
            }
            String url = null;
            if (!CommonUtil.isCollectionEmpty(thread.getShowPhotos())) {
                url = ImagePath.buildPath(thread.getShowPhotos()
                        .get(0)
                        .getImagePath())
                        .width(measures.threadWidth)
                        .path();
            }
            if (!TextUtils.isEmpty(url)) {
                ivLiveThread.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(ivLiveThread);
            } else {
                ivLiveThread.setVisibility(View.GONE);
            }
            tvLiveThread.setText(EmojiUtil.parseEmojiByText2(mContext,
                    thread.getShowTitle(),
                    measures.faceSize));
        }

        private void setShowThreadHeaderView(boolean showThreadHeaderView) {
            threadHeaderView.setVisibility(showThreadHeaderView ? View.VISIBLE : View.GONE);
        }

        private void setThreadHeaderTitle(String threadHeaderTitle) {
            tvThreadHeaderTitle.setText(threadHeaderTitle);
        }

        private void setShowBottomThinLineView(boolean showBottomThinLineView) {
            bottomThinLineLayout.setVisibility(showBottomThinLineView ? View.VISIBLE : View.GONE);
            bottomThickLineLayout.setVisibility(View.GONE);
        }

        private void setShowBottomThickLineView(boolean showBottomThickLineView) {
            bottomThinLineLayout.setVisibility(View.GONE);
            bottomThickLineLayout.setVisibility(showBottomThickLineView ? View.VISIBLE : View.GONE);
        }

        private void setItemBottomMargin(int bottomMargin) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) threadView
                    .getLayoutParams();
            params.bottomMargin = bottomMargin;
        }
    }

    public class ProductViewHolder extends BaseViewHolder<ShopProduct> {
        @BindView(R.id.product_header_view)
        View productHeaderView;
        @BindView(R.id.tv_product_header_title)
        TextView tvProductHeaderTitle;
        @BindView(R.id.product_view)
        View productView;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_work_title)
        TextView tvWorkTitle;
        @BindView(R.id.tv_wedding_price)
        TextView tvWeddingPrice;
        @BindView(R.id.tv_wedding_collect)
        TextView tvWeddingCollect;
        @BindView(R.id.bottom_thin_line_layout)
        View bottomThinLineLayout;
        @BindView(R.id.bottom_thick_line_layout)
        View bottomThickLineLayout;

        ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            productView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShopProduct product = getItem();
                    if (product != null && product.getId() > 0) {
                        Intent intent = new Intent(context, ShopProductDetailActivity.class);
                        intent.putExtra("id", product.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, final ShopProduct product, int position, int viewType) {
            if (product == null) {
                return;
            }
            String url = null;
            if (product.getCoverImage() != null) {
                url = ImageUtil.getImagePath(product.getCoverImage()
                        .getImagePath(), measures.workImgWidth);
            }
            if (!TextUtils.isEmpty(url)) {
                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(imgCover);
            } else {
                Glide.with(mContext)
                        .clear(imgCover);
                imgCover.setImageBitmap(null);
            }
            tvWorkTitle.setText(product.getTitle());
            tvWeddingPrice.setVisibility(View.VISIBLE);
            tvWeddingPrice.setText(CommonUtil.formatDouble2String(product.getShowPrice()));
            tvWeddingCollect.setText(mContext.getString(R.string.label_collect_count___live,
                    product.getCollectCount() + ""));
        }

        private void setShowProductHeaderView(boolean showProductHeaderView) {
            productHeaderView.setVisibility(showProductHeaderView ? View.VISIBLE : View.GONE);
        }

        private void setProductHeaderTitle(String productHeaderTitle) {
            tvProductHeaderTitle.setText(productHeaderTitle);
        }

        private void setShowBottomThinLineView(boolean showBottomThinLineView) {
            bottomThinLineLayout.setVisibility(showBottomThinLineView ? View.VISIBLE : View.GONE);
            bottomThickLineLayout.setVisibility(View.GONE);
        }

        private void setShowBottomThickLineView(boolean showBottomThickLineView) {
            bottomThinLineLayout.setVisibility(View.GONE);
            bottomThickLineLayout.setVisibility(showBottomThickLineView ? View.VISIBLE : View.GONE);
        }

        private void setItemBottomMargin(int bottomMargin) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) productView
                    .getLayoutParams();
            params.bottomMargin = bottomMargin;
        }
    }
}