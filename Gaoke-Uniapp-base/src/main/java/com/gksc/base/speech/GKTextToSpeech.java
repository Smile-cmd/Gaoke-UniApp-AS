package com.gksc.base.speech;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

/**
 * @author wh
 * @since 2025/9/15 19:18
 **/
public class GKTextToSpeech {
    private static final String TAG = "GKTextToSpeech";

    private static TextToSpeech textToSpeech;
    public static void speechInit(Context context, TTSProgressCallback callback){
        if (textToSpeech == null) {
            Log.i(TAG, "speechInit");
            textToSpeech = new TextToSpeech(context, status -> {
                if(status==TextToSpeech.SUCCESS){
                    Log.i(TAG, "speech init done");
                    textToSpeech.setOnUtteranceProgressListener(new TTSProgressListener(callback));
                    textToSpeech.speak(".",TextToSpeech.QUEUE_FLUSH,null);
                }else{
                    Log.e(TAG,"speech Init error");
                }
            });
        }
    }

    public static void asyncSpeech(String content, String utteranceId){
        Log.i(TAG, "asyncSpeech");
        speech(content, utteranceId);
    }

    public static void speechInterrupt(){
        if (textToSpeech == null) {
            Log.i(TAG, "textToSpeech is null");
            return;
        }
        textToSpeech.stop();
    }

    private static void speech(String text, String utteranceId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (textToSpeech == null) {
                Log.i(TAG, "textToSpeech is null");
                return;
            }
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        }
    }
}
