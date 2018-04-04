package me.suncloud.marrymemo.adpter.finder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.models.WorkMediaItem;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.viewholder.BaseFinderCaseViewHolder;
import me.suncloud.marrymemo.view.finder.FinderCaseMediaPagerActivity;

/**
 * Created by mo_yu on 2018/2/7.发现页-案例列表
 */

public class FinderCaseRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int IMAGE_STYLE_1 = 1;//大图模式
    private static final int IMAGE_STYLE_2 = 2;//两图模式
    private static final int IMAGE_STYLE_3 = 3;//三图模式
    private static final int IMAGE_STYLE_4 = 4;//四图模式
    private static final int FOOTER_TYPE = 5;
    private static final int HEADER_TYPE = 6;
    private static final int EMPTY_TYPE = 7;

    private Context context;
    private View footerView;
    private View headerView;
    private List<Work> cases;
    private String attrType;
    private LayoutInflater inflater;
    private BaseFinderCaseViewHolder.OnSearchSamesListener onSearchSamesListener;
    private BaseFinderCaseViewHolder.OnCollectCaseListener onCollectCaseListener;
    private OnCaseImageClickListener onCaseImageClickListener;

    public FinderCaseRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Work> getCases() {
        return cases;
    }

    public void setCases(List<Work> cases) {
        this.cases = cases;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    private int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public interface OnCaseImageClickListener {
        void onImageClick(Context context, Work work, long mediaId);
    }

    public void setOnCaseImageClickListener(OnCaseImageClickListener onCaseImageClickListener) {
        this.onCaseImageClickListener = onCaseImageClickListener;
    }

    public void setOnSearchSamesListener(
            BaseFinderCaseViewHolder.OnSearchSamesListener onSearchSamesListener) {
        this.onSearchSamesListener = onSearchSamesListener;
    }

    public void setOnCollectCaseListener(
            BaseFinderCaseViewHolder.OnCollectCaseListener onCollectCaseListener) {
        this.onCollectCaseListener = onCollectCaseListener;
    }

    public void addCases(List<Work> list) {
        if (!CommonUtil.isCollectionEmpty(list)) {
            int start = getItemCount() - getFooterViewCount();
            cases.addAll(list);
            notifyItemRangeInserted(start, list.size());
        }
    }

    public void addSamesCases(int position, List<Work> list) {
        if (position == -1) {
            return;
        }
        if (!CommonUtil.isCollectionEmpty(list)) {
            int start = position + getHeaderViewCount();
            cases.addAll(position + 1, list);
            notifyItemRangeInserted(start + 1, list.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return HEADER_TYPE;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return getItemImageStyle(getItem(position));
        }
    }

    private int getItemImageStyle(Work work) {
        if (work == null || CommonUtil.isCollectionEmpty(work.getMediaItems())) {
            return EMPTY_TYPE;
        }
        List<WorkMediaItem> verticalMediaItems = new ArrayList<>();
        List<WorkMediaItem> horizontalMediaItems = new ArrayList<>();
        int size = Math.min(4,
                work.getMediaItems()
                        .size());
        for (int i = 0; i < size; i++) {
            WorkMediaItem workMediaItem = work.getMediaItems()
                    .get(i);
            if (workMediaItem.getHeight() > workMediaItem.getWidth()) {
                verticalMediaItems.add(workMediaItem);
            } else {
                horizontalMediaItems.add(workMediaItem);
            }
        }
        work.setVerticalMediaItems(verticalMediaItems);
        work.setHorizontalMediaItems(horizontalMediaItems);
        if (verticalMediaItems.size() == 0 && size >= 4) {
            //没有竖图，图片数目大于等于4，显示四图模式
            return IMAGE_STYLE_4;
        } else if (verticalMediaItems.size() == 1 && size >= 3) {
            //有一张竖图，图片数目大于等于2，显示三图模式
            return IMAGE_STYLE_3;
        } else if (verticalMediaItems.size() >= 2) {
            //有两张竖图，显示两图模式
            return IMAGE_STYLE_2;
        } else {
            return IMAGE_STYLE_1;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case EMPTY_TYPE:
                return new ExtraBaseViewHolder(inflater.inflate(R.layout.empty_place_holder___cm,
                        parent,
                        false));
            case IMAGE_STYLE_1:
                return new BigImageCaseViewHolder(inflater.inflate(R.layout
                                .finder_case_list_item___cv,
                        parent,
                        false));
            case IMAGE_STYLE_2:
                return new TwoImageCaseViewHolder(inflater.inflate(R.layout
                                .finder_case_list_item___cv,
                        parent,
                        false));
            case IMAGE_STYLE_3:
                return new ThreeImageCaseViewHolder(inflater.inflate(R.layout
                                .finder_case_list_item___cv,
                        parent,
                        false));
            case IMAGE_STYLE_4:
                return new FourImageCaseViewHolder(inflater.inflate(R.layout
                                .finder_case_list_item___cv,
                        parent,
                        false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case IMAGE_STYLE_1:
            case IMAGE_STYLE_2:
            case IMAGE_STYLE_3:
            case IMAGE_STYLE_4:
                BaseFinderCaseViewHolder caseViewHolder = (BaseFinderCaseViewHolder) holder;
                caseViewHolder.setOnCollectCaseListener(onCollectCaseListener);
                caseViewHolder.setOnSearchSamesListener(onSearchSamesListener);
                caseViewHolder.setView(context, getItem(position), position, type);
                break;
        }
    }

    private Work getItem(int position) {
        position = position - getHeaderViewCount();
        return cases.get(position);
    }

    @Override
    public int getItemCount() {
        return getFooterViewCount() + getHeaderViewCount() + (cases == null ? 0 : cases.size());
    }

    public class BigImageCaseViewHolder extends BaseFinderCaseViewHolder {

        public BigImageCaseViewHolder(View view) {
            super(view);
            imgCaseCover1.setVisibility(View.VISIBLE);
            imgCaseCover1.getLayoutParams().width = horizontalImageWidth;
            imgCaseCover1.getLayoutParams().height = horizontalImageHeight;
        }

        @Override
        protected void setViewData(Context mContext, final Work item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            if (!CommonUtil.isCollectionEmpty(item.getMediaItems())) {
                mediaItem1 = item.getMediaItems()
                        .get(0);
                imgCaseCover1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goCaseViewPager(v.getContext(), item, mediaItem1.getId());
                    }
                });
                String path1 = ImagePath.buildPath(mediaItem1.getItemCover())
                        .width(horizontalImageWidth)
                        .path();
                mediaItem1.setLocalPath(path1);
                Glide.with(mContext)
                        .load(path1)
                        .into(imgCaseCover1);
            }
        }
    }

    public class TwoImageCaseViewHolder extends BaseFinderCaseViewHolder {

        public TwoImageCaseViewHolder(View view) {
            super(view);
            imgCaseCover1.setVisibility(View.VISIBLE);
            imgCaseCover3.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)
                    imgCaseCover1.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = horizontalImageHeight;
            layoutParams.rightMargin = imageSpace / 2;
            layoutParams = (ViewGroup.MarginLayoutParams) imgCaseCover3.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = horizontalImageHeight;
            layoutParams.leftMargin = imageSpace - imageSpace / 2;
        }

        @Override
        protected void setViewData(Context mContext, final Work item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            mediaItem1 = item.getVerticalMediaItems()
                    .get(0);
            imgCaseCover1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem1.getId());
                }
            });
            mediaItem3 = item.getVerticalMediaItems()
                    .get(1);
            imgCaseCover3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem3.getId());
                }
            });
            String path1 = ImagePath.buildPath(mediaItem1.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            String path3 = ImagePath.buildPath(mediaItem3.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            mediaItem1.setLocalPath(path1);
            mediaItem3.setLocalPath(path3);
            Glide.with(mContext)
                    .load(path1)
                    .into(imgCaseCover1);
            Glide.with(mContext)
                    .load(path3)
                    .into(imgCaseCover3);
        }
    }

    public class ThreeImageCaseViewHolder extends BaseFinderCaseViewHolder {

        public ThreeImageCaseViewHolder(View view) {
            super(view);
            imgCaseCover1.setVisibility(View.VISIBLE);
            imgCaseCover3.setVisibility(View.VISIBLE);
            imgCaseCover4.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)
                    imgCaseCover1.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = horizontalImageHeight;
            layoutParams.rightMargin = imageSpace / 2;
            layoutParams = (ViewGroup.MarginLayoutParams) imgCaseCover3.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = smallImageHeight;
            layoutParams.leftMargin = imageSpace / 2;
            layoutParams = (ViewGroup.MarginLayoutParams) imgCaseCover4.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = smallImageHeight;
            layoutParams.leftMargin = imageSpace / 2;
            layoutParams.topMargin = imageSpace;
        }

        @Override
        protected void setViewData(Context mContext, final Work item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            mediaItem1 = item.getVerticalMediaItems()
                    .get(0);
            imgCaseCover1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem1.getId());
                }
            });
            mediaItem3 = item.getHorizontalMediaItems()
                    .get(0);
            imgCaseCover3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem3.getId());
                }
            });
            mediaItem4 = item.getHorizontalMediaItems()
                    .get(1);
            imgCaseCover4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem4.getId());
                }
            });
            String path1 = ImagePath.buildPath(mediaItem1.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            String path3 = ImagePath.buildPath(mediaItem3.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            String path4 = ImagePath.buildPath(mediaItem4.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            mediaItem1.setLocalPath(path1);
            mediaItem3.setLocalPath(path3);
            mediaItem4.setLocalPath(path4);
            Glide.with(mContext)
                    .load(path1)
                    .into(imgCaseCover1);
            Glide.with(mContext)
                    .load(path3)
                    .into(imgCaseCover3);
            Glide.with(mContext)
                    .load(path4)
                    .into(imgCaseCover4);

        }
    }

    public class FourImageCaseViewHolder extends BaseFinderCaseViewHolder {

        public FourImageCaseViewHolder(View view) {
            super(view);
            imgCaseCover1.setVisibility(View.VISIBLE);
            imgCaseCover2.setVisibility(View.VISIBLE);
            imgCaseCover3.setVisibility(View.VISIBLE);
            imgCaseCover4.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)
                    imgCaseCover1.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = smallImageHeight;
            layoutParams.rightMargin = imageSpace / 2;
            layoutParams = (ViewGroup.MarginLayoutParams) imgCaseCover2.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = smallImageHeight;
            layoutParams.rightMargin = imageSpace / 2;
            layoutParams.topMargin = imageSpace;
            layoutParams = (ViewGroup.MarginLayoutParams) imgCaseCover3.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = smallImageHeight;
            layoutParams.leftMargin = imageSpace - imageSpace / 2;
            layoutParams = (ViewGroup.MarginLayoutParams) imgCaseCover4.getLayoutParams();
            layoutParams.width = verticalImageWidth;
            layoutParams.height = smallImageHeight;
            layoutParams.leftMargin = imageSpace - imageSpace / 2;
            layoutParams.topMargin = imageSpace;
        }

        @Override
        protected void setViewData(Context mContext, final Work item, int position, int viewType) {
            super.setViewData(mContext, item, position, viewType);
            mediaItem1 = item.getMediaItems()
                    .get(0);
            imgCaseCover1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem1.getId());
                }
            });
            mediaItem2 = item.getMediaItems()
                    .get(2);
            imgCaseCover2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem2.getId());
                }
            });
            mediaItem3 = item.getMediaItems()
                    .get(1);
            imgCaseCover3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem3.getId());
                }
            });
            mediaItem4 = item.getMediaItems()
                    .get(3);
            imgCaseCover4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goCaseViewPager(v.getContext(), item, mediaItem4.getId());
                }
            });
            String path1 = ImagePath.buildPath(mediaItem1.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            String path2 = ImagePath.buildPath(mediaItem2.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            String path3 = ImagePath.buildPath(mediaItem3.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            String path4 = ImagePath.buildPath(mediaItem4.getItemCover())
                    .width(verticalImageWidth)
                    .path();
            mediaItem1.setLocalPath(path1);
            mediaItem2.setLocalPath(path2);
            mediaItem3.setLocalPath(path3);
            mediaItem4.setLocalPath(path4);
            Glide.with(mContext)
                    .load(path1)
                    .into(imgCaseCover1);
            Glide.with(mContext)
                    .load(path2)
                    .into(imgCaseCover2);
            Glide.with(mContext)
                    .load(path3)
                    .into(imgCaseCover3);
            Glide.with(mContext)
                    .load(path4)
                    .into(imgCaseCover4);
        }
    }

    public void goCaseViewPager(Context context, Work work, long mediaId) {
        if (onCaseImageClickListener != null) {
            onCaseImageClickListener.onImageClick(context, work, mediaId);
        }
    }
}
