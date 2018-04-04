package com.hunliji.hljimagelibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljimagelibrary.views.activities.PicsPageViewActivity;

import java.util.ArrayList;

/**
 * Created by luohanlin on 2017/5/13.
 * 通用的图片点击进入大图浏览的控件
 */

public class OnPhotosItemClickListener implements View.OnClickListener, AdapterView
        .OnItemClickListener {
    private Context context;
    private ArrayList<Photo> photos;
    private int position;

    public OnPhotosItemClickListener(
            Context context, ArrayList<Photo> photos, int position) {
        this.context = context;
        this.photos = photos;
        this.position = position;
    }

    public OnPhotosItemClickListener(
            Context context, ArrayList<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, PicsPageViewActivity.class);
        intent.putExtra("photos", photos);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, PicsPageViewActivity.class);
        intent.putExtra("photos", photos);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }
}
