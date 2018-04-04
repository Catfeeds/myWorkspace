package me.suncloud.marrymemo.adpter.themephotography;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.PosterFloor;
import com.hunliji.hljcommonlibrary.models.Work;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.themephotography.JourneyTheme;
import me.suncloud.marrymemo.util.ImageLoadUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.viewholder.themephotography.AmorousLevel2ViewHolder;
import me.suncloud.marrymemo.viewholder.themephotography.AmorousViewHolder;
import me.suncloud.marrymemo.viewholder.themephotography.DestinationViewHolder;
import me.suncloud.marrymemo.viewholder.themephotography.GuideViewHolder;
import me.suncloud.marrymemo.viewholder.themephotography.ImageViewHolder;
import me.suncloud.marrymemo.viewholder.themephotography.MerchantViewHolder;
import me.suncloud.marrymemo.viewholder.themephotography.WorkViewHolder;

/**
 * 旅拍热城和单城市 adapter
 * Created by jinxin on 2016/9/19.
 */
public class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private JourneyTheme theme;
    private List<TypeWrapper> itemList;
    private int themeType;//Constant.THEME_TYPE
    private DisplayMetrics dm;
    private Point point;
    private int padding;

    public ThemeAdapter(Context mContext, int themeType) {
        this.mContext = mContext;
        this.themeType = themeType;
        itemList = new ArrayList<>();
        dm = mContext.getResources()
                .getDisplayMetrics();
        point = JSONUtil.getDeviceSize(mContext);
        padding = Math.round(dm.density * 10);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case Constants.JOURNEY_TYPE.AMOROUS:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.journey_amorous_item, parent, false);
                return new AmorousViewHolder(itemView, mContext);
            case Constants.JOURNEY_TYPE.AMOROUSLEVEL2:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.journey_amorous_item, parent, false);
                return new AmorousLevel2ViewHolder(itemView, mContext);
            case Constants.JOURNEY_TYPE.IMAGE:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.journey_image_item, parent, false);
                return new ImageViewHolder(itemView);
            case Constants.JOURNEY_TYPE.DESTINATION:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.journey_destination_item, parent, false);
                return new DestinationViewHolder(itemView, mContext);
            case Constants.JOURNEY_TYPE.GUIDE:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.journey_guide_item, parent, false);
                return new GuideViewHolder(itemView, mContext, theme);
            case Constants.JOURNEY_TYPE.WORK:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.journey_work_item, parent, false);
                return new WorkViewHolder(itemView, mContext, theme, themeType);
            case Constants.JOURNEY_TYPE.MERCHANT:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.journey_merchant_item, parent, false);
                return new MerchantViewHolder(itemView, mContext, theme);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TypeWrapper wrapper = itemList.get(position);
        int viewType = wrapper.type;
        switch (viewType) {
            case Constants.JOURNEY_TYPE.AMOROUS:
                AmorousViewHolder amorousViewHolder = (AmorousViewHolder) holder;
                setAmorous(amorousViewHolder);
                break;
            case Constants.JOURNEY_TYPE.AMOROUSLEVEL2:
                AmorousLevel2ViewHolder amorousLevel2ViewHolder = (AmorousLevel2ViewHolder) holder;
                setAmorousLevel2(amorousLevel2ViewHolder);
                break;
            case Constants.JOURNEY_TYPE.IMAGE:
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                setImage(imageViewHolder);
                break;
            case Constants.JOURNEY_TYPE.DESTINATION:
                DestinationViewHolder destinationViewHolder = (DestinationViewHolder) holder;
                setDestination(destinationViewHolder, position, wrapper);
                break;
            case Constants.JOURNEY_TYPE.GUIDE:
                GuideViewHolder guideViewHolder = (GuideViewHolder) holder;
                setGuide(guideViewHolder, position);
                break;
            case Constants.JOURNEY_TYPE.WORK:
                WorkViewHolder workViewHolder = (WorkViewHolder) holder;
                workViewHolder.tvAllInfo.setVisibility(View.GONE);
                workViewHolder.ivArrowRight.setVisibility(View.GONE);
                setWork(workViewHolder, position, wrapper);
                break;
            case Constants.JOURNEY_TYPE.MERCHANT:
                MerchantViewHolder merchantViewHolder = (MerchantViewHolder) holder;
                setMerchant(merchantViewHolder, position, wrapper);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);

        if (theme == null || itemList == null) {
            return type;
        }
        type = itemList.get(position).type;
        return type;
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public void setTheme(JourneyTheme theme) {
        this.theme = theme;
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.clear();
        //特色风情 旅拍单元
        if (!JSONUtil.isEmpty(theme.getCoverPath()) && (themeType == Constants.THEME_TYPE
                .AMOROUS_CITY || themeType == Constants.THEME_TYPE.UNIT)) {
            TypeWrapper wrapper = new TypeWrapper();
            wrapper.type = Constants.JOURNEY_TYPE.IMAGE;
            wrapper.collectPosition = -1;
            itemList.add(wrapper);
            wrapper.position = itemList.size();
        }

        //热城
        if (themeType == Constants.THEME_TYPE.HOTCITY) {
            if (theme.getPoster() != null && theme.getPoster()
                    .getHoles() != null) {
                List<PosterFloor> floors = theme.getPoster()
                        .getHoles();
                Iterator<PosterFloor> iterator = floors.iterator();
                boolean hasContent = false;
                while (iterator.hasNext()) {
                    PosterFloor floor = iterator.next();
                    if (floor == null || (floor.getPoster() == null || floor.getPoster()
                            .getId() == 0)) {
                        iterator.remove();
                    } else {
                        hasContent = true;
                    }
                }
                if (hasContent) {
                    int line = (int) Math.ceil(floors.size() / 2);
                    for (int i = 0, size = line + 1; i < size; i++) {
                        TypeWrapper wrapper = new TypeWrapper();
                        wrapper.type = Constants.JOURNEY_TYPE.DESTINATION;
                        wrapper.collectPosition = i;
                        wrapper.isItemHeader = i == 0;
                        itemList.add(wrapper);
                        wrapper.position = itemList.size();
                    }
                }
            }
        }

        //特色风情
        if (themeType == Constants.THEME_TYPE.AMOROUS) {
            if (theme.getPoster() != null && theme.getPoster()
                    .getHoles() != null) {
                List<PosterFloor> floors = theme.getPoster()
                        .getHoles();
                boolean hasContent = false;
                for (PosterFloor floor : floors) {
                    if (floor != null) {
                        hasContent = true;
                        break;
                    }
                }
                if (hasContent) {
                    TypeWrapper wrapper = new TypeWrapper();
                    wrapper.type = Constants.JOURNEY_TYPE.AMOROUS;
                    wrapper.collectPosition = -1;
                    itemList.add(wrapper);
                    wrapper.position = itemList.size();
                }
            }
        }

        //特色风情二级页
        if (themeType == Constants.THEME_TYPE.AMOROUSLEVEL2) {
            if (theme.getPoster() != null && theme.getPoster()
                    .getHoles() != null) {
                List<PosterFloor> floors = theme.getPoster()
                        .getHoles();
                boolean hasContent = false;
                for (PosterFloor floor : floors) {
                    if (floor != null) {
                        hasContent = true;
                        break;
                    }
                }
                if (hasContent) {
                    TypeWrapper wrapper = new TypeWrapper();
                    wrapper.type = Constants.JOURNEY_TYPE.AMOROUSLEVEL2;
                    wrapper.collectPosition = -1;
                    itemList.add(wrapper);
                    wrapper.position = itemList.size();
                }
            }
        }

        if (theme.getGuides() != null && !theme.getGuides()
                .isEmpty()) {
            TypeWrapper wrapper = new TypeWrapper();
            wrapper.type = Constants.JOURNEY_TYPE.GUIDE;
            wrapper.collectPosition = -1;
            itemList.add(wrapper);
            wrapper.position = itemList.size();
        }

        if (theme.getWorks() != null && !theme.getWorks()
                .isEmpty()) {
            List<Work> works = theme.getWorks();
            for (int i = 0, size = works.size() + 1; i < size; i++) {
                TypeWrapper wrapper = new TypeWrapper();
                wrapper.type = Constants.JOURNEY_TYPE.WORK;
                if (i == 0) {
                    wrapper.collectPosition = -1;
                    wrapper.isItemHeader = true;
                } else {
                    wrapper.collectPosition = i - 1;
                    wrapper.isItemHeader = false;
                }
                itemList.add(wrapper);
                wrapper.position = itemList.size();
            }
        }

        if (theme.getMerchants() != null && !theme.getMerchants()
                .isEmpty()) {
            List<Merchant> works = theme.getMerchants();
            for (int i = 0, size = works.size() + 1; i < size; i++) {
                TypeWrapper wrapper = new TypeWrapper();
                wrapper.type = Constants.JOURNEY_TYPE.MERCHANT;
                if (i == 0) {
                    wrapper.collectPosition = -1;
                    wrapper.isItemHeader = true;
                } else {
                    wrapper.collectPosition = i - 1;
                    wrapper.isItemHeader = false;
                }
                itemList.add(wrapper);
                wrapper.position = itemList.size();
            }
        }
        notifyDataSetChanged();
    }

    private void setAmorous(AmorousViewHolder holder) {
        if (theme.getPoster() == null || theme.getPoster()
                .getHoles() == null) {
            return;
        }
        holder.setAmorous(theme.getPoster()
                .getHoles());
    }

    private void setAmorousLevel2(AmorousLevel2ViewHolder holder) {
        if (theme.getPoster() == null || theme.getPoster()
                .getHoles() == null) {
            return;
        }
        holder.setAmorousLevel2(theme.getPoster()
                .getHoles());
    }

    private void setImage(ImageViewHolder holder) {
        if (theme == null) {
            return;
        }
        int height = Math.round(point.x * 1.0f / 2);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT,
                height);
        holder.itemView.setLayoutParams(params);
        String imgPath = JSONUtil.getImagePath(theme.getCoverPath(), point.x);
        if (!JSONUtil.isEmpty(imgPath)) {
            ImageLoadUtil.loadImageView(mContext, imgPath, holder.image);
        } else {
            ImageLoadUtil.clear(mContext, holder.image);
        }

        holder.tvTitle.setText(theme.getTitle());
        holder.tvCount.setText(String.valueOf(theme.getWatchCount()));
    }

    private void setDestination(DestinationViewHolder holder, int position, TypeWrapper wrapper) {
        if (theme.getPoster() == null || theme.getPoster()
                .getHoles() == null) {
            return;
        }
        if (wrapper.isItemHeader) {
            if (position == 0) {
                holder.itemView.setPadding(holder.itemView.getPaddingLeft(),
                        0,
                        holder.itemView.getPaddingRight(),
                        0);
            } else {
                holder.itemView.setPadding(holder.itemView.getPaddingLeft(),
                        padding,
                        holder.itemView.getPaddingRight(),
                        0);
            }
        }
        List<PosterFloor> floors = theme.getPoster()
                .getHoles();
        int line = wrapper.collectPosition - 1;
        int left = line * 2;
        int limit = floors.size() - 1;

        if (left <= limit) {
            holder.setDestination(holder.layoutDestination.getChildAt(0),
                    wrapper.isItemHeader ? null : floors.get(left),
                    wrapper.isItemHeader);
        }
        int right = left + 1;
        if (right <= limit) {
            holder.setDestination(holder.layoutDestination.getChildAt(1),
                    wrapper.isItemHeader ? null : floors.get(right),
                    wrapper.isItemHeader);
        }
    }

    private void setGuide(GuideViewHolder holder, int position) {
        if (theme.getGuides() == null || theme.getGuides()
                .isEmpty()) {
            return;
        }

        if (position == 0) {
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(),
                    0,
                    holder.itemView.getPaddingRight(),
                    holder.itemView.getPaddingBottom());
        } else {
            holder.itemView.setPadding(holder.itemView.getPaddingLeft(),
                    padding,
                    holder.itemView.getPaddingRight(),
                    holder.itemView.getPaddingBottom());
        }
        //攻略
        holder.setGuides(theme.getGuides());
    }

    private void setWork(WorkViewHolder holder, int position, TypeWrapper wrapper) {
        if (holder == null) {
            return;
        }
        if (theme == null || theme.getWorks() == null || theme.getWorks()
                .isEmpty()) {
            return;
        }
        int topPadding;
        if (wrapper.isItemHeader) {
            if (position == 0) {
                topPadding = 0;
            } else {
                topPadding = padding;
            }
        } else {
            topPadding = 0;
        }
        holder.itemView.setPadding(holder.itemView.getPaddingLeft(),
                topPadding,
                holder.itemView.getPaddingRight(),
                holder.itemView.getPaddingBottom());
        List<Work> works = theme.getWorks();
        holder.setWork(wrapper.isItemHeader ? null : works.get(wrapper.collectPosition),
                wrapper.collectPosition,
                works.size(),
                wrapper.isItemHeader);
    }

    private void setMerchant(MerchantViewHolder holder, int position, TypeWrapper wrapper) {
        if (holder == null) {
            return;
        }
        if (theme == null || theme.getMerchants() == null || theme.getMerchants()
                .isEmpty()) {
            return;
        }

        int topPadding = 0;
        if (wrapper.isItemHeader) {
            if (position == 0) {
                topPadding = 0;
            } else {
                topPadding = padding;
            }
        }
        holder.itemView.setPadding(holder.itemView.getPaddingLeft(),
                topPadding,
                holder.itemView.getPaddingRight(),
                holder.itemView.getPaddingBottom());
        List<Merchant> merchants = theme.getMerchants();
        holder.setMerchant(wrapper.isItemHeader ? null : merchants.get(wrapper.collectPosition),
                wrapper.collectPosition,
                merchants.size(),
                wrapper.isItemHeader);
    }

    private class TypeWrapper {
        private int position;//item position
        private int type;//item type
        private int collectPosition;//list 对应的position
        private boolean isItemHeader;//item header
    }

}
