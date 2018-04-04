package com.hunliji.hljimagelibrary.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtao on 2017/11/21.
 */

public enum StaticImageList {

    INSTANCE;

    private List<String> imagePaths;
    private List<String> selectedPaths;

    StaticImageList() {

    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public void setSelectedPaths(List<String> selectedPaths) {
        this.selectedPaths = selectedPaths;
    }

    public List<String> getImagePaths() {
        if(imagePaths==null){
            return new ArrayList<>();
        }
        return imagePaths;
    }

    public List<String> getSelectedPaths() {
        if(selectedPaths==null){
            return new ArrayList<>();
        }
        return selectedPaths;
    }

    public void onDestroy() {
        imagePaths = null;
        selectedPaths = null;
    }
}
