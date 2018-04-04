package me.suncloud.marrymemo.adpter.finder.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.note.NoteMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljnotelibrary.views.activities.NoteMarkDetailActivity;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 发现页推荐流-标签
 * Created by chen_bin on 2018/2/2 0002.
 */
public class FinderRecommendMarkViewHolder extends BaseViewHolder<NoteMark> {

    @BindView(R.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private int coverWidth;

    public FinderRecommendMarkViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        coverWidth = (CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px
                (itemView.getContext(),
                28)) / 2;
        imgCover.getLayoutParams().height = coverWidth;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteMark mark = getItem();
                if (mark == null) {
                    return;
                }
                Intent intent = new Intent(v.getContext(), NoteMarkDetailActivity.class);
                intent.putExtra(NoteMarkDetailActivity.ARG_MARK_ID, mark.getId());
                v.getContext()
                        .startActivity(intent);
            }
        });
    }

    @Override
    protected void setViewData(Context mContext, NoteMark mark, int position, int viewType) {
        if (mark == null) {
            return;
        }
        Glide.with(mContext)
                .load(ImagePath.buildPath(mark.getImagePath())
                        .width(coverWidth)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        if (CommonUtil.isEmpty(mark.getName())) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText("#" + mark.getName());
        }
    }
}