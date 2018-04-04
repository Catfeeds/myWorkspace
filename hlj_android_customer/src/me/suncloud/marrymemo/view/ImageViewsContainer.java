package me.suncloud.marrymemo.view;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.V2.ImageHoleV2;
import me.suncloud.marrymemo.widget.DraggableImageView;

/**
 * Created by luohanlin on 15/5/13.
 */
public class ImageViewsContainer {
    private HashMap<View, DraggableImageView> hashMap;
    private View dragTarget = null; // 拖动标识的新的目标
    public static final String TAG = ImageViewsContainer.class.getSimpleName();
    private ImageView dragHolder;
    private boolean holderVisible;
    private DraggableImageView touchImageView;
    public ImageViewsContainer(HashMap<View, DraggableImageView> hashMap) {
        this.hashMap = hashMap;
        for (DraggableImageView view : hashMap.values()) {
            view.setBelongToContainer(this);
        }
    }

    public View getHolderByImage(DraggableImageView imageView) {
        if (hashMap.containsValue(imageView)) {
            for (View key : hashMap.keySet()) {
                if (key != null && hashMap.get(key) == imageView) {
                    return key;
                }
            }
        }

        return null;
    }

    public boolean checkingCrossState(DraggableImageView currentImageView, float x, float y) {
        View currentHolder = getHolderByImage(currentImageView);
        x += currentHolder.getX();
        y += currentHolder.getY();
        int w = dragHolder.getMeasuredWidth();
        int h = dragHolder.getMeasuredHeight();
        boolean b = false;
        if (dragTarget != null) {
            DraggableImageView imageView = hashMap.get(dragTarget);
            if (imageView != null && imageView != currentImageView) {
                int pointX = (int) (x - imageView.getX());
                int pointY = (int) (y - imageView.getY());
                if (!imageView.isMaskTouch(pointX, pointY)) {
                    dragTarget.setBackgroundResource(android.R.color.transparent);
                    dragTarget = null;
                }
            }
        }

        if (holderVisible) {
            dragHolder.setX(x - w / 2);
            dragHolder.setY(y - h / 2);
            b = true;
        }
        if (dragTarget != null) {
            return b;
        }
        if (x > currentHolder.getX() && y > currentHolder.getY() && x < currentHolder.getX() + currentHolder.getWidth() && y < currentHolder.getY() + currentHolder.getHeight()) {
            return b;
        }
        for (View key : hashMap.keySet()) {
            DraggableImageView imageView = hashMap.get(key);
            if (imageView != null && imageView != currentImageView) {
                int pointX = (int) (x - imageView.getX());
                int pointY = (int) (y - imageView.getY());
                if (imageView.isMaskTouch(pointX, pointY)) {
                    key.setBackgroundResource(R.drawable.bg_card_page_v2_image);
                    dragTarget = key;

                    // 显示拖动指示图片
                    if (!holderVisible) {
                        dragHolder.setX(x - w / 2);
                        dragHolder.setY(y - h / 2);
                        holderVisible = true;
                        dragHolder.setImageDrawable(currentImageView.getDrawable());
                        currentImageView.setVisibility(View.INVISIBLE);
                        dragHolder.setVisibility(View.VISIBLE);
                        b = true;
                    }
                    return b;
                }
            }
        }
        return b;

    }

    public void exchangeImageResource(DraggableImageView imageView) {
        if (dragTarget != null) {
            DraggableImageView targetImageView = hashMap.get(dragTarget);
            Drawable drawable = imageView.getDrawable();
            imageView.setImageDrawable(targetImageView.getDrawable());
            targetImageView.setImageDrawable(drawable);
            imageView.setIsChange(true);
            targetImageView.setIsChange(true);
            ImageHoleV2 imageHoleV2_1 = imageView.getImageHoleV2();
            ImageHoleV2 imageHoleV2_2 = targetImageView.getImageHoleV2();
            if(imageHoleV2_1!=null&&imageHoleV2_2!=null) {
                String tempPath = imageHoleV2_1.getPath();
                imageHoleV2_1.setPath(imageHoleV2_2.getPath());
                imageHoleV2_2.setPath(tempPath);
                imageHoleV2_1.setH5ImagePath(null);
                imageHoleV2_2.setH5ImagePath(null);
            }
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
            targetImageView.setScaleType(ImageView.ScaleType.MATRIX);
            targetImageView.invalidate();
            imageView.invalidate();
            setHolderShadow(targetImageView, false);

            dragTarget = null;
        }

        holderVisible = false;
        dragHolder.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
    }

    public void setHolderShadow(DraggableImageView imageView, boolean visible) {
        DraggableImageView imageView1 = imageView;
        if (touchImageView != null && touchImageView != imageView) {
            imageView1 = touchImageView;
        }
        View view = getHolderByImage(imageView1);
        if (view != null) {
            touchImageView = visible ? imageView1 : null;
            view.setBackgroundResource(visible ? R.drawable.bg_card_page_v2_image : android.R
                    .color.transparent);
        }
    }

    public boolean isTouchImageView(DraggableImageView imageView) {
        return touchImageView == null || imageView == touchImageView;
    }

    public void setDragHolder(ImageView dragHolder) {
        this.dragHolder = dragHolder;
    }

    public boolean checkedTouchEvent(DraggableImageView imageView, float x, float y) {
        x += imageView.getX();
        y += imageView.getY();
        DraggableImageView imageView2 = null;
        for (DraggableImageView draggableImageView : hashMap.values()) {
            if (draggableImageView != null) {
                int pointX = (int) (x - draggableImageView.getX());
                int pointY = (int) (y - draggableImageView.getY());
                if (draggableImageView.isMaskTouch(pointX, pointY)) {
                    imageView2 = draggableImageView;
                    break;
                }
            }
        }
        return !(imageView2 != null && imageView != imageView2);
    }
}

