package com.hunliji.marrybiz.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.Photo;
import com.hunliji.marrybiz.model.Work;
import com.hunliji.marrybiz.task.AsyncBitmapDrawable;
import com.hunliji.marrybiz.task.ImageLoadTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.ScaleMode;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Suncloud on 2016/1/11.
 */
public class WorkImagesFragment extends RefreshFragment implements ObjectBindAdapter.ViewBinder<Photo> {


    @BindView(R.id.list)
    PullToRefreshListView list;

    private ObjectBindAdapter<Photo> adapter;
    private int width;
    private int imageWidth;
    private Work work;
    private boolean isSnapshot;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Point point = JSONUtil.getDeviceSize(getActivity());
        width = imageWidth = point.x;
        if (imageWidth > 805) {
            imageWidth = imageWidth * 3 / 4;
        }
        ArrayList<Photo> photos = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(), photos, R.layout.product_detail_photo_item, this);
        if (getArguments() != null) {
            work = (Work) getArguments().getSerializable("work");
            isSnapshot = getArguments().getBoolean("isSnapshot");
            if (work != null && work.getDetailPhotos() != null) {
                photos.addAll(work.getDetailPhotos());
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview2, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        View footerView = View.inflate(getActivity(), R.layout.work_images_footer, null);
        list.setMode(PullToRefreshBase.Mode.DISABLED);
        list.getRefreshableView().addFooterView(footerView);
        list.setAdapter(adapter);

        if (work != null) {
            DataConfig config = Session.getInstance().getDataConfig(getActivity());
            if (work.getMerchant() != null && work.getMerchant().getPropertyId() != 6 && config !=
                    null && !JSONUtil.isEmpty(config.getPrepayRemind(work.getPropertyId()))) {
                footerView.findViewById(R.id.purchase_notes_layout).setVisibility(View.VISIBLE);
                TextView purchaseNotes = (TextView) footerView.findViewById(R.id.purchase_notes);
                purchaseNotes.setText(config.getPrepayRemind(work.getPropertyId()));
            } else {
                footerView.findViewById(R.id.purchase_notes_layout).setVisibility(View.GONE);
            }


            View promiseLayout = footerView.findViewById(R.id.promise_layout);
            if (!JSONUtil.isEmpty(work.getPromiseImage()) && !isSnapshot) {
                Point point = JSONUtil.getDeviceSize(getActivity());
                promiseLayout.setVisibility(View.VISIBLE);
                promiseLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (work != null && !JSONUtil.isEmpty(work.getPromisePath())) {
                            String path = work.getPromisePath();
                            Intent intent = new Intent(getActivity(), HljWebViewActivity.class);
                            intent.putExtra("path", path);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R
                                    .anim.activity_anim_default);
                        }
                    }
                });
                ImageView promiseImage = (ImageView) footerView.findViewById(R.id.promise_image);
                promiseImage.getLayoutParams().height = Math.round(point.x * 9 / 40);
                String path = JSONUtil.getImagePathForRound(work.getPromiseImage(), point.x);
                ImageLoadTask task = new ImageLoadTask(promiseImage, null, null, 0, true);
                task.loadImage(path, point.x, ScaleMode.WIDTH, new
                        AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            } else {
                promiseLayout.setVisibility(View.GONE);
            }
        }
        return rootView;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void setViewValue(View view, final Photo photo, int position) {
        if (view.getTag() == null) {
            view.setTag(new ViewHolder(view));
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        String url = JSONUtil.getImagePath(photo.getImagePath(), imageWidth);
        if (photo.getHeight() > 0 && photo.getWidth() > 0) {
            holder.imageView.getLayoutParams().height = Math.round(width * photo.getHeight()
                    / photo.getWidth());
        }
        if (!JSONUtil.isEmpty(url)) {
            ImageLoadTask task = new ImageLoadTask(holder.imageView,
                    new OnHttpRequestListener() {

                        @Override
                        public void onRequestFailed(Object obj) {
                        }

                        @Override
                        public void onRequestCompleted(Object obj) {
                            Bitmap bitmap = (Bitmap) obj;
                            if (photo.getWidth() == 0 || photo.getHeight() == 0) {
                                holder.imageView.getLayoutParams().height = Math.round(width * bitmap.getHeight() / bitmap.getWidth());
                            }
                        }
                    });
            holder.imageView.setTag(url);
            task.loadImage(url, width, ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image,
                            task));
        } else {
            holder.imageView.setTag(null);
            holder.imageView.setImageBitmap(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'product_detail_photo_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.image_view)
        ImageView imageView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public boolean isTop() {
        return list == null || list.isReadyForPullStart();
    }
}
