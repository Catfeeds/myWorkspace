package me.suncloud.marrymemo.viewholder.experienceshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Comment;
import com.hunliji.hljcommonlibrary.views.widgets.MarkFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.view.experience.ExperienceShopImpressionActivity;

/**
 * experience_shop_item_impressions
 * Created by jinxin on 2017/3/24 0024.
 */

public class ExperienceShopCommentsHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.layout_comment)
    public LinearLayout layoutComment;
    @BindView(R.id.tv_all_comment)
    public TextView tvAllComment;
    @BindView(R.id.flow_layout)
    public MarkFlowLayout flowLayout;
    @BindView(R.id.flow_line)
    public View flowLine;
    private Context mContext;
    private long id;
    private List<String> tags;

    public ExperienceShopCommentsHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    @OnClick(R.id.tv_all_comment)
    void onAllComment() {
        Intent intent = new Intent(mContext, ExperienceShopImpressionActivity.class);
        intent.putExtra("testStoreId", this.id);
        intent.putStringArrayListExtra("mark", (ArrayList<String>) tags);
        mContext.startActivity(intent);
        ((Activity) (mContext)).overridePendingTransition(R.anim.slide_in_right,
                R.anim.activity_anim_default);
    }

    public void setComment(List<Comment> comments, List<String> tags, long id) {
        this.tags = tags;
        this.id = id;
        if (comments == null || comments.isEmpty()) {
            return;
        }
        int commentSize = comments.size();
        if (commentSize < 3) {
            tvAllComment.setVisibility(View.GONE);
        } else {
            tvAllComment.setVisibility(View.VISIBLE);
        }
        setImpression(tags);

        int size = Math.min(commentSize, layoutComment.getChildCount());
        for (int i = 0; i < size; i++) {
            Comment comment = comments.get(i);
            View item = layoutComment.getChildAt(i);
            CommentViewHolder holder = new CommentViewHolder(item, comments);
            holder.setView(mContext, comment, i, i);
            holder.userLine.setVisibility(i < size - 1 ? View.VISIBLE : View.GONE);
        }
    }

    public void setImpression(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            flowLine.setVisibility(View.GONE);
            flowLayout.setVisibility(View.GONE);
            return;
        } else {
            flowLine.setVisibility(View.VISIBLE);
            flowLayout.setVisibility(View.VISIBLE);
        }
        flowLayout.setMaxLineCount(3);
        int childCount = flowLayout.getChildCount();
        int size = tags.size();
        size = size > childCount ? size : childCount;
        if (childCount > size) {
            flowLayout.removeViews(size, childCount - size);
        }
        for (int i = 0; i < size; i++) {
            TextView tv = null;
            if (i < childCount) {
                tv = (TextView) flowLayout.getChildAt(i);
            }
            if (tv == null) {
                tv = (TextView) LayoutInflater.from(mContext)
                        .inflate(R.layout.experience_shop_impress_item, null, false);
                flowLayout.addView(tv);
            }
            tv.setText(tags.get(i));
        }
    }
}
