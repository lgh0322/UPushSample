package com.umeng.message.demo;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.demo.helper.PushHelper;

/**
 * 应用程序类
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //日志开关
        UMConfigure.setLogEnabled(true);
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
        boolean agreed = MyPreferences.getInstance(this).hasAgreePrivacyAgreement();
        if (agreed) {
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
