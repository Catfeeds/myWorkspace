package com.hunliji.hljlivelibrary.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljlivelibrary.R;
import com.hunliji.hljlivelibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2017/1/3.
 */

class ReviewThreadViewHolder extends BaseViewHolder<CommunityThread> {

    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_content)
    TextView tvContent;

    private int faceSize;

    ReviewThreadViewHolder(Context context) {
        this(View.inflate(context, R.layout.live_review_thread___live, null));
    }


    private ReviewThreadViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.faceSize = CommonUtil.dp2px(tvTitle.getContext(), 18);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommunityThread thread = getItem();
                if (thread != null) {
                    ARouter.getInstance()
                            .build(RouterPath.IntentPath.Customer.COMMUNITY_THREAD_DETAIL)
                            .withLong("id", thread.getId())
                            .navigation(itemView.getContext());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, CommunityThread thread, int position, int viewType) {
        tvTitle.setText(EmojiUtil.parseEmojiByText2(tvTitle.getContext(),
                thread.getShowTitle(),
                faceSize));
        tvContent.setText(EmojiUtil.parseEmojiByText2(tvContent.getContext(),
                thread.getShowSubtitle(),
                faceSize));
        tvTitle.post(new Runnable() {
            @Override
            public void run() {
                if (tvTitle.getLineCount() >= 2) {
                    tvContent.setMaxLines(1);
                } else {
                    tvContent.setMaxLines(2);
                }
            }
        });
    }
}
