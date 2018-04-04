package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljTaggerName;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;
import com.slider.library.SliderLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.ad.MiaoZhenUtil;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.ad.MadPoster;
import me.suncloud.marrymemo.util.BannerUtil;
import me.suncloud.marrymemo.util.JSONUtil;

public class FlowAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Poster> mDate;
    private Context mContext;
    private City city;
    private int width;
    private int layoutId;
    private SliderLayout sliderLayout;
    private int cornerRadius;
    private String miaoZhenPId;
    private String cpmSource;

    public FlowAdapter(
            Context context, ArrayList<Poster> list, int layoutId) {
        this.layoutId = layoutId;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;

        mDate = new ArrayList<>();
        if (list != null) {
            mDate.addAll(list);
        }
        width = JSONUtil.getDeviceSize(mContext).x;
    }

    public void setCpmSource(String cpmSource) {
        this.cpmSource = cpmSource;
    }

    public void setMiaoZhenPId(String miaoZhenPId) {
        this.miaoZhenPId = miaoZhenPId;
    }

    public void setmDate(List<Poster> posters) {
        if (posters != null) {
            mDate.clear();
            mDate.addAll(posters);
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
        View convertView = mInflater.inflate(layoutId, null);
        ViewHolder holder = new ViewHolder();
        holder.imageView = convertView.findViewById(R.id.image);
        holder.textView = convertView.findViewById(R.id.tv_title);
        convertView.setTag(holder);
        final Poster item = mDate.get(position);
        HljVTTagger.buildTagger(convertView)
                .tagName(HljTaggerName.BANNER)
                .atPosition(position)
                .addMiaoZhenClickUrl(MiaoZhenUtil.getClickUrl(mContext, miaoZhenPId))
                .addMiaoZhenImpUrl(MiaoZhenUtil.getImpUrl(mContext, miaoZhenPId))
                .poster(item)
                .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, cpmSource)
                .tag();
        if (item != null) {
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    Poster newItem = item;
                    if (sliderLayout != null) {
                        newItem = mDate.get(sliderLayout.getCurrentPosition());
                    }
                    BannerUtil.bannerAction(mContext, newItem, city, false, null);
                }
            });

            if (holder.textView != null) {
                holder.textView.setText(item.getTitle());
            }
            String url = item instanceof MadPoster ? item.getPath() : ImagePath.buildPath(item
                    .getPath())
                    .width(width)
                    .ignoreFormat(true)
                    .path();
            MultiTransformation transformation;
            if (cornerRadius > 0) {
                transformation = new MultiTransformation(new CenterCrop(),
                        new RoundedCorners(CommonUtil.dp2px(container.getContext(), cornerRadius)));
            } else {
                transformation = new MultiTransformation(new CenterCrop());
            }
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .transform(transformation))
                    .into(holder.imageView);
        }
        container.addView(convertView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}