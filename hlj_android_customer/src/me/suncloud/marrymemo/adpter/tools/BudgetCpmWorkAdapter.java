package me.suncloud.marrymemo.adpter.tools;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.WorkActivity;

/**
 * Created by luohanlin on 2017/11/10.
 */

public class BudgetCpmWorkAdapter extends RecyclerView.Adapter<BaseViewHolder<Work>> {

    Context context;
    List<Work> cpmWorks;

    public static final int TYPE_ITEM = 1;
    private int width;
    private int height;

    public BudgetCpmWorkAdapter(
            Context context, List<Work> cpmWorks) {
        this.context = context;
        this.cpmWorks = cpmWorks;
        width = CommonUtil.getDeviceSize(context).x * 116 / 375;
        height = width * 148 / 232;
    }

    @Override
    public BaseViewHolder<Work> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.budget_cpm_work_item, parent, false);
        return new CpmWorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Work> holder, int position) {
        if (holder instanceof CpmWorkViewHolder) {
            holder.setView(context, cpmWorks.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return cpmWorks == null ? 0 : cpmWorks.size();
    }

    class CpmWorkViewHolder extends BaseViewHolder<Work> {
        @BindView(R.id.start_padding)
        View startPadding;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_comments)
        TextView tvComments;
        @BindView(R.id.end_padding)
        View endPadding;

        CpmWorkViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            imgCover.getLayoutParams().width = width;
            imgCover.getLayoutParams().height = height;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Work work = getItem();
                    if (work == null) {
                        return;
                    }
                    Intent intent = new Intent(context, WorkActivity.class);
                    intent.putExtra("id", work.getId());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        protected void setViewData(Context mContext, Work item, int position, int viewType) {
            startPadding.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            endPadding.setVisibility(position == cpmWorks.size() - 1 ? View.VISIBLE : View.GONE);

            Glide.with(context)
                    .load(ImagePath.buildPath(item.getCoverPath())
                            .width(width)
                            .height(height)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate())
                    .into(imgCover);
            tvTitle.setText(item.getTitle());
            tvPrice.setText(context.getString(R.string.label_price,
                    CommonUtil.formatDouble2StringWithTwoFloat(item.getShowPrice())));
            tvComments.setText(context.getString(R.string.label_comment_count,
                    item.getCommentsCount()));
        }
    }
}
