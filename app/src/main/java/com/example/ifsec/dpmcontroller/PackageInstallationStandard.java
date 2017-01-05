package com.example.ifsec.dpmcontroller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.UserManager;

import java.io.File;

public class PackageInstallationStandard {

    private static final String APK_FILE_NAME = "sampleapp.apk";

    /**
     * Android標準機能を用いてアプリをインストール
     * @param context
     */
    public static void installApplication(Context context) {

        // インストールが禁止されていれば、一時的に解除する
        // (PackageInstallationReceiverで再び禁止する)
        if (DPMRestrictions.isRestricted(context, UserManager.DISALLOW_INSTALL_APPS))
            DPMRestrictions.setUserRestriction(context, UserManager.DISALLOW_INSTALL_APPS, false);

        File apkFile = new File(context.getFilesDir(), APK_FILE_NAME);
        if (!apkFile.exists()) Util.copyApkFile(context, APK_FILE_NAME);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
