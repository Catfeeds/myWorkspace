package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CasePhoto;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.HljViewTracker;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerCasePhotoViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerWorkViewHolder;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.OriginalImageScaleListener;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;
import com.makeramen.rounded.RoundedImageView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.viewholder.WorkDetailCommentViewHolder;
import me.suncloud.marrymemo.adpter.work_case.viewholder.WorkDetailInfoViewHolder;
import me.suncloud.marrymemo.fragment.work_case.WorkDetailFragment;
import me.suncloud.marrymemo.model.Photo;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.WorkParameter;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.WorkActivity;
import me.suncloud.marrymemo.view.work_case.CasePhotoDetailActivity;
import me.suncloud.marrymemo.view.work_case.WorkMediaImageActivity;

/**
 * Created by luohanlin on 2017/12/21.
 * 套餐详情
 */

public class WorkDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        StickyRecyclerHeadersAdapter<BaseViewHolder> {

    private Context context;
    private Work work;
    private boolean isSnapshot;
    private WorkDetailFragment.WorkCommentsQuestionsCasePhotosZip cqZip;
    private List<com.hunliji.hljcommonlibrary.models.Work> recommendWorks;
    private SparseArray<List<CasePhoto>> casePhotoList;
    private OnTabSelectedListener onTabSelectedListener;
    private WorkDetailInfoViewHolder.OnInfoListener onInfoListener;

    private static final int TYPE_INFO = 1;
    private static final int TYPE_COMMENT = 2;
    //    private static final int TYPE_DETAIL_TAB = 3;
    private static final int TYPE_PHOTO_ITEM = 4;
    private static final int TYPE_RECOMMEND_WORKS = 5;
    private static final int TYPE_WORK_PARAMETERS = 6;
    private static final int TYPE_WORK_NOTES = 7;
    private static final int TYPE_CASE_PHOTO = 8;//相关客照
    private static final int TYPE_CASE_PHOTO_INTRODUCE = 9;//客照介绍

    private LayoutInflater layoutInflater;
    private int detailTabIndex = 0;
    private int itemImgWidth;

    public WorkDetailAdapter(Context cxt, Work wk, boolean isSS) {
        this.context = cxt;
        this.work = wk;
        this.isSnapshot = isSS;
        layoutInflater = LayoutInflater.from(cxt);
        itemImgWidth = CommonUtil.getDeviceSize(context).x;
        casePhotoList = new SparseArray<>();
    }

    public void setCqZip(WorkDetailFragment.WorkCommentsQuestionsCasePhotosZip cqZip) {
        this.cqZip = cqZip;
        initCasePhotoListItem();
        notifyDataSetChanged();
    }

    private void initCasePhotoListItem() {
        this.casePhotoList.clear();
        if (cqZip != null && cqZip.casePhotoList != null && cqZip.casePhotoList.getData() != null) {
            List<CasePhoto> casePhotoList = cqZip.casePhotoList.getData();
            int size = casePhotoList.size();
            if (size <= 8) {
                for (int i = 0; i < size; i++) {
                    List<CasePhoto> casePhotos = new ArrayList<>();
                    casePhotos.add(casePhotoList.get(i));
                    this.casePhotoList.put(i, casePhotos);
                }
            } else {
                int lineCount = 0;
                List<CasePhoto> casePhotos;
                for (int i = 0; i < size; i++) {
                    int rePosition = i % 6;
                    switch (rePosition) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            //大图
                            lineCount = this.casePhotoList.size();
                            casePhotos = this.casePhotoList.get(lineCount);
                            if (casePhotos == null) {
                                casePhotos = new ArrayList<>();
                            }
                            casePhotos.add(casePhotoList.get(i));
                            this.casePhotoList.put(lineCount, casePhotos);
                            break;
                        case 4:
                            //小图
                            lineCount = this.casePhotoList.size();
                            casePhotos = this.casePhotoList.get(lineCount);
                            if (casePhotos == null) {
                                casePhotos = new ArrayList<>();
                            }
                            casePhotos.add(casePhotoList.get(i));
                            this.casePhotoList.put(lineCount, casePhotos);
                            break;
                        case 5:
                            casePhotos = this.casePhotoList.get(lineCount);
                            if (casePhotos != null) {
                                casePhotos.add(casePhotoList.get(i));
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void setTypeDetailTab(int index) {
        this.detailTabIndex = index;
    }

    public void setRecommendWorks(
            List<com.hunliji.hljcommonlibrary.models.Work> recommendWorks) {
        this.recommendWorks = recommendWorks;
        notifyDataSetChanged();
    }

    public void setOnTabSelectedListener(OnTabSelectedListener onTabSelectedListener) {
        this.onTabSelectedListener = onTabSelectedListener;
    }

    public void setOnInfoListener(WorkDetailInfoViewHolder.OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_INFO:
                return new WorkDetailInfoViewHolder(layoutInflater.inflate(R.layout
                                .work_detail_item_info,
                        parent,
                        false), work, isSnapshot, onInfoListener);
            case TYPE_COMMENT:
                return new WorkDetailCommentViewHolder(layoutInflater.inflate(R.layout
                                .work_detail_item_comment_and_merchant_info,
                        parent,
                        false), context, work);
            case TYPE_PHOTO_ITEM:
                return new ImgTxtViewHolder(layoutInflater.inflate(R.layout
                                .work_detail_item_image_text,
                        parent,
                        false));
            case TYPE_WORK_PARAMETERS:
                return new ParametersViewHolder(layoutInflater.inflate(R.layout
                                .work_detail_item_parameters,
                        parent,
                        false));
            case TYPE_RECOMMEND_WORKS:
                return new RecWorkViewHolder(layoutInflater.inflate(R.layout
                                .work_detail_item_recommend_work,
                        parent,
                        false));
            case TYPE_CASE_PHOTO_INTRODUCE:
                return new IntroduceCasePhotoViewHolder(layoutInflater.inflate(R.layout
                                .work_detail_customer_photo_introduce,
                        parent,
                        false));
            case TYPE_WORK_NOTES:
                return new NotesViewHolder(layoutInflater.inflate(R.layout.work_detail_item_notes,
                        parent,
                        false));
            case TYPE_CASE_PHOTO:
                return new CasePhotoViewHolder(layoutInflater.inflate(R.layout
                                .work_detail_customer_photo_item,
                        parent,
                        false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_COMMENT:
                holder.setView(context, cqZip, position, type);
                break;
            case TYPE_CASE_PHOTO:
                int casePosition = position - getTopItemsCount();
                holder.setView(context,
                        getCasePhoto(casePosition),
                        casePosition,
                        getItemViewType(position));
                break;
            default:
                holder.setView(context, getItem(position), position, getItemViewType(position));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_INFO;
            case 1:
                return TYPE_COMMENT;
            default:
                if (detailTabIndex == 0) {
                    if (position == getItemCount() - 1) {
                        return TYPE_WORK_NOTES;
                    } else if (getCasePhotoRealCount() > 0 && position == getItemCount() - 2) {
                        return TYPE_CASE_PHOTO_INTRODUCE;
                    } else if (position < getDetailPhotosCount() + getTopItemsCount()) {
                        return TYPE_PHOTO_ITEM;
                    } else
                        return TYPE_RECOMMEND_WORKS;
                } else if (detailTabIndex == 1) {
                    return TYPE_WORK_PARAMETERS;
                } else {
                    return TYPE_CASE_PHOTO;
                }
        }
    }

    public Object getItem(int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_PHOTO_ITEM:
                if (getDetailPhotosCount() > 0) {
                    return work.getDetailPhotos()
                            .get(position - getTopItemsCount());
                } else
                    return null;
            case TYPE_WORK_PARAMETERS:
                if (getParametersViewCount() > 0) {
                    return work.getParameters()
                            .get(position - getTopItemsCount());
                } else
                    return null;
            case TYPE_RECOMMEND_WORKS:
                if (getRecommendsWorksCount() > 0) {
                    return recommendWorks.get(position - getTopItemsCount() -
                            getDetailPhotosCount());
                } else
                    return null;
            case TYPE_CASE_PHOTO_INTRODUCE:
                if (getCasePhotoRealCount() > 0) {
                    return cqZip.casePhotoList.getData();
                }
                return null;
            default:
                return null;
        }
    }

    private List<CasePhoto> getCasePhoto(int position) {
        return casePhotoList.get(position);
    }


    @Override
    public long getHeaderId(int position) {
        if (position >= getDetailStartIndex()) {
            return 2;
        } else {
            return -1;
        }
    }

    @Override
    public BaseViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new TabViewHolder(layoutInflater.inflate(R.layout.work_detail_item_inside_tab,
                parent,
                false));
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int position) {
        TabViewHolder tabViewHolder = (TabViewHolder) holder;
        if (tabViewHolder == null) {
            return;
        }
        if (casePhotoList != null && casePhotoList.size() > 0) {
            tabViewHolder.setRbPhotoVisible(true);
        } else {
            tabViewHolder.setRbPhotoVisible(false);
        }
        tabViewHolder.setView(context, detailTabIndex, position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return getTopItemsCount() + getBottomItemsCount();
    }

    public int getTopItemsCount() {
        // 信息，评价，detail头部
        return 1 + 1;
    }

    public int getCommentIndex() {
        return 1;
    }

    public int getDetailStartIndex() {
        return 2;
    }

    private int getParametersStartIndex() {
        return 2;
    }

    private int getRecommendWorksStartIndex() {
        return getTopItemsCount() + getDetailPhotosCount();
    }

    private int getBottomItemsCount() {
        switch (detailTabIndex) {
            case 0:
                return getDetailsPhotosAndOthersCount();
            case 1:
                return getParametersViewCount();
            case 2:
                return getCasePhotoCount();
            default:
                break;
        }
        return 0;
    }

    private int getDetailsPhotosAndOthersCount() {
        return getDetailPhotosCount() + getRecommendsWorksCount() + getNotesCount();
    }

    private int getDetailPhotosCount() {
        return work.getDetailPhotos() == null ? 0 : work.getDetailPhotos()
                .size();
    }

    private int getRecommendsWorksCount() {
        return recommendWorks == null ? 0 : recommendWorks.size();
    }

    private int getNotesCount() {
        return detailTabIndex == 0 ? 1 : 0;
    }

    private int getParametersViewCount() {
        return work.getParameters() == null ? 0 : work.getParameters()
                .size();
    }

    private int getCasePhotoCount() {
        return casePhotoList.size();
    }

    /**
     * 套餐 图文介绍 计算客照的真实数量
     */
    private int getCasePhotoRealCount() {
        if (cqZip != null && cqZip.casePhotoList != null && cqZip.casePhotoList.getData() != null) {
            return CommonUtil.getCollectionSize(cqZip.casePhotoList.getData());
        }
        return 0;
    }


    class IntroduceCasePhotoViewHolder extends TrackerCasePhotoViewHolder {

        @BindView(R.id.tv_case_photo_count)
        TextView tvCasePhotoCount;
        @BindView(R.id.img_single)
        RoundedImageView imgSingle;
        @BindView(R.id.tv_single_title)
        TextView tvSingleTitle;
        @BindView(R.id.tv_single_time)
        TextView tvSingleTime;
        @BindView(R.id.layout_single)
        RelativeLayout layoutSingle;
        @BindView(R.id.view_space)
        View viewSpace;
        @BindView(R.id.rl_look_all)
        RelativeLayout rlLookAll;

        private int singleWidth;
        private int singleHeight;

        @OnClick(R.id.rl_look_all)
        void onLookAllClick() {
            if (onTabSelectedListener != null) {
                onTabSelectedListener.onTabSelected(2);
            }
        }

        IntroduceCasePhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Context context = itemView.getContext();
            singleWidth = CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 32);
            singleHeight = Math.round(singleWidth * 3 / 4);
            layoutSingle.getLayoutParams().height = singleHeight;
            layoutSingle.getLayoutParams().width = singleWidth;
        }

        @Override
        protected void setViewData(
                Context mContext, List<CasePhoto> casePhotos, int position, int viewType) {
            final CasePhoto casePhoto = casePhotos.get(0);
            if (casePhoto == null) {
                itemView.setVisibility(View.GONE);
                return;
            }
            if (getCasePhotoRealCount() > 1) {
                rlLookAll.setVisibility(View.VISIBLE);
                viewSpace.setVisibility(View.GONE);
            } else {
                rlLookAll.setVisibility(View.GONE);
                viewSpace.setVisibility(View.VISIBLE);
            }
            tvCasePhotoCount.setText(String.format(Locale.getDefault(),
                    "相关客照(%s)",
                    getCasePhotoRealCount()));
            trackerCasePhotoView(layoutSingle, position, casePhoto);
            tvSingleTitle.setText(TextUtils.isEmpty(casePhoto.getMarkContent()) ? "客照" :
                    casePhoto.getMarkContent());
            tvSingleTime.setText(casePhoto.getShootingTime());
            Glide.with(mContext)
                    .load(ImagePath.buildPath(casePhoto.getCoverPath())
                            .width(singleWidth)
                            .height(singleHeight)
                            .quality(75)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgSingle);
            layoutSingle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (casePhoto.getId() > 0) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CasePhotoDetailActivity.class);
                        intent.putExtra(CasePhotoDetailActivity.ARG_ID, casePhoto.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    class TabViewHolder extends BaseViewHolder {
        @BindView(R.id.rb_detail)
        RadioButton rbDetail;
        @BindView(R.id.rb_parameters)
        RadioButton rbParameters;
        @BindView(R.id.rb_photo)
        RadioButton rbPhoto;
        @BindView(R.id.line_rb_photo)
        View lineRbPhoto;

        CompoundButton.OnCheckedChangeListener rbDetailCheckListener = new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onTabSelectedListener != null) {
                        onTabSelectedListener.onTabSelected(0);
                    }
                }
            }
        };
        CompoundButton.OnCheckedChangeListener rbParametersCheckListener = new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (onTabSelectedListener != null) {
                        onTabSelectedListener.onTabSelected(1);
                    }
                }
            }
        };

        CompoundButton.OnCheckedChangeListener rbPhotoCheckListener = new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    HljViewTracker.fireViewClickEvent(buttonView);
                    if (onTabSelectedListener != null) {
                        onTabSelectedListener.onTabSelected(2);
                    }
                }
            }
        };

        TabViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            rbDetail.setChecked(true);
            rbParameters.setChecked(false);
            HljVTTagger.buildTagger(rbPhoto)
                    .tagName(HljTaggerName.WORK_CASE_PHOTO_TAB)
                    .hitTag();
        }

        @Override
        protected void setViewData(Context mContext, Object item, int position, int viewType) {
            int index = (int) item;
            rbDetail.setOnCheckedChangeListener(null);
            rbParameters.setOnCheckedChangeListener(null);
            rbPhoto.setOnCheckedChangeListener(null);
            if (index == 0) {
                rbDetail.setChecked(true);
                rbParameters.setChecked(false);
                rbPhoto.setChecked(false);
            } else if (index == 1) {
                rbDetail.setChecked(false);
                rbParameters.setChecked(true);
                rbPhoto.setChecked(false);
            } else if (index == 2) {
                rbDetail.setChecked(false);
                rbParameters.setChecked(false);
                rbPhoto.setChecked(true);
            }

            rbDetail.setOnCheckedChangeListener(rbDetailCheckListener);
            rbParameters.setOnCheckedChangeListener(rbParametersCheckListener);
            rbPhoto.setOnCheckedChangeListener(rbPhotoCheckListener);
        }

        void setRbPhotoVisible(boolean show) {
            lineRbPhoto.setVisibility(show ? View.VISIBLE : View.GONE);
            rbPhoto.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    class ImgTxtViewHolder extends BaseViewHolder {
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.img_play)
        ImageView imgPlay;
        @BindView(R.id.items_layout)
        RelativeLayout itemsLayout;
        @BindView(R.id.tv_describe)
        TextView tvDescribe;
        @BindView(R.id.tv_work_desc)
        TextView tvWorkDesc;

        View view;

        ImgTxtViewHolder(View v) {
            super(v);
            this.view = v;
            ButterKnife.bind(this, view);
            imgCover.getLayoutParams().width = itemImgWidth;
        }

        @Override
        protected void setViewData(
                Context mContext, Object item, final int position, int viewType) {
            final Photo photo = (Photo) item;
            if (photo == null) {
                return;
            }
            if (position == getDetailStartIndex() && !TextUtils.isEmpty(work.getDescribe())) {
                tvWorkDesc.setText(work.getDescribe());
                tvWorkDesc.setVisibility(View.VISIBLE);
            } else {
                tvWorkDesc.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(photo.getDescription())) {
                tvDescribe.setVisibility(View.GONE);
            } else {
                tvDescribe.setVisibility(View.VISIBLE);
                tvDescribe.setText(photo.getDescription());
            }
            Glide.with(context)
                    .load(ImagePath.buildPath(photo.getPath())
                            .path())
                    .apply(new RequestOptions().override(itemImgWidth, Target.SIZE_ORIGINAL)
                            .fitCenter())
                    .listener(new OriginalImageScaleListener(imgCover, itemImgWidth, 0))
                    .into(imgCover);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WorkMediaImageActivity.class);
                    intent.putExtra("work", work);
                    intent.putExtra(WorkMediaImageActivity.ARG_LOAD_DISABLE, true);
                    intent.putExtra("position", position - getDetailStartIndex());
                    context.startActivity(intent);
                }
            });
        }
    }

    class ParametersViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.name_layout)
        RelativeLayout nameLayout;
        @BindView(R.id.items_layout)
        LinearLayout itemsLayout;
        @BindView(R.id.top_padding)
        Space topPadding;
        @BindView(R.id.bottom_padding)
        Space bottomPadding;
        @BindView(R.id.top_line)
        View topLine;

        ParametersViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(Context mContext, Object item, int position, int viewType) {
            WorkParameter workParameter = (WorkParameter) item;
            if (workParameter == null) {
                return;
            }
            if (position == getParametersStartIndex()) {
                topLine.setVisibility(View.GONE);
                topPadding.setVisibility(View.VISIBLE);
            } else {
                topLine.setVisibility(View.VISIBLE);
                topPadding.setVisibility(View.GONE);
            }
            if (position == getItemCount() - 1) {
                bottomPadding.setVisibility(View.VISIBLE);
            } else {
                bottomPadding.setVisibility(View.GONE);
            }

            tvName.setText(workParameter.getTitle());
            int size = workParameter.getChildren() == null ? 0 : workParameter.getChildren()
                    .size();
            int childCount = itemsLayout.getChildCount();
            if (childCount > size) {
                itemsLayout.removeViews(size, childCount - size);
            }
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    WorkParameter parameter = workParameter.getChildren()
                            .get(i);
                    View childView = null;
                    if (i < childCount) {
                        childView = itemsLayout.getChildAt(i);
                    }
                    if (childView == null) {
                        childView = View.inflate(context, R.layout.work_parameter_item_view, null);
                        itemsLayout.addView(childView);
                    }
                    ChildViewHolder childViewHolder = (ChildViewHolder) childView.getTag();
                    if (childViewHolder == null) {
                        childViewHolder = new ChildViewHolder(childView);
                        childView.setTag(childViewHolder);
                    }
                    childViewHolder.key.setText(parameter.getTitle());
                    childViewHolder.value.setText(parameter.getValues());
                }
            }
        }

        class ChildViewHolder {
            @BindView(R.id.key)
            TextView key;
            @BindView(R.id.value)
            TextView value;

            ChildViewHolder(View view) {ButterKnife.bind(this, view);}
        }
    }

    class RecWorkViewHolder extends TrackerWorkViewHolder {
        @BindView(R.id.label_recommend)
        TextView labelRecommend;
        @BindView(R.id.top_layout)
        RelativeLayout topLayout;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.img_installment)
        ImageView imgInstallment;
        @BindView(R.id.img_hot_tag)
        ImageView imgHotTag;
        @BindView(R.id.img_custom)
        ImageView imgCustom;
        @BindView(R.id.img_cover_layout)
        RelativeLayout imgCoverLayout;
        @BindView(R.id.tv_work_title)
        TextView tvWorkTitle;
        @BindView(R.id.tv_hot_tag)
        TextView tvHotTag;
        @BindView(R.id.tv_installment)
        TextView tvInstallment;
        @BindView(R.id.tv_rmb)
        TextView tvRmb;
        @BindView(R.id.tv_work_price)
        TextView tvWorkPrice;
        @BindView(R.id.tv_work_collect)
        TextView tvWorkCollect;
        @BindView(R.id.tv_work_price1)
        TextView tvWorkPrice1;

        RecWorkViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public String cpmSource() {
            return WorkActivity.CPM_SOURCE;
        }

        @Override
        public View trackerView() {
            return itemView;
        }

        @Override
        protected void setViewData(
                Context mContext,
                final com.hunliji.hljcommonlibrary.models.Work work,
                final int position,
                int viewType) {
            if (work == null) {
                return;
            }

            if (position == getRecommendWorksStartIndex()) {
                topLayout.setVisibility(View.VISIBLE);
            } else {
                topLayout.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(ImagePath.buildPath(work.getCoverPath())
                            .width(CommonUtil.dp2px(context, 116))
                            .path())
                    .into(imgCover);
            imgInstallment.setVisibility(View.GONE);
            if (work.getHotTag() == 1 || work.getHotTag() == 2) {
                tvWorkTitle.setLineSpacing(0, 1f);
                tvWorkTitle.setMaxLines(1);
                tvHotTag.setVisibility(View.VISIBLE);
                tvHotTag.setText(work.getHotTag() == 1 ? R.string.label_work_rec : R.string
                        .label_work_top);
            } else {
                tvHotTag.setVisibility(View.GONE);
                tvWorkTitle.setLineSpacing(0, 1.3f);
                tvWorkTitle.setMaxLines(2);
            }
            tvWorkTitle.setText(work.getTitle());
            tvWorkPrice.setText(Util.formatDouble2String(work.getShowPrice()));
            tvWorkCollect.setText(context.getString(R.string.label_collect_count,
                    String.valueOf(work.getCollectorsCount())));
            if (work.getMarketPrice() > 0) {
                tvWorkPrice1.setVisibility(View.VISIBLE);
                tvWorkPrice1.getPaint()
                        .setAntiAlias(true);
                tvWorkPrice1.getPaint()
                        .setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                tvWorkPrice1.setText(Util.formatDouble2String(work.getMarketPrice()));
            } else {
                tvWorkPrice1.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WorkActivity.class);
                    JSONObject jsonObject = TrackerUtil.getSiteJson("S3/A1",
                            position + 1,
                            "套餐" + work.getId() + work.getTitle());
                    if (jsonObject != null) {
                        intent.putExtra("site", jsonObject.toString());
                    }
                    intent.putExtra("id", work.getId());
                    context.startActivity(intent);
                }
            });
        }
    }

    class NotesViewHolder extends BaseViewHolder {
        @BindView(R.id.promise_image)
        ImageView promiseImage;
        @BindView(R.id.promise_layout)
        LinearLayout promiseLayout;
        @BindView(R.id.label_notes)
        TextView labelNotes;
        @BindView(R.id.purchase_notes)
        TextView purchaseNotes;
        @BindView(R.id.purchase_notes_layout)
        RelativeLayout purchaseNotesLayout;

        NotesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(Context mContext, Object item, int position, int viewType) {
            DataConfig config = Session.getInstance()
                    .getDataConfig(context);
            if (config != null && !JSONUtil.isEmpty(config.getPrepayRemind(work.getPropertyId()))) {
                purchaseNotesLayout.setVisibility(View.VISIBLE);
                purchaseNotes.setText(config.getPrepayRemind(work.getPropertyId()));
            } else {
                purchaseNotesLayout.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(work.getPromiseImage()) && !isSnapshot) {
                promiseLayout.setVisibility(View.VISIBLE);
                promiseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (work != null && !JSONUtil.isEmpty(work.getPromisePath())) {
                            String path = work.getPromisePath();
                            HljWeb.startWebView(context, path);
                        }
                    }
                });
                Glide.with(context)
                        .load(ImagePath.buildPath(work.getPromiseImage())
                                .width(CommonUtil.getDeviceSize(context).x)
                                .path())
                        .into(promiseImage);
            } else {
                promiseLayout.setVisibility(View.GONE);
            }
        }
    }

    class CasePhotoViewHolder extends TrackerCasePhotoViewHolder {

        @BindView(R.id.img_single)
        RoundedImageView imgSingle;
        @BindView(R.id.tv_single_title)
        TextView tvSingleTitle;
        @BindView(R.id.tv_single_time)
        TextView tvSingleTime;
        @BindView(R.id.layout_single)
        RelativeLayout layoutSingle;
        @BindView(R.id.img_double_left)
        RoundedImageView imgDoubleLeft;
        @BindView(R.id.tv_double_left_title)
        TextView tvDoubleLeftTitle;
        @BindView(R.id.tv_double_left_time)
        TextView tvDoubleLeftTime;
        @BindView(R.id.layout_double_left)
        RelativeLayout layoutDoubleLeft;
        @BindView(R.id.img_double_right)
        RoundedImageView imgDoubleRight;
        @BindView(R.id.tv_double_right_title)
        TextView tvDoubleRightTitle;
        @BindView(R.id.tv_double_right_time)
        TextView tvDoubleRightTime;
        @BindView(R.id.layout_double_right)
        RelativeLayout layoutDoubleRight;
        @BindView(R.id.layout_double)
        LinearLayout layoutDouble;
        @BindView(R.id.space_top)
        Space spaceTop;
        @BindView(R.id.space_bottom)
        Space spaceBottom;

        private Context mContext;
        private int singleWidth;
        private int singleHeight;
        private int doubleWidth;
        private int doubleHeight;

        CasePhotoViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            ButterKnife.bind(this, itemView);
            singleWidth = CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext, 32);
            singleHeight = Math.round(singleWidth * 3 / 4);
            doubleWidth = (CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext,
                    42)) / 2;
            doubleHeight = Math.round(doubleWidth * 3 / 4);
        }

        private void onCasePhotoClick(CasePhoto casePhoto) {
            if (casePhoto == null || casePhoto.getId() <= 0) {
                return;
            }
            Intent intent = new Intent(mContext, CasePhotoDetailActivity.class);
            intent.putExtra(CasePhotoDetailActivity.ARG_ID, casePhoto.getId());
            mContext.startActivity(intent);
        }

        @Override
        protected void setViewData(
                Context mContext, List<CasePhoto> casePhotoList, int position, int viewType) {
            if (casePhotoList == null || casePhotoList.isEmpty()) {
                return;
            }

            spaceBottom.setVisibility(position == getCasePhotoCount() - 1 ? View.VISIBLE : View
                    .GONE);

            if (casePhotoList.size() == 1) {

                final CasePhoto casePhoto = casePhotoList.get(0);

                trackerCasePhotoView(layoutSingle, position, casePhoto);

                layoutDouble.setVisibility(View.GONE);
                layoutSingle.setVisibility(View.VISIBLE);
                layoutSingle.getLayoutParams().height = singleHeight;

                tvSingleTitle.setText(TextUtils.isEmpty(casePhoto.getMarkContent()) ? "客照" :
                        casePhoto.getMarkContent());
                tvSingleTime.setText(casePhoto.getShootingTime());
                Glide.with(mContext)
                        .load(ImagePath.buildPath(casePhoto.getCoverPath())
                                .width(singleWidth)
                                .height(singleHeight)
                                .quality(75)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imgSingle);
                layoutSingle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCasePhotoClick(casePhoto);
                    }
                });
            } else if (casePhotoList.size() == 2) {
                layoutDouble.setVisibility(View.VISIBLE);
                layoutSingle.setVisibility(View.GONE);
                layoutDouble.getLayoutParams().height = doubleHeight;

                final CasePhoto casePhotoLeft = casePhotoList.get(0);

                trackerCasePhotoView(layoutDoubleLeft, position, casePhotoLeft);

                tvDoubleLeftTitle.setText(TextUtils.isEmpty(casePhotoLeft.getMarkContent()) ?
                        "客照" : casePhotoLeft.getMarkContent());
                tvDoubleLeftTime.setText(casePhotoLeft.getShootingTime());
                Glide.with(mContext)
                        .load(ImagePath.buildPath(casePhotoLeft.getCoverPath())
                                .width(singleWidth)
                                .height(singleHeight)
                                .quality(75)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imgDoubleLeft);
                layoutDoubleLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCasePhotoClick(casePhotoLeft);
                    }
                });

                final CasePhoto casePhotoRight = casePhotoList.get(1);

                trackerCasePhotoView(layoutDoubleRight, position, casePhotoRight);

                tvDoubleRightTitle.setText(TextUtils.isEmpty(casePhotoRight.getMarkContent()) ?
                        "客照" : casePhotoRight.getMarkContent());
                tvDoubleRightTime.setText(casePhotoRight.getShootingTime());
                Glide.with(mContext)
                        .load(ImagePath.buildPath(casePhotoRight.getCoverPath())
                                .width(singleWidth)
                                .height(singleHeight)
                                .quality(75)
                                .cropPath())
                        .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                        .into(imgDoubleRight);
                layoutDoubleRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCasePhotoClick(casePhotoRight);
                    }
                });
            }
        }
    }

    public interface OnTabSelectedListener {
        void onTabSelected(int index);
    }
}
