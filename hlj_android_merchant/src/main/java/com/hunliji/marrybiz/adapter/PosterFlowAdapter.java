package com.hunliji.marrybiz.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.util.BannerUtil;
import com.hunliji.marrybiz.util.Util;
import com.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

public class PosterFlowAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Poster> mDate;
    private Context mContext;
    private City city;
    private int width;
    private int layoutId;
    private SliderLayout sliderLayout;

    public PosterFlowAdapter(
            Context context, List<Poster> list, int layoutId) {
        this.layoutId = layoutId;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
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
        View convertView = mInflater.inflate(layoutId, container, false);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_flow);
        final Poster item = mDate.get(position);

        HljVTTagger.buildTagger(imageView)
                .tagName(HljTaggerName.BANNER)
                .atPosition(position)
                .poster(item)
                .tag();

        if (item != null) {
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Poster newItem = item;
                    if (sliderLayout != null) {
                        newItem = mDate.get(sliderLayout.getCurrentPosition());
                    }
                    if (newItem != null) {
                        BannerUtil.bannerJump(mContext, newItem, null);
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