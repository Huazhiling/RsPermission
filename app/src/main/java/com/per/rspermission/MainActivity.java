package com.per.rspermission;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.per.rslibrary.IPermissionRequest;
import com.per.rslibrary.RsPermission;


public class MainActivity extends AppCompatActivity {

    private android.widget.Button request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.request = findViewById(R.id.request);
        request.setOnClickListener(v -> {
            RsPermission.getInstance().setiPermissionRequest(new IPermissionRequest() {
                @Override
                public void toSetting() {
                    RsPermission.getInstance().toSettingPer();
                }

                @Override
                public void cancle(int code, String permission) {
                    Log.d("MainActivity", permission);
                }

                @Override
                public void success(int code, String... per) throws PackageManager.NameNotFoundException {
                    Log.d("MainActivity", "code:" + code);
                    int length = per.length;
                    for (int i = 0; i < length; i++) {
                        Log.d("MainActivity", per[i]);
                    }
                }
            }).requestPermission(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION});
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        RsPermission.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
