package com.umeng.message.demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.umeng.message.PushAgent;
import com.umeng.message.demo.helper.PushHelper;
import com.umeng.message.demo.tester.UPushNotification;
import com.umeng.message.demo.tester.UPushAlias;

/**
 * 应用入口Activity
 */
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
            builder.setTitle(R.string.agreement_title);
            builder.setMessage(R.string.agreement_msg);
            builder.setPositiveButton(R.string.agreement_ok, (dialog, which) -> {
                dialog.dismiss();
                //用户点击隐私协议同意按钮后，初始化PushSDK
                MyPreferences.getInstance(this).setAgreePrivacyAgreement(true);
                PushHelper.init(this);
            });
            builder.setNegativeButton(R.string.agreement_cancel, (dialog, which) -> {
                dialog.dismiss();
                finish();
            });

            builder.create().show();
        }
    }

    /**
     * 推送消息测试
     */
    public void testPushMsg(View view) {
        UPushNotification.send("来消息了", "标题", "这是内容，这是内容...");
    }

    /**
     * 别名、标签等设置测试
     */
    public void testMoreFunc(View view) {
        UPushAlias.test(this);
    }

}
