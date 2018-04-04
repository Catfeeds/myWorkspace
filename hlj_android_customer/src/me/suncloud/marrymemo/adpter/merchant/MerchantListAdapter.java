package me.suncloud.marrymemo.adpter.merchant;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.SmallHotelViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.viewholder.FindMerchantViewHolder;
import me.suncloud.marrymemo.model.City;

/**
 * Created by hua_rong on 2018/3/9
 * 找商家
 */
public class MerchantListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private View headerView;
    private View footerView;
    private View parentFooterView;
    private List<Merchant> popularMerchants;
    private ArrayList<Merchant> merchants;
    private ArrayList<Merchant> parentMerchants;
    private boolean isInstallment;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_MERCHANT_ITEM = 1;
    private static final int ITEM_TYPE_FOOTER = 2;
    private static final int ITEM_TYPE_POPULAR = 3; //推广
    private static final int ITEM_TYPE_PARENT_FOOTER = 4;//上一级城市，没有加载更多
    private static final int ITEM_TYPE_HOTEL_ITEM = 5;
    private View popularView;
    private OnItemClickListener onItemClickListener;
    private City city;
    private String cpmSource;

    public MerchantListAdapter(Context context) {
        this.context = context;
    }

    public void setCpmSource(String cpmSource) {
        this.cpmSource = cpmSource;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setMerchants(ArrayList<Merchant> merchants) {
        this.merchants = merchants;
    }

    public void setPopularMerchants(List<Merchant> popularMerchants) {
        this.popularMerchants = popularMerchants;
        notifyDataSetChanged();
    }

    public void setParentMerchants(ArrayList<Merchant> parentMerchants) {
        this.parentMerchants = parentMerchants;
    }

    public void setInstallment(boolean installment) {
        isInstallment = installment;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setParentFooterView(View parentFooterView) {
        this.parentFooterView = parentFooterView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new ExtraBaseViewHolder(headerView);
        } else if (viewType == ITEM_TYPE_MERCHANT_ITEM) {
            FindMerchantViewHolder holder = new FindMerchantViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.find_merchant_item, parent, false));
            holder.setCpmSource(cpmSource);
            holder.setOnItemClickListener(onItemClickListener);
            return holder;
        } else if (viewType == ITEM_TYPE_HOTEL_ITEM) {
            SmallHotelViewHolder hotelViewHolder = new SmallHotelViewHolder(LayoutInflater.from(
                    context)
                    .inflate(R.layout.small_common_hotel_item___cv, parent, false));
            hotelViewHolder.setCpmSource(cpmSource);
            hotelViewHolder.setShowPriceView(!isInstallment);
            hotelViewHolder.setOnItemClickListener(onItemClickListener);
            return hotelViewHolder;
        } else if (viewType == ITEM_TYPE_POPULAR) {
            if (popularView == null) {
                popularView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.merchant_promote_layout, parent, false);
            }
            return new ExtraBaseViewHolder(popularView);
        } else if (viewType == ITEM_TYPE_FOOTER) {
            return new ExtraBaseViewHolder(footerView);
        } else {
            return new ExtraBaseViewHolder(parentFooterView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_MERCHANT_ITEM:
                FindMerchantViewHolder merchantHolder = (FindMerchantViewHolder) holder;
                merchantHolder.setCity(city);
                merchantHolder.setView(context, getItem(position), position, viewType);
                merchantHolder.setShowBottomLineView(position < getItemCount() - 2);
                int nextViewType = getItemViewType(position + 1);
                if (nextViewType == ITEM_TYPE_POPULAR || nextViewType == ITEM_TYPE_FOOTER ||
                        nextViewType == ITEM_TYPE_PARENT_FOOTER) {
                    merchantHolder.setShowBottomLineView(false);
                }
                break;
            case ITEM_TYPE_HOTEL_ITEM:
                SmallHotelViewHolder hotelHolder = (SmallHotelViewHolder) holder;
                hotelHolder.setView(context, getItem(position), position, viewType);
                hotelHolder.setShowBottomLineView(position < getItemCount() - 2);
                int nextHotelViewType = getItemViewType(position + 1);
                if (nextHotelViewType == ITEM_TYPE_POPULAR || nextHotelViewType ==
                        ITEM_TYPE_FOOTER || nextHotelViewType == ITEM_TYPE_PARENT_FOOTER) {
                    hotelHolder.setShowBottomLineView(false);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (headerView != null) {
            count++;
        }
        if (popularMerchants != null && !popularMerchants.isEmpty()) {
            //+1是推广标识
            count += popularMerchants.size() + 1;
        }
        if (merchants != null) {
            count += merchants.size();
        }
        if (footerView != null) {
            count++;
        }
        if (parentFooterView != null) {
            count++;
        }
        if (parentMerchants != null) {
            count += parentMerchants.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int currentPosition = position;
        if (currentPosition == getItemCount() - 1 && parentFooterView != null) {
            return ITEM_TYPE_PARENT_FOOTER;
        }
        if (headerView != null) {
            if (currentPosition == 0) {
                return ITEM_TYPE_HEADER;
            }
            currentPosition--;
        }
        if (!CommonUtil.isCollectionEmpty(popularMerchants)) {
            if (currentPosition == popularMerchants.size()) {
                //推广标识
                return ITEM_TYPE_POPULAR;
            }
            currentPosition -= popularMerchants.size() + 1;
        }
        if (currentPosition == ((merchants == null ? 0 : merchants.size())) && footerView != null) {
            return ITEM_TYPE_FOOTER;
        }
        //有推广商家和普通商家两个数组，获取item时需要进行区分，所以调用getItem()方法
        if (getItem(position) != null && getItem(position).getShopType() == Merchant
                .SHOP_TYPE_HOTEL) {
            return ITEM_TYPE_HOTEL_ITEM;
        }
        return ITEM_TYPE_MERCHANT_ITEM;
    }

    public Merchant getItem(int position) {
        if (headerView != null) {
            if (position == 0) {
                return null;
            }
            position--;
        }
        if (popularMerchants != null && !popularMerchants.isEmpty()) {
            if (position < popularMerchants.size()) {
                return popularMerchants.get(position);
            }
            position -= popularMerchants.size() + 1;
            if (position < 0) {
                //推广标识
                return null;
            }
        }

        if (!CommonUtil.isCollectionEmpty(merchants)) {
            if (position < merchants.size()) {
                return merchants.get(position);
            }
            position -= merchants.size();
        }
        if (footerView != null) {
            position--;
        }
        if (!CommonUtil.isCollectionEmpty(parentMerchants)) {
            if (position < parentMerchants.size()) {
                return parentMerchants.get(position);
            }
        }
        return null;
    }

}
