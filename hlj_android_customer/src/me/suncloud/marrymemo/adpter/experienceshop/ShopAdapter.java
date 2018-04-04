package me.suncloud.marrymemo.adpter.experienceshop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.modelwrappers.PosterData;
import com.hunliji.hljhttplibrary.utils.PosterUtil;

import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.experience.ExperienceShop;
import me.suncloud.marrymemo.model.experience.Planner;
import me.suncloud.marrymemo.model.experience.ShopResultZip;
import me.suncloud.marrymemo.model.experience.ShopResultZipWrapper;
import me.suncloud.marrymemo.model.experience.Store;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceShopAddressHolder;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceShopAlbumHolder;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceShopCommentsHolder;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceShopEventHolder;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceShopHeaderViewHolder;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceShopImageViewHolder;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceShopPlannerHolder;
import me.suncloud.marrymemo.viewholder.experienceshop.ExperienceShopVideoHolder;

/**
 * Created by jinxin on 2017/3/24 0024.
 */

public class ShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int ITEM_HEADER = 1;//头图
    private final int ITEM_IMAGE = 2;//单个banner
    private final int ITEM_VIDEO = 3;//视屏
    private final int ITEM_EVENTS = 4;//人气活动
    private final int ITEM_PLANNER = 5;//人气统筹师
    private final int ITEM_COMMENT = 6;//评论
    private final int ITEM_ADDRESS = 7;//店铺地址
    private final int ITEM_ALBUM = 8;//精彩图集
    private final int ITEM_FOOTER = 9;//footer

    private Context mContext;
    private ShopMeasureSize measureSize;
    private View headerView;
    private ShopResultZip resultZip;
    private ShopResultZipWrapper zipWrapper;
    private SparseArray<Integer> types;

    private ExperienceShopVideoHolder videoHolder;
    private ExperienceShopAddressHolder addressHolder;
    private long id;//体验店Id

    public ShopAdapter(Context mContext) {
        this.mContext = mContext;
        measureSize = new ShopMeasureSize(mContext);
        types = new SparseArray<>();
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setResultZip(ShopResultZip resultZip, long id) {
        this.resultZip = resultZip;
        this.id = id;
        initTypes();
    }

    private void initTypes() {
        types.clear();
        zipWrapper = createShopResultZipWrapper();
        if (zipWrapper != null) {
            //顶图
            if (zipWrapper.topPosters != null && !zipWrapper.topPosters.isEmpty()) {
                types.put(ITEM_HEADER, ITEM_HEADER);
            }
            //单图
            if (zipWrapper.singlePoster != null && !zipWrapper.singlePoster.isEmpty()) {
                types.put(ITEM_IMAGE, ITEM_IMAGE);
            }

            //视频
            if (zipWrapper.storeVideo != null && !zipWrapper.storeVideo.isEmpty()) {
                types.put(ITEM_VIDEO, ITEM_VIDEO);
            }

            //活动
            if (zipWrapper.choice != null && !zipWrapper.choice.isEmpty()) {
                types.put(ITEM_EVENTS, ITEM_EVENTS);
            }

            //统筹师
            if (zipWrapper.planners != null && !zipWrapper.planners.isEmpty()) {
                types.put(ITEM_PLANNER, ITEM_PLANNER);
            }

            //评论
            if (zipWrapper.comments != null && !zipWrapper.comments.isEmpty()) {
                types.put(ITEM_COMMENT, ITEM_COMMENT);
            }

            //地址
            if (!TextUtils.isEmpty(zipWrapper.location)) {
                types.put(ITEM_ADDRESS, ITEM_ADDRESS);
            }

            //图集
            if (zipWrapper.atlas != null && !zipWrapper.atlas.isEmpty()) {
                types.put(ITEM_ALBUM, ITEM_ALBUM);
            }

            //footer
            types.put(ITEM_FOOTER, ITEM_FOOTER);
        }
    }

    private ShopResultZipWrapper createShopResultZipWrapper() {
        ShopResultZipWrapper wrapper = null;
        if (resultZip != null) {
            wrapper = new ShopResultZipWrapper();
            PosterData posterData = resultZip.posterData;
            //顶图
            List<Poster> topPosters = PosterUtil.getPosterList(posterData.getFloors(),
                    Constants.POST_SITES.EEXPERIENCE_STORE_TOP_BANNER,
                    false);
            wrapper.topPosters = topPosters;
            //单图
            List<Poster> singleBannerPosters = PosterUtil.getPosterList(posterData.getFloors(),
                    Constants.POST_SITES.EXPERIENCE_STORE_SINGELPIC_BANNER,
                    false);
            wrapper.singlePoster = singleBannerPosters;

            ExperienceShop shop = resultZip.shop;
            if (shop != null) {
                wrapper.atlas = shop.getAtlas();
                wrapper.choice = shop.getChoice();
                if (shop.getStore() != null) {
                    Store store = shop.getStore();
                    wrapper.store = store;
                    wrapper.commentTag = store.getCommentTag();
                    wrapper.contactPhone = store.getContactPhone();
                    wrapper.location = store.getLocation();
                    wrapper.storeVideo = store.getStoreVideo();
                    wrapper.activityCount = store.getActivityCount();
                    wrapper.panorama = store.getPanorama();
                    wrapper.mediaAlbumId = store.getMediaAlbumId();
                    wrapper.name = store.getName();
                    wrapper.address = store.getAddress();
                }
            }
            wrapper.comments = resultZip.comments.getData();
            if (resultZip.planners != null) {
                wrapper.planners = resultZip.planners.getData();
                if (!wrapper.planners.isEmpty()) {
                    //截取前8个
                    wrapper.planners = wrapper.planners.subList(0,
                            wrapper.planners.size() >= 8 ? 8 : wrapper.planners.size());
                    Planner toShopPlaner = new Planner();
                    toShopPlaner.setId(-1);
                    toShopPlaner.setFullName(mContext.getString(R.string.label_reserve_store));
                    toShopPlaner.setIntroduce(mContext.getString(R.string.label_gold_planner));
                    wrapper.planners.add(toShopPlaner);
                }
            }
        }
        return wrapper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_HEADER:
                ExperienceShopHeaderViewHolder holder = new ExperienceShopHeaderViewHolder(
                        headerView);
                return holder;
            case ITEM_IMAGE:
                itemView = getLayoutInflater().inflate(R.layout.experience_shop_item_image,
                        parent,
                        false);
                return new ExperienceShopImageViewHolder(itemView);
            case ITEM_VIDEO:
                itemView = getLayoutInflater().inflate(R.layout.experience_shop_item_video,
                        parent,
                        false);
                videoHolder = new ExperienceShopVideoHolder(itemView);
                return videoHolder;
            case ITEM_EVENTS:
                itemView = getLayoutInflater().inflate(R.layout.experience_shop_item_events,
                        parent,
                        false);
                return new ExperienceShopEventHolder(itemView);
            case ITEM_PLANNER:
                itemView = getLayoutInflater().inflate(R.layout.experience_shop_item_planners,
                        parent,
                        false);
                return new ExperienceShopPlannerHolder(itemView);
            case ITEM_COMMENT:
                itemView = getLayoutInflater().inflate(R.layout.experience_shop_item_comments,
                        parent,
                        false);
                return new ExperienceShopCommentsHolder(itemView);
            case ITEM_ADDRESS:
                itemView = getLayoutInflater().inflate(R.layout.experience_shop_item_address,
                        parent,
                        false);
                addressHolder = new ExperienceShopAddressHolder(itemView);
                addressHolder.setIsRecyclable(false);
                return addressHolder;
            case ITEM_ALBUM:
                itemView = getLayoutInflater().inflate(R.layout.experience_shop_item_album,
                        parent,
                        false);
                return new ExperienceShopAlbumHolder(itemView);
            case ITEM_FOOTER:
                itemView = new View(mContext);
                return new ExtraBaseViewHolder(itemView);
        }
        return null;
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_HEADER:
                ExperienceShopHeaderViewHolder headerViewHolder =
                        (ExperienceShopHeaderViewHolder) holder;
                headerViewHolder.setPosterView(zipWrapper.topPosters.get(0),
                        zipWrapper.panorama,
                        zipWrapper.mediaAlbumId);
                break;
            case ITEM_IMAGE:
                ExperienceShopImageViewHolder imageViewHolder = (ExperienceShopImageViewHolder)
                        holder;
                imageViewHolder.imgBannerSingle.getLayoutParams().height = measureSize
                        .imgSingleHeight;
                imageViewHolder.setPosterView(zipWrapper.singlePoster.get(0));
                break;
            case ITEM_VIDEO:
                ExperienceShopVideoHolder videoHolder = (ExperienceShopVideoHolder) holder;
                videoHolder.setVideo(zipWrapper.storeVideo);
                break;
            case ITEM_EVENTS:
                ExperienceShopEventHolder eventHolder = (ExperienceShopEventHolder) holder;
                eventHolder.setEvent(zipWrapper.choice);
                break;
            case ITEM_PLANNER:
                ExperienceShopPlannerHolder plannerHolder = (ExperienceShopPlannerHolder) holder;
                plannerHolder.setPlanner(zipWrapper.planners, zipWrapper.store);
                break;
            case ITEM_COMMENT:
                ExperienceShopCommentsHolder commentsHolder = (ExperienceShopCommentsHolder) holder;
                commentsHolder.tvAllComment.setText(mContext.getString(R.string.label_look_all,
                        "",
                        "印象"));
                commentsHolder.setComment(zipWrapper.comments, zipWrapper.commentTag, this.id);
                break;
            case ITEM_ADDRESS:
                ExperienceShopAddressHolder addressHolder = (ExperienceShopAddressHolder) holder;
                addressHolder.setAddress(zipWrapper.location, zipWrapper.name, zipWrapper.address);
                break;
            case ITEM_ALBUM:
                ExperienceShopAlbumHolder albumHolder = (ExperienceShopAlbumHolder) holder;
                albumHolder.setPhoto(zipWrapper.atlas);
                break;
            case ITEM_FOOTER:
                ExtraBaseViewHolder footerHolder = (ExtraBaseViewHolder) holder;
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup
                        .LayoutParams.MATCH_PARENT,
                        Util.dp2px(mContext, 50));
                footerHolder.itemView.setLayoutParams(params);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return types.valueAt(position);
    }

    @Override
    public int getItemCount() {
        return types.size();
    }
}
