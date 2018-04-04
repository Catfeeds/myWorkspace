package com.hunliji.hljcardcustomerlibrary.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.models.RedPacket;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.widgets.QueueDialog;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 婚品新人气泡红包
 * Created by jinxin on 2017/11/9 0009.
 */

public class ProductRedPacketDialog extends QueueDialog {

    @BindView(R2.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.btn_submit)
    TextView btnSubmit;
    @BindView(R2.id.ll_red_pocket)
    LinearLayout llRedPocket;
    @BindView(R2.id.iv_red_pocket)
    ImageView ivRedPocket;

    private Context mContext;
    private CouponAdapter couponAdapter;
    private onUserClickListener onClickListener;

    public ProductRedPacketDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ProductRedPacketDialog(
            @NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected ProductRedPacketDialog(
            @NonNull Context context,
            boolean cancelable,
            @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        couponAdapter = new CouponAdapter(this.mContext);
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);

        ButterKnife.bind(this, view);
        initWidget();
        initTracker();
    }

    private void initWidget(){
        Window window = getWindow();
        if(window != null){
            if(window.getAttributes() != null){
                window.getAttributes().width = CommonUtil.getDeviceSize(mContext).x;
            }
        }
        int width = 0;
        int height = 0;
        if (HljCard.isCardMaster(getContext())) {
            width = CommonUtil.dp2px(getContext(), 280);
            height = width * 402 / 748;
            llRedPocket.getLayoutParams().width = width;
        } else {
            width = CommonUtil.getDeviceSize(mContext).x;
            height = width * 402 / 748;
        }

        ivRedPocket.getLayoutParams().width = width;
        ivRedPocket.getLayoutParams().height = height;
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(couponAdapter);
    }

    public void setHeadImg(String headImg) {
        Glide.with(mContext)
                .load(ImagePath.buildPath(headImg)
                        .width(ivRedPocket.getLayoutParams().width)
                        .height(ivRedPocket.getLayoutParams().height)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                .into(ivRedPocket);
    }

    private void initTracker() {
        HljVTTagger.buildTagger(btnSubmit)
                .tagName("hlg_coupon_button")
                .hitTag();
    }

    @OnClick(R2.id.img_close)
    void onClose() {
        dismiss();
    }

    @OnClick(R2.id.btn_submit)
    void onSubmit() {
        if (onClickListener != null) {
            onClickListener.onUserClick();
        }
        dismiss();
    }

    public void setRedPacketList(List<RedPacket> infoList) {
        if (infoList == null) {
            return;
        }
        couponAdapter.setRedPacketList(infoList);
    }

    class CouponAdapter extends RecyclerView.Adapter<BaseViewHolder> {
        private List<RedPacket> redPacketList;
        private LayoutInflater inflater;

        public CouponAdapter(Context mContext) {
            redPacketList = new ArrayList<>();
            inflater = LayoutInflater.from(mContext);
        }

        public void setRedPacketList(List<RedPacket> redPacketList) {
            this.redPacketList.clear();
            if (redPacketList != null) {
                this.redPacketList.addAll(redPacketList);
            }
            notifyDataSetChanged();
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.product_red_packet_list_item, parent, false);
            return new RedPacketViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            holder.setView(mContext, redPacketList.get(position), position, position);
        }

        @Override
        public int getItemCount() {
            return redPacketList.size();
        }
    }

    class RedPacketViewHolder extends BaseViewHolder<RedPacket> {

        @BindView(R2.id.tv_red_packet_type)
        TextView tvRedPacketType;
        @BindView(R2.id.tv_red_packet_money)
        TextView tvRedPacketMoney;
        @BindView(R2.id.tv_red_packet_date)
        TextView tvRedPacketDate;
        @BindView(R2.id.tv_red_packet_sill)
        TextView tvRedPacketSill;
        @BindView(R2.id.divider)
        Space divider;
        @BindView(R2.id.ll_item)
        LinearLayout llItem;

        public RedPacketViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Context context = itemView.getContext();
            if (HljCard.isCardMaster(context)) {
                llItem.getLayoutParams().width = CommonUtil.dp2px(context, 248);
                llItem.setPadding(CommonUtil.dp2px(context, 16),
                        0,
                        CommonUtil.dp2px(context, 16),
                        0);
            }
        }

        @Override
        protected void setViewData(
                Context mContext, RedPacket redPacket, int position, int viewType) {
            if (redPacket == null) {
                return;
            }
            divider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            tvRedPacketType.setText(redPacket.getRedPacketName());
            tvRedPacketMoney.setText(CommonUtil.formatDouble2String(redPacket.getAmount()));
            tvRedPacketSill.setText(redPacket.getMoneySill() <= 0 ? "无门槛红包" : "满" + CommonUtil
                    .formatDouble2String(
                    redPacket.getMoneySill()) + "可用");
            if (redPacket.getEndAt() != null) {
                long serverTime = HljTimeUtils.getServerCurrentTimeMillis();
                long endTime = redPacket.getEndAt()
                        .getMillis();
                long hours = (endTime - serverTime) / (60 * 60 * 1000);
                int day = (int) (hours <= 0 ? 0 : Math.max(Math.floor(hours / 24.0F), 1));
                String date = null;
                if (day <= 0) {
                    date = "已经过期";
                } else {
                    date = day + "天后过期";
                }
                tvRedPacketDate.setText(date);
            } else {
                tvRedPacketDate.setText(null);
            }
        }
    }

    public void setUserOnClickListener(onUserClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface onUserClickListener {
        void onUserClick();
    }

}
