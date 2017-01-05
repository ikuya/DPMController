package com.example.ifsec.dpmcontroller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        final PackageManager pm = getPackageManager();
        final int flags = PackageManager.GET_UNINSTALLED_PACKAGES;
        final List<ApplicationInfo> appList = pm.getInstalledApplications(flags);

        final List<AppData> dataList = new ArrayList<>();
        for (ApplicationInfo app : appList) {
            AppData data = new AppData();
            data.label = app.loadLabel(pm).toString();
            data.pName = app.packageName;
            data.icon = app.loadIcon(pm);
            dataList.add(data);
        }

        final ListView listView = new ListView(this);
        listView.setAdapter(new AppListAdapter(this, dataList));

        setContentView(listView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class AppData {
        String label;
        String pName;
        Drawable icon;
    }

    // アプリケーションのラベルとアイコンを表示するためのアダプタークラス
    private static class AppListAdapter extends ArrayAdapter<AppData> {

        private final LayoutInflater mInflater;

        public AppListAdapter(Context context, List<AppData> dataList) {
            super(context, R.layout.activity_app_list);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            addAll(dataList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = new ViewHolder();

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_app_list, parent, false);
                holder.textLabel = (TextView) convertView.findViewById(R.id.appName);
                holder.packageName = (TextView) convertView.findViewById(R.id.packageName);
                holder.imageIcon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 表示データを取得
            final AppData data = getItem(position);
            // ラベルとアイコンをリストビューに設定
            holder.textLabel.setText(data.label);
            holder.packageName.setText(data.pName);
            holder.imageIcon.setImageDrawable(data.icon);

            return convertView;
        }
    }

    // ビューホルダー
    private static class ViewHolder {
        TextView textLabel;
        TextView packageName;
        ImageView imageIcon;
    }
}
