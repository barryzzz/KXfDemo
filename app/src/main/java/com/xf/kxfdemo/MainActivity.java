package com.xf.kxfdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author : lsl 408930131@qq.com
 * @version : v1.0
 * @description :
 * <p>
 * <li>语音信息采集，讯飞sdk(语音听写)-----》采集语音转文本的信息-------》请求后端接口------》接收反馈</li>
 * <li>
 * 实现不按按钮接收声音数据
 * <ul>1.声音传感器</ul>
 * <ul>2.死循环暴力启动</ul>
 * </li>
 * <
 * </p>
 * @date : 2018-04-03
 */
public class MainActivity extends AppCompatActivity implements VoiceButton.OnTouchVoiceCallBack,
        InitListener, RecognizerListener {

    private static final String TAG = "MainActivity";

    private VoiceButton kxfBtn;

    private SpeechRecognizer speechRecognizer;

    private StringBuffer stringBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kxfBtn = findViewById(R.id.kxf_btn);
        kxfBtn.setTouchVoiceCallBack(this);

        initSpeech();

        stringBuffer = new StringBuffer();
    }

    /**
     * 初始化语音识别引擎
     */
    private void initSpeech() {
        speechRecognizer = SpeechRecognizer.createRecognizer(this, this);
        //设置识别引擎 TYPE_CLOUD:云识别  TYPE_LOCAL:离线识别
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //传输编码
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        //设置返回格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //语音焦点
        speechRecognizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");


        //语音输入超时时间 设置录取音频的最长时间,默认最长60秒
        //speechRecognizer.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT,"");


    }

    @Override
    public void onTouchDown() {
        startVoice();
    }

    @Override
    public void onTouchUp() {
        stopVoice();
    }

    //开始听
    public void startVoice(View v) {
        startVoice();

    }

    //停止听
    public void stopVoice(View v) {
        stopVoice();
    }

    @Override
    public void onInit(int code) {
        Log.d(TAG, "SpeechRecognizer init() code = " + code);
        if (code != ErrorCode.SUCCESS) {
            toast("初始化失败,错误码：" + code);
        }
    }

    @Override
    public void onVolumeChanged(int i, byte[] bytes) {

    }

    @Override
    public void onBeginOfSpeech() {
        loge("onBeginOfSpeech start speak!");
    }

    @Override
    public void onEndOfSpeech() {
        loge("onEndOfSpeech stop speak!");
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {
        if (recognizerResult != null) {
            String text = parseJson2String(recognizerResult.getResultString());
            loge("识别内容:" + recognizerResult.getResultString());
            stringBuffer.append(text);
            if (b) {
                loge("recognize finish");
                toast(stringBuffer.toString());//得到结果
                loge(stringBuffer.toString());
                stringBuffer.setLength(0);
                startVoice();
            }
        } else {
            loge("识别结果为空");
        }

    }

    @Override
    public void onError(SpeechError speechError) {
        loge(speechError.getErrorDescription() + speechError.getErrorCode());
        if (speechError.getErrorCode() == ErrorCode.MSP_ERROR_NO_DATA) {
            toast(speechError.getErrorDescription());
        } else {
            toast("onError Code：" + speechError.getErrorCode() + " " + speechError.getErrorDescription());
        }
        startVoice();
    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

    /**
     * 开始
     */
    private void startVoice() {
        if (speechRecognizer != null)
            speechRecognizer.startListening(this);
    }

    /**
     * 结束
     */
    private void stopVoice() {
        if (speechRecognizer != null)
            speechRecognizer.stopListening();
    }


    private void toast(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private void loge(String str) {
        Log.e(TAG, str);
    }

    private String parseJson2String(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject obj = items.getJSONObject(j);
                    if (obj.getString("w").contains("nomatch")) {
                        ret.append("");
                        return ret.toString();
                    }
                    ret.append(obj.getString("w"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.append("");
        }
        return ret.toString();
    }


}
