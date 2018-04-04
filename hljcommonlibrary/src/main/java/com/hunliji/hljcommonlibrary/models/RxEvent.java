package com.hunliji.hljcommonlibrary.models;

import android.support.annotation.Nullable;

public class RxEvent {

    private RxEventType type;
    private Object object;

    public enum RxEventType {
        //通用
        ORDER_BIND, //订单绑定
        WS_MESSAGE, //websocket 新消息
        SEND_MESSAGE, //发送 websocket 消息
        EM_MESSAGE, //环信新消息
        SEND_EM_MESSAGE, //发送环信新消息
        NEW_NOTIFICATION, // 获取通知结束 Object 新消息数 int； -1 特殊情况获取通知失败, 这个消息数不要直接用来显示未读条数
        MEASURE_KEYBOARD_HEIGHT, //测量键盘高度
        CREATE_NOTE_SUCCESS, //创建笔记成功
        DELETE_NOTE_SUCCESS, //删除笔记成功
        UPDATE_NOTE_LIST, //更新笔记信息
        DELETE_NOTE_COMMENT, //删除评论
        REPLY_NOTE_COMMENT, //回复笔记评论成功
        CHAT_DRAFT, //聊天草稿
        FINISH_SYNC_CHANNELS, //私信频道同步完成
        WS_RESET_UNREAD_MESSAGE, //私信重置未读消息数 Object 聊天用户id

        //用户端
        NEW_POINT_RECORD, // 更新金币信息
        PARTNER_INVITATION, // 收到结伴邀请
        INIT_PARTNER_INVITATION, // 发起结伴邀请
        PARTNER_INVITATION_DLG_SHOWED, // 结伴邀请弹窗显示
        PARTNER_INVITATION_DLG_CLOSED, // 结伴邀请弹窗关闭
        CREATE_COUPON_SUCCESS, //创建优惠券成功
        REPORT_SUCCESS, //举报成功返回数据
        NEW_CARD_NOTICE, // 收到请帖相关通知
        THREAD_WEDDING_PHOTO_GROUP_PRAISED, // 婚纱照组图大图查看时的点赞事件，obj为点赞图片所属组图在组图列表中的index
        THREAD_WEDDING_PHOTO_COLLECTED, // 婚纱照组图大图查看时收藏话题,不需要object
        THREAD_WEDDING_PHOTO_RELEASED_SUCCESS, // 婚纱照组图发布成功
        POLICY_INFO_COMPLETED_SUCCESS, //保单 资料不完整，填写资料成功
        WITHDRAW_CASH,//提现
        WITHDRAW_CASH_SUCCESS,//提现成功
        CLOSE_WITHDRAW_CARD_LIST,//关闭请帖可提现列表页
        ANSWER_PRAISE,//问答点赞
        THREAD_LIST_REPLY,//话题列表回复
        OPEN_MEMBER_SUCCESS,//开通会员成功
        NETWORK_CHANGE,//网络状态变化
        FINISH_CREATE_PHOTO,//发布完成关闭发布页面
        CITY_CHANGE,//城市切换
        BIND_WX_SUCCESS,//绑定微信账号成功
        BIND_BANK_SUCCESS,//绑定银行卡账号成功
        UNBIND_BANK_SUCCESS,//银行卡解绑成功
        UNBIND_WX_SUCCESS,//微信解绑成功
        COMMENT_SERVICE_SUCCESS,//评论服务商家或订单成功
        EDIT_COMMENT_SUCCESS, //修改评论成功
        THIRD_LOGIN_CALLBACK, //第三方登陆返回
        THIRD_BIND_CALLBACK, //第三方绑定返回
        POLICY_WRITE,//填写保单
        USE_COUPON_SUCCESS, //使用优惠券成功
        REFRESH_SHOPPING_CART,//刷新购物车
        REFRESH_CART_ITEM_COUNT,//刷新购物车婚品数量
        BIND_FUND_BANK_SUCCESS,//绑定理财银行卡账号成功
        ROLL_IN_OR_OUT_FUND_SUCCESS,//转入或转出理财成功
        NOTIFY_CARD_MUSIC,//请帖音乐
        CLOSE_BUDGET,//关闭结婚预算类目 结婚预算金额页
        HLJ_NOTIFY_PUSH,//应用内通知推送
        HLJ_LIVE_PUSH,//直播通知推送
        TRENDS_COUNT_REFRESH,//新动态数通知
        WEDDING_CAR_CART_COUNT,//婚车购物车数量
        HLJ_ACTIVITY_PUSH, // 应用内首页活动弹窗
        //商家端
        WECHAT_BIND_CHANGE, //微信绑定状态变化
        ADV_HELPER, //收到客资派单
        LOGIN_CERTIFY, //登陆获取验证码请求成功
        LOGIN_OUT,//登出
        COMMENT_DETAIL_REPLY_OR_DELETE_SUCCESS,//评论详情里面，评论或删除成功
        OPEN_EASY_CHAT_SUCCEED, //开通 轻松聊成功
        REGISTER_SUCCESS,//注册成功
        CERTIFY_SUCCESS,//实名认证成功
        CREATE_MERCHANT_SUCCESS,//创建店铺成功
        MERCHANT_ORDER_PAID, // 商家订单支付成功
        WS_USER_UPDATE, //更新私信用户
        OPEN_ULTIMATE_SUCCESS, //开通旗舰版成功
        OPEN_MERCHANT_SERVICE_SUCCESS, //开通单个服务成功

        //请帖大师
        NOTIFICATION_NEW_COUNT_CHANGE, // 未读通知数更新
    }

    /**
     * 构造一个用于RxBus消息传递的参数
     *
     * @param type   消息类型,MessageEvent.EventType里面取
     * @param object 传递的消息本身
     */
    public RxEvent(RxEventType type, @Nullable Object object) {
        this.type = type;
        this.object = object;
    }

    public RxEventType getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }
}
