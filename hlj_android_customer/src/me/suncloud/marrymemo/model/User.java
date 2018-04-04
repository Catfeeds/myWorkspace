/**
 *
 */
package me.suncloud.marrymemo.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.CommentStatistics;
import com.hunliji.hljcommonlibrary.models.Member;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import me.suncloud.marrymemo.model.wrappers.SerializableMember;
import me.suncloud.marrymemo.util.JSONUtil;

/**
 * @author iDay
 */
public class User implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = 4723889163553502490L;

    private long id;
    private String nick;
    private String avatar;
    private boolean haveCard;
    private Date birthday;
    private String description;
    private int gender;
    private Long collectCount;
    private Long montageCount;
    private Long likeCount;
    private Long fanCount;
    private Long watchCount;
    private String token;
    private int kind;
    private boolean auth;
    private boolean following;
    private String phone;
    private String realName;
    private boolean open;
    private Date weddingDay;
    private String hometown;
    private long hometownId;
    private boolean isFake; // 是否是马甲账户
    private boolean isCollected;
    private String hxName; // 环信账户
    private String hxPassword;
    private int followCount;
    private int isPending;//0未设置婚期 1婚期待定 2设置了婚期
    private boolean isNeed;//资料是否完善
    private String specialty;//达人标志

    private boolean isMerchant;

    // 新增伴侣id节点
    private Long partnerUid;
    private String partnerUserStr;
    private CustomerUser partnerUser;

    //新增默认头像节点
    private String defaultAvatar;
    //新增会员信息字段，为空时表示为非会员
    private SerializableMember member;

    public User(long id) {
        this.id = id;
    }

    public User(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("user_id", 0);
            if (id == 0) {
                this.id = jsonObject.optLong("id", 0);
            }
            this.nick = JSONUtil.getString(jsonObject, "nick");
            // 在使用微博登陆之后返回的数据,如果用户没有设置头像(数据库中avatar字段为空)的时候,这里会返回一个无意义的字段
            this.avatar = JSONUtil.getString(jsonObject, "avatar");
            if (!jsonObject.isNull("img") && JSONUtil.isEmpty(avatar)) {
                this.avatar = JSONUtil.getString(jsonObject, "img");
            }
            this.birthday = JSONUtil.getDate(jsonObject, "birthday");
            this.description = JSONUtil.getString(jsonObject, "description");
            this.gender = jsonObject.optInt("gender");
            this.collectCount = jsonObject.optLong("collect_count");
            this.montageCount = jsonObject.optLong("montage_count");
            this.likeCount = jsonObject.optLong("like_count");
            this.watchCount = jsonObject.optLong("watch_count");
            this.fanCount = jsonObject.optLong("fans_count");
            if (fanCount == 0) {
                this.fanCount = jsonObject.optLong("_fans_count");
            }
            this.token = JSONUtil.getString(jsonObject, "token");
            this.kind = jsonObject.optInt("kind");
            this.auth = jsonObject.optInt("auth") == 1;
            this.open = jsonObject.optBoolean("open", false);
            this.phone = JSONUtil.getString(jsonObject, "phone");
            this.realName = JSONUtil.getString(jsonObject, "name");
            this.weddingDay = JSONUtil.getDateFromFormatShort(jsonObject, "weddingday", false);
            this.isPending = jsonObject.optInt("is_pending", weddingDay != null ? 2 : 0);
            this.hometown = JSONUtil.getString(jsonObject, "hometown");
            this.hometownId = jsonObject.optLong("hometown_id", 0);
            this.isFake = jsonObject.optInt("is_faker", 0) > 0;
            this.isCollected = jsonObject.optInt("is_collected") > 0;
            JSONObject hxObject = jsonObject.optJSONObject("hx_user");
            if (hxObject != null) {
                this.hxName = JSONUtil.getString(hxObject, "username");
                this.hxPassword = JSONUtil.getString(hxObject, "password");
            }
            this.followCount = jsonObject.optInt("follow_count");
            this.isNeed = jsonObject.optBoolean("is_need", false);
            if (!isNeed) {
                this.isNeed = jsonObject.optInt("is_need", 0) > 0;
            }
            this.specialty = JSONUtil.getString(jsonObject, "specialty");
            isMerchant = jsonObject.optBoolean("is_merchant", false);
            if (!isMerchant) {
                isMerchant = jsonObject.optInt("is_merchant", 0) > 0;
            }
            this.partnerUid = jsonObject.optLong("partner_uid", 0);
            this.partnerUserStr = jsonObject.optString("partner");
            this.defaultAvatar = JSONUtil.getString(jsonObject, "default_avatar");
            if (!jsonObject.isNull("member") && !TextUtils.isEmpty(jsonObject.optString("member")
            )) {
                this.member = new SerializableMember(jsonObject.optJSONObject("member"));
            }
        }
    }

    public void editUser(JSONObject jsonObject) {
        if (!jsonObject.isNull("id")) {
            this.id = jsonObject.optLong("id");
        }
        if (!jsonObject.isNull("is_pending")) {
            this.isPending = jsonObject.optInt("is_pending", isPending);
        }
        if (!jsonObject.isNull("nick")) {
            this.nick = JSONUtil.getString(jsonObject, "nick");
        }
        if (!jsonObject.isNull("avatar")) {
            this.avatar = JSONUtil.getString(jsonObject, "avatar");
        }
        if (!jsonObject.isNull("have_card")) {
            this.haveCard = jsonObject.optInt("have_card") == 1;
        }
        if (!jsonObject.isNull("birthday")) {
            this.birthday = JSONUtil.getDate(jsonObject, "birthday");
        }
        if (!jsonObject.isNull("description")) {
            this.description = JSONUtil.getString(jsonObject, "description");
        }
        if (!jsonObject.isNull("gender")) {
            this.gender = jsonObject.optInt("gender");
        }
        if (!jsonObject.isNull("collect_count")) {
            this.collectCount = jsonObject.optLong("collect_count");
        }
        if (!jsonObject.isNull("montage_count")) {
            this.montageCount = jsonObject.optLong("montage_count");
        }
        if (!jsonObject.isNull("like_count")) {
            this.likeCount = jsonObject.optLong("like_count");
        }
        if (!jsonObject.isNull("token")) {
            this.token = JSONUtil.getString(jsonObject, "token");
        }
        if (!jsonObject.isNull("phone")) {
            this.phone = JSONUtil.getString(jsonObject, "phone");
        }
        if (!jsonObject.isNull("name")) {
            this.realName = JSONUtil.getString(jsonObject, "name");
        }
        if (!jsonObject.isNull("open")) {
            this.open = jsonObject.optBoolean("open", false);
        }
        if (!jsonObject.isNull("is_faker")) {
            this.isFake = jsonObject.optInt("is_faker", 0) > 0;
        }
        if (!jsonObject.isNull("hometown_id")) {
            this.hometownId = jsonObject.optLong("hometown_id", 0);
        }
        if (!jsonObject.isNull("hometown")) {
            this.hometown = jsonObject.optString("hometown");
        }
        if (!jsonObject.isNull("weddingday")) {
            this.weddingDay = JSONUtil.getDateFromFormatShort(jsonObject, "weddingday", false);
        }
        if (!jsonObject.isNull("specialty")) {
            this.specialty = JSONUtil.getString(jsonObject, "specialty");
        }
        if (!jsonObject.isNull("is_need")) {
            this.isNeed = jsonObject.optBoolean("is_need", false);
            if (!isNeed) {
                this.isNeed = jsonObject.optInt("is_need", 0) > 0;
            }
        }
        JSONObject hxObject = jsonObject.optJSONObject("hx_user");
        if (hxObject != null) {
            this.hxName = JSONUtil.getString(hxObject, "username");
            this.hxPassword = JSONUtil.getString(hxObject, "password");
        }
        if (!jsonObject.isNull("default_avatar")) {
            this.defaultAvatar = JSONUtil.getString(jsonObject, "default_avatar");
        }
        if (!jsonObject.isNull("member") && !TextUtils.isEmpty(jsonObject.optString("member"))) {
            this.member = new SerializableMember(jsonObject.optJSONObject("member"));
        }
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * @param nick the nick to set
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * @return the avatar
     */
    public String getAvatar() {
        return JSONUtil.getAvatar(avatar, 200);
    }

    public String getAvatar(int size) {
        return JSONUtil.getAvatar(avatar, size);
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar2() {
        return JSONUtil.isEmpty(avatar) ? null : avatar;
    }

    /**
     * @return the haveCard
     */
    public boolean isHaveCard() {
        return haveCard;
    }

    /**
     * @param haveCard the haveCard to set
     */
    public void setHaveCard(boolean haveCard) {
        this.haveCard = haveCard;
    }

    /**
     * @return the birthday
     */
    public Date getBirthday() {
        return birthday;
    }

    /**
     * @param birthday the birthday to set
     */
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the gender
     */
    public int getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * @return the collectCount
     */
    public Long getCollectCount() {
        return collectCount;
    }

    /**
     * @param collectCount the collectCount to set
     */
    public void setCollectCount(Long collectCount) {
        this.collectCount = collectCount;
    }

    /**
     * @return the montageCount
     */
    public Long getMontageCount() {
        return montageCount;
    }

    /**
     * @param montageCount the montageCount to set
     */
    public void setMontageCount(Long montageCount) {
        this.montageCount = montageCount;
    }

    /**
     * @return the likeCount
     */
    public Long getLikeCount() {
        return likeCount;
    }

    /**
     * @param likeCount the likeCount to set
     */
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * @return the kind
     */
    public int getKind() {
        return kind;
    }

    /**
     * @param kind the kind to set
     */
    public void setKind(int kind) {
        this.kind = kind;
    }

    public Long getFanCount() {
        return fanCount;
    }

    public Long getWatchCount() {
        return watchCount;
    }


    public boolean getFollow() {
        return following;
    }

    public void setFollow(boolean follow) {
        this.following = follow;
    }

    public boolean getAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public String getPhone() {
        return phone;
    }

    public String getRealName() {
        return realName;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getWeddingDay() {
        return isPending == 0 ? null : weddingDay;
    }

    public void setWeddingDay(Date date) {
        this.weddingDay = date;
    }

    public String getHometown() {
        return hometown;
    }

    public long getHometownId() {
        return hometownId;
    }

    public boolean isFake() {
        return isFake;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setIsCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public String getHxName() {
        return hxName;
    }

    public String getHxPassword() {
        return hxPassword;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public void setFanCount(Long fanCount) {
        this.fanCount = fanCount;
    }

    public int getIsPending() {
        return isPending;
    }

    public void setIsPending(int isPending) {
        this.isPending = isPending;
    }

    public boolean isNeed() {
        return isNeed;
    }

    public void setNeed(boolean need) {
        isNeed = need;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public boolean isMerchant() {
        return isMerchant;
    }

    public long getPartnerUid() {
        return partnerUid == null ? 0 : partnerUid.intValue();
    }

    public CustomerUser getPartnerUser() {
        if (partnerUser == null && !CommonUtil.isEmpty(partnerUserStr)) {
            partnerUser = GsonUtil.getGsonInstance()
                    .fromJson(partnerUserStr, CustomerUser.class);
        }
        return partnerUser;
    }

    public String getDefaultAvatar() {
        return JSONUtil.getAvatar(defaultAvatar, 200);
    }

    public SerializableMember getMember() {
        return member;
    }

    public Member getCommomMember() {
        if (member == null) {
            return null;
        }
        Member commonMember = new Member();
        commonMember.setId(member.getId());
        commonMember.setAddressId(member.getAddressId());
        return commonMember;
    }
}
