package com.hunliji.marrybiz.adapter.event;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 报名列表
 * Created by chen_bin on 2017/3/8 0008.
 */
public class SignUpRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<SignUpInfo> signUps;
    private EventInfo eventInfo;
    private LayoutInflater inflater;
    private int logoSize;
    private OnItemClickListener onItemClickListener;
    private OnCallListener onCallListener;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;

    public SignUpRecyclerAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.logoSize = CommonUtil.dp2px(context, 44);
    }

    public void setSignUps(List<SignUpInfo> signUps) {
        this.signUps = signUps;
        notifyDataSetChanged();
    }

    public void addSignUps(List<SignUpInfo> signUps) {
        if (!CommonUtil.isCollectionEmpty(signUps)) {
            int start = getItemCount() - getFooterViewCount();
            this.signUps.addAll(signUps);
            notifyItemRangeInserted(start, signUps.size());
            if (start - getHeaderViewCount() > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    public void setEventInfo(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnCallListener(OnCallListener onCallListener) {
        this.onCallListener = onCallListener;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(signUps);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new SignUpViewHolder(inflater.inflate(R.layout.sign_up_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                int index = position - getHeaderViewCount();
                holder.setView(context, signUps.get(index), index, viewType);
                break;
        }
    }

    public class SignUpViewHolder extends BaseViewHolder<SignUpInfo> {
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_real_name)
        TextView tvRealName;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_status1)
        TextView tvStatus1;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_tel)
        TextView tvTel;
        @BindView(R.id.btn_call)
        ImageButton btnCall;
        @BindView(R.id.btn_chat)
        ImageButton btnChat;
        @BindView(R.id.line_layout)
        View lineLayout;

        public SignUpViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCallListener != null) {
                        onCallListener.onCall(getItem());
                    }
                }
            });
            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignUpInfo signUpInfo = getItem();
                    if (signUpInfo != null && signUpInfo.getId() > 0) {
                        Intent intent = new Intent(context, WSMerchantChatActivity.class);
                        CustomerUser user = new CustomerUser();
                        user.setId(signUpInfo.getAuthor()
                                .getId());
                        user.setNick(signUpInfo.getAuthor()
                                .getName());
                        user.setAvatar(signUpInfo.getAuthor()
                                .getAvatar());
                        intent.putExtra("user", user);
                        intent.putExtra("source", 5);
                        context.startActivity(intent);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                final Context context,
                final SignUpInfo signUpInfo,
                final int position,
                int viewType) {
            if (signUpInfo == null) {
                return;
            }
            lineLayout.setVisibility(position < signUps.size() - 1 ? View.VISIBLE : View.GONE);
            Glide.with(context)
                    .load(ImagePath.buildPath(signUpInfo.getAuthor()
                            .getAvatar())
                            .width(logoSize)
                            .cropPath())
                    .apply(new RequestOptions().dontAnimate()
                            .placeholder(R.mipmap.icon_avatar_primary)
                            .error(R.mipmap.icon_avatar_primary))
                    .into(imgAvatar);
            if (!TextUtils.isEmpty(signUpInfo.getRealName())) {
                tvRealName.setVisibility(View.VISIBLE);
                tvRealName.setText(signUpInfo.getRealName());
            } else {
                tvRealName.setVisibility(View.GONE);
            }
            tvTel.setText(signUpInfo.getTel());
            tvTime.setText(signUpInfo.getCreatedAt() == null ? "" : signUpInfo.getCreatedAt()
                    .toString(context.getString(R.string.format_date_type13)));
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus1.setVisibility(View.GONE);
            //需要抽奖的活动
            if (eventInfo != null && eventInfo.getWinnerLimit() > 0) {
                //公布抽奖
                if (eventInfo.isDrawStatus()) {
                    //抽中
                    if (signUpInfo.getStatus() >= 2) {
                        tvStatus1.setVisibility(View.VISIBLE);
                        tvStatus.setText(R.string.label_get_winner);
                        tvStatus.setBackgroundResource(R.drawable.sp_r2_stroke_color_60cd63);
                        tvStatus.setTextColor(ContextCompat.getColor(context,
                                R.color.color_green7));
                        //未兑奖
                        if (signUpInfo.getStatus() == 2) {
                            tvStatus1.setText(R.string.label_disable_cash_prize);
                            tvStatus1.setBackgroundResource(R.drawable.sp_r2_stroke_color_ffa73c);
                            tvStatus1.setTextColor(ContextCompat.getColor(context,
                                    R.color.color_orange4));
                        }
                        //已兑奖
                        else {
                            tvStatus1.setText(R.string.label_cash_prize);
                            tvStatus1.setBackgroundResource(R.drawable.sp_r2_stroke_color_60cd63);
                            tvStatus1.setTextColor(ContextCompat.getColor(context,
                                    R.color.color_green7));
                        }
                    }
                    //未被抽中
                    else {
                        tvStatus.setText(R.string.label_disable_winner);
                        tvStatus.setBackgroundResource(R.drawable.sp_r2_stroke_gray);
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGray2));
                    }
                }
                //未公布抽奖，则文字全部显示待抽奖
                else {
                    tvStatus.setText(R.string.label_to_be_prize);
                    tvStatus.setBackgroundResource(R.drawable.sp_r2_stroke_gray);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGray2));
                }
            }
            //不需要抽奖的活动
            else {
                if (signUpInfo.getStatus() == 2) {
                    tvStatus.setText(R.string.label_disable_arrived);
                    tvStatus.setBackgroundResource(R.drawable.sp_r2_stroke_gray);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGray2));
                } else {
                    tvStatus.setText(R.string.label_arrived);
                    tvStatus.setBackgroundResource(R.drawable.sp_r2_stroke_color_60cd63);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.color_green7));
                }
            }
            if (!TextUtils.isEmpty(signUpInfo.getTel()) && !signUpInfo.getTel()
                    .contains("*")) {
                btnCall.setClickable(true);
                btnCall.setImageResource(R.drawable.icon_call_primary);
                btnChat.setClickable(true);
                btnChat.setImageResource(R.drawable.icon_chat_bubble_primary_44_43);
            } else {
                btnCall.setClickable(false);
                btnCall.setImageResource(R.drawable.icon_call_black_38_44);
                btnChat.setClickable(false);
                btnChat.setImageResource(R.drawable.icon_chat_bubble_black_46_44);
            }
        }
    }

    public interface OnCallListener {
        void onCall(SignUpInfo signUpInfo);
    }
}
