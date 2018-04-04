package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.RecyclingPagerAdapter;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.community.viewholder
        .SocialChannelHomeViewPagerAdapterItemViewHolder;

/**
 * Created by jinxin on 2018/3/14 0014.
 */

public class SocialChannelHomeViewPagerAdapter extends RecyclingPagerAdapter {

    private List<CommunityChannel> channelList;
    private LayoutInflater inflater;
    private Context mContext;

    public SocialChannelHomeViewPagerAdapter(Context mContext, List<CommunityChannel> channelList) {
        this.mContext = mContext;
        this.channelList = channelList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(
            int position, View convertView, ViewGroup container) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.social_channel_home_grid_layout, null, false);
        }
        SocialChannelHomeViewPagerAdapterItemViewHolder homeViewPagerAdapterItemViewHolder = new
                SocialChannelHomeViewPagerAdapterItemViewHolder(mContext, convertView);
        int start = position * 6;
        int end = start + (position == getCount() - 1 ? CommonUtil.getCollectionSize(channelList)
                - 6 * position : 6);
        List<CommunityChannel> data = channelList.subList(start,end);
        homeViewPagerAdapterItemViewHolder.setChannelList(data);
        return convertView;
    }

    @Override
    public int getCount() {
        int pageCount = (int) Math.ceil(CommonUtil.getCollectionSize(channelList) * 1.0D / 6);
        return pageCount;
    }
}
