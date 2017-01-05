package com.example.ifsec.dpmcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.CompoundButton;

public class DPMSettingsActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ifsec.dpmcontroller.R.layout.activity_dpmsettings);

        setOnClickListener(R.id.set_dpm_restrictions_btn);
        setOnClickListener(R.id.clear_dpm_restrictions_btn);
        setOnClickListener(R.id.install_packages_standard_btn);
        setOnClickListener(R.id.clear_device_owner_btn);
        setOnClickListener(R.id.package_list_btn);

        setOnCheckedChangeListener(R.id.set_proxy_btn);
        setOnCheckedChangeListener(R.id.set_update_restriction_btn);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkDeviceOwner();
        checkUserRestrictions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.ifsec.dpmcontroller.R.menu.menu_dpmsettings, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_dpm_restrictions_btn:
            {
                // DPMを用いて各種機能を制限する
                DPMRestrictions.setRestrictions(getApplicationContext());
                checkUserRestrictions();
                break;
            }
            case R.id.clear_dpm_restrictions_btn:
            {
                // DPMの各種制限を解除する
                DPMRestrictions.clearRestrictions(getApplicationContext());
                checkUserRestrictions();
                break;
            }
            case R.id.install_packages_standard_btn:
            {
                // Android標準機能を用いてアプリをインストール
                PackageInstallationStandard.installApplication(getApplicationContext());
                break;
            }
            case R.id.clear_device_owner_btn:
            {
                // Device Ownerを解除する
                DPMRestrictions.clearDeviceOwner(getApplicationContext());
                checkDeviceOwner();
                break;
            }
            case R.id.package_list_btn:
                // 端末にインストールされているアプリをリストアップする
                listPackages();
                break;
        }
    }

    private void setOnClickListener(int id) {
        this.findViewById(id).setOnClickListener(this);
    }

    private void setOnCheckedChangeListener(int id) {
        CompoundButton button = (CompoundButton)this.findViewById(id);
        if (button != null)
            button.setOnCheckedChangeListener(this);
    }

    /**
     * Device Ownerの状態をチェックし、ボタンを 有効化/無効化 する
     */
    private void checkDeviceOwner() {
        boolean isDeviceOwner = DPMRestrictions.isDeviceOwner(getApplicationContext());
        findViewById(R.id.set_dpm_restrictions_btn).setEnabled(isDeviceOwner);
        findViewById(R.id.clear_dpm_restrictions_btn).setEnabled(isDeviceOwner);
        findViewById(R.id.clear_device_owner_btn).setEnabled(isDeviceOwner);
        findViewById(R.id.install_packages_standard_btn).setEnabled(isDeviceOwner);

        TextView tv = (TextView) findViewById(R.id.info_label);
        String message = isDeviceOwner ?
                "I'M A DEVICE OWNER APP!" :
                "SET ME AS A DEVICE OWNER FIRST!";
        tv.setText(message);
    }

    private void checkUserRestrictions() {
        TextView tv = (TextView) findViewById(R.id.restriction_status_label);
        if (DPMRestrictions.checkUserRestrictionsEnabled(getApplicationContext())) {
            tv.setText("DPM Restrictions: ENABLED");
        } else {
            tv.setText("DPM Restrictions: DISABLED");
        }
    }

    private void listPackages() {
        Intent intent = getIntent();
        intent.setClassName("com.example.ifsec.dpmcontroller", "com.example.ifsec.dpmcontroller.AppListActivity");
        startActivity(intent);
    }

    private void setProxy(boolean set) {
        String proxy = "proxy.foo.bar.com";   // TODO set proxy host
        int port = 10080;                     // TODO set proxy port
        if (DPMRestrictions.setProxy(getApplicationContext(), set ? proxy : null, port)) {

        }
        TextView text = (TextView)findViewById(R.id.proxy_text);
        text.setText(set ? proxy + ":" + port : "");
    }

    private void setOSUpdateRestriction(boolean restrict) {
        DPMRestrictions.setOSUpdateRestriction(getApplicationContext(), restrict);

        TextView text = (TextView)findViewById(R.id.os_update_text);
        text.setText(restrict ? "restricted" : "not restricted");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.set_proxy_btn:
                setProxy(isChecked);
                break;
            case R.id.set_update_restriction_btn:
                setOSUpdateRestriction(isChecked);
            default:
                break;
        }
    }
}
