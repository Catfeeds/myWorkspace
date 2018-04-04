package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

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
 * Created by Suncloud on 2016/5/20.
 */

public class PageV2DraggableAdapter extends RecyclerView.Adapter<PageV2DraggableAdapter
        .MyViewHolder> implements DraggableItemAdapter<PageV2DraggableAdapter.MyViewHolder> {

    private List<CardPageV2> pages;
    private CardPageV2 frontPage;
    private CardPageV2 speechPage;
    private int width;
    private int height;
    private OnPageActionListener onPageActionListener;

    public PageV2DraggableAdapter(CardV2 card, int width, int height) {
        this.frontPage = card.getFrontPage();
        this.speechPage = card.getSpeechPage();
        this.pages = new ArrayList<>(card.getPages());
        this.width = width;
        this.height = height;
        setHasStableIds(true);
    }

    public void removePage(CardPageV2 page) {
        int index = pages.indexOf(page);
        if (index >= 0) {
            pages.remove(index);
            if (frontPage != null) {
                index++;
            }
            notifyItemRemoved(index);
        }
    }

    public void hidePageChanged() {
        if (speechPage != null) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public List<CardPageV2> getPages() {
        return pages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(View.inflate(parent.getContext(), R.layout.page_v2_sort_item,
                null), width, height);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CardPageV2 cardPage = getItem(position);
        holder.pageLayout.setTag(cardPage);
        Context context = holder.ivCover.getContext();
        String key = CardResourceUtil.getInstance().getPageMapKey(context, cardPage.getId());
        File file = FileUtil.createPageFile(context, cardPage.getCardPageKey());
        if (!cardPage.getCardPageKey().equals(key) && !JSONUtil.isEmpty(key)) {
            FileUtil.deleteFile(FileUtil.createPageFile(context, key));
        }
        if (file != null && file.exists() && file.length() > 0) {
            ImageLoadUtil.loadImageViewWithoutTransitionNoCache(context,
                    file.getAbsolutePath(),holder.ivCover);
        } else {
            holder.ivCover.setImageBitmap(null);
            new DrawPageTask(context, cardPage, new OnHttpRequestListener() {
                @Override
                public void onRequestCompleted(Object obj) {
                    Context context = holder.ivCover.getContext();
                    if (context != null && context instanceof Activity && !((Activity)
                            context).isFinishing() && holder.pageLayout.getTag() == cardPage) {
                        ImageLoadUtil.loadImageViewWithoutTransitionNoCache(context,
                                (String) obj,holder.ivCover);
                    }
                }

                @Override
                public void onRequestFailed(Object obj) {

                }
            }).executeOnExecutor(Constants.THEADPOOL);
        }
        int dragState = holder.getDragStateFlags();
        if (((dragState & DraggableItemConstants.STATE_FLAG_IS_UPDATED) != 0)) {
            if ((dragState & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                holder.pageLayout.setAlpha(0.7f);
                holder.pageLayout.setScaleX(1.1f);
                holder.pageLayout.setScaleY(1.1f);
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnHide.setVisibility(View.GONE);
                holder.frontHintView.setVisibility(View.GONE);
            } else {
                holder.pageLayout.setAlpha(1f);
                holder.pageLayout.setScaleX(1f);
                holder.pageLayout.setScaleY(1f);
                holder.btnDelete.setVisibility(cardPage == speechPage || cardPage == frontPage ?
                        View
                                .GONE : View.VISIBLE);
                holder.frontHintView.setVisibility(cardPage == frontPage ? View.VISIBLE : View
                        .GONE);
                if (cardPage == speechPage) {
                    holder.btnHide.setVisibility(View.VISIBLE);
                    holder.btnHide.setImageResource(cardPage.isHidden() ? R.drawable.icon_text_show_primary :
                            R.drawable.icon_text_hide_primary);
                } else {
                    holder.btnHide.setVisibility(View.GONE);
                }
            }
        } else {
            holder.btnDelete.setVisibility(cardPage == speechPage || cardPage == frontPage ? View
                    .GONE : View.VISIBLE);
            holder.frontHintView.setVisibility(cardPage == frontPage ? View.VISIBLE : View.GONE);
            if (cardPage == speechPage) {
                holder.btnHide.setVisibility(View.VISIBLE);
                holder.btnHide.setImageResource(cardPage.isHidden() ? R.drawable.icon_text_show_primary :
                        R.drawable.icon_text_hide_primary);
            } else {
                holder.btnHide.setVisibility(View.GONE);
            }
        }
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
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public CardPageV2 getItem(int position) {
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


    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        CardPageV2 page = getItem(position);
        return !page.isFront() && !page.isSpeech();
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        return new ItemDraggableRange(frontPage != null ? 1 : 0, getItemCount() - (speechPage !=
                null ? 2 : 1));
    }

    private int getPagePosition(int position) {
        if (frontPage != null) {
            return position - 1;
        }
        return position;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }
        CardPageV2 page = pages.remove(getPagePosition(fromPosition));
        pages.add(getPagePosition(toPosition), page);
        notifyItemMoved(fromPosition, toPosition);
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

    class MyViewHolder extends AbstractDraggableItemViewHolder {
        ImageView ivCover;
        ImageView btnHide;
        View btnDelete;
        View frontHintView;
        View pageLayout;

        MyViewHolder(View v, int width, int height) {
            super(v);
            ivCover = (ImageView) v.findViewById(R.id.iv_cover);
            btnHide = (ImageView) v.findViewById(R.id.btn_hide);
            btnDelete = v.findViewById(R.id.btn_delete);
            frontHintView = v.findViewById(R.id.front_hint);
            pageLayout = v.findViewById(R.id.page_layout);
            pageLayout.getLayoutParams().width = width;
            pageLayout.getLayoutParams().height = height;
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CardPageV2 page = pages.get(getPagePosition(getAdapterPosition()));
                    if (onPageActionListener != null) {
                        page.setDelete(true);
                        onPageActionListener.upLoadItem(page);
                    } else {
                        pages.remove(page);
                        notifyItemRemoved(getAdapterPosition());
                    }
                }
            });
            btnHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speechPage.setHidden(!speechPage.isHidden());
                    if (onPageActionListener != null) {
                        onPageActionListener.upLoadItem(speechPage);
                    } else {
                        notifyItemChanged(getAdapterPosition());
                    }
                }
            });
        }
    }


    public interface OnPageActionListener {

        void upLoadItem(CardPageV2 cardPage);
    }
}
