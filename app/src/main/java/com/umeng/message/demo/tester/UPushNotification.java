package com.umeng.message.demo.tester;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.umeng.commonsdk.service.UMGlobalContext;
import com.umeng.message.PushAgent;
import com.umeng.message.demo.helper.PushConstants;
import com.umeng.message.util.HttpRequest;

import org.json.JSONObject;

import java.security.MessageDigest;

public class UPushNotification {

    /**
     * 发送透传消息
     */
    public static void send(String ticker, String title, String content) {
        try {
            Context context = UMGlobalContext.getAppContext();
            final NotificationModel msg;
            msg = new NotificationModel(PushConstants.APP_KEY, PushConstants.APP_MASTER_SECRET);
            msg.setDeviceToken(PushAgent.getInstance(context).getRegistrationId());
            msg.setTicker(ticker);
            msg.setTitle(title);
            msg.setText(content);
            msg.goAppAfterOpen();
            msg.setTestMode();
            msg.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
            msg.setProductionMode();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        sendImpl(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(runnable).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendImpl(BaseNotification msg) throws Exception {
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        msg.setPredefinedKeyValue("timestamp", timestamp);

        String url = "http://msg.umeng.com/api/send";
        String postBody = msg.getPostBody();

        String p_sign = "POST" + url + postBody + msg.getAppMasterSecret();
        String sign = md5(p_sign);
        url = url + "?sign=" + sign;

        String response = HttpRequest.post(url).acceptJson().send(postBody).body("UTF-8");
        JSONObject responseJson = new JSONObject(response);
        String ret = responseJson.getString("ret");
        Runnable runnable;
        if (!ret.equalsIgnoreCase("SUCCESS")) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UMGlobalContext.getAppContext(), "发送失败", Toast.LENGTH_LONG).show();
                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UMGlobalContext.getAppContext(), "发送成功", Toast.LENGTH_LONG).show();
                }
            };
        }
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    private static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            int i = (b & 0xFF);
            if (i < 0x10) hex.append('0');
            hex.append(Integer.toHexString(i));
        }

        return hex.toString();
    }
}
