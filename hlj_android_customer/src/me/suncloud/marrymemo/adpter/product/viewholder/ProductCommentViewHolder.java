package me.suncloud.marrymemo.adpter.product.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.product.ProductComment;
import com.hunliji.hljcommonlibrary.models.product.Sku;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.CommentPhotoAdapter;

/**
 * Created by wangtao on 2016/11/15.
 */

public class ProductCommentViewHolder extends TrackerProductCommentViewHolder {

    @BindView(R.id.user_icon)
    RoundedImageView userIcon;
    @BindView(R.id.name)
    TextView tvName;
    @BindView(R.id.time)
    TextView tvTime;
    @BindView(R.id.grade)
    TextView tvGrade;
    @BindView(R.id.content)
    TextView tvContent;
    @BindView(R.id.sku)
    TextView tvSku;
    @BindView(R.id.images_layout)
    GridView imagesLayout;

    public ProductCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, ProductComment comment, int position, int viewType) {
        super.setViewData(mContext, comment, position, viewType);
        tvGrade.setText(comment.getRating() > 0 ? R.string.label_grade_level1 : comment.getRating
                () < 0 ? R.string.label_grade_level3 : R.string.label_grade_level2);
        if (comment.getCreatedAt() != null) {
            tvTime.setHint(comment.getCreatedAt()
                    .toString(tvTime.getContext()
                            .getString(R.string.format_date_type4)));
        }
        if (comment.getAuthor() != null) {
            Author author = comment.getAuthor();
            int size = CommonUtil.dp2px(userIcon.getContext(), 30);
            String path = ImageUtil.getAvatar(author.getAvatar(), size);
            tvName.setText(author.getName());
            tvName.setPadding(0,
                    0,
                    Math.round(tvGrade.getPaint()
                            .measureText(tvGrade.getText()
                                    .toString()) + CommonUtil.dp2px(tvName.getContext(), 10)),
                    0);
            Glide.with(userIcon.getContext())
                    .load(path)
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary))
                    .into(userIcon);
        }

        tvContent.setVisibility(TextUtils.isEmpty(comment.getContent()) ? View.GONE : View.VISIBLE);
        tvContent.setText(comment.getContent());

        Sku sku = comment.getSku();
        if (sku == null || CommonUtil.isEmpty((sku.getName()))) {
            tvSku.setVisibility(View.GONE);
        } else {
            tvSku.setVisibility(View.VISIBLE);
            tvSku.setText(mContext.getString(R.string.label_sku2, sku.getName()));
        }
        List<Photo> photos = comment.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            imagesLayout.setVisibility(View.VISIBLE);
            int spacing = CommonUtil.dp2px(imagesLayout.getContext(), 2);
            int pluralSize = Math.round(CommonUtil.getDeviceSize(imagesLayout.getContext()).x / 3
                    - CommonUtil.dp2px(
                    imagesLayout.getContext(),
                    26));
            CommentPhotoAdapter adapter = (CommentPhotoAdapter) imagesLayout.getAdapter();
            if (adapter == null) {
                adapter = new CommentPhotoAdapter(pluralSize, photos);
            } else {
                adapter.setPhotos(photos);
            }
            imagesLayout.setAdapter(adapter);
            imagesLayout.getLayoutParams().height = pluralSize * ((photos.size() + 2) / 3) + (
                    (photos.size() - 1) / 3) * spacing;

        } else {
            imagesLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public View trackerView() {
        return itemView;
    }
}
