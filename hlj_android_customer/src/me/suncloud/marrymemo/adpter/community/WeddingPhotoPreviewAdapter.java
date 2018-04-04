package me.suncloud.marrymemo.adpter.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.communitythreads.WeddingPhotoGroup;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

import me.suncloud.marrymemo.model.realm.WeddingPhotoItemDraft;
import me.suncloud.marrymemo.view.wedding_dress.ViewLargeImageActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.community.WeddingGroupPhotosView;

/**
 * Created by luohanlin on 2017/5/4.
 */

public class WeddingPhotoPreviewAdapter extends RecyclerView
        .Adapter<BaseViewHolder<WeddingPhotoItemDraft>> {

    private final int MERCHANT_FOOTER = 0;
    private final int ITEM_TYPE = 1;
    private Context context;
    private ArrayList<WeddingPhotoItemDraft> groups;
    private View merchantFooterView;
    private CommunityThread thread;

    public WeddingPhotoPreviewAdapter(
            Context context, ArrayList<WeddingPhotoItemDraft> groups) {
        this.context = context;
        this.groups = groups;
    }

    public void setMerchantFooterView(View merchantFooterView) {
        this.merchantFooterView = merchantFooterView;
    }

    public void setThread(CommunityThread thread) {
        this.thread = thread;
    }

    @Override
    public BaseViewHolder<WeddingPhotoItemDraft> onCreateViewHolder(
            ViewGroup parent, int viewType) {
        switch (viewType) {
            case MERCHANT_FOOTER:
                return new ExtraBaseViewHolder(merchantFooterView);
            default:
                final LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.wedding_photo_group_recyclerview_item,
                        parent,
                        false);
                return new GroupViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<WeddingPhotoItemDraft> holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_TYPE) {
            holder.setView(context, getItem(position), position, viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (merchantFooterView != null && position == getItemCount() - 1) {
            return MERCHANT_FOOTER;
        } else {
            return ITEM_TYPE;
        }
    }

    public WeddingPhotoItemDraft getItem(int position) {
        return groups.get(position);
    }

    @Override
    public int getItemCount() {
        return (groups == null ? 0 : groups.size()) + (merchantFooterView == null ? 0 : 1);
    }

    class GroupViewHolder extends BaseViewHolder<WeddingPhotoItemDraft> {
        @BindView(R.id.photos_view)
        WeddingGroupPhotosView photosView;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_praise_hint)
        TextView tvPraiseHint;
        @BindView(R.id.tv_praise_count)
        TextView tvPraiseCount;
        @BindView(R.id.check_praised)
        CheckableLinearLayoutButton checkPraised;

        GroupViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            checkPraised.setClickable(false);
        }

        @Override
        protected void setViewData(
                Context mContext,
                final WeddingPhotoItemDraft item,
                final int position,
                int viewType) {
            if (TextUtils.isEmpty(item.getDescription())) {
                tvPraiseHint.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.GONE);
            } else {
                tvPraiseHint.setVisibility(View.INVISIBLE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(item.getDescription());
            }
            tvPraiseCount.setText(String.valueOf(1));
            photosView.setPhotos(item.getPhotos(),
                    !item.isCollapseViewOpened(),
                    new WeddingGroupPhotosView.OnPhotoClickListener() {
                        @Override
                        public void onPhotoClick(Photo photo, int photoP) {
                            if (thread != null) {
                                Intent intent = new Intent(context, ViewLargeImageActivity.class);
                                intent.putExtra("thread", thread);
                                intent.putExtra("item_position", position);
                                intent.putExtra("photo_position", photoP);
                                context.startActivity(intent);
                                ((Activity) context).overridePendingTransition(R.anim
                                                .slide_in_from_bottom,
                                        R.anim.activity_anim_default);
                            }
                        }
                    });
            photosView.setOnShowAllPhotoListener(new WeddingGroupPhotosView
                    .OnShowAllPhotoListener() {
                @Override
                public void onShowAllPhoto() {
                    item.setCollapseViewOpened(true);
                }
            });
        }
    }
}
