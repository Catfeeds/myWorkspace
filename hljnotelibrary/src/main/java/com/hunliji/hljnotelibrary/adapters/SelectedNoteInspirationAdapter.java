package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.note.NoteInspiration;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 发布-已经选中的图片adapter
 * Created by chen_bin on 2017/6/26 0026.
 */
public class SelectedNoteInspirationAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View footerView;
    private List<NoteInspiration> inspirations;
    private int selectedPosition;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;

    public SelectedNoteInspirationAdapter(Context context, List<NoteInspiration> inspirations) {
        this.context = context;
        this.inspirations = inspirations;
        this.inflater = LayoutInflater.from(context);
    }

    public void setInspirations(List<NoteInspiration> inspirations) {
        this.inspirations.clear();
        if (!CommonUtil.isCollectionEmpty(inspirations)) {
            this.inspirations.addAll(inspirations);
        }
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + CommonUtil.getCollectionSize(inspirations);
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterViewCount() > 0 && position == getItemCount() - getFooterViewCount()) {
            return ITEM_TYPE_FOOTER;
        }
        return ITEM_TYPE_LIST;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new SelectedPhotoViewHolder(inflater.inflate(R.layout
                                .selected_note_inspiration_list_item___note,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                holder.setView(context, inspirations.get(position), position, 0);
                break;
        }
    }

    public class SelectedPhotoViewHolder extends BaseViewHolder<NoteInspiration> {
        @BindView(R2.id.img_cover)
        ImageView imgCover;
        @BindView(R2.id.border_view)
        View borderView;
        private int imageWidth;

        public SelectedPhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.imageWidth = CommonUtil.dp2px(itemView.getContext(), 32);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, NoteInspiration inspiration, int position, int viewType) {
            if (inspiration == null) {
                return;
            }
            String imagePath = inspiration.getNoteMedia()
                    .getPhoto()
                    .getImagePath();
            if (CommonUtil.isHttpUrl(imagePath)) {
                imagePath = ImagePath.buildPath(imagePath)
                        .width(imageWidth)
                        .cropPath();
            }
            Glide.with(mContext)
                    .load(imagePath)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image)
                            .dontAnimate())
                    .into(imgCover);
            borderView.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);
        }
    }
}
