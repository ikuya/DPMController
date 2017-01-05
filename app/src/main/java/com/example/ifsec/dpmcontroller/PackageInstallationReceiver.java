package com.example.ifsec.dpmcontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.UserManager;

import java.util.Arrays;
import java.util.List;

public class PackageInstallationReceiver extends BroadcastReceiver {

    // インストールするアプリのパッケージ名リスト
    private static final List<String> PACKAGES = Arrays.asList(
            "com.example.ssag.sampleapp"
    );

    public PackageInstallationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri uri = intent.getData();
        String packageName = uri.getSchemeSpecificPart();
        // 端末にアプリがインストールされるとNotificationを表示する
        // このレシーバはGoogle Playや他の方法でアプリがインストールされたときにも反応する。
        Util.showNotification(context, packageName, "INSTALL PACKAGE (STANDARD)");

        // DevicePolicyManagerによる「インストール禁止」が解除された状態ならば、再び禁止する。
        // 再禁止処理は、PackageInstallationStandardにより明示的にインストールされた場合のみ行う。
        // これは、DevicePolicyManager.setApplicationHiddenを用いてアプリを表示させる際
        // (非表示にしていたアプリを再表示させる際)にも、
        // システムが"android.intent.action.PACKAGE_ADDED"インテントをブロードキャストしてしまうためである。
        if (PACKAGES.contains(packageName)
                && ! DPMRestrictions.isRestricted(context, UserManager.DISALLOW_INSTALL_APPS))
            DPMRestrictions.setUserRestriction(context, UserManager.DISALLOW_INSTALL_APPS, true);
    }
}
