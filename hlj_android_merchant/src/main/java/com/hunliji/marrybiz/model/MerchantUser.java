package com.hunliji.marrybiz.model;

import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.marrybiz.model.merchant.MerchantIsAdv;
import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MerchantUser implements Identifiable {

    public transient static final int PROPERTY_WEDDING_PLAN = 2; //婚礼策划
    public transient static final int PROPERTY_WEDDING_DRESS_PHOTO = 6;//婚纱摄影
    public transient static final int PROPERTY_WEDDING_PHOTO = 7; //婚礼摄影
    public transient static final int PROPERTY_WEDDING_SHOOTING = 8;//婚礼摄像
    public transient static final int PROPERTY_WEDDING_MAKEUP = 9; //婚礼跟妆
    public transient static final int PROPERTY_WEDDING_COMPERE = 11; //婚礼司仪
    public transient static final int PROPERTY_WEDDING_DRESS = 12; //婚纱礼服
    public transient static final int PROPERTY_WEEDING_HOTEL = 13; //婚宴酒店

    private Long userId;
    private String name;
    private Long city_code;
    private String address;
    private String contactPerson;
    private int auth;
    private String memo;
    private Date createdAt;
    private Date updatedAt;
    private String logoPath;
    private String licensePath;
    private String cityName;
    private String email;
    private Long sequence;
    private Long parameter;
    private int mCount;
    private int nice;
    private double latitude;
    private double longitude;
    private String hashedPassword;
    private String companyName;
    private String des;// 商家提交的简介
    private int contactCount;
    private int fansCount;
    private int opusCount;
    private String shearPath;
    private String rank;
    private double rating;
    private String coverPath;
    private String phone; // 绑定的登陆手机号码,与联系电话/门店电话没有任何关系
    private String reason; // 店铺资料审核失败的原因
    private City city; // 商家选择的城市
    private MerchantProperty property; // 商家经营范围
    private MerchantProperty category; // 商家经营范围二级类目
    private String lincensePath; // 凭证图片
    private int kind; // 商家账户所属类型, 1:个人, 2:企业
    private String shopPhone; // 之前的联系电话,现在作为门店电话,从1.6开始只使用一个门店电话
    private String contactMobile; // 现在的联系电话,只有一个,作为短信通知通道,如果老用户设置过绑定电话bind_phone,
    // 在新版本中将会把bind_phone作为新的联系电话
    private Long id;
    /**
     * examine和status两个状态共同来判断店铺资料的状态
     * examine,-1表示没有填写过店铺资料,0表示正在审核中或者审核失败,1是首次审核通过,只要通过一次审核变为1后,这个值就会一直保持为通过状态
     * status,只有examine等于0或者1的时候,status才有意义,
     * examine=0&&status=0:第一次提交正在审核中,
     * examine=1&&status=0:再次审核中
     * examine=1&&status=1:再次审核被拒绝
     * <p/>
     * examine有3个值，-1（代表资料未填写），0（审核中），1（审核通过）
     * status有3个值,0表示审核中，1表示审核被拒绝，2表示审核通过
     * <p/>
     * examine主要用于判断新注册商家资料的审核状态
     * 而status则用于判断修改商家资料的审核状态
     */
    private int examine; // -1表示没有开通店铺,没有提交过开店资料
    private int status;
    private String token;
    private boolean open_data;
    private String user_token;
    /**
     * 保证金商家标识的状态,没有审核了
     * bondSign=true: 保证金商家的权限转台是否正常,true是正常的,false是权限被关闭(余额不足超过限定时间)
     * bondPaid=true: 已付保证金
     */
    private boolean bondSign;
    private boolean bondPaid;
    private ShareInfo shareInfo;
    private long shopAreaId;
    private String squareLogoPath;
    private int shopType;//0 服务商家，1 婚品商家 2.婚车
    private ArrayList<AddressArea> shopAreas;
    private int todoOrdersCount; // 待处理订单数目
    /**
     * 开通实名认证的状态, 默认状态下存在certify但是status=0
     * 提交过开通实名认证的资料后:
     * status=1:审核中
     * status=2:审核不通过
     * status=3:审核通过
     */
    private Certify certify;
    private double bondFee;
    private IconSign sign;
    private boolean isOpenedTrade; //老商家线交易的状态,优先判断
    private boolean advHelper; //派单开关标志
    private boolean special; //特殊派单开关标志

    private int gradeLevel; // 0 普通 1铜牌 2银牌 3.金牌 4.钻石
    private int rate; //打败商家百分比
    private int bondMerchantExpireDays; // bond_merchant_expire_time,保证金余额不足自动关闭权限的限制天数

    private int isPro; //是否为专业版商家  0-普通版 1-专业版本 2-旗舰版
    private Date proDate; //专业版到期时间
    private ArrayList<Photo> realPhotos;//店铺照片
    private boolean isSpecial; //1 特殊商家0普通商家
    private int specialMerchantType; // (结合is_special)0普通 1主店铺 2子店铺
    private int bankStatus; // 结算账户状态；-1未录入 0待审核 1通过 2失败
    private ExperienceStore experienceStore;//体验店合作商家，为null表示非合作商家
    private MerchantIsAdv merchantIsAdv;

    private double score;//评价星级
    private long goodRate;//好评率
    private int countStar1;//一星好评数
    private int countStar2;//二星好评数
    private int countStar3;//三星好评数
    private int countStar4;//四星好评数
    private int countStar5;//五星好评数
    public int commentCount;//评价总数
    private int thirtyGoodCount;//30天好评
    private int thirtyMediumCount;//30天中评
    private int thirtyBadCount;//30天差评
    public int thirtyCommentCount;//30天评价总数
    private long parentId;//如果有这个id说明是子帐号

    public static final int BANK_STATUS_NO_APPLY = -1;
    public static final int BANK_STATUS_REVIEWING = 0;
    public static final int BANK_STATUS_SUCCESSED = 1;
    public static final int BANK_STATUS_FAILED = 2;
    public static final int SHOP_TYPE_SERVICE = 0;
    public static final int SHOP_TYPE_PRODUCT = 1;
    public static final int SHOP_TYPE_CAR = 2;
    public static final int SHOP_TYPE_HOTEL = 3;
    public static final int SHOP_TYPE_SERVICE_PARENT_ACCOUNT = 5; // 服务商家父账号，（客户端不允许登陆）


    public MerchantUser(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.userId = jsonObject.optLong("user_id");
            this.id = jsonObject.optLong("id");
            parentId = jsonObject.optLong("parent_id");
            this.name = JSONUtil.getString(jsonObject, "name");
            this.shopAreaId = jsonObject.optLong("shop_area_id", 0);
            this.shopType = jsonObject.optInt("shop_type", -1);
            this.city_code = jsonObject.optLong("city_code");
            this.address = jsonObject.optString("address");
            this.contactPerson = JSONUtil.getString(jsonObject, "contact_person");
            this.auth = jsonObject.optInt("auth");
            this.memo = jsonObject.optString("memo");
            this.createdAt = JSONUtil.getDate(jsonObject, "created_at");
            this.updatedAt = JSONUtil.getDate(jsonObject, "updated_at");
            this.logoPath = JSONUtil.getString(jsonObject, "logo_path");
            this.squareLogoPath = JSONUtil.getString(jsonObject, "logo_path_square");
            this.licensePath = JSONUtil.getString(jsonObject, "license_path");
            this.cityName = JSONUtil.getString(jsonObject, "city");
            this.email = JSONUtil.getString(jsonObject, "email");
            this.sequence = jsonObject.optLong("sequence");
            this.parameter = jsonObject.optLong("parameter");
            this.mCount = jsonObject.optInt("m_count");
            this.nice = jsonObject.optInt("nice");
            this.latitude = jsonObject.optDouble("latitude", 0);
            this.longitude = jsonObject.optDouble("longitude", 0);
            this.hashedPassword = JSONUtil.getString(jsonObject, "hashed_password");
            this.companyName = JSONUtil.getString(jsonObject, "company_name");
            this.des = JSONUtil.getString(jsonObject, "des");
            this.contactCount = jsonObject.optInt("contact_count");
            this.examine = jsonObject.optInt("examine", -1);
            this.fansCount = jsonObject.optInt("fans_count");
            this.opusCount = jsonObject.optInt("opus_count");
            this.shearPath = JSONUtil.getString(jsonObject, "shear_path");
            this.rank = JSONUtil.getString(jsonObject, "rank");
            this.rating = jsonObject.optDouble("rating");
            this.coverPath = JSONUtil.getString(jsonObject, "cover_path");
            this.phone = JSONUtil.getString(jsonObject, "phone");
            this.contactMobile = JSONUtil.getString(jsonObject, "contact_mobile");
            this.status = jsonObject.optInt("status");
            this.reason = JSONUtil.getString(jsonObject, "reason");
            this.bondSign = jsonObject.optInt("bond_sign", 0) > 0;
            this.bondMerchantExpireDays = jsonObject.optInt("bond_merchant_expire_time", 0);
            if (jsonObject.optJSONObject("sign") != null) {
                JSONObject signObject = jsonObject.optJSONObject("sign");
                sign = new IconSign(signObject.optJSONObject("bond_sign"));
                if (JSONUtil.isEmpty(sign.getIconUrl())) {
                    sign = null;
                }
            }
            if (!jsonObject.isNull("city")) {
                this.city = new City(jsonObject.optJSONObject("city"));
            }
            this.property = new MerchantProperty(jsonObject.optJSONObject("property"));
            this.category = new MerchantProperty(null); // 确保category不会为null
            if (jsonObject.optJSONObject("property") != null) {
                this.category = new MerchantProperty(jsonObject.optJSONObject("property")
                        .optJSONObject("category"));
                bondFee = jsonObject.optJSONObject("property")
                        .optDouble("bond_fee", 0);
            }
            this.licensePath = JSONUtil.getString(jsonObject, "license_path");
            this.kind = jsonObject.optInt("kind");
            if (!jsonObject.isNull("shop_phone")) {
                shopPhone = JSONUtil.getString(jsonObject, "shop_phone");
            }
            this.token = JSONUtil.getString(jsonObject, "token");
            this.open_data = jsonObject.optInt("open_data") > 0;
            this.user_token = JSONUtil.getString(jsonObject, "user_token");
            this.shareInfo = new ShareInfo(jsonObject.optJSONObject("share"));
            JSONObject areaObj = jsonObject.optJSONObject("shop_area");
            shopAreas = new ArrayList<>();
            if (areaObj != null) {
                AddressArea area = new AddressArea(areaObj);
                AddressArea areaParent = null;
                AddressArea areaGrandParent = null;
                JSONObject areaParentObj = areaObj.optJSONObject("parent");
                if (areaParentObj != null) {
                    areaParent = new AddressArea(areaParentObj);
                    JSONObject areaGrantParentObj = areaParentObj.optJSONObject("parent");
                    if (areaGrantParentObj != null) {
                        areaGrandParent = new AddressArea(areaGrantParentObj);
                    }
                }
                if (areaGrandParent != null) {
                    shopAreas.add(areaGrandParent);
                }
                if (areaParent != null) {
                    shopAreas.add(areaParent);
                }
                shopAreas.add(area);
            }
            this.todoOrdersCount = jsonObject.optInt("todo_order_count", 0);
            this.bondPaid = jsonObject.optInt("bond_paid", 0) > 0;
            if (jsonObject.optJSONObject("certify") != null) {
                certify = new Certify(jsonObject.optJSONObject("certify"));
            }
            isOpenedTrade = jsonObject.optBoolean("is_opened_trade", false);
            if (!isOpenedTrade) {
                isOpenedTrade = jsonObject.optInt("is_opened_trade", 0) > 0;
            }
            advHelper = jsonObject.optBoolean("adv_helper");
            special = jsonObject.optInt("special") > 0;
            JSONObject gradeObject = jsonObject.optJSONObject("grade");
            if (gradeObject != null) {
                gradeLevel = gradeObject.optInt("grade_level");
                rate = gradeObject.optInt("rate");
            }

            isPro = jsonObject.optInt("is_pro", 0);
            proDate = JSONUtil.getDateFromFormatLong(jsonObject, "pro_date", true);
            JSONArray realPhotos = jsonObject.optJSONArray("real_photos");
            if (realPhotos != null) {
                this.realPhotos = new ArrayList<>();
                for (int i = 0; i < realPhotos.length(); i++) {
                    JSONObject p = realPhotos.optJSONObject(i);
                    Photo photo = new Photo(p);
                    if (!JSONUtil.isEmpty(photo.getImagePath())) {
                        this.realPhotos.add(photo);
                    }
                }
            }
            this.isSpecial = jsonObject.optInt("is_special", 0) > 0;
            this.specialMerchantType = jsonObject.optInt("special_merchant_type");
            this.bankStatus = jsonObject.optInt("bank_status", BANK_STATUS_NO_APPLY);
            JSONObject commentJson = jsonObject.optJSONObject("merchant_comment");
            if (commentJson != null) {
                this.score = commentJson.optDouble("score", 0);
                this.goodRate = commentJson.optLong("good_rate", 0);
                this.countStar1 = commentJson.optInt("count_1star", 0);
                this.countStar2 = commentJson.optInt("count_2star", 0);
                this.countStar3 = commentJson.optInt("count_3star", 0);
                this.countStar4 = commentJson.optInt("count_4star", 0);
                this.countStar5 = commentJson.optInt("count_5star", 0);
                this.thirtyGoodCount = commentJson.optInt("thirty_good_count", 0);
                this.thirtyMediumCount = commentJson.optInt("thirty_medium_count", 0);
                this.thirtyBadCount = commentJson.optInt("thirty_bad_count", 0);
            }
            this.commentCount = jsonObject.optInt("comments_count", 0);
            JSONObject experienceStoreJson = jsonObject.optJSONObject("experience_store");
            if (experienceStoreJson != null) {
                this.experienceStore = new ExperienceStore(experienceStoreJson);
            }
            JSONObject isAdvJson = jsonObject.optJSONObject("is_adv");
            if (isAdvJson != null) {
                this.merchantIsAdv = GsonUtil.getGsonInstance()
                        .fromJson(isAdvJson.toString(), MerchantIsAdv.class);
            }
        }
    }

    public boolean isSpecial() {
        return special;
    }

    public int getSpecialMerchantType() {
        return specialMerchantType;
    }

    /**
     * 是不是特殊店铺的子店铺
     *
     * @return
     */
    public boolean isSpecialChildMerchant() {
        return isSpecial && specialMerchantType == 2;
    }

    /**
     * 是不是子帐号
     *
     * @return 有id->是
     */
    public boolean isSubAccount() {
        return parentId > 0;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getMerchantId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCity_code() {
        return city_code;
    }

    public void setCity_code(Long city_code) {
        this.city_code = city_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLogoPath() {
        return !JSONUtil.isEmpty(squareLogoPath) ? squareLogoPath : logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Long getParameter() {
        return parameter;
    }

    public void setParameter(Long parameter) {
        this.parameter = parameter;
    }

    public int getmCount() {
        return mCount;
    }

    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public int getNice() {
        return nice;
    }

    public void setNice(int nice) {
        this.nice = nice;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public int getExamine() {
        return examine;
    }

    public void setExamine(int examine) {
        this.examine = examine;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getOpusCount() {
        return opusCount;
    }

    public void setOpusCount(int opusCount) {
        this.opusCount = opusCount;
    }

    public String getShearPath() {
        return shearPath;
    }

    public void setShearPath(String shearPath) {
        this.shearPath = shearPath;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public Long getId() {
        return userId;
    }

    public City getCity() {
        return city;
    }

    public MerchantProperty getProperty() {
        return property;
    }

    public String getLincensePath() {
        return lincensePath;
    }

    public int getKind() {
        return kind;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isOpenData() {
        return open_data;
    }

    public void setOpenData(boolean open_data) {
        this.open_data = open_data;
    }

    public String getUserToken() {
        return user_token;
    }

    public void setUserToken(String user_token) {
        this.user_token = user_token;
    }

    public boolean isBondSign() {
        return bondSign;
    }

    public long getPropertyId() {
        return property == null ? 0 : property.getId();
    }

    public String getPropertyName() {
        return property == null ? null : property.getName();
    }

    public boolean isIndividualMerchant() {
        long propertyId = getPropertyId();
        return isIndividualMerchant(propertyId);
    }

    public boolean isIndividualMerchant(long propertyId) {
        return propertyId == 7 || propertyId == 8 || propertyId == 9 || propertyId == 11;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isOpen_data() {
        return open_data;
    }

    public long getShopAreaId() {
        return shopAreaId;
    }

    public String getSquareLogoPath() {
        return squareLogoPath;
    }

    public int getShopType() {
        return shopType;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public ArrayList<AddressArea> getShopAreas() {
        return shopAreas;
    }

    public int getTodoOrdersCount() {
        return todoOrdersCount;
    }

    public boolean isBondPaid() {
        return bondPaid;
    }

    public Certify getCertify() {
        return certify;
    }

    public void setCertify(Certify certify) {
        this.certify = certify;
    }

    public double getBondFee() {
        return bondFee;
    }

    public void setBondPaid(boolean bondPaid) {
        this.bondPaid = bondPaid;
    }

    public IconSign getSign() {
        return sign;
    }

    public boolean isOpenedTrade() {
        return isOpenedTrade;
    }

    public int getCertifyStatus() {
        int certifyStatus = 0;
        if (certify != null) {
            certifyStatus = certify.getStatus();
        }
        if (certifyStatus == 0 && isOpenedTrade) {
            certifyStatus = 3;
        }
        return certifyStatus;
    }

    public boolean isAdvHelper() {
        return advHelper;
    }

    public int getGradeLevel() {
        return Math.min(gradeLevel, 4);
    }

    public int getRate() {
        return rate;
    }

    public MerchantProperty getCategory() {
        return category;
    }

    public boolean isOrderSpecial() {
        return special;
    }

    public String getAvatar() {
        if (!JSONUtil.isEmpty(squareLogoPath)) {
            return squareLogoPath;
        }
        return logoPath;
    }

    public int getBondMerchantExpireDays() {
        return bondMerchantExpireDays;
    }

    public boolean isPro() {
        return isPro > 0;
    }

    public int getIsPro() {
        return isPro;
    }

    public void setIsPro(int isPro) {
        this.isPro = isPro;
    }

    public ArrayList<Photo> getRealPhotos() {
        return realPhotos;
    }

    public Date getProDate() {
        return proDate;
    }

    /**
     * 结算账户状态；-1未录入 0待审核 1通过 2失败
     *
     * @return
     */
    public int getBankStatus() {
        return bankStatus;
    }

    public double getScore() {
        return score;
    }

    public long getGoodRate() {
        return goodRate;
    }

    public int getCountStar1() {
        return countStar1;
    }

    public int getCountStar2() {
        return countStar2;
    }

    public int getCountStar3() {
        return countStar3;
    }

    public int getCountStar4() {
        return countStar4;
    }

    public int getCountStar5() {
        return countStar5;
    }

    public int getCommentCount() {
        if (commentCount == 0) {
            commentCount = countStar1 + countStar2 + countStar3 + countStar4 + countStar5;
        }
        return commentCount;
    }

    public int getThirtyCommentCount() {
        if (thirtyCommentCount == 0) {
            thirtyCommentCount = thirtyGoodCount + thirtyMediumCount + thirtyBadCount;
        }
        return thirtyCommentCount;
    }

    public ExperienceStore getExperienceStore() {
        return experienceStore;
    }

    // 是否是(出现在婚礼顾问-派单商家列表 中的 “所有非婚宴商家”)
    public boolean isAdv() {
        if (merchantIsAdv == null) {
            return false;
        }
        return merchantIsAdv.isAdv();
    }

    public void setProDate(Date proDate) {
        this.proDate = proDate;
    }
}
