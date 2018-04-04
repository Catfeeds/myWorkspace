package com.hunliji.hljlivelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.Media;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.Video;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suncloud on 2016/10/28.
 */

public class LiveContent implements Parcelable {

    private String text;
    private double voiceDuration;
    private String voicePath;
    private List<String> images;
    private transient int kind;
    private Video video;
    private List<LiveSpotMedia> medias;

    public LiveContent() {
    }

    /**
     * 文本
     *
     * @param text
     */
    public LiveContent(String text) {
        this.text = text;
        this.kind = LiveMessage.MSG_KIND_TEXT;
    }

    /**
     * 图文构造
     *
     * @param text   描述
     * @param images 图片
     */
    public LiveContent(String text, List<String> images) {
        this.text = text;
        this.images = images;
        medias = new ArrayList<>();
        for (String path : images) {
            Media media = new Media();
            media.setPhoto(new Photo(path));
            media.setType(Media.TYPE_PHOTO);
            medias.add(new LiveSpotMedia(media));
        }
        this.kind = LiveMessage.MSG_KIND_TEXT;
    }

    /**
     * 视频
     *
     * @param text
     * @param video
     */
    public LiveContent(String text, Video video) {
        this.text = text;
        this.video = video;
        medias = new ArrayList<>();
        Media media = new Media();
        media.setVideo(video);
        media.setType(Media.TYPE_VIDEO);
        medias.add(new LiveSpotMedia(media));
        this.kind = LiveMessage.MSG_KIND_VIDEO;
    }


    /**
     * 语音构造
     *
     * @param voiceDuration 持续时间
     * @param voicePath     语音链接
     */
    public LiveContent(String voicePath, double voiceDuration) {
        this.voiceDuration = voiceDuration;
        this.voicePath = voicePath;
        this.kind = LiveMessage.MSG_KIND_AUDIO;
    }

    public static final Creator<LiveContent> CREATOR = new Creator<LiveContent>() {
        @Override
        public LiveContent createFromParcel(Parcel in) {
            return new LiveContent(in);
        }

        @Override
        public LiveContent[] newArray(int size) {
            return new LiveContent[size];
        }
    };

    public int getKind() {
        return kind;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(double voiceDuration) {
        this.voiceDuration = voiceDuration;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    // 不论图片视频，返回全部列表
    public List<LiveSpotMedia> getAllMedias() {
        if (CommonUtil.isCollectionEmpty(medias)) {
            medias = new ArrayList<>();
            medias.addAll(getImageMedias());
            LiveSpotMedia videoMedia = getVideoMedia();
            if (videoMedia != null) {
                medias.add(videoMedia);
            }
        }
        return medias;
    }

    // 理论上图片和视频不共存，但尽可能兼容
    public List<LiveSpotMedia> getImageMedias() {
        if (!CommonUtil.isCollectionEmpty(medias)) {
            return medias;
        } else {
            List<LiveSpotMedia> mediaList = new ArrayList<>();
            if (!CommonUtil.isCollectionEmpty(images)) {
                for (String path : images) {
                    Media media = new Media();
                    media.setPhoto(new Photo(path));
                    media.setType(Media.TYPE_PHOTO);
                    mediaList.add(new LiveSpotMedia(media));
                }
            }
            return mediaList;
        }
    }

    public LiveSpotMedia getVideoMedia() {
        if (!CommonUtil.isCollectionEmpty(medias)) {
            for (LiveSpotMedia media : medias) {
                if (media.getMedia()
                        .getType() == Media.TYPE_VIDEO && media.getMedia()
                        .getVideo() != null) {
                    return media;
                }
            }
        }

        if (video != null) {
            Media media = new Media();
            media.setVideo(video);
            media.setType(Media.TYPE_VIDEO);
            return new LiveSpotMedia(media);
        }

        return null;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeDouble(this.voiceDuration);
        dest.writeString(this.voicePath);
        dest.writeStringList(this.images);
        dest.writeParcelable(this.video, flags);
        dest.writeTypedList(this.medias);
    }

    protected LiveContent(Parcel in) {
        this.text = in.readString();
        this.voiceDuration = in.readDouble();
        this.voicePath = in.readString();
        this.images = in.createStringArrayList();
        this.video = in.readParcelable(Video.class.getClassLoader());
        this.medias = in.createTypedArrayList(LiveSpotMedia.CREATOR);
    }

}
