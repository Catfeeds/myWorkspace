package com.hunliji.hljimagelibrary.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.adapters.GalleryAdapter;
import com.hunliji.hljimagelibrary.models.Gallery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtao on 2017/7/20.
 */

public class GalleryChooseFragment extends RefreshFragment implements AdapterView.OnItemClickListener{

    private ListView listView;
    private GalleryAdapter galleryAdapter;
    private ArrayList<Gallery> galleries;
    private List<Long> bucketIds;


    public static GalleryChooseFragment newInstance(
            ArrayList<Gallery> galleries,
            List<Long> bucketIds) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("galleries", galleries);
        if (!CommonUtil.isCollectionEmpty(bucketIds)) {
            args.putString("ids",
                    GsonUtil.getGsonInstance()
                            .toJson(bucketIds));
        }
        GalleryChooseFragment fragment = new GalleryChooseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            galleries = getArguments().getParcelableArrayList("galleries");
            String idsStr = getArguments().getString("ids");
            if(!TextUtils.isEmpty(idsStr)) {
                bucketIds = GsonUtil.getGsonInstance()
                        .fromJson(idsStr, new TypeToken<List<Long>>() {}.getType());
            }
        }
        galleryAdapter=new GalleryAdapter(getContext());
        galleryAdapter.setGalleries(galleries);
        galleryAdapter.setGalleryIds(bucketIds);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery_choose___img, container, false);
        listView = (ListView) rootView.findViewById(R.id.fileList);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(this);
        listView.setAdapter(galleryAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(listView.getCheckedItemPosition()<0){
            listView.setItemChecked(0,true);
        }
    }

    @Override
    public void refresh(Object... params) {
        if(params!=null&&params.length>0){
            try {
                bucketIds= (List<Long>) params[0];
                galleryAdapter.setGalleryIds(bucketIds);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Gallery gallery = (Gallery) parent.getAdapter()
                .getItem(position);
        if (gallery == null) {
            return;
        }
        getFragmentManager().popBackStack();
        if(getActivity()!=null&&getActivity() instanceof OnGalleryListListener){
            ((OnGalleryListListener) getActivity()).onGalleryChoose(gallery);
        }
    }

    public interface OnGalleryListListener{
        void onGalleryChoose(Gallery gallery);
    }
}
