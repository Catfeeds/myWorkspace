package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangtao on 2017/6/10.
 */

public class CardPage implements Parcelable {

    @SerializedName("card_id")
    private long cardId;
    private long id;
    @SerializedName("page_template_id")
    private long pageTemplateId;
    @SerializedName("images")
    private List<ImageInfo> imageInfos;
    @SerializedName("texts")
    private List<TextInfo> textInfos;
    private boolean hidden;
    @SerializedName("page_template")
    private Template template;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;

    private boolean select;

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public CardPage(long pageTemplateId) {
        this.pageTemplateId = pageTemplateId;
    }

    public CardPage(Template template) {
        this.pageTemplateId = template.getId();
        this.template = template;
        if (!CommonUtil.isCollectionEmpty(template.getImageHoles())) {
            imageInfos = new ArrayList<>();
            for (ImageHole imageHole : template.getImageHoles()) {
                imageInfos.add(new ImageInfo(imageHole));
            }

        }
        if (!CommonUtil.isCollectionEmpty(template.getTextHoles())) {
            textInfos = new ArrayList<>();
            for (TextHole textHole : template.getTextHoles()) {
                textInfos.add(new TextInfo(textHole));
            }

        }

    }

    public long getPageTemplateId() {
        return pageTemplateId;
    }

    public long getId() {
        return id;
    }

    public long getCardId() {
        return cardId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<ImageInfo> getImageInfos() {
        if (imageInfos == null) {
            return new ArrayList<>();
        }
        return imageInfos;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        if (imageInfos == null) {
            imageInfos = new ArrayList<>();
        }
        int index=-1;
        for (ImageInfo info : imageInfos) {
            if (info.getHoleId() == imageInfo.getHoleId()) {
                index=imageInfos.indexOf(info);
                break;
            }
        }
        if(index>=0) {
            imageInfos.set(index,imageInfo);
        }else {
            imageInfos.add(imageInfo);
        }
    }

    public void setTextInfo(TextInfo textInfo) {
        if (textInfos == null) {
            textInfos = new ArrayList<>();
        }
        for (TextInfo info : textInfos) {
            if (info.getHoleId() == textInfo.getHoleId()) {
                textInfos.remove(info);
                break;
            }
        }
        textInfos.add(textInfo);
    }

    public List<TextInfo> getTextInfos() {
        if (textInfos == null) {
            return new ArrayList<>();
        }
        return textInfos;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Template getTemplate() {
        return template;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }


    public ArrayList<String> getImagePaths() {
        ArrayList<String> imagePaths = new ArrayList<>();
        for (ImageInfo imageInfo : getImageInfos()) {
            if (imageInfo.isVideo()) {
                if (!TextUtils.isEmpty(imageInfo.getPath())) {
                    imagePaths.add(imageInfo.getPath() + HljCommon.QINIU.SCREEN_SHOT_URL_0_SECONDS);
                }
            } else {
                if(!TextUtils.isEmpty(imageInfo.getH5ImagePath())) {
                    imagePaths.add(imageInfo.getH5ImagePath());
                }
            }
        }
        for (TextInfo textInfo : getTextInfos()) {
            if(!TextUtils.isEmpty(textInfo.getH5ImagePath())) {
                imagePaths.add(textInfo.getH5ImagePath());
            }
        }
        return imagePaths;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.cardId);
        dest.writeLong(this.id);
        dest.writeLong(this.pageTemplateId);
        dest.writeTypedList(this.imageInfos);
        dest.writeTypedList(this.textInfos);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.template, flags);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeByte(this.select ? (byte) 1 : (byte) 0);
    }

    protected CardPage(Parcel in) {
        this.cardId = in.readLong();
        this.id = in.readLong();
        this.pageTemplateId = in.readLong();
        this.imageInfos = in.createTypedArrayList(ImageInfo.CREATOR);
        this.textInfos = in.createTypedArrayList(TextInfo.CREATOR);
        this.hidden = in.readByte() != 0;
        this.template = in.readParcelable(Template.class.getClassLoader());
        this.createdAt = (Date) in.readSerializable();
        this.updatedAt = (Date) in.readSerializable();
        this.select = in.readByte() != 0;
    }

    public static final Creator<CardPage> CREATOR = new Creator<CardPage>() {
        @Override
        public CardPage createFromParcel(Parcel source) {return new CardPage(source);}

        @Override
        public CardPage[] newArray(int size) {return new CardPage[size];}
    };
}
