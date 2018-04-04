package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.realm.WeddingPhotoItemDraft;
import me.suncloud.marrymemo.widget.community.WeddingGroupPhotosView;

/**
 * Created by mo_yu on 2017/5/3.发布晒婚纱照
 */

public class CreateWeddingPhotoAdapter extends RecyclerView
        .Adapter<BaseViewHolder<WeddingPhotoItemDraft>> {

    private static final int ITEM_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    private static final int FOOTER_TYPE = 3;

    private View headerView;
    private View footerView;
    private Context context;
    private List<WeddingPhotoItemDraft> list;
    public OnGroupItemClickListener onGroupItemClickListener;

    public CreateWeddingPhotoAdapter(Context context) {
        this.context = context;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setData(List<WeddingPhotoItemDraft> list) {
        this.list = list;
    }

    public void setOnGroupItemClickListener(OnGroupItemClickListener onGroupItemClickListener) {
        this.onGroupItemClickListener = onGroupItemClickListener;
    }

    public interface OnGroupItemClickListener {
        void onGroupClick(int groupIndex);
    }

    @Override
    public BaseViewHolder<WeddingPhotoItemDraft> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new WeddingPhotoViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.wedding_photo_group_edit_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<WeddingPhotoItemDraft> holder, int position) {
        if (holder instanceof WeddingPhotoViewHolder) {
            int currentPosition = (headerView == null ? position : position - 1);
            holder.setView(context,
                    list.get(currentPosition),
                    currentPosition,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0) + (headerView != null ? 1 : 0) + (footerView !=
                null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return HEADER_TYPE;
        } else if (footerView != null && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    class WeddingPhotoViewHolder extends BaseViewHolder<WeddingPhotoItemDraft> {
        @BindView(R.id.photos_view)
        WeddingGroupPhotosView photosView;
        @BindView(R.id.et_describe)
        EditText etDescribe;
        @BindView(R.id.action_modify_photo)
        View actionModifyPhoto;


        WeddingPhotoViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            etDescribe.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    WeddingPhotoItemDraft item = getItem();
                    boolean flag = view.getTag() == null || (int) view.getTag() ==
                            getAdapterPosition();
                    if (item != null && flag && etDescribe.hasFocus()) {
                        item.setDescription(s.toString());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext,
                final WeddingPhotoItemDraft item,
                final int position,
                int viewType) {
            itemView.setTag(getAdapterPosition());
            etDescribe.setText(item.getDescription());
            photosView.setPhotos(item.getPhotos(), new WeddingGroupPhotosView.OnPhotoClickListener() {
                @Override
                public void onPhotoClick(Photo photo, int photoP) {
                    if (onGroupItemClickListener != null) {
                        onGroupItemClickListener.onGroupClick(position);
                    }
                }
            });
        }
    }
}
