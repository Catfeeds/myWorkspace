package me.suncloud.marrymemo.model.community;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityPost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luohanlin on 2017/5/10.
 * 话题评论列表数据
 */

public class HljCommunityPostsData implements HljRZData, Parcelable {
    List<CommunityPost> list;
    @SerializedName("start_serial_nos")
    List<String> startSerialNos;
    @SerializedName("total_count")
    int totalCount;

    @Override
    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }

    public List<CommunityPost> getList() {
        return list;
    }

    public List<String> getStartSerialNos() {
        if (startSerialNos == null) {
            return new ArrayList<>();
        }
        return startSerialNos;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public HljCommunityPostsData() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.list);
        dest.writeStringList(this.startSerialNos);
        dest.writeInt(this.totalCount);
    }

    protected HljCommunityPostsData(Parcel in) {
        this.list = in.createTypedArrayList(CommunityPost.CREATOR);
        this.startSerialNos = in.createStringArrayList();
        this.totalCount = in.readInt();
    }

    public static final Creator<HljCommunityPostsData> CREATOR = new
            Creator<HljCommunityPostsData>() {
        @Override
        public HljCommunityPostsData createFromParcel(Parcel source) {
            return new HljCommunityPostsData(source);
        }

        @Override
        public HljCommunityPostsData[] newArray(int size) {return new HljCommunityPostsData[size];}
    };
}
