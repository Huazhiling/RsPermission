# RsPermission
Project 引用
```
	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```
Module:app 引用
```
	dependencies {
	        implementation 'com.github.Huazhiling:RsPermission:v1.0.1'
	}
```
使用方法示例
#1:
```
public class BaseActivity extends AppCompatActivity implements IPermissionRequest {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.request = findViewById(R.id.request);
        RsPermission.getInstance().setiPermissionRequest(this).requestPermission(this, Manifest.permission.CAMERA);
    }
    /***
    *子类可以自己实现三个方法
    * toSetting: 当权限框中的禁止询问按钮被选中并且弹框提示的时候,点击设置会走此方法,可调用方法:RsPermission.getInstance().toSettingPer();
    * cancle : 拒绝的时候回调
    * success: 申请成功, 或者权限存在的时候回调 #code为申请权限是的code, #per为可变参数,里面的参数为当前申请的权限
    */
        @Override
    public void toSetting() {
        
    }

    @Override
    public void cancle() {

    }

    @Override
    public void success(int code, String... per) throws PackageManager.NameNotFoundException {

    }
}
```
#2
```
RsPermission.getInstance().setiPermissionRequest(new IPermissionRequest() {
                @Override
                public void toSetting() {

                }

                @Override
                public void cancle() {

                }

                @Override
                public void success(int code, String... per) throws PackageManager.NameNotFoundException {

                }
            }).requestPermission(this, Manifest.permission.CAMERA);
```
#必须重写权限回调方法
```
@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        RsPermission.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
```

#参数说明


参数名 | 备注
---|---
isPerNotShow | 是否显示弹框(权限禁止时会弹出, false为禁止弹出)
isDialogMsgDefault | 弹框是否显示默认文字(如果需要自定义需置为false)

