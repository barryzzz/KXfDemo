package com.xf.kxfdemo;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;
import com.xingfu.app.communication.EndPointRouter;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author : lsl 408930131@qq.com
 * @version : v1.0
 * @description :
 * @date : 2018-04-03
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, "appid=58a40842"); //讯飞sdk初始化

//        initNet();
    }

    /**
     * 初始化网络
     */
    private void initNet() {
        InputStream propertiesInputStream = null;
        try {
            propertiesInputStream = getApplicationContext().getResources().getAssets().open("");
            EndPointRouter.get().load(propertiesInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (propertiesInputStream != null)
                    propertiesInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
