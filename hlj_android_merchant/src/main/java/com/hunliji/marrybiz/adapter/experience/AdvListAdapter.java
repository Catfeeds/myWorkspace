package com.hunliji.marrybiz.adapter.experience;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.view.experience.AdvListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/12/19.体验店推荐列表
 */

public class AdvListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int ITEM_TYPE = 0;
    public static final int FOOTER_TYPE = 1;

    private ArrayList<AdvDetail> list;
    private Context mContext;
    private View footerView;
    private OnItemClickListener onItemClickListener;
    //用于退出activity,避免countdown，造成资源浪费。
    private List<CountDownTimer> countDownTimers;
    private int advType;

    public AdvListAdapter(Context context) {
        this.mContext = context;
        countDownTimers = new ArrayList<>();
    }

    public void setList(ArrayList<AdvDetail> list) {
        this.list = list;
    }

    public void setAdvType(int advType) {
        this.advType = advType;
    }

    /**
     * 清空资源
     */
    public void cancelAllTimers() {
        if (countDownTimers == null) {
            return;
        }
        for (CountDownTimer countDownTimer : countDownTimers) {
            countDownTimer.cancel();
        }
        countDownTimers.clear();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE:
                return new ExperienceShopViewHolder(LayoutInflater.from(mContext)
                        .inflate(R.layout.experience_shop_list_item, parent, false));
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == ITEM_TYPE) {
            holder.setView(mContext, getItem(position), position, itemType);
        }
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private AdvDetail getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && footerView != null) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return (list == null ? 0 : list.size()) + (footerView == null ? 0 : 1);
    }

    class ExperienceShopViewHolder extends BaseViewHolder<AdvDetail> {
        @BindView(R.id.tv_lead_name)
        TextView tvLeadName;
        @BindView(R.id.tv_lead_phone)
        TextView tvLeadPhone;
        @BindView(R.id.tv_is_come)
        TextView tvIsCome;
        @BindView(R.id.tv_order_state)
        TextView tvOrderState;
        @BindView(R.id.tv_recommend_name)
        TextView tvRecommendName;
        @BindView(R.id.tv_saler_name)
        TextView tvSalerName;
        @BindView(R.id.tv_recommend_time)
        TextView tvRecommendTime;
        @BindView(R.id.img_arrow_right)
        ImageView imgArrowRight;
        @BindView(R.id.tv_recommend_label)
        TextView tvRecommendLabel;
        @BindView(R.id.tv_saler_label)
        TextView tvSalerLabel;

        public CountDownTimer countDownTimer;

        ExperienceShopViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
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
                Context mContext, AdvDetail item, int position, int viewType) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                if (countDownTimers.contains(countDownTimer)) {
                    countDownTimers.remove(countDownTimer);
                }
            }
            StringBuilder statusStr = new StringBuilder(AdvDetail.getStatusStringByType(item
                            .getStatus(),
                    advType));
            if (advType == AdvListActivity.ADV_FOR_OTHERS) {
                switch (item.getStatus()) {
                    case AdvDetail.ORDER_UN_READ:
                    case AdvDetail.ORDER_HAVE_READ:
                    case AdvDetail.ORDER_COME_SHOP:
                        tvOrderState.setText(statusStr);
                        tvOrderState.setTextColor(ContextCompat.getColor(mContext,
                                R.color.colorAccent));
                        break;
                    case AdvDetail.ORDER_HAVE_CREATE:
                        tvOrderState.setText(statusStr);
                        tvOrderState.setTextColor(ContextCompat.getColor(mContext,
                                R.color.colorPrimary));
                        break;
                    case AdvDetail.ORDER_FOLLOW_UP_FAILED:
                    case AdvDetail.ORDER_HAVE_EXPIRED:
                    case AdvDetail.ORDER_HAVE_REFUND:
                        tvOrderState.setText(statusStr);
                        tvOrderState.setTextColor(ContextCompat.getColor(mContext,
                                R.color.colorGray));
                        break;
                }
            } else {
                switch (item.getStatus()) {
                    case AdvDetail.ORDER_UN_READ:
                        countDownRemainTimer(item);
                        tvOrderState.setTextColor(ContextCompat.getColor(mContext,
                                R.color.colorAccent));
                        break;
                    case AdvDetail.ORDER_HAVE_READ:
                    case AdvDetail.ORDER_COME_SHOP:
                    case AdvDetail.ORDER_HAVE_CREATE:
                        tvOrderState.setText(statusStr);
                        tvOrderState.setTextColor(ContextCompat.getColor(mContext,
                                R.color.colorPrimary));
                        break;
                    case AdvDetail.ORDER_FOLLOW_UP_FAILED:
                    case AdvDetail.ORDER_HAVE_EXPIRED:
                    case AdvDetail.ORDER_HAVE_REFUND:
                        tvOrderState.setText(statusStr);
                        tvOrderState.setTextColor(ContextCompat.getColor(mContext,
                                R.color.colorGray));
                        break;
                }
            }
            if (item.getStatus() == AdvDetail.ORDER_HAVE_EXPIRED) {
                imgArrowRight.setVisibility(View.GONE);
            } else {
                imgArrowRight.setVisibility(View.VISIBLE);
            }

            tvIsCome.setVisibility(item.isCome() ? View.VISIBLE : View.GONE);
            if (advType == AdvListActivity.ADV_FOR_OTHERS && !CommonUtil.isEmpty(item.getLead()
                    .getNickName())) {
                tvLeadName.setText(item.getLead()
                        .getNickName());
            } else {
                tvLeadName.setText(item.getLead()
                        .getName());
            }


            tvLeadPhone.setText("(" + item.getLead()
                    .getPhone() + ")");

            if (advType == AdvListActivity.ADV_FOR_OTHERS) {
                tvRecommendLabel.setText("对接人：");
                tvRecommendName.setText(item.getExpSaler());
                tvSalerName.setVisibility(View.GONE);
                tvSalerLabel.setVisibility(View.GONE);
            } else {
                tvRecommendLabel.setText("派单：");
                tvRecommendName.setText(item.getAdmin()
                        .getNickname());
            }

            tvSalerName.setText(item.getExpSaler());
            if (item.getCreatedAt() != null) {
                tvRecommendTime.setText("推荐时间：" + item.getCreatedAt()
                        .toString(HljTimeUtils.DATE_FORMAT_LONG_SIMPLE));
            }
        }

        private void countDownRemainTimer(final AdvDetail item) {
            //将前一个缓存清除
            if (item.getExpiredAt() == null) {
                return;
            }
            long time = item.getExpiredAt()
                    .getMillis() - HljTimeUtils.getServerCurrentTimeMillis();
            if (time > 0) {
                countDownTimer = new CountDownTimer(time, 1000) {
                    public void onTick(long millisUntilFinished) {
                        StringBuilder statusStr = new StringBuilder(AdvDetail.getStatusStringByType(
                                item.getStatus(),
                                advType));
                        tvOrderState.setText(statusStr.append(getRemainStr(millisUntilFinished)));
                    }

                    public void onFinish() {
                        item.setStatus(AdvDetail.ORDER_HAVE_EXPIRED);
                        tvOrderState.setText("已过期");
                        tvOrderState.setTextColor(ContextCompat.getColor(mContext,
                                R.color.colorGray));
                    }
                }.start();
                countDownTimers.add(countDownTimer);
            }
        }

        private String getRemainStr(long remainTime) {
            int sec = (int) (remainTime / 1000);
            int min = sec / 60;
            int hour = min / 60;
            if (hour > 0) {
                return "(剩余" + hour + "小时" + ")";
            } else if (min > 0) {
                return "(剩余" + min + "分" + ")";
            } else if (sec > 0) {
                return "(剩余" + sec + "秒" + ")";
            } else {
                return "";
            }
        }
    }
}
