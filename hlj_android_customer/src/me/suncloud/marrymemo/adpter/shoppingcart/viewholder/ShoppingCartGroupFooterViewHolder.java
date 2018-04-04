package me.suncloud.marrymemo.adpter.shoppingcart.viewholder;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.shoppingcard.ShoppingCartGroup;

/**
 * Created by jinxin on 2017/11/7 0007.
 */

public class ShoppingCartGroupFooterViewHolder extends BaseViewHolder<ShoppingCartGroup> {

    @BindView(R.id.line_top)
    View lineTop;
    @BindView(R.id.tv_discount)
    TextView tvDiscount;
    @BindView(R.id.tv_add_on)
    TextView tvAddOn;
    @BindView(R.id.img_arrow)
    ImageView imgArrow;
    @BindView(R.id.layout_discount)
    LinearLayout layoutDiscount;

    private Context mContext;
    private ShoppingCartGroup cartGroup;
    private onCartGroupFooterClickListener onCartGroupFooterClickListener;
    private boolean isSuit;
    private List<CouponInfo> arriveList;
    private ArrayMap<Double, List<CouponInfo>> notArriveMap;

    public ShoppingCartGroupFooterViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        //arrayMap 默认排序方式是按照key值从小到大排序
        arriveList = new ArrayList<>();
        notArriveMap = new ArrayMap<>();
        initTracker();
    }

    private void initTracker() {
        HljVTTagger.buildTagger(tvAddOn)
                .tagName("coupon_tip_item")
                .hitTag();
    }

    @Override
    protected void setViewData(
            Context mContext, ShoppingCartGroup cartGroup, int position, int viewType) {
        this.cartGroup = cartGroup;
        tvDiscount.setText(null);
        if (cartGroup.getCouponList()
                .isEmpty()) {
            layoutDiscount.setVisibility(View.GONE);
            return;
        }
        double allMoney = cartGroup.getCurrentMoney();
        sortCouponInfo(allMoney, cartGroup.getCouponList());
        layoutDiscount.setVisibility(View.VISIBLE);
        CouponInfo arriveCoupon = null;
        CouponInfo notArriveCoupon = null;
        if (!arriveList.isEmpty()) {
            arriveCoupon = arriveList.get(0);
        }
        notArriveCoupon = getNotArriveCoupon(allMoney);
        if (arriveCoupon.getMoneySill() == 0D) {
            //无门槛
            if (arriveCoupon.getValue() > getNoteArriveMaxValue()) {
                notArriveCoupon = null;
            }
        }
        isSuit = false;
        StringBuilder builder = new StringBuilder();
        if (arriveCoupon != null) {
            builder.append("下单立减")
                    .append(CommonUtil.formatDouble2String(arriveCoupon.getValue()))
                    .append("元");
        }
        if (notArriveCoupon != null) {
            if (arriveCoupon != null) {
                builder.append(",");
            }
            builder.append("满")
                    .append(CommonUtil.formatDouble2String(notArriveCoupon.getMoneySill()))
                    .append("减")
                    .append(CommonUtil.formatDouble2String(notArriveCoupon.getValue()))
                    .append("元");
            imgArrow.setVisibility(View.VISIBLE);
            tvAddOn.setText("去凑单");
            tvAddOn.setTextColor(mContext.getResources()
                    .getColor(R.color.colorPrimary));
        } else {
            isSuit = true;
            imgArrow.setVisibility(View.GONE);
            tvAddOn.setText("已达成");
            tvAddOn.setTextColor(mContext.getResources()
                    .getColor(R.color.colorSuccess));
        }

        tvDiscount.setText(builder.toString());
    }

    private CouponInfo getNotArriveCoupon(double allMoney) {
        for (int i = 0, size = notArriveMap.size(); i < size; i++) {
            double key = notArriveMap.keyAt(i);
            List<CouponInfo> list = notArriveMap.get(key);
            if (key > allMoney && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    private double getNoteArriveMaxValue() {
        double maxValue = 0D;
        for (int i = 0, size = notArriveMap.size(); i < size; i++) {
            double key = notArriveMap.keyAt(i);
            List<CouponInfo> list = notArriveMap.get(key);
            if (!list.isEmpty()) {
                double value = list.get(0)
                        .getValue();
                maxValue = Math.max(maxValue, value);
            }
        }
        return maxValue;
    }

    private void sortCouponInfo(double money, List<CouponInfo> couponInfoList) {
        if (couponInfoList == null) {
            return;
        }

        arriveList.clear();
        notArriveMap.clear();

        for (CouponInfo info : couponInfoList) {
            double moneySill = info.getMoneySill();
            if (money >= moneySill) {
                arriveList.add(info);
            } else {
                double moneySill1 = info.getMoneySill();
                if (!notArriveMap.containsKey(moneySill1)) {
                    List<CouponInfo> list = new ArrayList<>();
                    notArriveMap.put(moneySill1, list);
                }
                List<CouponInfo> list = notArriveMap.get(moneySill1);
                list.add(info);
            }
        }

        sortCouponByValueDesc(arriveList);
        for (int i = 0, size = notArriveMap.size(); i < size; i++) {
            double key = notArriveMap.keyAt(i);
            List<CouponInfo> list = notArriveMap.get(key);
            sortCouponByValueDesc(list);
        }
    }

    private void sortCouponByValueDesc(List<CouponInfo> couponInfoList) {
        if (couponInfoList == null) {
            return;
        }
        Collections.sort(couponInfoList, new Comparator<CouponInfo>() {
            @Override
            public int compare(CouponInfo o1, CouponInfo o2) {
                return (int) (o2.getValue() - o1.getValue());
            }
        });
    }

    @OnClick(R.id.tv_add_on)
    void onAddOn() {
        if (isSuit) {
            return;
        }
        if (onCartGroupFooterClickListener != null) {
            onCartGroupFooterClickListener.onAddOn(cartGroup);
        }
    }

    public void setOnCartGroupFooterClickListener(
            onCartGroupFooterClickListener onCartGroupFooterClickListener) {
        this.onCartGroupFooterClickListener = onCartGroupFooterClickListener;
    }

    public interface onCartGroupFooterClickListener {
        void onAddOn(ShoppingCartGroup cartGroup);
    }
}
