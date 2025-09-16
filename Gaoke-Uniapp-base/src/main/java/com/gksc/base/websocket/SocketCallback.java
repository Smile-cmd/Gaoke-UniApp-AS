package com.gksc.base.websocket;

/**
 * @author wh
 * @date 2024/4/17 15:59
 **/
public interface SocketCallback {
    void onSocketMessage(String msg);
    void onSocketClose();
    void onSocketOpen();
    void onSocketError(String e);
}
