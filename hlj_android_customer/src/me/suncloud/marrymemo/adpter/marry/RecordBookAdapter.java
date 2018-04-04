package me.suncloud.marrymemo.adpter.marry;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.cardview.CardView;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.marry.RecordBook;


/**
 * Created by hua_rong on 2017/11/3 记账
 */

public class RecordBookAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private List<RecordBook> currentBookList;
    private Context context;
    private LayoutInflater inflater;
    private int fatherPosition;
    private int recyclerType;
    public static final int PARENT = 1;
    public static final int CHILD = 2;
    private boolean expand;

    public void setFatherPosition(int fatherPosition) {
        this.fatherPosition = fatherPosition;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public RecordBookAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 设置当前适配器的type 第一级还是第二级
     *
     * @param recyclerType PARENT or CHILD
     */
    public void setRecordBookList(
            List<RecordBook> currentBookList, int recyclerType) {
        this.currentBookList = currentBookList;
        this.recyclerType = recyclerType;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.record_book_item, parent, false);
        return new RecordBookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof RecordBookViewHolder) {
            RecordBookViewHolder viewHolder = (RecordBookViewHolder) holder;
            viewHolder.setView(context, getItem(position), position, getItemViewType(position));
        }
    }

    public RecordBook getItem(int position) {
        return currentBookList.get(position);
    }


    @Override
    public int getItemCount() {
        return currentBookList == null ? 0 : currentBookList.size();
    }


    public class RecordBookViewHolder extends BaseViewHolder<RecordBook> {

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.iv_cover)
        RoundedImageView ivCover;
        @BindView(R.id.rl_item)
        RelativeLayout rlItem;
        @BindView(R.id.iv_more)
        ImageView ivMore;
        @BindView(R.id.tv_child_name)
        TextView tvChildName;
        @BindView(R.id.ll_parent)
        LinearLayout llParent;
        @BindView(R.id.iv_indicator)
        ImageView ivIndicator;
        @BindView(R.id.card_view)
        CardView cardView;

        private int width;

        public RecordBookViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            width = CommonUtil.dp2px(itemView.getContext(), 44);
        }

        @Override
        protected void setViewData(
                Context context, RecordBook recordBook, final int position, int viewType) {
            int deviceWidth = CommonUtil.getDeviceSize(context).x;
            int dp_16 = CommonUtil.dp2px(context, 16);
            boolean isSelect = recordBook.isSelect();
            switch (recyclerType) {
                case PARENT:
                    rlItem.getLayoutParams().width = Math.round((deviceWidth - dp_16) / 5);
                    llParent.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(ImagePath.buildPath(isSelect ? recordBook.getSelectedImagePath
                                    () : recordBook.getImagePath())
                                    .width(width)
                                    .height(width)
                                    .cropPath())
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(
                                        Drawable resource,
                                        Transition<? super Drawable> transition) {
                                    ivCover.setImageDrawable(resource);
                                }
                            });
                    tvTitle.setText(recordBook.getTitle());
                    if (CommonUtil.isCollectionEmpty(getItem().getChildren())) {
                        ivMore.setVisibility(View.GONE);
                    } else {
                        ivMore.setVisibility(View.VISIBLE);
                        ivMore.setImageResource(isSelect ? R.drawable
                                .icon_marry_book_more_selected : R.drawable
                                .icon_marry_book_more_unselected);
                    }
                    ivIndicator.setVisibility(expand && isSelect ? View.VISIBLE : View.GONE);
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) {
                                if (!CommonUtil.isCollectionEmpty(getItem().getChildren())) {
                                    listener.onItemClick(getItem().getChildren(),
                                            getAdapterPosition());
                                } else {
                                    listener.onItemSelect(getItem(),
                                            fatherPosition,
                                            recyclerType,
                                            position);
                                }
                            }
                        }
                    });
                    break;
                case CHILD:
                    llParent.setVisibility(View.GONE);
                    tvChildName.setText(recordBook.getTitle());
                    cardView.setVisibility(View.VISIBLE);
                    tvChildName.setTextColor(isSelect ? Color.parseColor("#ffa400") :
                            ContextCompat.getColor(
                            context,
                            R.color.colorGray));
                    tvChildName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) {
                                for (RecordBook book : currentBookList) {
                                    book.setSelect(false);
                                }
                                getItem().setSelect(true);
                                listener.onItemSelect(getItem(),
                                        fatherPosition,
                                        recyclerType,
                                        position);
                            }
                        }
                    });
                    break;
            }
        }
    }

    public void setRecordBookListener(OnRecordBookListener listener) {
        this.listener = listener;
    }

    private OnRecordBookListener listener;

    public interface OnRecordBookListener {

        void onItemClick(List<RecordBook> recordBooks, int position);

        void onItemSelect(
                RecordBook recordBook, int fatherPosition, int recyclerType, int position);

    }

}
