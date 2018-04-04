package com.hunliji.marrybiz.adapter.reservation;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.marrybiz.R;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2017/5/22 0022.
 */

public class ReservationManagerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.img_avatar)
    RoundedImageView imgAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_des)
    TextView tvDes;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.img_call)
    ImageView imgCall;
    @BindView(R.id.layout_user)
    LinearLayout layoutUser;
    @BindView(R.id.tv_time_hint)
    TextView tvTimeHint;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.layout_time)
    LinearLayout layoutTime;
    @BindView(R.id.tv_state_hint)
    TextView tvStateHint;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.layout_state)
    LinearLayout layoutState;
    @BindView(R.id.tv_condition_hint)
    TextView tvConditionHint;
    @BindView(R.id.tv_condition)
    TextView tvCondition;
    @BindView(R.id.layout_condition)
    LinearLayout layoutCondition;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_reservation)
    TextView tvReservation;
    @BindView(R.id.layout_button)
    LinearLayout layoutButton;
    @BindView(R.id.tv_look)
    TextView tvLook;
    public ReservationManagerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
