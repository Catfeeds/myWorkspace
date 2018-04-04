package me.suncloud.marrymemo.adpter.finder.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.finder.UserPrepareCategory;

/**
 * 获取备婚阶段
 * Created by chen_bin on 2017/10/17 0017.
 */
public class UserPrepareCategoryViewHolder extends BaseViewHolder<UserPrepareCategory> {
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.check_layout)
    CheckableLinearLayout checkLayout;
    private OnItemClickListener onItemClickListener;

    public UserPrepareCategoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        int imageWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                90)) / 3;
        int imageHeight = (int) Math.ceil(imageWidth * 168.0f / 192.0f);
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
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
            Context mContext, UserPrepareCategory category, int position, int viewType) {
        if (category == null) {
            return;
        }
        Glide.with(mContext)
                .load(category.getImagePath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        checkLayout.setChecked(category.isSelected());
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
