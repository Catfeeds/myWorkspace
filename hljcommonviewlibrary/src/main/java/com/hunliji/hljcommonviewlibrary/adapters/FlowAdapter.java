package com.hunliji.hljcommonviewlibrary.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.BannerJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

public class FlowAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Poster> mDate;
    private Context mContext;
    private City city;
    private int width;
    private int layoutId;
    private int kind;
    private SliderLayout sliderLayout;

    public FlowAdapter(
            Context context, ArrayList<Poster> list, int kind, int layoutId) {
        this.layoutId = layoutId;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.kind = kind;
        mDate = new ArrayList<>(list);
        width = CommonUtil.getDeviceSize(mContext).x;
    }

    public void setmDate(List<Poster> posters) {
        if (posters != null) {
            mDate.clear();
            mDate.addAll(posters);
            if (sliderLayout != null && sliderLayout.getmViewPager() != null) {
                ArrayList<String> titles = new ArrayList<>();
                for (Poster poster : mDate) {
                    titles.add(poster.getTitle());
                }
            }
            notifyDataSetChanged();
        }
    }

    public void setCity(City city) {
        this.city = city;
    }


    public void setSliderLayout(SliderLayout sliderLayout) {
        this.sliderLayout = sliderLayout;
    }

    @Override
    public int getCount() {
        return mDate == null ? 0 : mDate.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View convertView;
        if (layoutId != 0) {
            convertView = mInflater.inflate(layoutId, container, false);
        } else {
            convertView = mInflater.inflate(R.layout.flow_item___cv, container, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_flow);
        final Poster item = mDate.get(position);
        if (item != null) {
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 使用Name索引寻找ARouter中已注册的对应服务
                    BannerJumpService bannerJumpService = (BannerJumpService) ARouter.getInstance()
                            .build(RouterPath.ServicePath.BANNER_JUMP)
                            .navigation();
                    if (bannerJumpService != null) {
                        bannerJumpService.bannerJump(mContext, item, null);
                    }
                }
            });

            String url = null;
            if (!TextUtils.isEmpty(item.getPath())) {
                url = ImageUtil.getImagePath(item.getPath(), width);
            }
            if (!TextUtils.isEmpty(url)) {
                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(imageView);
            } else {
                Glide.with(mContext)
                        .clear(imageView);
                imageView.setImageBitmap(null);
            }
        }
        container.addView(convertView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
    }
}