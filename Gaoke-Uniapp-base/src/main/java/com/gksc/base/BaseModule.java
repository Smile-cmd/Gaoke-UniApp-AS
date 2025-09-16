package com.gksc.base;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.gksc.base.audio.AudioRecorderCallback;
import com.gksc.base.network.NetworkService;
import com.gksc.base.network.instance.NetworkInstance;
import com.gksc.base.speech.GKTextToSpeech;
import com.gksc.base.speech.TTSProgressCallback;
import com.gksc.base.websocket.SocketCallback;
import com.gksc.base.websocket.WebsocketUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

/**
 * @author wh
 * @since 2025/9/15 11:02
 **/
public class BaseModule extends UniModule implements TTSProgressCallback, SocketCallback, AudioRecorderCallback {

    private static final String TAG = "BaseModule";

    // websocket工具
    private WebsocketUtil websocketUtil;
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

    /**
     * 整个语音转文字入口
     * @param uri wss连接地址
     * @return 0 成功，-1 失败
     * webSocket的状态和语音识别的结果都会从 speechToTextCallback 回调，需要调用者做好区分
     * @see #onSocketError(String) socket异常回调
     * @see #onSocketMessage(String) socket结果回调
     * @see #onSocketClose() socket关闭回调
     * @see #onSocketOpen() socket打开回调
     */
    @UniJSMethod(uiThread = false)
    public int connSocket(String uri) {
        disConnSocket();
        websocketUtil = new WebsocketUtil(this);
        websocketUtil.connWebsocket(uri);
        return websocketUtil.isOpenSocket() ? 0 : 1;
    }
    @UniJSMethod(uiThread = false)
    public void disConnSocket(){
        if(websocketUtil != null){
            websocketUtil.closeSocket();
            websocketUtil = null;
        }
    }

    // ================================== websocket ======================
    @Override
    public void onSocketMessage(String data) {
        //System.out.println("收到的文字内容："+msg);
        this.mWXSDKInstance.fireGlobalEventCallback("speechToTextCallback", setCallbackParam(200, "receive webSocket messages", data));
    }

    @Override
    public void onSocketClose() {
        this.mWXSDKInstance.fireGlobalEventCallback("speechToTextCallback", setCallbackParam(300, "webSocket session closed", ""));
    }

    @Override
    public void onSocketOpen() {
        // 固定信令格式
        String startMsg = "{\"chunk_size\": [5, 10, 5], \"wav_name\": \"h5\", \"is_speaking\": true, \"chunk_interval\": 10, \"itn\": false, \"mode\": \"2pass\", \"hotwords\": {\"阿里巴巴\":20,\"hello world\":40}}";
        websocketUtil.sendStringMsg( startMsg);
        this.mWXSDKInstance.fireGlobalEventCallback("speechToTextCallback", setCallbackParam(100, "webSocket session open", ""));
    }

    @Override
    public void onSocketError(String msg) {
        this.mWXSDKInstance.fireGlobalEventCallback("speechToTextCallback", setCallbackParam(500, msg, ""));
    }
    public Map<String, Object> setCallbackParam(int code, String msg, Object data){
        Map<String, Object> param = new HashMap<>();
        param.put("code", code);
        param.put("msg", msg);
        param.put("data", data);
        return param;
    }

    // ==================================== 录音数据 ====================
    @Override
    public void onAudioData(byte[] data, int dataSize) {
        websocketUtil.sendByteMsg( data);
    }

    @Override
    public void onError(String msg) {
        this.mWXSDKInstance.fireGlobalEventCallback("speechToTextCallback", setCallbackParam(500, msg, ""));
    }
}
