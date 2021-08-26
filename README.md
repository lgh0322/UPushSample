## U-Push SDK Sample

友盟推送SDK集成的示例工程说明

### 账号申请
在[U-Push官网](http://message.umeng.com/)添加应用申请Appkey。  
详细路径：U-Push官网->应用->新建应用->创建新应用。

### 集成说明
1. app/build.gradle中替换您的应用id
```groovy
android {
    defaultConfig {
        applicationId "您的应用id"
    }
}
```

2. PushConstants类中替换Appkey、MessageSecret和Channel等
```java
class PushConstants {
    /**
     * 应用申请的Appkey
     */
    public static final String APP_KEY = "应用申请的Appkey";

    /**
     * 应用申请的UmengMessageSecret
     */
    public static final String MESSAGE_SECRET = "应用申请的UmengMessageSecret";

    /**
     * 设置您打包时的渠道名称
     */
    public static final String CHANNEL = "Umeng";
}
```

3. PushHelper类初步封装PushSDK的初始化
```java
class PushHelper {
    /**
     * SDK预初始化
     * @param context 应用上下文
     */
    public static void preInit(Context context) {
        ...
    }
    
    /**
     * SDK初始化。
     * 合规说明：需在用户已确认同意"隐私政策协议"后调用
     * @param context 应用上下文
     */
    public static void init(Context context) {
        ...
    }

    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     * @param context 应用上下文
     */
    private static void registerDeviceChannel(Context context) {
        ...
    }
}
```

4. MyApplication类onCreate()方法中调用预初始化或初始化
```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //预初始化
        PushHelper.preInit(this);
        //初始化
        initPushSDK();
    }

    /**
     * 初始化推送SDK
     */
    private void initPushSDK() {
        /*
         * 若用户已同意隐私政策，直接初始化；
         * 若用户未同意隐私政策，待用户同意后，再通过PushHelper.init(...)方法初始化。
         */
        boolean agreed = ...
        if (agreed && PushHelper.isMainProcess(this)) {
            //建议在线程中执行初始化
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PushHelper.init(getApplicationContext());
                }
            }).start();
        }
    }
}
```

5. MainActivity类中，用户同意隐私政策协议后，执行初始化
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasAgreedAgreement()) {
            PushAgent.getInstance(this).onAppStart();
        }
        setContentView(R.layout.activity_main);
        handleAgreement();
    }

    private boolean hasAgreedAgreement() {
        return MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
    }

    private void handleAgreement() {
        if (!hasAgreedAgreement()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ...
            builder.setPositiveButton(R.string.agreement_ok, (dialog, which) -> {
                dialog.dismiss();
                ...
                MyPreferences.getInstance(this).setAgreePrivacyAgreement(true);
                //同意隐私政策后，初始化PushSDK
                PushHelper.init(this);
            });
            ...
            builder.create().show();
        }
    }
}
```
6. push包依赖情况如下
```
+--- com.umeng.umsdk:push:6.4.0
|    +--- com.umeng.umsdk:alicloud-httpdns:1.3.2.3
|    +--- com.umeng.umsdk:alicloud-utils:2.0.0
|    +--- com.umeng.umsdk:alicloud_beacon:1.0.5
|    +--- com.umeng.umsdk:agoo-accs:3.4.2.7
|    +--- com.umeng.umsdk:agoo_aranger:1.0.6
|    +--- com.umeng.umsdk:agoo_networksdk:3.5.8
|    +--- com.umeng.umsdk:agoo_tnet4android:3.1.14.10-open
|    \--- com.umeng.umsdk:utdid:1.5.2.1
```
移除重复组件的方法，如utdid：
```groovy
    api('com.umeng.umsdk:push:6.4.0') {
        exclude group: 'com.umeng.umsdk', module: 'utdid'
    }
```


**具体使用，请参考UPushSample工程**

