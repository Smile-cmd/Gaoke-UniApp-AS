package com.gksc.base.speech;

import android.content.Context;
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
            textToSpeech = new TextToSpeech(context, status -> {
                if(status==TextToSpeech.SUCCESS){
                    textToSpeech.setOnUtteranceProgressListener(new TTSProgressListener(callback));
                    textToSpeech.speak(".",TextToSpeech.QUEUE_FLUSH,null);
                }else{
                    Log.e(TAG,"speechInit error");
                }
            });
        }
    }
}
