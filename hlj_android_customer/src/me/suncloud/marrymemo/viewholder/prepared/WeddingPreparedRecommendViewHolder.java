package me.suncloud.marrymemo.viewholder.prepared;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.JsonPic;
import me.suncloud.marrymemo.model.WeddingPreparedListModelItem;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by jinxin on 2017/9/27 0027.
 */

public class WeddingPreparedRecommendViewHolder extends
        BaseViewHolder<WeddingPreparedListModelItem> {

    @BindView(R.id.wedding_prepare_item_group_type)
    TextView weddingPrepareItemGroupType;
    @BindView(R.id.wedding_prepare_item_group_des)
    TextView weddingPrepareItemGroupDes;
    @BindView(R.id.wedding_prepare_item_group_list)
    LinearLayout weddingPrepareItemGroupList;
    @BindView(R.id.wedding_prepare_item_group_count)
    TextView weddingPrepareItemGroupCount;
    @BindView(R.id.wedding_prepare_item_group)
    LinearLayout weddingPrepareItemGroup;
    @BindView(R.id.wedding_prepare_item_thread_image)
    ImageView weddingPrepareItemThreadImage;
    @BindView(R.id.wedding_prepare_item_thread_image_hint)
    ImageView weddingPrepareItemThreadImageHint;
    @BindView(R.id.wedding_prepare_item_thread_title)
    TextView weddingPrepareItemThreadTitle;
    @BindView(R.id.wedding_prepare_item_thread_des)
    TextView weddingPrepareItemThreadDes;
    @BindView(R.id.wedding_prepare_item_thread_count)
    TextView weddingPrepareItemThreadCount;
    @BindView(R.id.wedding_prepare_item_thread)
    LinearLayout weddingPrepareItemThread;
    @BindView(R.id.layout_header)
    LinearLayout layoutHeader;
    @BindView(R.id.wedding_prepare_item_group_new)
    ImageView weddingPrepareItemGroupNew;

    private int threadImageWidth;
    private int threadImageHeight;
    private int groupImageWidth;
    private int groupImageHeight;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public WeddingPreparedRecommendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        threadImageWidth = CommonUtil.dp2px(mContext, 96);
        threadImageHeight = CommonUtil.dp2px(mContext, 60);
        groupImageWidth = Math.round((CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(
                mContext,
                48)) / 4);
        groupImageHeight = CommonUtil.dp2px(mContext, 68);
    }

    @Override
    protected void setViewData(
            Context mContext, WeddingPreparedListModelItem item, int position, int viewType) {
        layoutHeader.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        int type = item.getType();
        switch (type) {
            case 3:
            case 2:
                setThreadView(item, position);
                break;
            case 4:
                setThreadGroupView(item, position);
                break;
            default:
                break;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void setThreadView(final WeddingPreparedListModelItem item, final int position) {
        weddingPrepareItemThread.setVisibility(View.VISIBLE);
        weddingPrepareItemGroup.setVisibility(View.GONE);

        loadImage(weddingPrepareItemThreadImage,
                item.getImgList(),
                threadImageWidth,
                threadImageHeight);
        weddingPrepareItemThreadImageHint.setVisibility(item.getIsNew() ? View.VISIBLE : View.GONE);
        weddingPrepareItemThreadTitle.setText(JSONUtil.isEmpty(item.getTitle()) ? "" : item
                .getTitle());
        weddingPrepareItemThreadDes.setText(JSONUtil.isEmpty(item.getGoodTitle()) ? "" : item
                .getGoodTitle());
        weddingPrepareItemThreadCount.setText(String.valueOf(item.getWatchCount()));

        weddingPrepareItemThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(item,
                            position,
                            R.id.wedding_prepare_item_thread);
                }
            }
        });
    }

    private void setThreadGroupView(final WeddingPreparedListModelItem item, final int position) {
        weddingPrepareItemThread.setVisibility(View.GONE);
        weddingPrepareItemGroup.setVisibility(View.VISIBLE);

        weddingPrepareItemGroupType.setText(TextUtils.isEmpty(item.getTitle()) ? "" : item
                .getTitle());
        weddingPrepareItemGroupDes.setText(TextUtils.isEmpty(item.getGoodTitle()) ? "" : item
                .getGoodTitle());
        weddingPrepareItemGroupCount.setText(String.valueOf(item.getWatchCount()));
        weddingPrepareItemGroupNew.setVisibility(View.GONE);

        List<JsonPic> images = item.getContent();
        if (images == null || images.isEmpty()) {
            weddingPrepareItemGroupList.setVisibility(View.GONE);
        } else {
            weddingPrepareItemGroupList.setVisibility(View.VISIBLE);
            int childCount = weddingPrepareItemGroupList.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View childView = weddingPrepareItemGroupList.getChildAt(i);
                if (i < images.size()) {
                    JsonPic jsonPic = images.get(i);
                    childView.setVisibility(View.VISIBLE);
                    if (i == 0) {
                        if (childView instanceof FrameLayout) {
                            FrameLayout frameLayout = (FrameLayout) childView;
                            ImageView img = (ImageView) frameLayout.getChildAt(0);
                            ImageView hint = (ImageView) frameLayout.getChildAt(1);
                            hint.setVisibility(item.getIsNew() ? View.VISIBLE : View.GONE);
                            if (jsonPic != null) {
                                loadImage(img,
                                        jsonPic.getPath(),
                                        groupImageWidth,
                                        groupImageHeight);
                            }
                        }
                    } else if (i == childCount - 1) {
                        if (childView instanceof FrameLayout) {
                            FrameLayout frameLayout = (FrameLayout) childView;
                            ImageView img = (ImageView) frameLayout.getChildAt(0);
                            TextView tv = (TextView) frameLayout.getChildAt(1);
                            tv.setVisibility(View.VISIBLE);
                            tv.setText(String.format(mContext.getString(R.string.label_more_pages),
                                    String.valueOf(item.getMediaItemCount())));
                            if (jsonPic != null) {
                                loadImage(img,
                                        jsonPic.getPath(),
                                        groupImageWidth,
                                        groupImageHeight);
                            }
                        }
                    } else {
                        if (childView instanceof ImageView) {
                            ImageView img = (ImageView) childView;
                            loadImage(img, jsonPic.getPath(), groupImageWidth, groupImageHeight);
                        }
                    }
                } else {
                    childView.setVisibility(View.INVISIBLE);
                }
            }
        }

        weddingPrepareItemGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(item,
                            position,
                            R.id.wedding_prepare_item_group);
                }
            }
        });
    }

    private void loadImage(ImageView imageView, String path, int width, int height) {
        Glide.with(mContext)
                .load(ImagePath.buildPath(path)
                        .width(width)
                        .height(height)
                        .cropPath())
                .into(imageView);
    }

    public interface OnItemClickListener {
        void onItemClickListener(WeddingPreparedListModelItem item, int position, int id);
    }
}
