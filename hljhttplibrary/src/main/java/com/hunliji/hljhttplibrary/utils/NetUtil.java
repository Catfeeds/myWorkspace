package com.hunliji.hljhttplibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Suncloud on 2016/8/18.
 */
public class NetUtil {

    public static class NETWORK_TYPE {
        /**
         * Network type is unknown
         */
        public static final int NETWORK_TYPE_UNKNOWN = 0;
        /**
         * Current network is GPRS
         */
        public static final int NETWORK_TYPE_GPRS = 1;
        /**
         * Current network is EDGE
         */
        public static final int NETWORK_TYPE_EDGE = 2;
        /**
         * Current network is UMTS
         */
        public static final int NETWORK_TYPE_UMTS = 3;
        /**
         * Current network is CDMA: Either IS95A or IS95B
         */
        public static final int NETWORK_TYPE_CDMA = 4;
        /**
         * Current network is EVDO revision 0
         */
        public static final int NETWORK_TYPE_EVDO_0 = 5;
        /**
         * Current network is EVDO revision A
         */
        public static final int NETWORK_TYPE_EVDO_A = 6;
        /**
         * Current network is 1xRTT
         */
        public static final int NETWORK_TYPE_1xRTT = 7;
        /**
         * Current network is HSDPA
         */
        public static final int NETWORK_TYPE_HSDPA = 8;
        /**
         * Current network is HSUPA
         */
        public static final int NETWORK_TYPE_HSUPA = 9;
        /**
         * Current network is HSPA
         */
        public static final int NETWORK_TYPE_HSPA = 10;
        /**
         * Current network is iDen
         */
        public static final int NETWORK_TYPE_IDEN = 11;
        /**
         * Current network is EVDO revision B
         */
        public static final int NETWORK_TYPE_EVDO_B = 12;
        /**
         * Current network is LTE
         */
        public static final int NETWORK_TYPE_LTE = 13;
        /**
         * Current network is eHRPD
         */
        public static final int NETWORK_TYPE_EHRPD = 14;
        /**
         * Current network is HSPA+
         */
        public static final int NETWORK_TYPE_HSPAP = 15;
    }

    public static String getNetType(Context context) {
        String netType = "none";
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = "wifi";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {
                    case NETWORK_TYPE.NETWORK_TYPE_GPRS:
                    case NETWORK_TYPE.NETWORK_TYPE_EDGE:
                    case NETWORK_TYPE.NETWORK_TYPE_CDMA:
                    case NETWORK_TYPE.NETWORK_TYPE_1xRTT:
                    case NETWORK_TYPE.NETWORK_TYPE_IDEN:
                        netType = "cell-2";
                        break;
                    case NETWORK_TYPE.NETWORK_TYPE_UMTS:
                    case NETWORK_TYPE.NETWORK_TYPE_EVDO_0:
                    case NETWORK_TYPE.NETWORK_TYPE_EVDO_A:
                    case NETWORK_TYPE.NETWORK_TYPE_HSDPA:
                    case NETWORK_TYPE.NETWORK_TYPE_HSUPA:
                    case NETWORK_TYPE.NETWORK_TYPE_HSPA:
                    case NETWORK_TYPE.NETWORK_TYPE_EVDO_B:
                    case NETWORK_TYPE.NETWORK_TYPE_EHRPD:
                    case NETWORK_TYPE.NETWORK_TYPE_HSPAP:
                        netType = "cell-3";
                        break;
                    case NETWORK_TYPE.NETWORK_TYPE_LTE:
                        netType = "cell-4";
                        break;
                    default:
                        netType = "cell";
                        break;
                }
            }
        }
        return netType;
    }

    public static String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
                    .getNetworkInterfaces(); mEnumeration.hasMoreElements(); ) {
                NetworkInterface intf = mEnumeration.nextElement();
                for (Enumeration<InetAddress> enumIPAddr = intf.getInetAddresses(); enumIPAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIPAddr.nextElement();
                    // 如果不是回环地址
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // 直接返回本地IP地址
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            System.err.print("error");
        }
        return null;
    }


    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddr(Context context) {
        if (context == null) {
            return "";
        }
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null)
                return info.getMacAddress();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 运营商 1  移动 2 联通 3 电信
     *
     * @param context
     * @return
     */
    public static int getOperator(Context context) {
        String operator = null;
        try {
            operator = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                    .getSimOperator();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (operator != null) {
            switch (operator) {
                case "46000":
                case "46002":
                case "46007":
                    //中国移动
                    return 1;
                case "46001":
                    //中国联通
                    return 2;
                case "46003":
                    //中国电信
                    return 3;
            }
        }
        return 0;
    }
}
