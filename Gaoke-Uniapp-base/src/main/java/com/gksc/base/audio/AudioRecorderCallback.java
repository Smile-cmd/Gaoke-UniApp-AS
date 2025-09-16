package com.gksc.base.audio;

/**
 * @author wh
 * @date 2024/4/17 19:49
 **/
public interface AudioRecorderCallback {
    void onAudioData(byte[] data, int dataSize);
    void onError(String msg);
}
