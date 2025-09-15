package com.gksc.base.speech;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wh
 * @since 2025/9/15 19:23
 **/
public interface TTSProgressCallback {
    default void onStart(String utteranceId){

    }

    void onDone(String utteranceId);

    default void onError(String utteranceId){

    }
}
