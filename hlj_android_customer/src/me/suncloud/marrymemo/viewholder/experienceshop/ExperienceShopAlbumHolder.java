package me.suncloud.marrymemo.viewholder.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.experience.ExperienceApi;
import me.suncloud.marrymemo.model.experience.ExperiencePhoto;
import me.suncloud.marrymemo.model.experience.ExperienceShop;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.experience.ExperienceShopActivity;
import me.suncloud.marrymemo.view.experience.ExperienceShopImageActivity;
import me.suncloud.marrymemo.view.experience.ExperienceShopPhotoActivity;
import rx.Observable;

/**
 * experience_shop_item_album
 * Created by jinxin on 2017/3/24 0024.
 */

public class ExperienceShopAlbumHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private Context mContext;
    private int padding;
    private int imgWidth;
    private SpaceItemDecoration itemDecoration;
    private EventItemAdapter eventItemAdapter;

    public ExperienceShopAlbumHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        padding = Util.dp2px(mContext, 8);
        imgWidth = Util.dp2px(mContext, 100);
        itemDecoration = new SpaceItemDecoration();
        eventItemAdapter = new EventItemAdapter();
    }

    public void setPhoto(List<ExperiencePhoto> atlas) {
        if (atlas == null) {
            return;
        }
        if (atlas != null && !atlas.isEmpty()) {
            recyclerView.removeItemDecoration(itemDecoration);
            recyclerView.addItemDecoration(itemDecoration);
            eventItemAdapter.setPhotos(atlas);
            if (recyclerView.getAdapter() == null) {
                LinearLayoutManager manager = new LinearLayoutManager(mContext);
                manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(eventItemAdapter);
            } else {
                eventItemAdapter.notifyDataSetChanged();
            }
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int size = parent.getAdapter()
                    .getItemCount();
            if (position == 0) {
                outRect.left = 0;
                outRect.right = 0;
            } else if (position == size - 1) {
                outRect.right = 0;
                outRect.left = padding;
            } else {
                outRect.right = 0;
                outRect.left = padding;
            }
        }
    }


    private class EventItemAdapter extends RecyclerView.Adapter<ViewHolder> {
        private List<ExperiencePhoto> photos;

        public void setPhotos(List<ExperiencePhoto> photos) {
            if (photos == null) {
                return;
            }
            this.photos.clear();
            this.photos.addAll(photos);
        }

        public EventItemAdapter() {
            photos = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.experience_photo_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final ExperiencePhoto experiencePhoto = photos.get(position);
            if (experiencePhoto == null || experiencePhoto.getId() == 0) {
                return;
            }

            Photo photo = experiencePhoto.getCover();
            if (photo != null) {
                String coverPath = ImageUtil.getImagePath(photo.getImagePath(), imgWidth);
                if (!CommonUtil.isEmpty(coverPath)) {
                    Glide.with(mContext)
                            .load(coverPath)
                            .into(holder.imgBg);
                } else {
                    Glide.with(mContext)
                            .clear(holder.imgBg);
                }
            }

            holder.tvName.setText(experiencePhoto.getTitle());
            holder.tvCount.setText(String.valueOf(experiencePhoto.getItemsCount()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ExperienceShopImageActivity.class);
                    intent.putExtra("id", experiencePhoto.getId());
                    intent.putExtra("title", experiencePhoto.getTitle());
                    mContext.startActivity(intent);
                    ((Activity) (mContext)).overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_bg)
        ImageView imgBg;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_count)
        TextView tvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
