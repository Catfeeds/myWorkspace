package com.hunliji.hljcommonlibrary.models.realm;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Suncloud on 2016/9/2.
 * 客户端通知类型 统一说明文档：
 * 社区通知：
 * =========================================================================
 * <p/>
 * thread_reply    话题回复  entity：CommunityPost  entity_id:评论ID parent_entity_id:当前帖子ID
 * <p/>
 * post_reply      评论回复  entity：CommunityPost  entity_id:评论id parent_entity_id:当前帖子ID
 * <p/>
 * post_praise     评论点赞  entity：CommunityPost  entity_id:评论id parent_entity_id：被赞的帖子id
 * <p/>
 * plus1           话题点赞  entity：CommunityThread entity_id:话题id parent_entity_id：被赞的帖子id
 * <p/>
 * del_thread       删除帖子 entity:CommunityThread entity_id:话题id  parent_entity_id:当前帖子id
 * <p/>
 * del_post        删除回复  entity:CommunityPost   entity_id:回复id parent_entity_id:当前帖子id
 * <p/>
 * community_warning  社区警告 entity:CommunityPunish entity_id:警告记录id  parent_entity_id:警告起因
 * (回复或者帖子内容对应的id)
 * <p/>
 * community_banned  社区封禁 entity:CommunityPunish entity_id:封禁记录id  parent_entity_id:封禁起因
 * (回复或者帖子内容对应的id)
 * <p/>
 * follow        关注  entity:Follow    entity_id:关注ID
 * <p/>
 * qa_answer               回答问题     entity：QaAnswer   entity_id: 回答id     parent_entity_id: 问题ID
 * (QaQusetion)
 * <p/>
 * qa_answer_praise   回答点赞     entity:   QaVote        entity_id:点赞id     parent_entity_id: 回答ID
 * (QaAnswer)
 * <p/>
 * post_qa_question_deleted    删除问题         entity:QaQuestion         entity_id:问题id
 * <p/>
 * post_qa_answer_deleted      删除回答         entity:QaQuestion            entity_id:问题id
 * （由于文案只需要 所属问题信息 并不需要任何回答的信息）
 * <p/>
 * post_qa_comment_deleted   删除问题回答评论            entity:CommunityComment      entity_id:评论id
 * <p/>
 * qa_answer_comment            回答评论                entity:CommunityComment     entity_id:评论id
 * parent_entity_id: 回答ID(QaAnswer)
 * <p/>
 * qa_answer_comment_reply  回答评论的回复     entity:CommunityComment      entity_id:评论id
 * parent_entity_id: 回答ID(QaAnswer)
 * <p/>
 * qa_praise        对回答评论的点赞   entity:CommunityPraise      entity_id:点赞id
 * parent_entity_id: 回答ID
 * <p/>
 * <p/>
 * 订单通知：  entity_id均为order_sub子订单id
 * ===========================================================================
 * remind_pay               提醒买家付款       entity:OrderSub （删除）
 * <p/>
 * remind_receive           提醒卖家接单（买家下单时发送的）  entity:OrderSub
 * <p/>
 * pay_all                  买家全款支付       entity:OrderSub
 * <p/>
 * pay_deposit              买家支付定金       entity:OrderSub
 * <p/>
 * pay_rest                 买家全款余款       entity:OrderSub
 * <p/>
 * change_price             商家改价          entity:OrderSub
 * <p/>
 * remind_payrest           提醒买家支付余款   entity:OrderSub
 * <p/>
 * remind_confirm           提醒用户确认服务   entity:OrderSub（删除）
 * <p/>
 * receive_order            商家同意接单      entity:OrderSub
 * <p/>
 * refuse_order             商家拒绝接单      entity:OrderSub
 * <p/>
 * confirm_service          买家已确认服务    entity:OrderSub
 * <p/>
 * user_appointment         用户预约看店      entity:MerchantAppointment
 * <p/>
 * =============================================================================
 * refuse_refund            拒绝退款          entity:OrderRefund  entity_id：退款订单id
 * <p/>
 * success_refund           退款成功          entity:OrderRefund  entity_id：退款订单id
 * <p/>
 * apply_refund             用户申请退款      entity:OrderRefund   entity_id:退款订单id
 * <p/>
 * 商动态评论
 * =============================================================================
 * feed_comment              动态评论     entity:MerchantFeedComment   entity_id: 评论ID
 * <p/>
 * comment_reply             评论回复     entity:MerchantFeedComment   entity_id: 评论ID
 * <p/>
 * 审核小秘书
 * =============================================================================
 * audit_success             作品审核通过   entity:SetMeal         entity_id:套餐/案例ID
 * <p/>
 * audit_fail                作品审核失败   entity:SetMeal         entity_id:套餐/案例Id
 * <p/>
 * merchant_audit_success    商家资料审核通过 entity:Merchant       entity_id:商家id
 * <p/>
 * merchant_audit_fail       商家资料审核未通过 entity:Merchant       entity_id:商家id
 * <p/>
 * feed_reject               动态审核未通过    entity:MerchantFeed    entity_id:动态id
 * <p/>
 * open_trade_certify     在线交易通过审核   entity:MerchantCertify  entity_id certify表id
 * <p/>
 * open_trade_fail     在线交易未通过审核   entity:MerchantCertify  entity_id certify表id
 * merchant_and_setmeal  在线交易及店铺资料通过审核 entity:Merchant  entity_id 商家id
 * <p/>
 * <p/>
 * 收入小管家
 * =============================================================================
 * merchant_withdraw        商家提现收入    entity: MerchantWithdraw   entity_id:流水ID
 * <p/>
 * <p/>
 * 婚品通知
 * ==============================================================================
 * story_praise             故事点赞       entity:Story           entity_id:stories故事ID
 * <p/>
 * story_comment            故事回复       entity:StoryComment    entity_id:故事评论表ID
 * parent_entity_id：stories故事ID
 * <p/>
 * story_comment_reply      故事评论回复   entity:StoryComment    entity_id:故事评论表ID
 * parent_entity_id：stories故事ID
 * <p/>
 * send_express             发货         entity:ShopOrder  entity_id:主订单id
 * <p/>
 * shopchange_price         改价         entity：ShopOrderSub entity_id:子订单ID
 * <p/>
 * <p/>
 * shopsuccess_refund       申请退款     entity:ShopOrderSub entity_id:子订单id
 * <p/>
 * shopapply_refund         发起退款     entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shopchange_shipping      修改运费     entity:ShopOrder    entity_id:订单id
 * <p/>
 * order_submit             用户下单     entity:ShopOrder    entity_id:订单id
 * <p/>
 * confirm_receive          确认收货     entity:ShopOrder    entity_id:订单id
 * <p/>
 * pay_back                 买家付款     entity:ShopOrder    entity_id:订单id
 * <p/>
 * guests_reply             宾客反馈     entity:Card  entity_id:请柬idfeed_rejectopen_trade_verify
 * 在线交易通过审核   entity:MerchantCerify  entity_id certify表idopen_trade_verify     在线交易通过审核
 * entity:MerchantCerify  entity_id certify表id
 * <p/>
 * shop_order_withdraw     婚品提现    entity:ShopMerchantWithdraw
 * entity_id:shop_merchant_withdraw表主键id
 * <p/>
 * ==============================================================================
 * 客资派单 notify_type 2
 * ==============================================================================
 * <p/>
 * adv_helper   客资派单      entity:AdvHelperMerchant  entity_id:adv_helper_merchant表id
 * <p/>
 * adv_gift_customer  赠送客资  entity:AdvHelperCustomerWallet
 * entity_id:AdvHelperCustomerWallet表idmerchant_and_setmeal
 * <p/>
 * ==============================================================================
 * merchant_privilege_verify 营销管理审核通过 entity:MerchantPrivilege entity_id:MerchantPrivilege表id
 * <p/>
 * merchant_privilege_fail 营销管理审核失败 entity:MerchantPrivilege entity_id:MerchantPrivilege表id
 * <p/>
 * =====================================================================================
 * 定制套餐 通知
 * ====================================================================================
 * 商家通知：
 * <p/>
 * custom_order_submit  订单提交  entity:CustomOrder    entity_id:订单Id
 * <p/>
 * custom_pay_all       支付全款    entity:CustomOrder      entity_id:订单Id
 * <p/>
 * custom_pay_deposit   支付定金  entity:CustomOrder    entity_id:订单Id
 * <p/>
 * custom_pay_split    分笔支付   entity:CustomOrder   entity_id:订单Id
 * <p/>
 * custom_pay_rest     支付余款   entity:CustomOrder   entity_id:订单Id
 * --------------------------------------------------------------------------------
 * 用户通知：
 * <p/>
 * custom_receive_order  商家接单  entity:CustomOrder   entity_id:订单Id
 * <p/>
 * custom_reject_order   商家拒绝接单  entity:CustomOrder  entity_id:CustomOrder主键Id
 * <p/>
 * custom_change_price   商家改价   entity:CustomOrder    entity_id:CustomOrder主键Id
 * <p/>
 * custom_success_refund  退款成功   entity:CustomOrder    entity_id:订单Id
 * <p/>
 * custom_reject_refund  退款被拒绝   entity:CustomOrder    entity_id:订单Id
 * <p/>
 * <p/>
 * =====================================================================================
 * 婚品退款通知====================================================================================
 * 用户通知：
 * <p/>
 * shop_order_refuse_refund   退款被拒绝  entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_refuse_regood  退货被拒绝  entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_not_receive  卖家未收到货   entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_agree_regood  等待买家退货  entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_regood_finish 退货成功    entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_refund_finish 退款成功  entity:ShopOrderSub  entity_id:子订单id
 * --------------------------------------------------------------------------------
 * 商家通知：
 * <p/>
 * shop_order_apply_refund 买家申请退款  entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_apply_regood  买家申请退货  entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_return_good  买家发回退货  entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_cancel_refund  买家取消退款 entity:ShopOrderSub  entity_id:子订单id
 * <p/>
 * shop_order_cancel_regood 买家取消退货 entity:ShopOrderSub entity_id:子订单id
 * <p/>
 * ================================================================================
 * 专题发现页
 * ===============================================================================
 * subPage_comment_reply 专题动态评论回复 entity:CommunityComment  entity_id:community_comment 表id
 * <p/>
 * subPage_praise 专题动态点赞 entity:communityPraise  entity_id:community_praise 表id
 * <p/>
 * ======================================================================================
 * 结伴通知
 * ======================================================================================
 * notify_type = 12
 * invite_partner 结伴申请 entity:User entity_id 用户id
 * agree_partner 同意结伴 entity:User entity_id 用户id
 * <p/>
 * <p/>
 * ======================================================================================
 * （发现）活动开始通知
 * ======================================================================================
 * finder_activity_begin  活动开始通知  entity:FinderActivity  entity_id:finder_activity 表id
 * <p/>
 * finder_activity_online  活动上架通知商家  entity:FinderActivity  entity_id:finder_activity 表id
 * <p/>
 * ======================================================================================
 * （发现）活动中奖用户通知
 * ======================================================================================
 * finder_activity_winner_notify 活动中奖通知 entity:FinderActivity entity_id:finder_activity 表id
 * <p/>
 * <p>
 * 笔记评论相关通知
 * =================================================================================================
 * notify_type=17
 * note_comment            笔记评论
 * entity:CommunityComment     entity_id:评论id        parent_entity_id: 笔记ID(Note)
 * note_comment_reply  笔记评论的回复     entity:CommunityComment      entity_id:评论id
 * parent_entity_id: 笔记ID(Note)
 * 笔记相关通知
 * =================================================================================================
 * notify_type=17
 * del_note  笔记删除通知     entity:Note      entity_id:笔记id        parent_entity_id: 笔记ID(Note)
 * note_join_repository  笔记加入精品库通知     entity:Note      entity_id:笔记id
 * 系统消息
 * =================================================================================================
 * notify_type=24
 * 商家订单即将关闭：aciton:merchant_order_warn entity:MerchantOrder entity_id:订单ID
 * 商家订单久未付款：aciton:merchant_pay_warn entity:MerchantOrder entity_id:订单ID
 * BD线下发起支付订单：aciton:bd_merchant_order entity:MerchantOrder entity_id:订单ID
 */
public class Notification extends RealmObject {
    /**
     * 通知类型
     */
    public static class NotificationType {
        /**
         * 没有定义的通知分类，可能是历史遗留问题
         */
        public static final int UNDEFINED = 0;
        /**
         * 社区类通知（用户端,商家端）
         */
        public static final int COMMUNITY = 10;
        /**
         * 订单类通知（用户端,商家端）
         */
        public static final int ORDER = 2;
        /**
         * 宾客反馈通知（用户端）
         */
        public static final int SIGN = 8;
        /**
         * 收到礼物.礼金（用户端）
         */
        public static final int GIFT = 14;
        /**
         * 结婚助手金融通知（用户端）
         */
        public static final int FINANCIAL = 15;
        /**
         * 商家动态类通知（用户端,商家端）
         */
        public static final int MERCHANT_FEED = 5;
        /**
         * 专题类通知（用户端）
         */
        public static final int SUB_PAGE = 11;

        /**
         * 活动类通知（用户端,商家端）
         */

        public static final int EVENT = 3;
        /**
         * 结伴通知（用户端）
         */
        public static final int PARTNER_INVITE = 12;
        /**
         * 预约小助手（商家端）
         */
        public static final int RESERVATION = 4;
        /**
         * 收入小管家（商家端）
         */
        public static final int INCOME = 6;
        /**
         * 审核小秘书 （商家端）
         */
        public static final int VERIFICATION = 7;
        /**
         * 纪小犀，系统通知 （商家端,用户端）
         */
        public static final int HUNLIJI = 9;
        /**
         * 商家警告（商家端）
         */
        public static final int MERCHANT_WARNING = 13;
        /**
         * 商家、套餐评论的回复相关通知（商家端,用户端）
         */
        public static final int ORDER_COMMENT = 16;
        /**
         * 笔记相关通知
         */
        public static final int NOTE_TYPE = 17;
        /**
         * 蜜月保通知，收到保险礼物
         */
        public static final int RECV_INSURANCE = 18;

        /**
         * 优惠券被领取的通知（商家端）
         */
        public static final int COUPON_RECEIVED = 19;

        /**
         * 预约的直播开始
         * action:appointment_live entity:LiveAppointmentModel   entity_id:预约直播的记录id
         */
        public static final int APPOINTMENT_LIVE_START = 20;

        /**
         * 商家等级通知
         */
        public static final int MERCHANT_GRADE = 23;
        /**
         * 会员权益通知
         */
        public static final int MEMBER_RIGHTS = 22;
        /**
         * 系统消息
         */
        public static final int SYSTEM_MESSAGE = 24;
        /**
         * 客资推荐, 这个命名需要修改
         */
        public static final int ADV_OTHER_ORDER = 25;
    }

    /**
     * 通知动作类型定义
     * ！！！有待补全
     */
    public static class NotificationAction {
        /**
         * 收到结伴邀请（用户端）
         */
        public static final String INVITE_PARTNER = "invite_partner";
        /**
         * 对方同意结伴申请（用户端）
         */
        public static final String ACCEPT_PARTNER = "agree_partner";
        /**
         * 解绑伴侣
         */
        public static final String UNBIND_PARTNER = "unbind_partner";
        /**
         * 邀请另一半金币奖励通知（用户端）
         */
        public static final String INVITE_PARTNER_POINT = "invite_partner_point";
        /**
         * 邀请者已收到邀请另一半金币奖励通知（用户端）
         */
        public static final String ACCEPT_PARTNER_POINT = "accept_partner_point";

        /**
         * 收到请帖礼物
         */
        public static final String RECV_CARD_GIFT = "recv_card_gift";
        /**
         * 收到请帖礼物
         */
        public static final String RECV_CASH_GIFT = "recv_cash_gift";
        /**
         * 商家资料审核通过（商家端）
         */
        public static final String MERCHANT_AUDIT_SUCCESS = "merchant_audit_success";
        /**
         * 商家资料审核未通过（商家端）
         */
        public static final String MERCHANT_AUDIT_FAIL = "merchant_audit_fail";
        /**
         * 在线交易通过审核（商家端）
         */
        public static final String OPEN_TRADE_CERTIFY = "open_trade_certify";
        /**
         * 在线交易未通过审核（商家端）
         */
        public static final String OPEN_TRADE_FAIL = "open_trade_fail";
        /**
         * 在线交易及店铺资料通过审核（商家端）
         */
        public static final String MERCHANT_AND_SETMEAL = "merchant_and_setmeal";
        /**
         * 客资派单（商家端）
         */
        public static final String ADV_HELPER = "adv_helper";


        /**
         * 评论类型
         */
        public static final String FEED_COMMENT = "feed_comment"; //动态评论
        public static final String QA_ANSWER_COMMENT = "qa_answer_comment"; //问答回答评论
        public static final String ORDER_COMMENT_REVIEW = "order_comment_review"; // 套餐点评评论
        public static final String ORDER_COMMENT_MERCHANT = "order_comment_merchant"; // 套餐评论(商家端)
        public static final String NOTE_COMMENT = "note_comment"; //笔记评论

        /**
         * 回复类型
         */
        public static final String COMMENT_REPLY = "comment_reply"; // 动态评论回复
        public static final String SUBPAGE_COMMENT_REPLY = "subPage_comment_reply"; // 专题评论回复
        public static final String SUBPAGE_COMMENT_PRAISE = "subPage_praise"; // 专题评论点赞
        public static final String QA_ANSWER_COMMENT_REPLY = "qa_answer_comment_reply"; // 问答回答评论回复
        public static final String ORDER_COMMENT_REVIEW_REPLY = "order_comment_review_reply"; //
        // 套餐点评评论的回复
        public static final String ORDER_COMMENT_REVIEW_REPLY_MERCHANT =
                "order_comment_review_reply_merchant"; // 套餐点评评论的回复(商家端)
        public static final String NOTE_COMMENT_REPLY = "note_comment_reply"; //笔记评论的回复

        /**
         * 互动通知（商家）
         */
        public static final String POST_QA_ANSWER_HOT = "post_qa_answer_hot"; // 回答标为热门
        public static final String POST_QA_ANSWER_DELETED = "post_qa_answer_deleted"; // 回答被删除

        /**
         * 笔记删除
         */
        public static final String DEL_NOTE = "del_note";
        /**
         * 笔记加入精品库通知
         */
        public static final String NOTE_JOIN_REPOSITORY = "note_join_repository";
        /**
         * 邀请回答
         */
        public static final String QA_QUESTION_HELP = "qa_question_help";
        /**
         * 商家端发起问题
         */
        public static final String QA_QUESTION_MERCHANT = "qa_question_merchant";
        /**
         * 商家端回答问题
         */
        public static final String QA_ANSWER_MERCHANT = "qa_answer_merchant";

        /**
         * 商家端，用户领券通知
         */
        public static final String COUPON_RECEIVE = "receive";

        /**
         * 预约的直播开始
         */
        public static final String APPOINTMENT_LIVE_START = "appointment_live";

        /**
         * 请帖姓名审核结果通知, entity_id:请帖ID
         */
        public static final String MODIFY_NAME_PASSED = "modify_name_passed";
        public static final String MODIFY_NAME_FAILED = "modify_name_failed";

        /**
         * 会员权益通知
         */
        // 去往会员中心
        public static final String MEMBER_CENTER = "member_center";
        // 去请帖列表
        public static final String GO_TO_INVITATION_CARD = "go_to_invitation_card";

        /**
         * 系统消息
         */
        public static final String MERCHANT_ORDER_WARN = "merchant_order_warn";//商家订单即将关闭
        public static final String MERCHANT_PAY_WARN = "merchant_pay_warn";//商家订单久未付款
        public static final String BD_MERCHANT_ORDER = "bd_merchant_order";//BD线下发起支付订单
    }

    @PrimaryKey
    private long id;
    private long userId;
    @SerializedName("item_id")
    private long itemId; // 暂时没用
    @SerializedName("participant_id")
    private long participantId;  // 通知来源的ID,0表示系统通知
    @SerializedName("entity_id")
    private long entityId;  // 通知动作action对应的对象的ID,
    @SerializedName("parent_entity_id")
    private long parentEntityId;  // 对应对象的上一级别对象ID,比如帖子ID
    @SerializedName("created_at")
    private Date createdAt;   // 创建时间
    //!!!body和content有时候是一样的，有时候不一样，每一次使用都需要确认后才能具体使用
    //!!!比如结伴邀请的通知的body和content就是不一样的，但某个版本使用content而另一个版本则使用body
    //!!! So, be very careful!!!
    private String body;     // 通知的主体,主要消息内容字符串
    private String content;  // 额外的文字信息,比如订单的退款信息
    @SerializedName("origin_name")
    private String originName;  // 社区的应用内容
    private String entity;
    private String action;   // 通知类型,动作
    @Index
    @SerializedName("notify_type")
    private int notifyType;  //通知类型
    @SerializedName("participant_name")
    private String participantName;  // 通知来源的用户昵称
    @SerializedName("participant_avatar")
    private String participantAvatar;  // 通知来源用户头像
    @Ignore
    @SerializedName("extra_objects")
    private JsonElement extraJson;  //通知对象类型 gson 对象
    private String extraString;  //通知对象类型 存入数据库的String对象
    private int status;   //status != 2 未读
    @Ignore
    private int newCount;
    @Ignore
    private boolean isGroup;
    @Ignore
    private NotificationExtra extraObject;
    @Index
    private Long mergeId;
    @Ignore
    private String mergeParticipantName;
    @Ignore
    private boolean isMerge;
    @Ignore
    private int mergeCount;
    @Ignore
    private int mergeNewsCount;

    public Notification() {}

    public Notification(int notifyType) {
        this.notifyType = notifyType;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getItemId() {
        return itemId;
    }

    public long getParticipantId() {
        return participantId;
    }

    public long getEntityId() {
        return entityId;
    }

    public long getParentEntityId() {
        return parentEntityId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getBody() {
        return body;
    }

    public String getContent() {
        return content;
    }

    public String getOriginName() {
        return originName;
    }

    public String getEntity() {
        return entity;
    }

    public String getAction() {
        return action;
    }

    public int getNotifyType() {
        return notifyType;
    }

    public String getParticipantName() {
        return TextUtils.isEmpty(participantName) ? "" : participantName;
    }

    public String getParticipantAvatar() {
        return participantAvatar;
    }

    public String getExtraString() {
        return extraString;
    }

    public int getStatus() {
        return status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public void setParentEntityId(long parentEntityId) {
        this.parentEntityId = parentEntityId;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public void setParticipantAvatar(String participantAvatar) {
        this.participantAvatar = participantAvatar;
    }

    public void setExtraString(String extraString) {
        this.extraString = extraString;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }

    public int getNewCount() {
        return newCount;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isNew() {
        return status != 2;
    }

    public Long getMergeId() {
        return mergeId;
    }

    public void setMergeId(Long mergeId) {
        this.mergeId = mergeId;
    }

    public String getMergeParticipantName() {
        return mergeParticipantName;
    }

    public void setMergeParticipantName(String mergeParticipantName) {
        this.mergeParticipantName = mergeParticipantName;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }

    public int getMergeCount() {
        return mergeCount;
    }

    public void setMergeCount(int mergeCount) {
        this.mergeCount = mergeCount;
    }

    public int getMergeNewsCount() {
        return mergeNewsCount;
    }

    public void setMergeNewsCount(int mergeNewsCount) {
        this.mergeNewsCount = mergeNewsCount;
    }

    /**
     * Notification realm数据处理
     * <p/>
     * 将用户id存入数据
     * 将 extraJson 数据转为 String 类型存入
     *
     * @param userId 用户id
     */
    public void onRealmChange(long userId) {
        this.userId = userId;
        if (extraJson != null) {
            extraString = extraJson.toString();
        }
    }

    public NotificationExtra getExtraObject() {
        if (extraObject != null) {
            return extraObject;
        }
        if (!TextUtils.isEmpty(extraString)) {
            try {
                extraObject = new Gson().fromJson(extraString, NotificationExtra.class);
            } catch (Exception e) {
                extraObject = new NotificationExtra();
            }
        }
        return extraObject;
    }

    /**
     * 通知列表item中右边显示的图片，不同类型的图片路径不一样，如果没有则不显示
     *
     * @return
     */
    public String getExtraImage() {
        String path = null;
        if (notifyType == NotificationType.MERCHANT_FEED) {
            if (getExtraObject() != null && getExtraObject().getFeed() != null) {
                path = getExtraObject().getFeed()
                        .getImage();
            }
        } else if (getExtraObject() != null && getExtraObject().getSubPage() != null) {
            path = getExtraObject().getSubPage()
                    .getImagePath();
        } else if (getExtraObject() != null && getExtraObject().getExpand() != null) {
            path = getExtraObject().getExpand()
                    .getCoverPath();
        }

        return path;
    }


    /**
     * 无图片时显示文字
     * 通知列表item中右边显示的文字，不同类型的文字不一样，如果没有则不显示
     *
     * @return
     */
    public String getExtraText() {
        String text = null;
        if (getExtraObject() != null && getExtraObject().getExpand() != null) {
            text = getExtraObject().getExpand()
                    .getTitle();
        }
        return text;
    }
}
