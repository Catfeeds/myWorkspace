package com.hunliji.marrybiz.util.event;

/**
 * 填写活动信息时的枚举
 * Created by chen_bin on 2017/2/8 0008.
 */
public enum PropertyEnum {
    //全部
    PROPERTY_0(0, "请简要填写您要发起活动的形式及内容（500字以内）。"),
    //婚礼策划
    PROPERTY_2(2, "请简要填写您要发起活动的形式及内容（500字以内），例如：婚礼手绘图免费送、婚礼沙龙门票0元抢、婚礼方案到店就送等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。"),
    //婚纱摄影
    PROPERTY_6(6, "请简要填写您要发起活动的形式及内容（500字以内），例如：最美证件照免费拍、最新场景免费试拍、感恩父母婚纱照限量送等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。"),
    //婚礼摄影
    PROPERTY_7(7, "请简要填写您要发起活动的形式及内容（500字以内），例如：领证跟拍免费送、最美证件照免费拍等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。"),
    //婚礼摄像
    PROPERTY_8(8, "请简要填写您要发起活动的形式及内容（500字以内），例如：领证跟拍免费送、婚前采访视频免费送等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。"),
    //婚礼跟妆
    PROPERTY_9(9, "请简要填写您要发起活动的形式及内容（500字以内），例如：免费试妆/试纱、新娘妆发小课堂等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。"),
    //婚礼主持
    PROPERTY_11(11, "请简要填写您要发起活动的形式及内容（500字以内），例如：冷门档期特价抢订、婚礼歌单免费定制、新人誓词量身定制等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。"),
    //婚纱礼服
    PROPERTY_12(12, "请简要填写您要发起活动的形式及内容（500字以内），例如：全场免费试纱、样衣特卖会、头纱DIY等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。"),
    //婚宴酒店
    PROPERTY_13(13, "请简要填写您要发起活动的形式及内容（500字以内）。"),
    //婚礼首戒
    PROPERTY_14(14, "请简要填写您要发起活动的形式及内容（500字以内），例如：预约到店送情侣对戒、裸钻定制超低折扣等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。"),
    //花艺甜品
    PROPERTY_15(15, "请简要填写您要发起活动的形式及内容（500字以内），例如：手捧花DIY、甜品蛋糕DIY等等。请在方案中详述活动的所有利益点和限制条件，尽量避免误解或遗漏。");

    private long propertyId;
    private String hint;

    PropertyEnum(long propertyId, String hint) {
        this.propertyId = propertyId;
        this.hint = hint;
    }

    public long getPropertyId(){return  propertyId;}

    private String getHint(){return  hint;}

    public static String getHint(long propertyId) {
        for (PropertyEnum p : PropertyEnum.values()) {
            if (p.getPropertyId() == propertyId) {
                return p.getHint();
            }
        }
        return "请简要填写您要发起活动的形式及内容（500字以内）。";
    }
}