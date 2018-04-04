package me.suncloud.marrymemo.adpter.marry.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.marry.MarryTask;
import me.suncloud.marrymemo.model.marry.Template;
import me.suncloud.marrymemo.util.BannerUtil;

/**
 * 新娘说-同婚期-结婚任务viewHolder
 * Created by chen_bin on 2018/3/15 0015.
 */
public class SimilarWeddingTaskViewHolder extends BaseViewHolder<MarryTask> {

    @BindView(R.id.check_layout)
    CheckableLinearLayout checkLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_poster_title)
    TextView tvPosterTitle;

    private OnCheckTaskListener onCheckTaskListener;

    public SimilarWeddingTaskViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(Context mContext, MarryTask task, int position, int viewType) {
        if (task == null) {
            return;
        }
        checkLayout.setChecked(task.getStatus() == MarryTask.STATUS_DONE);
        tvTitle.setText(task.getTitle());

        Template template = task.getTemplate();
        Poster poster = null;
        if (template != null) {
            poster = template.getConfig();
        }
        if (poster == null || CommonUtil.isEmpty(poster.getTitle())) {
            tvPosterTitle.setVisibility(View.GONE);
        } else {
            tvPosterTitle.setVisibility(View.VISIBLE);
            tvPosterTitle.setText(poster.getTitle());
        }
    }

    @OnClick(R.id.check_layout)
    void onCheck() {
        if (onCheckTaskListener != null) {
            onCheckTaskListener.onCheckTask(checkLayout, getItem());
        }
    }

    @OnClick(R.id.tv_poster_title)
    void onPoster(View v) {
        MarryTask task = getItem();
        if (task == null) {
            return;
        }
        Template template = task.getTemplate();
        if (template == null) {
            return;
        }
        Poster poster = template.getConfig();
        if (poster != null) {
            BannerUtil.bannerJump(v.getContext(), poster, null);
        }
    }

    public interface OnCheckTaskListener {
        void onCheckTask(CheckableLinearLayout checkLayout, MarryTask task);
    }

    public void setOnCheckTaskListener(OnCheckTaskListener onCheckTaskListener) {
        this.onCheckTaskListener = onCheckTaskListener;
    }

}
