package com.hunliji.hljcardlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.hunliji.hljcardlibrary.R;
import com.hunliji.hljcardlibrary.R2;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcardlibrary.models.CardPage;
import com.hunliji.hljcardlibrary.models.Template;
import com.hunliji.hljcardlibrary.utils.PageImageUtil;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.FileUtil;
import com.makeramen.rounded.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hua_rong on 2017/6/16.
 */

public class CardDraggableAdapter extends RecyclerView.Adapter<CardDraggableAdapter
        .DraggableViewHolder> implements DraggableItemAdapter<CardDraggableAdapter
        .DraggableViewHolder> {


    private List<CardPage> pages;
    private CardPage frontPage;//首页 封面
    private CardPage speechPage;//宾客
    private int width;
    private int height;
    private OnPageActionListener onPageActionListener;
    private Context context;

    public CardDraggableAdapter(Context context, Card card, int width, int height) {
        this.context = context;
        this.frontPage = card.getFrontPage();
        this.speechPage = card.getSpeechPage();
        this.pages = new ArrayList<>(card.getPages());
        this.width = width;
        this.height = height;
        setHasStableIds(true);
        if (frontPage != null) {
            frontPage.setSelect(true);
        } else if (!CommonUtil.isCollectionEmpty(pages)) {
            CardPage cardPage = pages.get(0);
            if (cardPage != null) {
                cardPage.setSelect(true);
            }
        } else if (speechPage != null) {
            speechPage.setSelect(true);
        }
    }

    public void setCard(Card card) {
        this.frontPage = card.getFrontPage();
        this.speechPage = card.getSpeechPage();
        this.pages = new ArrayList<>(card.getPages());
        notifyDataSetChanged();
    }

    public void setCurrentPage(int pageIndex) {
        List<CardPage> allPages = new ArrayList<>();
        if (frontPage != null) {
            allPages.add(frontPage);
        }
        allPages.addAll(pages);
        if (speechPage != null) {
            allPages.add(speechPage);
        }
        for (CardPage cardPage : allPages) {
            boolean isSelect = allPages.indexOf(cardPage) == pageIndex;
            if (isSelect != cardPage.isSelect()) {
                notifyItemChanged(allPages.indexOf(cardPage));
                cardPage.setSelect(isSelect);
            }
        }
    }

    public int removePage(CardPage page) {
        int index = pages.indexOf(page);
        if (index >= 0) {
            pages.remove(index);
            if (page.isSelect()) {
                if (index > 0) {
                    pages.get(index - 1)
                            .setSelect(true);
                } else if (index == 0) {
                    if (frontPage != null) {
                        frontPage.setSelect(true);
                    } else if (speechPage != null) {
                        speechPage.setSelect(true);
                    }
                }
            }
            notifyDataSetChanged();
        }
        if (frontPage != null) {
            return index + 1;
        }
        return index;
    }

    public void hidePageChanged() {
        if (speechPage != null) {
            boolean hidden = speechPage.isHidden();
            boolean selected = speechPage.isSelect();
            if (hidden && selected) {
                if (pages != null && pages.size() > 0) {
                    pages.get(pages.size() - 1)
                            .setSelect(true);
                } else if (frontPage != null) {
                    frontPage.setSelect(true);
                }
            } else if (!hidden) {
                clearAllSelect();
                speechPage.setSelect(true);
            }
            notifyDataSetChanged();
        }
    }

    public List<CardPage> getPages() {
        return pages;
    }

    /**
     * 排序失败后复原
     *
     * @param cardPages 页面返回的最新数据
     */
    public void reSortPageOrder(List<CardPage> cardPages) {
        if (!CommonUtil.isCollectionEmpty(cardPages)) {
            for (CardPage page : pages) {
                if (page.isSelect()) {
                    for (CardPage cardPage : cardPages) {
                        if (page.getId() == cardPage.getId()) {
                            cardPage.setSelect(true);
                        }
                    }
                }
            }
            this.pages = cardPages;
            notifyDataSetChanged();
        }
    }

    @Override
    public DraggableViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.card_sort_item___card, parent, false);
        return new DraggableViewHolder(view, width, height);
    }

    /**
     * 1.默认pageId 设置选中状态
     * 2.隐藏的致宾客页不可点击
     * 3.删除其他页、隐藏致宾客页，若为当前页，改为选中前一页
     * 4.显示致宾客页-->选中致宾客页
     */
    @Override
    public void onBindViewHolder(final DraggableViewHolder holder, int position) {
        final CardPage cardPage = getItem(position);
        holder.setCardPage(cardPage);
        holder.pageLayout.setTag(cardPage);
        if (position == 0) {
            holder.tvTitle.setText(R.string.label_card_page_cover___card);
        } else if (position == getItemCount() - 1) {
            holder.tvTitle.setText(R.string.label_card_page_to_guest___card);
        } else {
            holder.tvTitle.setText(String.format("第%s页", position));
        }
        int dragState = holder.getDragStateFlags();
        if (((dragState & DraggableItemConstants.STATE_FLAG_IS_UPDATED) != 0)) {
            if ((dragState & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                holder.pageLayout.setAlpha(0.7f);
                holder.pageLayout.setScaleX(1.1f);
                holder.pageLayout.setScaleY(1.1f);
                holder.btnDelete.setVisibility(View.GONE);
                holder.tvHide.setVisibility(View.GONE);
                holder.viewSelect.setVisibility(View.GONE);
                holder.ivCover.setBackgroundResource(R.mipmap.icon_card_shadow___card);
            } else {
                showNormalItemView(holder, cardPage.isSelect(), cardPage);
            }
        } else {
            showNormalItemView(holder, cardPage.isSelect(), cardPage);
        }
        holder.rlPageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardPage != speechPage || !cardPage.isHidden()) {
                    if (!cardPage.isSelect()) {
                        clearAllSelect();
                        cardPage.setSelect(true);
                        if (onPageActionListener != null) {
                            onPageActionListener.onSelectPage(cardPage);
                        }
                        notifyDataSetChanged();
                    }
                }
            }
        });
        Template template = cardPage.getTemplate();
        if (template != null) {
            String coverPath = null;
            File pageFile = PageImageUtil.getPageThumbFile(context, cardPage.getId());
            if (pageFile == null || !pageFile.exists() || pageFile.length() == 0) {
                coverPath = template.getThumbPath();
            } else {
                coverPath = pageFile.getAbsolutePath();
            }
            if (!TextUtils.isEmpty(coverPath)) {
                Glide.with(context)
                        .load(coverPath)
                        .apply(new RequestOptions().dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(holder.ivCover);
            } else {
                Glide.with(context)
                        .clear(holder.ivCover);
                holder.ivCover.setImageBitmap(null);
            }
        }
    }

    private void clearAllSelect() {
        if (frontPage != null) {
            frontPage.setSelect(false);
        }
        if (speechPage != null) {
            speechPage.setSelect(false);
        }
        if (pages != null) {
            for (CardPage cardPage : pages) {
                cardPage.setSelect(false);
            }
        }
    }

    private void showNormalItemView(
            DraggableViewHolder holder, boolean selected, CardPage cardPage) {
        holder.pageLayout.setAlpha(1f);
        holder.pageLayout.setScaleX(1f);
        holder.pageLayout.setScaleY(1f);
        holder.btnDelete.setVisibility(cardPage == speechPage || cardPage == frontPage ? View
                .GONE : View.VISIBLE);
        if (cardPage == speechPage) {
            holder.tvHide.setVisibility(View.VISIBLE);
            boolean hidden = speechPage.isHidden();
            if (hidden) {
                holder.tvHide.setText(R.string.label_card_page_show___card);
                holder.viewSelect.setVisibility(View.GONE);
                holder.viewCover.setVisibility(View.VISIBLE);
                holder.tvHide.setBackgroundResource(R.drawable.sp_r9_color_accent_subdark);
            } else {
                holder.tvHide.setText(R.string.label_card_page_hide___card);
                holder.viewSelect.setVisibility(speechPage.isSelect() ? View.VISIBLE : View.GONE);
                holder.viewCover.setVisibility(View.GONE);
                holder.tvHide.setBackgroundResource(R.drawable.sp_r9_primary);
            }
        } else {
            holder.viewSelect.setVisibility(selected ? View.VISIBLE : View.GONE);
            holder.tvHide.setVisibility(View.GONE);
        }
        holder.ivCover.setBackgroundResource(R.drawable.sp_r2_image_background);
    }


    public CardPage getItem(int position) {
        if (frontPage != null) {
            position--;
        }
        if (position < 0) {
            return frontPage;
        } else if (position < pages.size()) {
            return pages.get(position);
        } else {
            return speechPage;
        }
    }

    private int getPagePosition(int position) {
        if (frontPage != null) {
            return position - 1;
        }
        return position;
    }

    @Override
    public int getItemCount() {
        int count = pages.size();
        if (frontPage != null) {
            count++;
        }
        if (speechPage != null) {
            count++;
        }
        return count;
    }

    @Override
    public boolean onCheckCanStartDrag(
            DraggableViewHolder holder, int position, int x, int y) {
        CardPage page = getItem(position);
        return page != frontPage && page != speechPage;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(
            DraggableViewHolder holder, int position) {
        return new ItemDraggableRange(frontPage != null ? 1 : 0,
                getItemCount() - (speechPage != null ? 2 : 1));
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        CardPage page = pages.remove(getPagePosition(fromPosition));
        pages.add(getPagePosition(toPosition), page);
        notifyItemMoved(fromPosition, toPosition);
        if (onPageActionListener != null) {
            onPageActionListener.onDraggedPage(pages, fromPosition, toPosition);
        }
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    public void setOnPageActionListener(OnPageActionListener onPageActionListener) {
        this.onPageActionListener = onPageActionListener;
    }

    public class DraggableViewHolder extends AbstractDraggableItemViewHolder {

        @BindView(R2.id.iv_cover)
        RoundedImageView ivCover;
        @BindView(R2.id.btn_delete)
        ImageView btnDelete;
        @BindView(R2.id.tv_hide)
        TextView tvHide;
        @BindView(R2.id.page_layout)
        RelativeLayout pageLayout;
        @BindView(R2.id.tv_title)
        TextView tvTitle;
        @BindView(R2.id.rl_page_select)
        RelativeLayout rlPageSelect;
        @BindView(R2.id.view_cover)
        View viewCover;
        @BindView(R2.id.view_select)
        View viewSelect;
        CardPage cardPage;

        public void setCardPage(CardPage cardPage) {
            this.cardPage = cardPage;
        }

        public DraggableViewHolder(View itemView, int width, int height) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            pageLayout.getLayoutParams().width = width;
            pageLayout.getLayoutParams().height = height;
        }


        @OnClick(R2.id.btn_delete)
        public void onDelete(View view) {
            if(cardPage==null){
                return;
            }
            if (onPageActionListener != null) {
                onPageActionListener.onDeletePage(cardPage);
            }
        }

        @OnClick(R2.id.tv_hide)
        public void onHide(View view) {
            speechPage.setHidden(!speechPage.isHidden());
            if (onPageActionListener != null) {
                onPageActionListener.onHidePage(speechPage);
            }
        }

    }


    public interface OnPageActionListener {

        void onDeletePage(CardPage cardPage);

        void onHidePage(CardPage cardPage);

        void onSelectPage(CardPage cardPage);

        void onDraggedPage(List<CardPage> pages, int fromPosition, int toPosition);
    }

}
