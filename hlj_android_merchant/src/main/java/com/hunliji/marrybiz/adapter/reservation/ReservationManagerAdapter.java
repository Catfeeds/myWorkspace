package com.hunliji.marrybiz.adapter.reservation;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.reservation.Reservation;

import java.util.List;

/**
 * Created by jinxin on 2017/5/22 0022.
 */

public class ReservationManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int RESERVATION_HISTORY = 100;//预约历史
    public static final int RESERVATION_CONFIRM = 101;//待确认预约
    public static final int RESERVATION_LIST = 102;//预约列表

    private final int ITEM_ITEM = 10;
    private final int ITEM_FOOTER = 11;

    private Context mContext;
    private int type;
    private List<Reservation> reservations;
    private onViewClickListener onViewClickListener;
    private View footerView;

    public ReservationManagerAdapter(Context mContext, int type, List<Reservation> reservations) {
        this.mContext = mContext;
        this.type = type;
        this.reservations = reservations;
    }

    public void setItems(List<Reservation> items) {
        if (!CommonUtil.isCollectionEmpty(items)) {
            this.reservations.clear();
            this.reservations.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void addItems(List<Reservation> items) {
        if (!CommonUtil.isCollectionEmpty(items)) {
            int start = this.reservations.size();
            this.reservations.addAll(items);
            notifyItemRangeInserted(start, items.size());
        }
    }

    public void removeItem(int position) {
        this.reservations.remove(position);
        notifyItemRemoved(position);
    }

    public void notifyItemLooked(int position) {
        if (this.reservations.isEmpty()) {
            return;
        }
        boolean look = this.reservations.get(position)
                .isLook();
        this.reservations.get(position)
                .setLook(!look);
        notifyItemChanged(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_ITEM:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.reservation_manager_item, parent, false);
                return new ReservationManagerViewHolder(itemView);
            case ITEM_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        int viewType = getItemViewType(position);
        if (viewType != ITEM_ITEM) {
            return;
        }
        ReservationManagerViewHolder holder = (ReservationManagerViewHolder) h;
        Reservation reservation = reservations.get(position);
        OnItemClickListener onItemClickListener = new OnItemClickListener(reservation, position);
        holder.imgCall.setOnClickListener(onItemClickListener);
        holder.tvReservation.setOnClickListener(onItemClickListener);
        holder.tvDelete.setOnClickListener(onItemClickListener);
        holder.tvLook.setOnClickListener(onItemClickListener);

        String name = reservation.getFullName();
        String phone;
        if (type == RESERVATION_CONFIRM) {
            phone = reservation.isLook() ? reservation.getPhone() : reservation.getPhoneNum();
        } else {
            phone = reservation.getPhoneNum();
        }

        Author author = reservation.getUser();
        String avatar = null;
        if (author != null) {
            if (TextUtils.isEmpty(name)) {
                name = author.getName();
            }
            if (TextUtils.isEmpty(phone)) {
                phone = author.getPhone();
                if (type == RESERVATION_CONFIRM) {
                    if (reservation.isLook()) {
                        //如果取了user的手机号 则手动加*
                        if (phone.length() >= 3) {
                            StringBuilder builder = new StringBuilder(phone);
                            builder.replace(3, 7, "****");
                            phone = builder.toString();
                        }
                    }
                }
            }
            int size = CommonUtil.dp2px(mContext, 24);
            avatar = ImagePath.buildPath(author.getAvatar())
                    .width(size)
                    .height(size)
                    .cropPath();
        }
        Glide.with(mContext)
                .load(avatar)
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_avatar_primary))
                .into(holder.imgAvatar);
        holder.tvName.setText(name);
        holder.tvDes.setText(phone);
        switch (type) {
            case RESERVATION_CONFIRM:
                //待确认预约
                setConfirm(holder, reservation);
                break;
            case RESERVATION_LIST:
                //预约列表
                setList(holder, reservation);
                break;
            case RESERVATION_HISTORY:
                //预约历史
                setHistory(holder, reservation);
                break;
            default:
                break;
        }
        if (type == RESERVATION_CONFIRM) {
            //待预约确认
            if (reservation.isLook()) {
                holder.tvReservation.setVisibility(View.VISIBLE);
                holder.tvDelete.setVisibility(View.VISIBLE);
                holder.imgCall.setVisibility(View.VISIBLE);
                holder.tvLook.setVisibility(View.GONE);
            } else {
                holder.tvLook.setVisibility(View.VISIBLE);
                holder.tvReservation.setVisibility(View.GONE);
                holder.tvDelete.setVisibility(View.GONE);
                holder.imgCall.setVisibility(View.GONE);
            }
        }else{
            if("——".contains(phone)){
                holder.imgCall.setVisibility(View.GONE);
            }else{
                holder.imgCall.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setConfirm(ReservationManagerViewHolder holder, Reservation reservation) {
        holder.layoutUser.setVisibility(View.VISIBLE);
        holder.layoutButton.setVisibility(View.VISIBLE);
        holder.layoutCondition.setVisibility(View.GONE);

        holder.tvTimeHint.setText("提交时间：");
        if (reservation.getCreatedAt() != null) {
            holder.tvTime.setText(reservation.getCreatedAt()
                    .toString(Constants.DATE_FORMAT_LONG));
        }
        holder.tvStateHint.setText("来        源：");
        int formType = reservation.getFormType();
        String form = getForm(formType);
        holder.tvState.setText(form);
        holder.tvState.setText(form);

        holder.tvReservation.setTextColor(mContext.getResources()
                .getColor(R.color.colorPrimary));
        holder.tvReservation.setBackgroundResource(R.drawable.sp_r12_stroke_primary);
        holder.tvDelete.setTextColor(mContext.getResources()
                .getColor(R.color.colorBlack3));
        holder.tvDelete.setBackgroundResource(R.drawable.sp_r12_stroke_gray3);
    }

    private void setList(ReservationManagerViewHolder holder, Reservation reservation) {
        holder.layoutUser.setVisibility(View.VISIBLE);
        holder.layoutButton.setVisibility(View.VISIBLE);
        holder.layoutCondition.setVisibility(View.GONE);
        holder.tvReservation.setTextColor(mContext.getResources()
                .getColor(R.color.colorPrimary));
        holder.tvReservation.setBackgroundResource(R.drawable.sp_r12_stroke_primary);
        holder.tvReservation.setText("到店");
        holder.tvDelete.setTextColor(mContext.getResources()
                .getColor(R.color.colorBlack3));
        holder.tvDelete.setBackgroundResource(R.drawable.sp_r12_stroke_gray3);
        holder.tvDelete.setText("未到店");
        holder.tvTimeHint.setText("预约时间：");
        holder.tvStateHint.setText("来        源：");
        if (reservation.getGoTime() != null) {
            holder.tvTime.setText(reservation.getGoTime()
                    .toString(Constants.DATE_FORMAT_LONG));
        }
        holder.tvStateHint.setText("来        源：");

        int formType = reservation.getFormType();
        String form = getForm(formType);
        holder.tvState.setText(form);
    }

    private void setHistory(ReservationManagerViewHolder holder, Reservation reservation) {
        holder.layoutUser.setVisibility(View.VISIBLE);
        holder.tvState.setVisibility(View.VISIBLE);

        holder.layoutButton.setVisibility(View.GONE);
        holder.layoutCondition.setVisibility(View.GONE);

        holder.tvTimeHint.setText("预约时间：");
        holder.tvStateHint.setText("来        源：");

        if (reservation.getGoTime() != null) {
            holder.tvTime.setText(reservation.getGoTime()
                    .toString(Constants.DATE_FORMAT_LONG));
        }
        holder.tvStateHint.setText("来        源：");

        int formType = reservation.getFormType();
        String form = getForm(formType);
        holder.tvState.setText(form);
        int arriveState = reservation.getArriveStatus();
        switch (arriveState) {
            case 1:
                //到店
                holder.tvStatus.setText("到店");
                holder.tvStatus.setBackgroundResource(R.drawable.sp_r2_solid_accent);
                holder.tvStatus.setTextColor(mContext.getResources()
                        .getColor(R.color.colorWhite));
                holder.tvStatus.setVisibility(View.VISIBLE);
                break;
            case 2:
                //未到店
                holder.tvStatus.setText("未到店");
                holder.tvStatus.setBackgroundResource(R.drawable.sp_r2_stroke_gray3);
                holder.tvStatus.setTextColor(mContext.getResources()
                        .getColor(R.color.colorGray3));
                holder.tvStatus.setVisibility(View.VISIBLE);
                break;
            case 0:
                //未处理
            default:
                holder.tvStatus.setVisibility(View.GONE);
                break;
        }

    }

    private String getForm(int formType) {
        String form = "预约到店";
        switch (formType) {
            case 0://老数据
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                form = "预约到店";
                break;
            case 6:
                form = "微官网";
                break;
            case 7:
                form = "聚客宝报名";
                break;
            case 8:
                form = "外部平台报名";
                break;
            case 9:
                form = "私信预约到店";
                break;
            case 10:
                form = "小程序";
                break;
            case 11:
                form = "预约回电";
                break;
            default:
                break;
        }
        return form;
    }

    @Override
    public int getItemCount() {
        return reservations == null ? 0 : (reservations.size() + (footerView == null ? 0 : 1));
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (position != getItemCount() - 1) {
            type = ITEM_ITEM;
        } else {
            type = ITEM_FOOTER;
        }
        return type;
    }

    public void setOnViewClickListener(onViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    class OnItemClickListener implements View.OnClickListener {

        private Reservation reservation;
        private int position;

        public OnItemClickListener(Reservation reservation, int position) {
            this.reservation = reservation;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onViewClickListener != null) {
                onViewClickListener.onViewClick(v.getId(), reservation, position, type);
            }
        }
    }

    public interface onViewClickListener {
        void onViewClick(int id, Reservation reservation, int position, int type);
    }
}
