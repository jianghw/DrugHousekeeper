# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\tools\sdk/tools/proguard/proguard-android.txt
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

#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#混淆时是否做预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
-keepattributes Signature

 #实体类不参与混淆
-keep class com.cjy.flb.bean.** { *; }
-keep class com.cjy.flb.domain.** { *; }
-keep class com.cjy.flb.event.** { *; }
#自定义控件不参与混淆
-keep class com.cjy.flb.customView.** { *; }
#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
#gson
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
#greendao
#-libraryjars libs/greendao:2.0.0
-keep class de.greenrobot.dao.** {*;}
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#EventBus
#-libraryjars libs/eventbus-2.4.0.jar
-keep class de.greenrobot.event.** {*;}
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}
#JPush
#-libraryjars libs/jpush-android-2.0.5.jar
#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }

#把所有你的jar包都申明进来
#-libraryjars libs/gson-2.3.1.jar
#-libraryjars libs/ccd12320-Auth-2.1.jar
#-libraryjars libs/commons-codec-1.4.jar
#-libraryjars libs/AndroidSwipeLayout-v1.1.8.jar
#-libraryjars libs/zxing.jar
#-libraryjars libs/xUtils-2.6.14.jar

#-libraryjars libs/android-support-v4.jar
#-keep class android.support.v4.** { *; }
#-dontwarn android.support.v4.**

#-keep class com.baidu.** { *; }
#-keep class vi.com.gdi.bgl.android.**{*;}
#保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService

#-keep class com.google.gson.stream.** { *; }
#-keep class com.google.gson.examples.android.model.** { *; }
#-keep class com.uuhelper.Application.** { *; }
#-keep class net.sourceforge.zbar.** { *; }
#-keep class com.google.android.gms.** { *; }

#-keep class com.bank.pingan.model.** { *; }

#-keep public class * extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
#-keep public class * extends com.j256.ormlite.android.apptools.OpenHelperManager
#
#-keep class com.android.vending.licensing.ILicensingService
#-keep class android.support.v4.** { *; }
#-keep class org.apache.commons.net.** { *; }
#-keep class com.tencent.** { *; }

#-keep class com.umeng.** { *; }
#-keep class com.umeng.analytics.** { *; }
#-keep class com.umeng.common.** { *; }
#-keep class com.umeng.newxp.** { *; }
#
#-keep class com.j256.ormlite.** { *; }
#-keep class com.j256.ormlite.android.** { *; }
#-keep class com.j256.ormlite.field.** { *; }
#-keep class com.j256.ormlite.stmt.** { *; }
#下面在贴上关于Umeng分享统计的避免混淆的申明
#-dontwarn android.support.v4.**
#-dontwarn org.apache.commons.net.**
#-dontwarn com.tencent.**

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

#-dontshrink
#-dontoptimize
#-dontwarn com.google.android.maps.**
#-dontwarn android.webkit.WebView
#-dontwarn com.umeng.**
#-dontwarn com.tencent.weibo.sdk.**
#-dontwarn com.facebook.**
#
#-keep enum com.facebook.**
#-keepattributes Exceptions,InnerClasses,Signature
#-keepattributes *Annotation*
#-keepattributes SourceFile,LineNumberTable
#
#-keep public interface com.facebook.**
#-keep public interface com.tencent.**
#-keep public interface com.umeng.socialize.**
#-keep public interface com.umeng.socialize.sensor.**
#-keep public interface com.umeng.scrshot.**
#
#-keep public class com.umeng.socialize.* {*;}
#-keep public class javax.**
#-keep public class android.webkit.**
#
#-keep class com.facebook.**
#-keep class com.umeng.scrshot.**
#-keep public class com.tencent.** {*;}
#-keep class com.umeng.socialize.sensor.**
#
#-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
#
#-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}
#
#-keep class im.yixin.sdk.api.YXMessage {*;}
#-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

-keep public class [your_pkg].R$*{
    public static final int *;
}
#微信的sdk
-keep class com.tencent.mm.sdk.** {
   *;
}
