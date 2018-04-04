package me.suncloud.marrymemo.adpter.comment.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.product.ProductComment;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.models.product.Sku;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearGroup;
import com.hunliji.hljimagelibrary.adapters.DraggableImgGridAdapter;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 婚品子订单评价viewHolder
 * Created by chen_bin on 2018/1/8 0008.
 */
public class CommentProductSubOrderViewHolder extends BaseViewHolder<ProductComment> {

    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_sku)
    TextView tvSku;
    @BindView(R.id.cb_group)
    CheckableLinearGroup cbGroup;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.tv_count_hint)
    TextView tvCountHint;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.img_add_photos_hint)
    ImageView imgAddPhotosHint;

    private DraggableImgGridAdapter adapter;

    private int dragImageSize;
    private int imageWidth;

    private DraggableImgGridAdapter.OnItemAddListener onItemAddListener;
    private OnSyncNoteCheckedChangeListener onSyncNoteCheckedChangeListener;

    public final static int LIMIT = 9;

    private final static String[] CONTENT_HINTS = {"快告诉新娘们婚品有多好吧！大家都等着你的评价呢～",
            "有什么不足之处吗？和我们说说呗～", "先消消气，告诉我们遇到了什么问题，我们一定为你解决～"};
    private final static String[] COUNT_HINTS = {"至少5个字哦", "加油！再写%s个字就可以得到 20 枚金币",
            "如果再加%s个字和图片就可以得到 50枚金币哦", "不错哦！更详细的婚品介绍可以获得更多的赞赏哦～", "上传图片就可以得到 50 枚金币哦"};

    public CommentProductSubOrderViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        DisplayMetrics dm = itemView.getContext()
                .getResources()
                .getDisplayMetrics();
        dragImageSize = (int) ((dm.widthPixels - 62 * dm.density) / 4);
        imageWidth = CommonUtil.dp2px(itemView.getContext(), 72);
        GridLayoutManager layoutManager = new GridLayoutManager(itemView.getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new RefactoredDefaultItemAnimator());
        RecyclerViewDragDropManager dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(
                itemView.getContext(),
                R.drawable.sp_dragged_shadow));
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setLongPressTimeout(500);
        dragDropManager.attachRecyclerView(recyclerView);
        adapter = new DraggableImgGridAdapter(itemView.getContext(), dragImageSize, LIMIT);
        recyclerView.setAdapter(dragDropManager.createWrappedAdapter(adapter));
        adapter.setOnItemAddListener(new DraggableImgGridAdapter.OnItemAddListener() {
            @Override
            public void onItemAdd(Object... objects) {
                if (onItemAddListener != null) {
                    onItemAddListener.onItemAdd(getAdapterPosition(), getItem());
                }
            }
        });
        adapter.setOnItemDeleteListener(new DraggableImgGridAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int position) {
                ProductComment comment = getItem();
                if (comment == null) {
                    return;
                }
                comment.getPhotos()
                        .remove(position);
                setCountHint(comment);
                adapter.notifyDataSetChanged();
                if (onSyncNoteCheckedChangeListener != null) {
                    onSyncNoteCheckedChangeListener.onSyncNoteCheckedChange();
                }
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener<Photo>() {
            @Override
            public void onItemClick(int position, Photo photo) {
                ProductComment comment = getItem();
                if (comment != null && !CommonUtil.isCollectionEmpty(comment.getPhotos())) {
                    Intent intent = new Intent(itemView.getContext(), PicsPageViewActivity.class);
                    intent.putExtra(PicsPageViewActivity.ARG_PHOTOS, comment.getPhotos());
                    intent.putExtra(PicsPageViewActivity.ARG_POSITION, position);
                    itemView.getContext()
                            .startActivity(intent);
                }
            }
        });
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ProductComment comment = getItem();
                if (comment == null || s.toString()
                        .equals(comment.getContent())) {
                    return;
                }
                if (comment.getRating() == ProductComment.INIT_RATING) {
                    comment.setRating(ProductComment.POSITIVE_RATING);
                    checkRating(comment);
                }
                comment.setContent(s.toString());
                setCountHint(comment);
                if (onSyncNoteCheckedChangeListener != null) {
                    onSyncNoteCheckedChangeListener.onSyncNoteCheckedChange();
                }
            }
        });
        cbGroup.setOnCheckedChangeListener(new CheckableLinearGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CheckableLinearGroup group, int checkedId) {
                ProductComment comment = getItem();
                if (comment == null) {
                    return;
                }
                int rating = ProductComment.INIT_RATING;
                String contentHint = CONTENT_HINTS[0];
                switch (checkedId) {
                    case R.id.cb_positive:
                        rating = ProductComment.POSITIVE_RATING;
                        break;
                    case R.id.cb_neutral:
                        rating = ProductComment.NEUTRAL_RATING;
                        contentHint = CONTENT_HINTS[1];
                        break;
                    case R.id.cb_negative:
                        rating = ProductComment.NEGATIVE_RATING;
                        contentHint = CONTENT_HINTS[2];
                        break;
                }
                comment.setRating(rating);
                etContent.setHint(contentHint);
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, ProductComment comment, int position, int viewType) {
        if (comment == null) {
            return;
        }
        ShopProduct product = comment.getProduct();
        Glide.with(mContext)
                .load(ImagePath.buildPath(product.getCoverPath())
                        .width(imageWidth)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvTitle.setText(product.getTitle());
        Sku sku = comment.getSku();
        if (sku == null || CommonUtil.isEmpty(sku.getName())) {
            tvSku.setVisibility(View.GONE);
        } else {
            tvSku.setVisibility(View.VISIBLE);
            tvSku.setText(mContext.getString(R.string.label_sku2, sku.getName()));
        }
        checkRating(comment);
        etContent.setText(comment.getContent());
        setCountHint(comment);
        adapter.setPhotos(comment.getPhotos());
    }

    private void checkRating(ProductComment comment) {
        int id = -1;
        switch (comment.getRating()) {
            case ProductComment.POSITIVE_RATING:
                id = R.id.cb_positive;
                break;
            case ProductComment.NEUTRAL_RATING:
                id = R.id.cb_neutral;
                break;
            case ProductComment.NEGATIVE_RATING:
                id = R.id.cb_negative;
                break;
        }
        cbGroup.check(id);
    }

    private void setCountHint(ProductComment comment) {
        String str;
        int len = comment.getContent()
                .length();
        if (len == 0) {
            str = COUNT_HINTS[0];
        } else if (len >= 1 && len < 5) {
            str = String.format(COUNT_HINTS[1], 5 - len);
        } else if (len >= 5 && len < 30) {
            str = String.format(COUNT_HINTS[2], 30 - len);
        } else if (len >= 30 && hasPhotos(comment)) {
            str = COUNT_HINTS[3];
        } else {
            str = COUNT_HINTS[4];
        }
        tvCountHint.setText(str);
    }

    public void setShowAddPhotosHintView(Context context, boolean showAddPhotosHintView) {
        if (!showAddPhotosHintView) {
            imgAddPhotosHint.setVisibility(View.GONE);
        } else {
            imgAddPhotosHint.setVisibility(View.VISIBLE);
            ((ViewGroup.MarginLayoutParams) imgAddPhotosHint.getLayoutParams()).leftMargin =
                    dragImageSize + CommonUtil.dp2px(
                    context,
                    17);
        }
    }

    private boolean hasPhotos(ProductComment comment) {
        return !CommonUtil.isCollectionEmpty(comment.getPhotos());
    }

    public void setOnItemAddListener(DraggableImgGridAdapter.OnItemAddListener onItemAddListener) {
        this.onItemAddListener = onItemAddListener;
    }

    public void setOnSyncNoteCheckedChangeListener(
            OnSyncNoteCheckedChangeListener onSyncNoteCheckedChangeListener) {
        this.onSyncNoteCheckedChangeListener = onSyncNoteCheckedChangeListener;
    }

    public interface OnSyncNoteCheckedChangeListener {
        void onSyncNoteCheckedChange();
    }

}