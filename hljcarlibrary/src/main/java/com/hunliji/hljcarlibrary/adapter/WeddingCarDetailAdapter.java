package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarAddressViewHolder;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarAnswerViewHolder;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarCommentViewHolder;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarHeaderViewHolder;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarImageViewHolder;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarPhotoDetailViewHolder;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.CommentMark;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinxin on 2017/12/26 0026.
 */

public class WeddingCarDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        WeddingCarAddressViewHolder.onWeddingCarAddressClickListener, WeddingCarCommentViewHolder.onWeddingCarCommentClickListener, WeddingCarAnswerViewHolder.onWeddingCarAnswerViewHolderClickListener {

    private final int ITEM_TYPE_HEADER = 10;
    private final int ITEM_TYPE_COMMENT = 11;
    private final int ITEM_TYPE_QUESTION = 12;
    private final int ITEM_TYPE_ADDRESS = 13;
    private final int ITEM_TYPE_DETAIL = 14;
    private final int ITEM_PHOTO_DETAIL = 15;

    private Context mContext;
    private LayoutInflater inflater;
    private WeddingCarProduct weddingCarProduct;
    private HljHttpQuestion<List<Question>> carQuestion;
    private List<CommentMark> markList;
    private List<ItemTypeWrapper> wrapperList;
    private onWeddingCarClickListener onWeddingCarClickListener;

    public WeddingCarDetailAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        wrapperList = new ArrayList<>();
    }

    public void setWeddingCarProduct(WeddingCarProduct weddingCarProduct) {
        this.weddingCarProduct = weddingCarProduct;
    }

    public void setCarQuestion(HljHttpQuestion<List<Question>> carQuestion) {
        this.carQuestion = carQuestion;
    }

    public void setOnWeddingCarClickListener(
            WeddingCarDetailAdapter.onWeddingCarClickListener onWeddingCarClickListener) {
        this.onWeddingCarClickListener = onWeddingCarClickListener;
    }

    public void setMarkList(List<CommentMark> markList) {
        this.markList = markList;
    }


    public int calculateItemCount() {
        wrapperList.clear();
        //基本信息
        if (weddingCarProduct != null) {
            ItemTypeWrapper wrapper = new ItemTypeWrapper();
            wrapper.type = ITEM_TYPE_HEADER;
            wrapperList.add(wrapper);
        }
        //评论
        if (weddingCarProduct != null && weddingCarProduct.getMerchantComment() != null &&
                weddingCarProduct.getMerchantComment()
                .getLastMerchantComment() != null) {
            ItemTypeWrapper wrapper = new ItemTypeWrapper();
            wrapper.type = ITEM_TYPE_COMMENT;
            wrapperList.add(wrapper);
        }
        //问答
        if (carQuestion != null) {
            ItemTypeWrapper wrapper = new ItemTypeWrapper();
            wrapper.type = ITEM_TYPE_QUESTION;
            wrapperList.add(wrapper);
        }

        //地址和预约
        if (weddingCarProduct != null && weddingCarProduct.getMerchantComment() != null &&
                weddingCarProduct.getMerchantComment()
                .isAppointment()) {
            ItemTypeWrapper wrapper = new ItemTypeWrapper();
            wrapper.type = ITEM_TYPE_ADDRESS;
            wrapperList.add(wrapper);
        }

        if (weddingCarProduct != null && !TextUtils.isEmpty(weddingCarProduct.getDescribe())) {
            ItemTypeWrapper wrapper = new ItemTypeWrapper();
            wrapper.type = ITEM_PHOTO_DETAIL;
            wrapperList.add(wrapper);
        }

        if (weddingCarProduct != null) {
            List<Photo> detailPhotos = weddingCarProduct.getDetailPhotos();
            if (!CommonUtil.isCollectionEmpty(detailPhotos)) {
                for (int i = 0; i < detailPhotos.size(); i++) {
                    ItemTypeWrapper photoWrapper = new ItemTypeWrapper();
                    photoWrapper.type = ITEM_TYPE_DETAIL;
                    photoWrapper.position = i;
                    wrapperList.add(photoWrapper);
                }
            }
        }

        return wrapperList.size();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                itemView = inflater.inflate(R.layout.wedding_car_product_header___car,
                        viewGroup,
                        false);
                WeddingCarHeaderViewHolder headerViewHolder = new WeddingCarHeaderViewHolder(
                        itemView);
                return headerViewHolder;
            case ITEM_TYPE_COMMENT:
                itemView = inflater.inflate(R.layout.wedding_car_product_comment___car,
                        viewGroup,
                        false);
                WeddingCarCommentViewHolder carCommentViewHolder = new WeddingCarCommentViewHolder(
                        itemView);
                carCommentViewHolder.setOnWeddingCarCommentClickListener(this);
                carCommentViewHolder.setMarkList(markList);
                carCommentViewHolder.setNeedToPicsActivity(true);
                return carCommentViewHolder;
            case ITEM_TYPE_QUESTION:
                itemView = inflater.inflate(R.layout.wedding_car_product_answer___car,
                        viewGroup,
                        false);
                WeddingCarAnswerViewHolder viewHolder = new WeddingCarAnswerViewHolder(itemView,
                        weddingCarProduct.getMerchantComment() == null ? 0L : weddingCarProduct.getMerchantComment()
                                .getId());
                viewHolder.setOnWeddingCarAnswerViewHolderClickListener(this);
                return viewHolder;
            case ITEM_TYPE_ADDRESS:
                itemView = inflater.inflate(R.layout.wedding_car_product_address___car,
                        viewGroup,
                        false);
                WeddingCarAddressViewHolder addressViewHolder = new WeddingCarAddressViewHolder(itemView);
                addressViewHolder.setOnWeddingCarAddressClickListener(this);
                return addressViewHolder;
            case ITEM_PHOTO_DETAIL:
                itemView = inflater.inflate(R.layout.wedding_car_photo_detail___car,
                        viewGroup,
                        false);
                return new WeddingCarPhotoDetailViewHolder(itemView);
            case ITEM_TYPE_DETAIL:
                itemView = inflater.inflate(R.layout.image_item___cm,viewGroup,false);
                return new WeddingCarImageViewHolder(itemView);
            default:
                break;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ItemTypeWrapper wrapper = wrapperList.get(position);
        int itemType = wrapper.type;
        switch (itemType) {
            case ITEM_TYPE_HEADER:
                holder.setView(mContext, weddingCarProduct, position, ITEM_TYPE_HEADER);
                break;
            case ITEM_TYPE_COMMENT:
                holder.setView(mContext, weddingCarProduct, position, ITEM_TYPE_COMMENT);
                break;
            case ITEM_TYPE_QUESTION:
                holder.setView(mContext, carQuestion, position, ITEM_TYPE_QUESTION);
                break;
            case ITEM_TYPE_ADDRESS:
                holder.setView(mContext, weddingCarProduct, position, ITEM_TYPE_ADDRESS);
                break;
            case ITEM_PHOTO_DETAIL:
                holder.setView(mContext, weddingCarProduct, position, ITEM_PHOTO_DETAIL);
                break;
            case ITEM_TYPE_DETAIL:
                List<Photo> detailPhotos = weddingCarProduct.getDetailPhotos();
                if (!CommonUtil.isCollectionEmpty(detailPhotos)) {
                    Photo photo = detailPhotos.get(wrapper.position);
                    holder.setView(mContext, photo, wrapper.position, ITEM_TYPE_DETAIL);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return !wrapperList.isEmpty() ? wrapperList.get(position).type : super.getItemViewType(
                position);
    }

    @Override
    public int getItemCount() {
        return wrapperList.size();
    }

    @Override
    public void onOrderCar() {
        if (onWeddingCarClickListener != null) {
            onWeddingCarClickListener.onOrderCar();
        }
    }

    @Override
    public void onCommentIdList(WeddingCarProduct carProduct, long commentId) {
        if (onWeddingCarClickListener != null) {
            onWeddingCarClickListener.onCommentIdList(carProduct, commentId);
        }
    }

    @Override
    public void onCommentMarkIdList(WeddingCarProduct carProduct, long markId) {
        if (onWeddingCarClickListener != null) {
            onWeddingCarClickListener.onCommentMarkIdList(carProduct, markId);
        }
    }

    @Override
    public void onAskQuestionList(long merchantId, boolean isShowKeyboard) {
        if (onWeddingCarClickListener != null) {
            onWeddingCarClickListener.onAskQuestionList(merchantId, isShowKeyboard);
        }
    }

    public interface onWeddingCarClickListener {
        void onOrderCar();

        void onCommentIdList(WeddingCarProduct carProduct, long commentId);

        void onCommentMarkIdList(WeddingCarProduct carProduct, long markId);

        void onAskQuestionList(long merchantId, boolean isShowKeyboard);
    }

    class ItemTypeWrapper {
        int type;
        int position;
    }
}
