package me.suncloud.marrymemo.adpter.merchant;

import android.content.Context;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.merchant.HotelMenu;
import com.hunliji.hljcommonlibrary.models.merchant.HotelMenuSet;
import com.hunliji.hljcommonviewlibrary.adapters.GroupRecyclerAdapter;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.merchant.viewholder.HotelMenuFoodNameViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.HotelMenuNameViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.HotelMenuServiceFeeViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.HotelMenuSetNameViewHolder;
import me.suncloud.marrymemo.adpter.merchant.viewholder.MerchantHomeQuestionEmptyViewHolder;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by wangtao on 2017/10/10.
 */

public class HotelMenuInfoAdapter extends GroupRecyclerAdapter<BaseViewHolder<String>> {

    private Context context;
    private HotelMenu menu;
    private List<HotelMenuSet> menuSets;

    public static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};

    private static class GroupIndex {
        private static final int HEADER = 0;
        private static final int FOOTER = 999;
    }

    private static class GroupType {
        private static final int HEADER = 0;
        private static final int FOOTER = 999;
    }

    private static class ItemType {
        private static final int HEADER = -1;
        private static final int FOOTER = -2;
        private static final int MENU_SET_HEADER = -3;
        private static final int FOOD = 1;
    }

    public HotelMenuInfoAdapter(
            Context context) {
        this.context = context;
    }

    public void setMenu(HotelMenu menu) {
        this.menu = menu;
        this.menuSets = menu.getHotelMenuSets();
        resetGroup();
        setGroup(GroupIndex.HEADER, GroupType.HEADER, 0);
        if (menuSets != null) {
            for (HotelMenuSet menuSet : menuSets) {
                int index = menuSets.indexOf(menuSet) + 1;
                setGroup(index,
                        index,
                        menuSet.getListNames()
                                .size());
            }
        }
        if (menu.getServiceFee() > 0) {
            setGroup(GroupIndex.FOOTER, GroupType.FOOTER, 0);
        }

    }

    @Override
    public boolean hasGroupHeader(int groupType) {
        switch (groupType) {
            case GroupType.FOOTER:
                return false;
            default:
                return true;

        }
    }

    @Override
    public boolean hasGroupFooter(int groupType) {
        switch (groupType) {
            case GroupType.FOOTER:
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getGroupHeaderType(int groupType) {
        switch (groupType) {
            case GroupType.HEADER:
                return ItemType.HEADER;
            default:
                return ItemType.MENU_SET_HEADER;

        }
    }

    @Override
    public int getGroupFooterType(int groupType) {
        return ItemType.FOOTER;
    }

    @Override
    public int getItemViewType(int groupType, int childPosition) {
        return ItemType.FOOD;
    }

    @Override
    public void onBindChildViewHolder(
            BaseViewHolder<String> holder, int groupType, int childPosition) {
        holder.setView(context,
                menuSets.get(groupType - 1)
                        .getListNames()
                        .get(childPosition),
                childPosition,
                getItemViewType(groupType, childPosition));
    }

    @Override
    public void onBindGroupHeaderViewHolder(BaseViewHolder<String> holder, int groupType) {
        switch (groupType) {
            case GroupType.HEADER:
                holder.setView(context, menu.getName(), 0, groupType);
                break;
            default:
                holder.setView(context, b[Math.min(26, groupType - 1)] + "套", 0, groupType);
                break;
        }
    }

    @Override
    public void onBindGroupFooterViewHolder(BaseViewHolder<String> holder, int groupType) {
        holder.setView(context,
                context.getString(R.string.label_menu_addition,
                        Util.formatDouble2String(menu.getServiceFee())),
                0,
                groupType);
    }

    @Override
    public BaseViewHolder<String> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemType.HEADER:
                return new HotelMenuNameViewHolder(parent);

            case ItemType.MENU_SET_HEADER:
                return new HotelMenuSetNameViewHolder(parent);

            case ItemType.FOOD:
                return new HotelMenuFoodNameViewHolder(parent);

            case ItemType.FOOTER:
                return new HotelMenuServiceFeeViewHolder(parent);
        }

        return null;
    }
}
