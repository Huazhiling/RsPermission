package com.per.rslibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018\5\3 0003.
 */

public class RsPermission {
    private static RsPermission rsPermission;
    private int REQUEST_CODE = 200;
    private Activity pActivity;
    private AlertDialog.Builder ald;
    private boolean isPerNotShow = false;   //是否有被禁止显示
    private boolean isDialogMsgDefault = true;  //dialog是否显示默认的文字提示
    private IPermissionRequest iPermissionRequest;

    private RsPermission() {
    }

    public RsPermission setiPermissionRequest(IPermissionRequest iPermissionRequest) {
        this.iPermissionRequest = iPermissionRequest;
        return this;
    }

    /**
     * 设置是否显示默认的dialogmsg
     *
     * @param dialogMsgDefault false:用户自定义
     */
    public RsPermission setDialogMsgDefault(boolean dialogMsgDefault) {
        isDialogMsgDefault = dialogMsgDefault;
        return this;
    }

    public static RsPermission getInstance() {
        if (rsPermission == null) {
            synchronized (RsPermission.class) {
                if (rsPermission == null) {
                    rsPermission = new RsPermission();
                }
            }
        }
        return rsPermission;
    }

    /**
     * 申请权限
     *
     * @param pActivity
     * @param mPermissions
     */
    public RsPermission requestPermission(Activity pActivity, String... mPermissions) {
        if (iPermissionRequest == null) {
            setiPermissionRequest(new IPermissionRequest() {
                @Override
                public void toSetting() {
                    toSettingPer();
                }

                @Override
                public void cancle(int REQUEST_CODE, String permission) {

                }

                @Override
                public void success(int code, String... per) {

                }
            });
        }
        //版本>=6.0才执行
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] mPs = addNoPermission(pActivity, mPermissions);
            if (mPs.length != 0) {
                setDefualtDialog(mPermissions);
                pActivity.requestPermissions(mPs, REQUEST_CODE);
            } else {
                try {
                    iPermissionRequest.success(REQUEST_CODE, mPermissions);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    /**
     * 设置dialog  如果权限框被禁止弹出 需要dialog提示
     *
     * @param mPermissions
     */
    private void setDefualtDialog(String[] mPermissions) {
        ald = new AlertDialog.Builder(pActivity);
        String sPs = getDialogMsg(mPermissions);
        ald.setTitle("权限异常");
        ald.setMessage(sPs);
        ald.setPositiveButton("前往设置", (dialog, which) -> {
            if (iPermissionRequest != null) {
                iPermissionRequest.toSetting();
            }
            dialog.dismiss();
        });
        ald.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
    }

    /**
     * 添加没有的权限
     *
     * @param pActivity
     * @param mPermissions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private String[] addNoPermission(Activity pActivity, String[] mPermissions) {
        this.pActivity = pActivity;
        ArrayList<String> list = new ArrayList<>();
        int length = mPermissions.length;
        for (int i = 0; i < length; i++) {
            if (pActivity.checkSelfPermission(mPermissions[i]) == PackageManager.PERMISSION_DENIED) {
                list.add(mPermissions[i]);
            }
        }
        String[] mPs = new String[list.size()];
        int size = list.size();
        for (int i = 0; i < size; i++) {
            mPs[i] = list.get(i);
        }
        return mPs;
    }

    /**
     * 权限回调 , 只要有一个权限被禁止弹出 就需要弹框
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> mNPer = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    //弹框被拒绝弹起 并且权限被拒绝
                    if (!pActivity.shouldShowRequestPermissionRationale(permissions[i])) {
                        isPerNotShow = true;
                        mNPer.add(permissions[i]);
                    } else {
                        //权限被拒绝
                        if (iPermissionRequest != null) {
                            iPermissionRequest.cancle(REQUEST_CODE, permissions[i]);
                        }
                    }
                } else {
                    //权限是已有的 成功权限
                    if (iPermissionRequest != null) {
                        try {
                            iPermissionRequest.success(REQUEST_CODE, permissions[i]);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //有被点击不让询问的弹框
        if (isPerNotShow) {
            String[] mNp = mNPer.toArray(new String[0]);
            if (isDialogMsgDefault) {
                ald.setMessage(getDialogMsg(mNp));
            }
            ald.show();
        }
        isPerNotShow = false;
        isDialogMsgDefault = true;
        this.REQUEST_CODE = 200;
    }

    public void setRequestCode(int requestCode) {
        this.REQUEST_CODE = requestCode;
    }

    public AlertDialog.Builder getDialog() {
        return ald;
    }

    /**
     * 获取需要的权限
     *
     * @param mPermissions
     * @return
     */
    public String getDialogMsg(String[] mPermissions) {
        StringBuffer buffer = new StringBuffer();
        int length = mPermissions.length;
        buffer.append("您已禁用下列权限的申请:\n");
        for (int i = 0; i < length; i++) {
            switch (mPermissions[i]) {
                case Manifest.permission.CAMERA:
                    buffer.append("拍照\n");
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    buffer.append("写入外部存储\n");
                    break;
                case Manifest.permission.READ_CONTACTS:
                    buffer.append("读取联系人\n");
                    break;
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    buffer.append("读取外部存储\n");
                    break;
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                    buffer.append("定位\n");
                    break;
            }
        }
        buffer.append("没有权限会导致部分功能无法正常使用");
        if (mPermissions.length == 0)
            isPerNotShow = false;
        return buffer.toString();
    }

    /**
     * 跳转到设置页面
     */
    public void toSettingPer() {
        Uri uri = Uri.parse("package:" + pActivity.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
        pActivity.startActivity(intent);
    }
}
