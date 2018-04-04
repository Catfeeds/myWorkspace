# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/LuoHanLin/wedding-memo-ws/tools/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepattributes Signature

-ignorewarnings

#百度地图
-keep class com.baidu.mapapi.**{ *; }
-dontwarn com.baidu.**
-keep class com.baidu.**{ *; }
-keep class com.baidu.platform.** {*; }
-keep class com.baidu.location.** {*; }
-keep class com.baidu.vi.** {*; }
-keep class vi.com.gdi.bgl.android.** {*; }

-keep class com.umeng.** {*;}
-dontwarn sdk.**
-keep class **.R$* {*;}
-keep class sdk.** { *; }
-keep public class * extends com.umeng.**
-keep class com.fasterxml.jackson.annotation.** {*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.ut.*

-dontwarn org.apache.commons.codec.binary.**
-dontwarn com.squareup.okhttp.**

-keep public class com.hunliji.marrybiz.R$*{
    public static final int *;
}

-dontwarn android.support.v4.**
-dontwarn android.support.v13.**
-keep class android.support.v4.** { *; }
-keep class android.support.v13.** { *; }
-keep public abstract interface com.asqw.android.Listener{
public protected <methods>;
}
-keep public class com.asqw.android{
public void Start(java.lang.String);
}
-keep public class com.umeng.fb.ui.ThreadView {
}
-dontwarn java.awt.**,javax.security.**,java.beans.**,javax.xml.**,java.util.**,org.w3c.dom.**
-dontwarn android.net.http.**
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keep class com.hunliji.marrybiz.model.Location{ *; }
-keep class com.hunliji.marrybiz.socket.**{*;}
-keep class com.hunliji.marrybiz.model.**{*;}


-keepclasseswithmembernames class * {
native <methods>;
}

-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}
#不混淆所有 Parcelable
-keep class * implements android.os.Parcelable {*;
}
-dontwarn com.github.mikephil.**
-keep public class com.github.mikephil.** {
     public protected *;
}
-keepclassmembers class ** {
    public void onEvent*(**);
}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keepattributes  *Annotation*
-keep class de.greenrobot.** {*;}

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

## Joda Time
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

##QQ

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

##微信

-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage { *;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

#微博
-keep class com.sina.weibo.sdk.** { *; }

# unionpay
-keep class org.simalliance.**{*;}
-dontwarn org.simalliance.**
-keep class com.unionpay.** {*;}

#支付宝
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.auth.AlipaySDK{ public *;}
-keep class com.alipay.sdk.auth.APAuthInfo{ public *;}
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.ut.*

#微信
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage { *;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

# Web handler
-keep class com.example.suncloud.hljweblibrary.HljChromeClient{ *; }
-keep class com.hunliji.marrybiz.jsinterface.WebHandler {*;}



-keepnames class * extends android.view.View

-keep class * extends android.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}
-keep class * extends android.support.v4.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}

## TalkingData
-dontwarn com.tendcloud.tenddata.**
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}


#环信客服
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**

#umeng update tencent 6.0
-dontwarn com.tencent.connect.avatar.**
-keep class com.tencent.** {*;}
-dontwarn com.umeng.**

# butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#jackson
-keep class com.fasterxml.jackson.annotation.** {*;}

#okhttp
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-keep class com.squareup.okhttp.** { *;}
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

#Glide okhttp
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


# okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

#RecyclerViewPager
-keep class com.lsjwzh.widget.recyclerviewpager.**
-dontwarn com.lsjwzh.widget.recyclerviewpager.**



-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keepclassmembers class * extends android.app.Activity {
public void *(android.view.View);
}
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}


########--------Retrofit + RxJava--------#########
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit2.appengine.UrlFetchClient
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit2.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn retrofit2.**

-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   rx.internal.util.atomic.LinkedQueueNode producerNode;
   long producerNode;
   long consumerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
   rx.internal.util.atomic.LinkedQueueNode consumerNode;
 }

-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes *Annotation*,Signature

# hlj modules library
-keep class com.hunliji.hljhttplibrary.**{*;}
-keep class com.hunliji.hljcommonlibrary.models.**{*;}
-keep class com.hunliji.hljmerchantfeedslibrary.models.**{*;}
-keep class com.hunliji.hljpaymentlibrary.**{*;}
-keep class com.hunliji.hljweblibrary.**{*;}
-keep class com.hunliji.hljsharelibrary.**{*;}
-keep class com.hunliji.hljtrackerlibrary.**{*;}
-keep class com.hunliji.hljquestionanswer.**{*;}
-keep class com.hunliji.hljcommonlibrary.components.**{*;}
-keep class **.models.**{*;}

# 反射方法
-keep class me.suncloud.marrymemo.util.BannerUtil
-keep class me.suncloud.marrymemo.util.BannerUtil {*;}
-keep class com.hunliji.marrybiz.components
-keep class com.hunliji.marrybiz.components.**{*;}

#招行支付
-keepclasseswithmembers class cmb.pb.util.CMBKeyboardFunc {
    public <init>(android.app.Activity);
    public boolean HandleUrlCall(android.webkit.WebView,java.lang.String);
    public void callKeyBoardActivity();
}


#个推
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }

# ARouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# Disabling obfuscation is useful if you collect stack traces from production crashes
# (unless you are using a system that supports de-obfuscate the stack traces).
-dontobfuscate

# 高德，3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

# 定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

# 搜索
-keep   class com.amap.api.services.**{*;}

#pingyin
-dontwarn demo.Pinyin4jAppletDemo