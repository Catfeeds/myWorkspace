package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.V2.CardPageV2;
import me.suncloud.marrymemo.model.V2.CardV2;
import me.suncloud.marrymemo.task.DrawPageTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;
import me.suncloud.marrymemo.util.CardResourceUtil;
import me.suncloud.marrymemo.util.FileUtil;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2016/5/24.
 */
public class PageV2Adapter extends RecyclerView.Adapter<BaseViewHolder<CardPageV2>> {

    private List<CardPageV2> pages;
    private Context mContext;
    private int width;
    private int height;
    private int spaceWidth;
    private OnPageItemClickListener onPageItemClickListener;
    private static final int ITEM_TYPE_PAGE = 1;
    private static final int ITEM_TYPE_SPACE = -1;

    public PageV2Adapter(
            Context mContext,
            int width,
            int height,
            int spaceWidth,
            OnPageItemClickListener onPageItemClickListener) {
        this.mContext = mContext;
        this.pages = new ArrayList<>();
        this.width = width;
        this.height = height;
        this.spaceWidth = spaceWidth;
        this.onPageItemClickListener = onPageItemClickListener;
    }

    public void setCard(CardV2 card) {
        pages.clear();
        if (card.getFrontPage() != null) {
            pages.add(card.getFrontPage());
        }
        this.pages.addAll(card.getPages());
        if (card.getPages()
                .size() < 10) {
            this.pages.add(null);
        }
        if (card.getSpeechPage() != null) {
            pages.add(card.getSpeechPage());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_SPACE;
        }
        position--;
        if (pages.size() > position) {
            return ITEM_TYPE_PAGE;
        }
        return ITEM_TYPE_SPACE;
    }

    @Override
    public BaseViewHolder<CardPageV2> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_PAGE:
                return new PageViewHolder(View.inflate(parent.getContext(),
                        R.layout.card_page_v2_item,
                        null), width);
            default:
                return new EmptyViewHolder(new Space(parent.getContext()));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<CardPageV2> holder, int position) {
        holder.setView(mContext, getItem(position), position, getItemViewType(position));
    }

    public CardPageV2 getItem(int position) {
        if (position == 0) {
            return null;
        }
        position--;
        if (pages.size() > position) {
            return pages.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (pages.size() > 0) {
            return pages.size() + 2;
        }
        return 0;
    }

    public class EmptyViewHolder extends BaseViewHolder<CardPageV2> {

        private EmptyViewHolder(View view) {
            super(view);
            view.setLayoutParams(new RecyclerView.LayoutParams(spaceWidth,1));
        }

        @Override
        protected void setViewData(Context mContext, CardPageV2 item, int position, int viewType) {

        }
    }


    public class PageViewHolder extends BaseViewHolder<CardPageV2> {

        private ImageView pageView;
        private View addLayout;
        private View pageLayout;
        private View iconEdit;
        private DrawPageTask drawPageTask;

        public PageViewHolder(View itemView, int width) {
            super(itemView);
            pageView = (ImageView) itemView.findViewById(R.id.page_cover);
            iconEdit = itemView.findViewById(R.id.icon_edit);
            addLayout = itemView.findViewById(R.id.add_layout);
            pageLayout = itemView.findViewById(R.id.page_layout);
            pageLayout.getLayoutParams().width = width;
            pageLayout.getLayoutParams().height = height;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getScaleX() < 0.9) {
                        return;
                    }
                    if (onPageItemClickListener != null) {
                        onPageItemClickListener.pageClick(getItem(), getAdapterPosition());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context mContext, final CardPageV2 cardPage, int position, int viewType) {
            iconEdit.setTag(cardPage);
            if (cardPage == null) {
                if (drawPageTask != null) {
                    drawPageTask.cancel(true);
                    drawPageTask = null;
                }
                pageView.setVisibility(View.GONE);
                iconEdit.setVisibility(View.GONE);
                addLayout.setVisibility(View.VISIBLE);
            } else {
                pageView.setVisibility(View.VISIBLE);
                iconEdit.setVisibility(cardPage.isSpeech() ? View.GONE : View.VISIBLE);
                addLayout.setVisibility(View.GONE);
                String key = CardResourceUtil.getInstance()
                        .getPageMapKey(mContext, cardPage.getId());
                File file = FileUtil.createPageFile(mContext, cardPage.getCardPageKey());
                if (!cardPage.getCardPageKey()
                        .equals(key) && !JSONUtil.isEmpty(key)) {
                    FileUtil.deleteFile(FileUtil.createPageFile(mContext, key));
                }
                if (file != null && file.exists() && file.length() > 0) {
                    if (drawPageTask != null) {
                        drawPageTask.cancel(true);
                        drawPageTask = null;
                    }
                    ImageLoadUtil.loadImageViewWithoutTransitionNoCache(mContext,
                            file.getAbsolutePath(),
                            pageView);
                } else {
                    pageView.setImageBitmap(null);
                    OnHttpRequestListener drawPageListener = new OnHttpRequestListener() {
                        @Override
                        public void onRequestCompleted(Object obj) {
                            if (mContext != null && mContext instanceof Activity && !((Activity)
                                    mContext).isFinishing() && iconEdit.getTag() == cardPage) {
                                ImageLoadUtil.loadImageViewWithoutTransitionNoCache(mContext,
                                        (String) obj,
                                        pageView);
                            }
                        }

                        @Override
                        public void onRequestFailed(Object obj) {

                        }
                    };
                    if (drawPageTask != null) {
                        if (!cardPage.getCardPageKey()
                                .equals(drawPageTask.getCardPageKey())) {
                            drawPageTask.cancel(true);
                            drawPageTask = null;
                        } else {
                            drawPageTask.setListener(drawPageListener);
                        }
                    }
                    if (drawPageTask == null) {
                        drawPageTask = new DrawPageTask(mContext, cardPage, drawPageListener);
                        drawPageTask.executeOnExecutor(Constants.THEADPOOL);
                    }
                }
            }
        }
    }


    public interface OnPageItemClickListener {

        void pageClick(CardPageV2 page, int position);
    }
}
