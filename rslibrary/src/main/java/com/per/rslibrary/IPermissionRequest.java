package com.per.rslibrary;

import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2018\5\3 0003.
 */

public interface IPermissionRequest {
    void toSetting();

    void cancle(int code, String permission);

    void success(int code,String... per) throws PackageManager.NameNotFoundException;
}
