package com.hunliji.hljcommonviewlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityChannel;
import com.hunliji.hljcommonlibrary.models.communitythreads.CommunityThread;
import com.hunliji.hljcommonlibrary.models.event.CommunityEvent;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.FinancialSwitch;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2016/6/24. 社区V2首页热门话题推荐(包含话题,故事,问答数据结构)
 */
public class CommunityFeed implements Parcelable {

    public static final String COMMUNITY_THREAD = "CommunityThread";
    public static final String QA_QUESTION = "QaQuestion";
    public static final String POSTER = "Poster";
    public static final String HOT_THREAD = "HotThread";
    public static final String HOT_QUESTION = "HotQuestion";
    public static final String HOT_CHANNEL = "HotChannel";
    public static final String COMMUNITY_EVENT = "CommunityActivity";

    private long id;
    private long cid;
    @SerializedName(value = "entity_id")
    private long entityId;
    @SerializedName(value = "entity_type")
    private String entityType;//类型 CommunityThread 话题 QaQuestion问题
    private int weight;
    @SerializedName(value = "entity")
    private JsonElement entityJson;

    //存储entity手动解析后的数据，不进行自动解析
    transient private CommunityThread thread;
    transient private Question question;
    transient private List<CommunityFeed> hotThreads;
    transient private List<Question> hotQuestions;
    transient private Poster poster;
    transient private List<CommunityChannel> hotChannels;
    transient private CommunityEvent communityEvent;
    transient private Boolean isShowThree;//是否能显示三图样式

    public boolean isShowThree() {
        if (isShowThree == null) {
            isShowThree = (Math.random() * 100) < 80;
        }
        return isShowThree;
    }

    /**
     * entity节点数据类型根据entityType进行区分解析
     * CommunityThread社区类型；QaQuestion问题类型；Story故事类型
     */
    public synchronized Object getEntity() {
        if (entityJson == null) {
            return null;
        }
        //将字符串转换成jsonObject对象
        Gson gson = GsonUtil.getGsonInstance();
        if (entityType.equals(COMMUNITY_THREAD)) {
            if (thread == null) {
                thread = gson.fromJson(entityJson.toString(), CommunityThread.class);
            }
            return thread;
        } else if (entityType.equals(QA_QUESTION)) {
            if (question == null) {
                question = gson.fromJson(entityJson.toString(), Question.class);
            }
            return question;
        } else if (entityType.equals(HOT_QUESTION)) {
            if (hotQuestions == null) {
                hotQuestions = gson.fromJson(entityJson.toString(),
                        new TypeToken<List<Question>>() {}.getType());
            }
            return hotQuestions;
        } else if (entityType.equals(HOT_THREAD)) {
            if (hotThreads == null) {
                hotThreads = gson.fromJson(entityJson.toString(),
                        new TypeToken<List<CommunityFeed>>() {}.getType());
            }
            return hotThreads;
        } else if (entityType.equals(HOT_CHANNEL)) {
            if (hotChannels == null) {
                hotChannels = gson.fromJson(entityJson.toString(),
                        new TypeToken<List<CommunityChannel>>() {}.getType());
            }
            return hotChannels;
        } else if (entityType.equals(POSTER)) {
            if (poster == null) {
                List<Poster> posters = getPoster(entityJson.getAsJsonObject());
                if (!CommonUtil.isCollectionEmpty(posters)) {
                    poster = posters.get(0);
                }
            }
            return poster;
        } else if (entityType.equals(COMMUNITY_EVENT)) {
            if (communityEvent == null) {
                communityEvent = gson.fromJson(entityJson.toString(), CommunityEvent.class);
            }
            return communityEvent;
        }
        return null;
    }

    private List<Poster> getPoster(JsonObject entityJson) {
        boolean isClosed = FinancialSwitch.INSTANCE.isClosed(null);
        ArrayList<Poster> posters = new ArrayList<>();
        try {
            if (entityJson != null) {
                JsonArray array = entityJson.getAsJsonArray("holes");
                Gson gson = GsonUtil.getGsonInstance();
                for (JsonElement jsonElement : array) {
                    try {
                        Poster poster = gson.fromJson(jsonElement.getAsJsonObject()
                                .get("posters"), Poster.class);
                        if (poster == null) {
                            continue;
                        }
                        if (poster.getId() > 0) {
                            if (isClosed && poster.getTargetType()
                                    == 48) {
                                continue;
                            }
                            posters.add(poster);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posters;
    }

    public long getId() {
        return id;
    }

    public long getCid() {
        return cid;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

    public int getWeight() {
        return weight;
    }

    public CommunityFeed() {}


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.cid);
        dest.writeLong(this.entityId);
        dest.writeString(this.entityType);
        dest.writeInt(this.weight);
        dest.writeString(this.entityJson.toString());
    }

    protected CommunityFeed(Parcel in) {
        this.id = in.readLong();
        this.cid = in.readLong();
        this.entityId = in.readLong();
        this.entityType = in.readString();
        this.weight = in.readInt();
        this.entityJson = (JsonObject) new JsonParser().parse(in.readString());
    }

    public static final Parcelable.Creator<CommunityFeed> CREATOR = new Parcelable
            .Creator<CommunityFeed>() {
        @Override
        public CommunityFeed createFromParcel(Parcel source) {return new CommunityFeed(source);}

        @Override
        public CommunityFeed[] newArray(int size) {return new CommunityFeed[size];}
    };
}
