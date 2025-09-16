package com.gksc.base;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.gksc.base.network.NetworkService;
import com.gksc.base.network.instance.NetworkInstance;
import com.gksc.base.speech.GKTextToSpeech;
import com.gksc.base.speech.TTSProgressCallback;

import java.util.Random;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

/**
 * @author wh
 * @since 2025/9/15 11:02
 **/
public class BaseModule extends UniModule implements TTSProgressCallback {

    private static final String TAG = "BaseModule";
    /*
     * 获取设备ip地址
     * @return code：0成功，-1发生异常
     * */
    @UniJSMethod(uiThread = false)
    public JSONObject getIpAddress(String flag) {
        Log.i(TAG, "getIpAddress");
        NetworkService networkService = NetworkInstance.getInstance(flag == null ? "gaoke" : flag);
        return networkService.localIpAddress();
    }

    // ================================== TTS ======================
    private UniJSCallback ttsCallback;
    @UniJSMethod(uiThread = false)
    public void speechInit(){
        Log.i(TAG, "speechInit");
        GKTextToSpeech.speechInit(this.mWXSDKInstance.getContext(), this);
    }

    /**
     * 语音打断
     */
    @UniJSMethod
    public void speechInterrupt(){
        Log.i(TAG, "speechInterrupt");
        GKTextToSpeech.speechInterrupt();
    }

    /**
     * 语音播报
     */
    @UniJSMethod
    public void speechTxt(JSONObject jsonObject, UniJSCallback callback){
        Log.i(TAG, "speechTxt");
        this.ttsCallback = callback;
        String content = jsonObject.getString("content");
        if (!jsonObject.containsKey("utteranceId")) {
            jsonObject.put("utteranceId", String.valueOf(new Random().nextInt()));
        }
        String utteranceId = jsonObject.getString("utteranceId");
        GKTextToSpeech.asyncSpeech(content, utteranceId);
    }

    @Override
    public void onDone(String utteranceId) {
        JSONObject res = new JSONObject();
        res.put("utteranceId", utteranceId);
        res.put("state", "onDone");
        if(ttsCallback != null){
            ttsCallback.invoke(Response.getSuccess(res));
        }
    }
    // ================================== TTS ======================
}
