package me.suncloud.marrymemo.adpter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljtrackerlibrary.utils.TrackerUtil;
import com.makeramen.rounded.RoundedImageView;
import com.slider.library.ClipSliderLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.ad.MiaoZhenUtil;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.BannerUtil;

public class ClipFlowAdapter extends PagerAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Poster> mDate;
    private Context mContext;
    private City city;
    private int imageWidth;
    private int imageHeight;
    private ClipSliderLayout sliderLayout;
    private String viewTrackTagName;
    private String cpmSource;

    public ClipFlowAdapter(
            Context context, List<Poster> list) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mDate = new ArrayList<>();
        if (list != null) {
            mDate.addAll(list);
        }
        imageWidth = CommonUtil.getDeviceSize(mContext).x - CommonUtil.dp2px(mContext, 32);
        imageHeight = imageWidth * 276 / 686;
    }

    public void setmDate(List<Poster> posters) {
        if (posters != null) {
            mDate.clear();
            mDate.addAll(posters);
            notifyDataSetChanged();
        }
    }

    public void setCpmSource(String cpmSource) {
        this.cpmSource = cpmSource;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setViewTrackTagName(String viewTrackTagName) {
        this.viewTrackTagName = viewTrackTagName;
    }

    public void setSliderLayout(ClipSliderLayout sliderLayout) {
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
        View convertView = mInflater.inflate(R.layout.flow_item_r3, null);
        ViewHolder holder = new ViewHolder();
        holder.imageView = convertView.findViewById(R.id.image);
        holder.imgBg = convertView.findViewById(R.id.image_bg);
        convertView.setTag(holder);

        holder.imageView.getLayoutParams().width = imageWidth;
        holder.imageView.getLayoutParams().height = imageHeight;
        holder.imgBg.getLayoutParams().width = imageWidth;
        holder.imgBg.getLayoutParams().height = imageHeight;
        final Poster item = mDate.get(position);
        if (!TextUtils.isEmpty(viewTrackTagName)) {
            HljVTTagger.buildTagger(convertView)
                    .tagName(viewTrackTagName)
                    .atPosition(position)
                    .poster(item)
                    .addDataExtra(VTMetaData.EXTRA_DATA_KEY.KEY_CPM_SOURCE, cpmSource)
                    .addMiaoZhenClickUrl(MiaoZhenUtil.getClickUrl(mContext,
                            MiaoZhenUtil.PId.HOME_PAGE_POSTER))
                    .addMiaoZhenImpUrl(MiaoZhenUtil.getImpUrl(mContext,
                            MiaoZhenUtil.PId.HOME_PAGE_POSTER))
                    .tag();
        }
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

            MultiTransformation transformation = new MultiTransformation(new CenterCrop(),
                    new RoundedCorners(CommonUtil.dp2px(container.getContext(), 3)));

            Glide.with(mContext)
                    .load(ImagePath.buildPath(item.getPath())
                            .width(imageWidth)
                            .height(imageHeight)
                            .ignoreFormat(true)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image)
                            .transform(transformation))
                    .into(holder.imageView);

        }
        container.addView(convertView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        RoundedImageView imgBg;
    }
}