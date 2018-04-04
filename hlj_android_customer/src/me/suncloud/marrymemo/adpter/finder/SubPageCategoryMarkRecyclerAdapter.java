package me.suncloud.marrymemo.adpter.finder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.subpage.SubPageCategoryMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.finder.SubPageCategoryMarkListActivity;
import me.suncloud.marrymemo.view.finder.SubPageRankListActivity;

/**
 * 推荐标签组列表
 * Created by chen_bin on 2017/1/3 0003.
 */
public class SubPageCategoryMarkRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private List<SubPageCategoryMark> categoryMarks;
    private LayoutInflater inflater;
    private int imageWidth;
    private int imageHeight;

    public SubPageCategoryMarkRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.imageWidth = CommonUtil.dp2px(context, 80);
        this.imageHeight = CommonUtil.dp2px(context, 50);
    }

    public void setCategoryMarks(List<SubPageCategoryMark> categoryMarks) {
        this.categoryMarks = categoryMarks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(categoryMarks);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryMarkViewHolder(inflater.inflate(R.layout
                        .sub_page_category_mark_list_item,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setView(context, categoryMarks.get(position), position, 0);
    }

    public class CategoryMarkViewHolder extends BaseViewHolder<SubPageCategoryMark> {
        @BindView(R.id.img_cover)
        RoundedImageView imgCover;

        public CategoryMarkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SubPageCategoryMark categoryMark = getItem();
                    if (categoryMark == null) {
                        return;
                    }
                    if (categoryMark.getId() == 0) {
                        context.startActivity(new Intent(context, SubPageRankListActivity.class));
                    } else {
                        Intent intent = new Intent(context, SubPageCategoryMarkListActivity.class);
                        intent.putExtra("markGroupId", categoryMark.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context,
                final SubPageCategoryMark categoryMark,
                final int position,
                int viewType) {
            if (categoryMark == null) {
                return;
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(categoryMark.getImg())
                            .width(imageWidth)
                            .height(imageHeight)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_empty_image)
                            .error(R.mipmap.icon_empty_image))
                    .into(imgCover);
        }
    }
}