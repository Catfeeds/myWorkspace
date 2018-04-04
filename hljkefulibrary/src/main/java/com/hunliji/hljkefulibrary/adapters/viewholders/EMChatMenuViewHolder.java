package com.hunliji.hljkefulibrary.adapters.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljkefulibrary.R;
import com.hunliji.hljkefulibrary.moudles.EMChat;
import com.hyphenate.helpdesk.model.RobotMenuInfo;

import java.util.Collection;

/**
 * Created by wangtao on 2017/10/25.
 */

public class EMChatMenuViewHolder extends EMChatMessageBaseViewHolder {

    private TextView tvTitle;
    private LinearLayout menuList;


    public EMChatMenuViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.em_chat_menu_item___kefu, parent, false));
    }

    private EMChatMenuViewHolder(View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tv_title);
        menuList = itemView.findViewById(R.id.menu_item_list);
    }

    @Override
    protected void setViewData(
            Context mContext, EMChat chat, int position, int viewType) {
        RobotMenuInfo menuInfo = chat.getRobotMenuInfo();
        tvTitle.setText(menuInfo.getTitle());
        Collection menuItems = null;
        if (!CommonUtil.isCollectionEmpty(menuInfo.getItems())) {
            menuItems = menuInfo.getItems();
        } else if (!CommonUtil.isCollectionEmpty(menuInfo.getList())) {
            menuItems = menuInfo.getList();
        }
        if (menuItems == null) {
            menuList.setVisibility(View.GONE);
        } else {
            menuList.setVisibility(View.VISIBLE);
            int size = menuItems.size();
            int count = menuList.getChildCount();
            if (count > size) {
                menuList.removeViews(size, count - size);
            }
            int i=0;
            for(Object menuItem:menuItems){
                View view = null;
                if (count > i) {
                    view = menuList.getChildAt(i);
                }
                if (view == null) {
                    view=View.inflate(menuList.getContext(), R.layout.em_menu_item___kefu, null);
                    menuList.addView(view,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,CommonUtil.dp2px(menuList.getContext(),45)));
                    view.setTag(new MenuViewHolder(view));
                }
                if(view.getTag()==null){
                    view.setTag(new MenuViewHolder(view));
                }
                MenuViewHolder viewHolder= (MenuViewHolder) view.getTag();
                viewHolder.setMenuItem(menuItem);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MenuViewHolder viewHolder= (MenuViewHolder) v.getTag();
                        if(onChatClickListener!=null){
                            onChatClickListener.onRobotMenuClick(viewHolder.menuItem);
                        }
                    }
                });
                i++;
            }
        }
    }

    private class MenuViewHolder{
        ImageView ivIcon;
        TextView tvName;
        Object menuItem;

        private MenuViewHolder(View view){
            ivIcon=view.findViewById(R.id.iv_icon);
            tvName=view.findViewById(R.id.tv_name);
        }

        public void setMenuItem(Object menuItem) {
            this.menuItem = menuItem;
            if(menuItem instanceof RobotMenuInfo.Item){
                if(((RobotMenuInfo.Item) menuItem).getId().endsWith("TransferToKf")){
                    ivIcon.setVisibility(View.VISIBLE);
                    ivIcon.setImageResource(R.mipmap.icon_kefu_primary);
                }else {
                    ivIcon.setVisibility(View.GONE);
                }
                tvName.setText(((RobotMenuInfo.Item) menuItem).getName());
            }else if(menuItem instanceof String){
                ivIcon.setVisibility(View.GONE);
                tvName.setText((String) menuItem);
            }
        }
    }
}
