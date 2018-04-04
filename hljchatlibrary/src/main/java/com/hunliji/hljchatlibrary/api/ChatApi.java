package com.hunliji.hljchatlibrary.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljchatlibrary.WSRealmHelper;
import com.hunliji.hljchatlibrary.WebSocket;
import com.hunliji.hljchatlibrary.models.modelwrappers.ChannelMessages;
import com.hunliji.hljchatlibrary.models.modelwrappers.Channels;
import com.hunliji.hljcommonlibrary.models.realm.ExtendMember;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/10/18.
 */

public class ChatApi {

    public static Observable<Boolean> getChannelsObb(final long userId) {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .getChannels(WebSocket.SOCKET_HOST + "api/v1/channels?page=1&per_page=9999")
                .map(new Func1<Channels, Boolean>() {
                    @Override
                    public Boolean call(Channels channels) {
                        try {
                            WSRealmHelper.updateChannels(userId, channels.getChannels());
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Boolean> deleteChannel(String channel) {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .deleteChannel(WebSocket.SOCKET_HOST + "api/v1/channels/" + channel)
                .map(new Func1<JsonElement, Boolean>() {
                    @Override
                    public Boolean call(JsonElement jsonElement) {
                        try {
                            return jsonElement.getAsJsonObject()
                                    .get("result")
                                    .getAsBoolean();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Boolean> getChannelMessagesObb(
            final WSChatAuthor merchantUser, final WSChatAuthor session, final String channel) {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .getChannelMessages(WebSocket.SOCKET_HOST + "api/v1/channels/" + channel +
                        "/messages?page=1&per_page=9999")
                .map(new Func1<ChannelMessages, Boolean>() {
                    @Override
                    public Boolean call(ChannelMessages channelMessages) {
                        WSRealmHelper.saveChats(merchantUser,
                                session,
                                channel,
                                channelMessages.getMessages());
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获取简单用户信息，用于用户转商家类型
     *
     * @param userId userid
     * @return User
     */
    public static Observable<User> getMiniUser(long userId) {
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .getMiniUser(userId)
                .map(new HljHttpResultFunc<JsonObject>())
                .map(new Func1<JsonObject, User>() {
                    @Override
                    public User call(JsonObject jsonObject) {
                        try {
                            User user;
                            JsonObject userJson = jsonObject.getAsJsonObject("user");
                            if (userJson.get("kind")
                                    .getAsInt() == 1) {
                                JsonObject merchantJson = jsonObject.getAsJsonObject("merchant");
                                user = new MerchantUser();
                                ((MerchantUser) user).setId(userJson.get("id")
                                        .getAsLong());
                                try {
                                    user.setNick(userJson.get("nick")
                                            .getAsString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    user.setAvatar(userJson.get("avatar")
                                            .getAsString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ((MerchantUser) user).setMerchantId(merchantJson.get("id")
                                        .getAsLong());
                                ((MerchantUser) user).setShopType(merchantJson.get("shop_type")
                                        .getAsInt());
                            } else {
                                user = new CustomerUser();
                                ((CustomerUser) user).setId(userJson.get("id")
                                        .getAsLong());
                                try {
                                    user.setNick(userJson.get("nick")
                                            .getAsString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    user.setAvatar(userJson.get("avatar")
                                            .getAsString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    user.setExtend(GsonUtil.getGsonInstance()
                                            .fromJson(userJson.get("extend"), ExtendMember.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            return user;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 频道置顶
     *
     * @param channel channel_id
     * @param userId  当前操作置顶的user_id
     * @return
     */
    public static Observable stickChannel(String channel, long userId, boolean isStick) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("channel_id", Long.valueOf(channel));
            jsonObject.addProperty("stick_at", isStick ? 1 : 0);
            jsonObject.addProperty("user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .postChannelStick(WebSocket.SOCKET_HOST + "api/v1/channel_stick", jsonObject)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 触发商家智能回复
     *
     * @param merchantUserId 商家UserId
     * @param userId         用户id
     * @return
     */
    public static Observable postForSmartReplay(final long merchantUserId, final long userId) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("creator_id", merchantUserId);
            jsonObject.addProperty("to_user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .postForSmartReplay(WebSocket.SOCKET_HOST + "api/v1/smart_reply",
                        merchantUserId,
                        userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable saveUserCity(long cid, long merchantId) {
        JsonObject body = new JsonObject();
        body.addProperty("city_code", cid);
        body.addProperty("merchant_id", merchantId);
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .saveUserCity(body)
                .map(new HljHttpResultFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 一个公用的接口用来让商家给当前用户发送私信
     * @param content 文本
     * @param kind
     * @param fromId
     * @param toId
     * @param source 渠道来源
     * @return
     */
    public static Observable postMsgProxy(
            String content,
            String kind,
            long fromId,
            long toId,
            int source) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("content", content);
        message.put("kind", kind);
        message.put("from", fromId);
        message.put("to", toId);
        message.put("source", source);
        data.put("message", message);
        map.put("data", data);
        return HljHttp.getRetrofit()
                .create(ChatService.class)
                .postMsgProxy(WebSocket.SOCKET_HOST + "api/v1/msg_proxy", map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
