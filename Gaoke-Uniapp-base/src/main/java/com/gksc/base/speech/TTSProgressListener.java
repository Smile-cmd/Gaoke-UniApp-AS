package com.gksc.base.speech;

import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

/**
 * @author wh
 * @date 2024/12/23 20:03
 **/
public class TTSProgressListener extends UtteranceProgressListener {

    private final String TAG = "TTSProgressListener";

    private TTSProgressCallback ttsProgressCallback;

    public TTSProgressListener(TTSProgressCallback ttsProgressCallback) {
        this.ttsProgressCallback = ttsProgressCallback;
    }

    @Override
    public void onStart(String utteranceId) {
        Log.i(TAG, " utteranceId = "+ utteranceId + "，onStart");
        ttsProgressCallback.onStart(utteranceId);
    }

    @Override
    public void onDone(String utteranceId) {
        Log.i(TAG, " utteranceId = "+ utteranceId + "，onDone");
        ttsProgressCallback.onDone(utteranceId);
    }

    @Override
    public void onError(String utteranceId) {
        Log.i(TAG, " utteranceId = "+ utteranceId + "，onError");
        ttsProgressCallback.onError(utteranceId);
    }

}
