package me.suncloud.marrymemo.adpter.finder.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkMediaItem;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.widgets.MarkFlowLayout;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerCaseViewHolder;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.finder.FinderCaseListFragment;
import me.suncloud.marrymemo.view.CaseDetailActivity;

/**
 * Created by mo_yu on 2018/2/7.发现页-案例列表
 */

public class BaseFinderCaseViewHolder extends TrackerCaseViewHolder {
    @BindView(R.id.case_image_layout)
    RelativeLayout caseImageLayout;
    @BindView(R.id.img_case_cover_1)
    public ImageView imgCaseCover1;
    @BindView(R.id.img_case_cover_2)
    public ImageView imgCaseCover2;
    @BindView(R.id.img_case_cover_3)
    public ImageView imgCaseCover3;
    @BindView(R.id.img_case_cover_4)
    public ImageView imgCaseCover4;
    @BindView(R.id.img_search_sames)
    ImageView imgSearchSames;
    @BindView(R.id.tv_case_title)
    TextView tvCaseTitle;
    @BindView(R.id.tags_layout)
    MarkFlowLayout tagsLayout;
    @BindView(R.id.img_share)
    ImageView imgShare;
    @BindView(R.id.action_share_case)
    FrameLayout actionShareCase;
    @BindView(R.id.img_collect_case)
    ImageView imgCollectCase;
    @BindView(R.id.tv_collect_state)
    TextView tvCollectState;
    @BindView(R.id.action_collect_case)
    LinearLayout actionCollectCase;
    @BindView(R.id.case_bottom_layout)
    LinearLayout caseBottomLayout;
    @BindView(R.id.search_shadow_view)
    View searchShadowView;
    public WorkMediaItem mediaItem1 = null;
    public WorkMediaItem mediaItem2 = null;
    public WorkMediaItem mediaItem3 = null;
    public WorkMediaItem mediaItem4 = null;
    public int horizontalImageWidth;
    public int horizontalImageHeight;
    public int verticalImageWidth;
    public int smallImageHeight;
    public int imageSpace;
    private int normalTagCount = 6;
    private OnSearchSamesListener onSearchSamesListener;
    private OnCollectCaseListener onCollectCaseListener;

    @Override
    public String cpmSource() {
        return FinderCaseListFragment.FIND_CASE_LIST;
    }

    public interface OnSearchSamesListener {
        void onSearchSames(int position, Object object);
    }

    public interface OnCollectCaseListener {
        void onCollectCase(
                int position, Object object, ImageView imgCollect, TextView tvCollectState);
    }

    public void setOnSearchSamesListener(OnSearchSamesListener onSearchSamesListener) {
        this.onSearchSamesListener = onSearchSamesListener;
    }

    public void setOnCollectCaseListener(OnCollectCaseListener onCollectCaseListener) {
        this.onCollectCaseListener = onCollectCaseListener;
    }

    public BaseFinderCaseViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        horizontalImageWidth = CommonUtil.getDeviceSize(view.getContext()).x;
        horizontalImageHeight = horizontalImageWidth * 10 / 16;
        verticalImageWidth = (CommonUtil.getDeviceSize(view.getContext()).x / 2) - CommonUtil.dp2px(
                view.getContext(),
                2);
        smallImageHeight = (horizontalImageHeight / 2) - CommonUtil.dp2px(view.getContext(), 2);
        imageSpace = horizontalImageWidth - verticalImageWidth * 2;
        caseImageLayout.getLayoutParams().height = horizontalImageHeight;
        imgSearchSames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSearchSamesListener != null) {
                    onSearchSamesListener.onSearchSames(getAdapterPosition(),
                            getItem());
                }
            }
        });
        actionShareCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Work work = getItem();
                if (work == null || work.getShareInfo() == null) {
                    return;
                }
                ShareDialogUtil.onCommonShare(v.getContext(), work.getShareInfo());
            }
        });
        actionCollectCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCollectCaseListener != null) {
                    onCollectCaseListener.onCollectCase(getAdapterPosition(),
                            getItem(),
                            imgCollectCase,
                            tvCollectState);
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Work work = getItem();
                Activity activity = (Activity) v.getContext();
                if (work == null) {
                    return;
                }
                Intent intent = new Intent(v.getContext(), CaseDetailActivity.class);
                intent.putExtra(CaseDetailActivity.ARG_ID, work.getId());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });
        try {
            HljVTTagger.buildTagger(imgSearchSames)
                    .tagName(HljTaggerName.BTN_GET_SIMILAR)
                    .hitTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(Context mContext, Work item, int position, int viewType) {
        if (item.isCollected()) {
            imgCollectCase.setImageResource(R.mipmap.icon_collect_primary_32_32_selected);
            tvCollectState.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        } else {
            imgCollectCase.setImageResource(R.mipmap.icon_collect_black2_32_32);
            tvCollectState.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack3));
        }
        tvCaseTitle.setText(item.getTitle());
        imgSearchSames.setVisibility(item.isHideSearch() ? View.GONE : View.VISIBLE);
        searchShadowView.setVisibility(item.isHideSearch() ? View.GONE : View.VISIBLE);
        addTag(mContext, tagsLayout, item.getMarks());
    }

    private void addTag(Context mContext, MarkFlowLayout layout, List<Mark> marks) {
        if (marks != null && !marks.isEmpty()) {
            layout.setVisibility(View.VISIBLE);
            int count = layout.getChildCount();
            int size = marks.size();
            layout.setMaxLineCount(1);
            if (count > size) {
                layout.removeViews(size, count - size);
            }
            for (int i = 0; i < size; i++) {
                View view = null;
                TextView tv = null;
                Mark mark = marks.get(i);
                if (i < count) {
                    view = layout.getChildAt(i);
                }
                if (view == null) {
                    view = View.inflate(mContext, R.layout.find_case_mark_item, null);
                    layout.addView(view);
                }
                tv = view.findViewById(R.id.mark);
                view.setTag(mark);
                tv.setText(mark.getName());
            }
        } else {
            layout.setVisibility(View.INVISIBLE);
        }
    }
}
