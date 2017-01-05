package com.example.ifsec.dpmcontroller;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {

    /**
     * Notificationを表示
     * @param context
     * @param title
     * @param text
     */
    public static void showNotification(Context context, String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(com.example.ifsec.dpmcontroller.R.mipmap.ic_launcher)
                .setOnlyAlertOnce(true)
                .setTicker(title)
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }

    /**
     * インストール対象のAPKファイルを/data/data/<app>/files/ にコピーする
     *
     * @param context
     * @param apkFileName
     */
    public static void copyApkFile(Context context, String apkFileName) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(context.getResources().openRawResource(com.example.ifsec.dpmcontroller.R.raw.sampleapp));
            os = new BufferedOutputStream(context.openFileOutput(apkFileName, Context.MODE_PRIVATE));

            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null && os != null) {
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Fileをコピーする
     * @param src
     * @param dst
     */
    public static void copyFile(File src, File dst) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dst);

            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
