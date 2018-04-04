package com.hunliji.hljimagelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljimagelibrary.R;
import com.hunliji.hljimagelibrary.adapters.viewholders.BasePhotoViewHolder;
import com.hunliji.hljimagelibrary.adapters.viewholders.ChooseImageViewHolder;
import com.hunliji.hljimagelibrary.adapters.viewholders.ChooseVideoViewHolder;
import com.hunliji.hljimagelibrary.adapters.viewholders.ExtraViewHolder;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by wangtao on 2017/5/9.
 */

public class ChoosePhotoAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        ChooseImageViewHolder.OnPhotoItemInterface {

    protected List<Photo> photos;
    protected ArrayList<Photo> selectedPhotos;
    private boolean takePhoto;
    private Context context;
    private List<Long> bucketIds;
    private OnPhotoListInterface photoListInterface;
    private int limit;

    private View headerView;

    private final int HEADER = -1;

    private final int TAKE_PHOTO = 1;

    final int IMAGE_ITEM = 2;

    final int VIDEO_ITEM = 3;

    public enum ItemSpace {
        None, Left, Right, Middle
    }

    private boolean countEnable;

    @Override
    public void onItemSelectClick(Photo photo) {
        if (selectedPhotos == null) {
            selectedPhotos = new ArrayList<>();
        }
        if (selectedPhotos.contains(photo)) {
            int index = selectedPhotos.indexOf(photo);
            selectedPhotos.remove(index);
            bucketIds.remove(photo.getBucketId());
            notifyItemChanged(startIndex() + photos.indexOf(photo));
            if (countEnable) {
                Observable.range(index, selectedPhotos.size())
                        .filter(new Func1<Integer, Boolean>() {
                            @Override
                            public Boolean call(Integer index) {
                                return selectedPhotos != null && selectedPhotos.size() > index;
                            }
                        })
                        .map(new Func1<Integer, Photo>() {
                            @Override
                            public Photo call(Integer index) {
                                return selectedPhotos.get(index);
                            }
                        })
                        .filter(new Func1<Photo, Boolean>() {
                            @Override
                            public Boolean call(Photo photo) {
                                return photos != null && photos.indexOf(photo) >= 0;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Photo>() {
                            @Override
                            public void call(Photo photo) {
                                notifyItemChanged(startIndex() + photos.indexOf(photo));
                            }
                        });
            }
        } else if (limit == 0 || selectedPhotos.size() < limit) {
            selectedPhotos.add(photo);
            bucketIds.add(photo.getBucketId());
            notifyItemChanged(startIndex() + photos.indexOf(photo));
        } else if (limit == 1 && selectedPhotos.size() == 1) {
            Photo oldPhoto = selectedPhotos.remove(0);
            bucketIds.remove(oldPhoto.getBucketId());
            notifyItemChanged(startIndex() + photos.indexOf(oldPhoto));
            selectedPhotos.add(photo);
            bucketIds.add(photo.getBucketId());
            notifyItemChanged(startIndex() + photos.indexOf(photo));
        } else {
            ToastUtil.showToast(context, null, R.string.msg_choose_photo_limit_out___img);
            return;
        }
        if (photoListInterface != null) {
            photoListInterface.onSelectedCountChange(selectedPhotos.size());
        }
    }

    @Override
    public void onItemPreviewClick(Photo photo) {
        if (photoListInterface == null||photo==null) {
            return;
        }
        if (!photo.isVideo()) {
            photoListInterface.onPreview(photos, selectedPhotos, photo);
        } else {
            photoListInterface.onVideoPreview(photo);
        }
    }

    @Override
    public int selectedIndex(Photo photo) {
        if (selectedPhotos == null) {
            return -1;
        }
        return selectedPhotos.indexOf(photo);
    }

    @Override
    public boolean selectedCountEnable() {
        return countEnable;
    }

    public List<Long> getBucketIds() {
        return bucketIds;
    }

    public void setCountEnable(boolean countEnable) {
        this.countEnable = countEnable;
    }

    public ChoosePhotoAdapter(Context context) {
        this.context = context;
        this.bucketIds = new ArrayList<>();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setPhotoListInterface(OnPhotoListInterface photoListInterface) {
        this.photoListInterface = photoListInterface;
    }

    public void setTakePhoto(boolean takePhoto) {
        this.takePhoto = takePhoto;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public ArrayList<Photo> getSelectedPhotos() {
        return selectedPhotos;
    }

    public void setSelectedPhotos(ArrayList<Photo> selectedPhotos) {
        if (CommonUtil.isCollectionEmpty(selectedPhotos)) {
            this.selectedPhotos = new ArrayList<>();
        } else {
            this.selectedPhotos = selectedPhotos;
        }
        bucketIds.clear();
        for (Photo photo : selectedPhotos) {
            bucketIds.add(photo.getBucketId());
        }

        if (photoListInterface != null) {
            photoListInterface.onSelectedCountChange(selectedPhotos.size());
        }
        notifyDataSetChanged();
    }

    public void setPhotos(List<Photo> photos) {
        if (photos == null) {
            return;
        }
        this.photos = photos;
        notifyDataSetChanged();
    }

    public void addPhotos(List<Photo> photos) {
        if (photos == null) {
            return;
        }
        if (this.photos == null) {
            this.photos = new ArrayList<>();
            this.photos.addAll(photos);
            notifyDataSetChanged();
        } else {
            this.photos.addAll(photos);
            notifyItemRangeInserted(startIndex() + this.photos.size() - photos.size(),
                    photos.size());
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TAKE_PHOTO:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.take_photo_item___img, parent, false);
                view.getLayoutParams().height = Math.round((CommonUtil.getDeviceSize(parent
                        .getContext()).x - CommonUtil.dp2px(
                        parent.getContext(),
                        20)) / 3);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (photoListInterface != null) {
                            photoListInterface.onTakePhotoClick();
                        }
                    }
                });
                return new ExtraViewHolder<R>(view);
            case HEADER:
                return new ExtraViewHolder<R>(headerView);
            case IMAGE_ITEM:
                return new ChooseImageViewHolder(parent, this);
            case VIDEO_ITEM:
                return new ChooseVideoViewHolder(parent, this);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof BasePhotoViewHolder) {
            ((BasePhotoViewHolder) holder).setView(context,
                    getItem(position),
                    position,
                    getItemViewType(position));
        }
    }

    private Photo getItem(int position) {
        return photos.get(position - startIndex());
    }

    int startIndex() {
        int index = 0;
        if (takePhoto) {
            index++;
        }
        if (headerView != null) {
            index++;
        }
        return index;
    }

    @Override
    public int getItemCount() {
        int count = startIndex();
        if (photos != null) {
            count += photos.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (startIndex() > position) {
            if (position == 0 && headerView != null) {
                return HEADER;
            }
            return TAKE_PHOTO;
        }
        if (getItem(position).isVideo()) {
            return VIDEO_ITEM;
        }
        return IMAGE_ITEM;
    }

    public ItemSpace getSpaceType(int position) {
        if (getItemViewType(0) == HEADER) {
            position--;
        }
        if (position >= 0) {
            switch (position % 3) {
                case 0:
                    return ItemSpace.Left;
                case 1:
                    return ItemSpace.Middle;
                case 2:
                    return ItemSpace.Right;
            }
        }
        return ItemSpace.None;
    }

    public interface OnPhotoListInterface {

        void onSelectedCountChange(int selectedCount);

        void onPreview(List<Photo> photos, List<Photo> selectedPhotos, Photo currentPhoto);

        void onVideoPreview(Photo currentPhoto);

        void onTakePhotoClick();
    }
}
