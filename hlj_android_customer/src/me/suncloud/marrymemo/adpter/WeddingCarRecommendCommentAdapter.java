package me.suncloud.marrymemo.adpter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.car.CarComment;
import me.suncloud.marrymemo.view.CarCommentListActivity;

/**
 * Created by Suncloud on 2015/10/8.
 */
public class WeddingCarRecommendCommentAdapter extends RecyclingPagerAdapter {

    private ArrayList<CarComment> comments;
    private Context context;
    private int size;
    private int photoSize;
    private long cityId;

    public WeddingCarRecommendCommentAdapter(Context context, long cityId) {
        this.cityId = cityId;
        DisplayMetrics dm = context.getResources()
                .getDisplayMetrics();
        size = Math.round(dm.density * 30);
        photoSize = Math.round(dm.density * 60);
        this.context = context;
        comments = new ArrayList<>();
    }

    public void setComments(List<CarComment> comments) {
        if (comments != null) {
            this.comments.clear();
            this.comments.addAll(comments);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.wedding_car_recommend_comment, container, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CarCommentListActivity.class);
                intent.putExtra("cityId", cityId);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });
        ViewHolder holder = (ViewHolder) convertView.getTag();
        CarComment comment = comments.get(position);
        if (comment.getUser() != null) {
            Author user = comment.getUser();
            holder.nick.setText(user.getName());
            Glide.with(holder.avatar.getContext())
                    .load(ImageUtil.getAvatar(user.getAvatar(), size))
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary))
                    .into(holder.avatar);
        }
        holder.content.setText(comment.getContent());
        int size = holder.photosLayout.getChildCount();
        int count = comment.getPhotos() == null ? 0 : comment.getPhotos()
                .size();
        for (int i = 0; i < size; i++) {
            ImageView imageView = (ImageView) holder.photosLayout.getChildAt(i);
            if (i < count) {
                Photo photo = comment.getPhotos()
                        .get(i);
                imageView.setVisibility(View.VISIBLE);
                Glide.with(imageView.getContext())
                        .load(ImageUtil.getImagePath2(photo.getImagePath(), photoSize))
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imageView);
            } else {
                Glide.with(imageView.getContext())
                        .clear(imageView);
                imageView.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'wedding_car_recommend_comment.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.avatar)
        RoundedImageView avatar;
        @BindView(R.id.nick)
        TextView nick;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.photos_layout)
        RelativeLayout photosLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
