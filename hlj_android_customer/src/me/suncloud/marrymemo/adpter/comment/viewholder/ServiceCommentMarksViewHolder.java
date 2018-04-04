package me.suncloud.marrymemo.adpter.comment.viewholder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.ServiceCommentMark;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ThemeUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.MarkFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * 评价标签死筛选
 * Created by chen_bin on 2017/9/26 0026.
 */
public class ServiceCommentMarksViewHolder extends
        BaseViewHolder<List<ServiceCommentMark>> {
    @BindView(R.id.marks_layout)
    LinearLayout marksLayout;
    @BindView(R.id.marks_flow_layout)
    MarkFlowLayout marksFlowLayout;
    @BindView(R.id.img_arrow)
    ImageView imgArrow;
    @BindView(R.id.line_layout)
    View lineLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    private ArrayList<CheckBox> childViews;
    private long markId;
    private boolean isCanCheck = true;
    private boolean isCanShowArrowIcon = true;
    private int paddingTop;
    private int paddingBottom;
    private OnCommentFilterListener onCommentFilterListener;

    private static int DEFAULT_MAX_LINE_COUNT = 2;

    public ServiceCommentMarksViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.childViews = new ArrayList<>();
        this.paddingTop = CommonUtil.dp2px(itemView.getContext(), 16);
        this.paddingBottom = CommonUtil.dp2px(itemView.getContext(), 14);
        marksFlowLayout.setMaxLineCount(DEFAULT_MAX_LINE_COUNT);
        marksFlowLayout.setOnMeasuredListener(new MarkFlowLayout.OnMeasuredListener() {
            @Override
            public void onMeasured() {
                if (getItem() != null && !CommonUtil.isCollectionEmpty(getItem())) {
                    boolean isShowArrowIcon = isCanShowArrowIcon && (marksFlowLayout
                            .getTotalCount() < getItem().size() || marksFlowLayout
                            .getMaxLineCount() > DEFAULT_MAX_LINE_COUNT);
                    imgArrow.setVisibility(isShowArrowIcon ? View.VISIBLE : View.GONE);
                    marksLayout.setPadding(marksLayout.getPaddingLeft(),
                            paddingTop,
                            marksLayout.getPaddingRight(),
                            isShowArrowIcon ? 0 : paddingBottom);
                }
            }
        });
        imgArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marksFlowLayout.getMaxLineCount() == Integer.MAX_VALUE) {
                    marksFlowLayout.setMaxLineCount(DEFAULT_MAX_LINE_COUNT);
                    imgArrow.setImageResource(R.mipmap.icon_arrow_down_gray_26_14);
                } else {
                    marksFlowLayout.setMaxLineCount(Integer.MAX_VALUE);
                    imgArrow.setImageResource(R.mipmap.icon_arrow_up_gray_26_14);
                }
                marksFlowLayout.requestLayout();
            }
        });
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                if (onCommentFilterListener != null) {
                    onCommentFilterListener.onCommentFilter(markId);
                }
            }
        });
    }

    @Override
    protected void setViewData(
            final Context mContext,
            final List<ServiceCommentMark> marks,
            int position,
            int viewType) {
        if (CommonUtil.isCollectionEmpty(marks)) {
            return;
        }
        childViews.clear();
        int count = marksFlowLayout.getChildCount();
        int size = marks.size();
        if (count > size) {
            marksFlowLayout.removeViews(size, count - size);
        }
        for (int i = 0; i < size; i++) {
            CheckBox checkBox = null;
            final ServiceCommentMark mark = marks.get(i);
            if (count > i) {
                checkBox = (CheckBox) marksFlowLayout.getChildAt(i);
            }
            if (checkBox == null) {
                View.inflate(mContext,
                        R.layout.service_comment_marks_flow_item,
                        marksFlowLayout);
                checkBox = (CheckBox) marksFlowLayout.getChildAt(marksFlowLayout.getChildCount()
                        - 1);
            }
            checkBox.setChecked(isCanCheck && markId == mark.getId());
            checkBox.setText(mark.getName() + mark.getCommentsCountStr());
            if (mark.getId() == ServiceCommentMark.ID_BAD_REPUTATION) {
                checkBox.setBackgroundResource(ThemeUtil.getAttrResourceId(checkBox.getContext(),
                        R.attr.hljBadMarkBackground,
                        R.drawable.sl_r2_background2_2_primary));
                ColorStateList textColor = ThemeUtil.getAttrColorList(checkBox.getContext(),
                        R.attr.hljDadMarkTextColor);
                if (textColor != null) {
                    checkBox.setTextColor(textColor);
                }
            } else {
                checkBox.setBackgroundResource(ThemeUtil.getAttrResourceId(checkBox.getContext(),
                        R.attr.hljMarkBackground,
                        R.drawable.sl_r2_color_fff3f5_2_primary));
                ColorStateList textColor = ThemeUtil.getAttrColorList(checkBox.getContext(),
                        R.attr.hljMarkTextColor);
                if (textColor != null) {
                    checkBox.setTextColor(textColor);
                }
            }
            final CheckBox finalCheckBox = checkBox;

            HljVTTagger.buildTagger(checkBox)
                    .tagName("merchant_comment_tag_item")
                    .atPosition(i)
                    .dataType("MerchantCommentTag")
                    .dataId(mark.getId())
                    .hitTag();

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (CheckBox child : childViews) {
                        child.setChecked(false);
                    }
                    finalCheckBox.setChecked(isCanCheck);
                    if (isCanCheck && markId == mark.getId()) { //防止选择同一个
                        return;
                    }
                    markId = mark.getId();
                    if (onCommentFilterListener != null) {
                        onCommentFilterListener.onCommentFilter(markId);
                    }
                }
            });
            childViews.add(checkBox);
        }
    }

    public void setMarkId(long markId) {
        this.markId = markId;
    }

    public void setCanCheck(boolean canCheck) {
        this.isCanCheck = canCheck;
    }

    public void setCanShowArrowIcon(boolean canShowArrowIcon) {
        this.isCanShowArrowIcon = canShowArrowIcon;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setShowEmptyView(boolean showEmptyView) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height;
        if (showEmptyView) {
            emptyView.showEmptyView();
            emptyView.setVisibility(View.VISIBLE);
            height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            emptyView.hideEmptyView();
            emptyView.setVisibility(View.GONE);
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if (itemView.getLayoutParams() == null) {
            itemView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        } else {
            itemView.getLayoutParams().width = width;
            itemView.getLayoutParams().height = height;
        }
    }

    public void setOnCommentFilterListener(OnCommentFilterListener onCommentFilterListener) {
        this.onCommentFilterListener = onCommentFilterListener;
    }

    public interface OnCommentFilterListener {
        void onCommentFilter(long markId);
    }

}