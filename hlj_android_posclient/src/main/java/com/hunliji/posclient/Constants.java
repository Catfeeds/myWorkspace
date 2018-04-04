package com.hunliji.posclient;

/**
 * Created by chen_bin on 2018/1/17 0017.
 */
public class Constants {

    public static final boolean DEBUG = BuildConfig.APP_DEBUG; // 开发模式是 true 的时候可以动态修改服务器地址

    public static String HOST = "http://www.hunliji.com/"; // 默认数据服务器地址

    // 开发调试才会实用到的接口, 用于重置修改服务器地址
    // !!!!!!!注意!!!!!!! 新增与 HOST 有关的接口需要在申明的地方创建默认值
    // 也需要在这里新增修改, 与申明初始化一样
    public static void setHOST(String HOST) {
        Constants.HOST = HOST;
    }

    public static class RequestCode {
        public static final int UNIONPAY = 1; //pos机银联支付
    }

}
