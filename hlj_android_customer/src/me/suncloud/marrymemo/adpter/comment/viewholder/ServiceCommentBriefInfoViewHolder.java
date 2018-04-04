package me.suncloud.marrymemo.adpter.comment.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.HljGridView;
import com.hunliji.hljcommonviewlibrary.utils.FixedColumnGridInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 服务评价简要信息
 * Created by chen_bin on 2017/6/8 0008.
 */
public class ServiceCommentBriefInfoViewHolder extends BaseServiceCommentViewHolder {
    @BindView(R.id.images_layout)
    HljGridView imagesLayout;
    private OnItemClickListener onItemClickListener;

    public ServiceCommentBriefInfoViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        imagesLayout.setGridInterface(new FixedColumnGridInterface(CommonUtil.dp2px(itemView
                        .getContext(),
                2)));
        imagesLayout.setItemClickListener(new HljGridView.GridItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemView.performClick();
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            Context mContext, ServiceComment comment, int position, int viewType) {
        if (comment == null) {
            return;
        }
        super.setViewData(mContext, comment, position, viewType);
        setPhotos(comment);
    }

    private void setPhotos(ServiceComment comment) {
        if (!CommonUtil.isCollectionEmpty(comment.getPhotos()) && comment.getPhotos()
                .size() > 3) {
            comment.setPhotos(new ArrayList<>(comment.getPhotos()
                    .subList(0, 3)));
        }
        if (CommonUtil.isCollectionEmpty(comment.getPhotos())) {
            imagesLayout.setVisibility(View.GONE);
        } else {
            imagesLayout.setVisibility(View.VISIBLE);
            imagesLayout.setDate(comment.getPhotos());
        }
    }

    @Override
    public void setContent(ServiceComment comment) {
        if (TextUtils.isEmpty(comment.getContent())) {
            contentLayout.setVisibility(View.GONE);
        } else {
            contentLayout.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setMaxLines(3);
            tvContent.setEllipsize(TextUtils.TruncateAt.END);
            tvContent.setText(comment.getContent());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}