package com.example.ifsec.dpmcontroller;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.UserManager;
import android.net.ProxyInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DPMRestrictions {

    // 非表示にするアプリ
    public final static List<String> HIDDEN_APPS = Arrays.asList(
            "com.android.email",                              // Email
            "com.android.vending",                            // Google Play ストア
            "com.android.providers.downloads.ui",          // ダウンロード
            "com.android.calendar",                          // カレンダー
            "com.android.providers.contacts",                 // 連絡先
            "com.android.calculator2",                        // 電卓

            "com.google.android.googlequicksearchbox",        // Googleアプリ
            "com.google.android.play.games",                  // Google Play ゲーム
            "com.google.android.apps.maps",                   // マップ
            "com.google.android.gm",                          // Gmail
            "com.google.android.apps.magazines",              // Google Play ニューススタンド
            "com.google.android.apps.books",                  // Google Play ブックス
            "com.google.android.apps.plus",                   // Google+
            "com.google.android.talk",                        // Hangouts
            "com.google.android.videos",                      // Google Play ムービー＆TV
            "com.google.android.youtube",                     // YouTube

            // additional
            "com.mobisystems.fileman",                        // File Commander
            "com.trendmicro.tmmspersonal.jp",                 // ウイルスバスター
            "com.twitter.android",                            // Twitter
            "com.facebook.katana",                            // Facebook
            "com.skype.raider"                               // Skype
    );

    public final static String UPDATER = "com.example.updaterapp";  // TODO set an updater app

    // Chromeのパッケージ名
    public final static String PACKAGE_NAME_CHROME = "com.android.chrome";

    // 抑制対象の機能
    private final static List<String> USER_RESTRICTION_KEYS = Arrays.asList(
            UserManager.DISALLOW_ADD_USER,              // ユーザー追加の禁止
            UserManager.DISALLOW_REMOVE_USER,           // ユーザー削除の禁止
            UserManager.DISALLOW_APPS_CONTROL,          // 各アプリの設定を無効化
            UserManager.DISALLOW_CONFIG_WIFI,           // WiFi設定の固定化
            UserManager.DISALLOW_CONFIG_BLUETOOTH,      // Bluetooth設定の固定化
            UserManager.DISALLOW_CONFIG_VPN,            // VPN設定の固定化
            UserManager.DISALLOW_INSTALL_APPS,          // アプリのインストールの禁止
            UserManager.DISALLOW_UNINSTALL_APPS,        // アプリのアンインストールの禁止
            UserManager.DISALLOW_MODIFY_ACCOUNTS,       // アカウントの追加・削除の禁止
            UserManager.DISALLOW_SHARE_LOCATION,        // 位置情報の取得を禁止
            UserManager.DISALLOW_DEBUGGING_FEATURES,    // 開発者オプションを消す(ADBも禁止)
            UserManager.DISALLOW_FACTORY_RESET,           // Factory resetの禁止
            UserManager.DISALLOW_OUTGOING_BEAM,            // Android Beamの禁止 (>= API Lv. 22)
            UserManager.DISALLOW_USB_FILE_TRANSFER        // MTP,PTPファイル転送の禁止(ADBも禁止になる)
    );

    /**
     * DPM を用いて各種機能を制限する
     * @param context
     */
    public static void setRestrictions(Context context) {

        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);

        // 各種機能の抑制
        for (String key : USER_RESTRICTION_KEYS) {
            manager.addUserRestriction(componentName, key);
        }

        // 特定アプリの非表示
        for (String app : HIDDEN_APPS) {
            manager.setApplicationHidden(componentName, app, true);
        }

        // Chromeの機能制限(アクセス制限)
        Bundle settings = new Bundle();
        settings.putString("URLBlacklist", "[\"*\"]");  // とりあえず全てのドメインを不許可
        settings.putString("URLWhitelist", "[\"foo.com\", \"bar.com\", \"bazz.com\"]");  // アクセスを許可するドメインを指定
        manager.setApplicationRestrictions(componentName, PACKAGE_NAME_CHROME, settings);

    }

    /**
     * DPMの各種制限を解除する
     * @param context
     */
    public static void clearRestrictions(Context context) {

        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);

        // 各種機能の抑制を解除
        for (String key : USER_RESTRICTION_KEYS) {
            manager.clearUserRestriction(componentName, key);
        }

        // 特定アプリ非表示の解除
        for (String app : HIDDEN_APPS) {
            manager.setApplicationHidden(componentName, app, false);
        }

        // Chromeの機能制限を解除
        manager.setApplicationRestrictions(componentName, PACKAGE_NAME_CHROME, null);

    }

    /**
     * Device Ownerを解除する
     * @param context
     */
    public static void clearDeviceOwner(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        manager.clearDeviceOwnerApp(context.getPackageName());
    }

    /**
     * アプリがDevice Ownerになっているかどうかを返す
     * @param context
     * @return
     */
    public static boolean isDeviceOwner(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return manager.isDeviceOwnerApp(context.getPackageName());
    }

    /**
     * 全ての DPM restrictions が有効かどうかを返す
     * @param context
     * @return
     */
    public static boolean checkUserRestrictionsEnabled(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);

        List<Boolean> boolList = new ArrayList<>();
        for (String key : USER_RESTRICTION_KEYS) {
            boolList.add(userManager.hasUserRestriction(key));
        }
        return boolList.contains(true);
    }

    /**
     *
     */
    public static boolean setOSUpdateRestriction(Context context, boolean restrict) {

        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);

        return manager.setApplicationHidden(componentName, UPDATER, restrict);
    }

    /**
     * 特定の DPM Restriction が有効かどうかを返す
     * @param context
     * @param key
     * @return
     */
    public static boolean isRestricted(Context context, String key) {
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        return userManager.hasUserRestriction(key);
    }

    /**
     * 特定の DPM Restriction を有効化/無効化する
     * @param context
     * @param key
     * @param restriction
     */
    public static void setUserRestriction(Context context, String key, boolean restriction) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);
        if (restriction) {
            manager.addUserRestriction(componentName, key);
        } else {
            manager.clearUserRestriction(componentName, key);
        }
    }

    /**
     * 端末パスワードをリセットする
     * @param context
     */
    public static void resetPassword(Context context) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        // パスワードをクリアする
        manager.resetPassword(null, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
    }


    /**
     * Proxy の設定を有効化/無効化する
     * @param host : if host equals to null, the proxy setting will be cleared.
     * @param port
     */
    public static boolean setProxy(Context context, String host, int port) {
        DevicePolicyManager manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, MyDeviceAdminReceiver.class);
        if (host == null) {
            manager.setRecommendedGlobalProxy(componentName, null);
            return false;
        }
        ProxyInfo info = ProxyInfo.buildDirectProxy(host, port);
        manager.setRecommendedGlobalProxy(componentName, info);
        return true;
    }

}
