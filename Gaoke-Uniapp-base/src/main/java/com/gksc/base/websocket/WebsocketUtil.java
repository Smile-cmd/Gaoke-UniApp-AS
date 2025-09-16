package com.gksc.base.websocket;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.gksc.base.BaseModule;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * @author wh
 * @date 2024/4/17 10:26
 **/
public class WebsocketUtil {

    private long heartbeatLast;

    private final String TAG = "WebsocketUtil";

    private static boolean openSocket;

    private WebSocketClient webSocketClient;

    private SocketCallback mSocketCallback;

    public WebSocketClient getWebSocketClient(){
        return webSocketClient;
    }
    public WebsocketUtil() {
    }
    public WebsocketUtil(SocketCallback callback) {
        this.mSocketCallback = callback;
    }

    public void connWebsocket(String uri){
        webSocketClient = getClient(uri);
        if (webSocketClient != null) {
            openSocket = webSocketClient.isOpen();
            heartbeatLast = System.currentTimeMillis();
        }
    }

    private WebSocketClient getClient(String uri){

        try {
            WebSocketClient client = new SSLWebSocketClient(new URI(uri),new Draft_6455()) {
                /**
                 *成功建立连接时调用
                 */
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    }catch (Exception e){
                        Log.w(TAG, "延迟操作出现问题，但并不影响功能");
                    }
                    Log.i(TAG, "连接成功");
                    mSocketCallback.onSocketOpen();
                }
                /**
                 *收到服务端消息调用
                 */
                @Override
                public void onMessage(String s) {
                    Log.i(TAG, "收到来自服务端的消息:::" + s);
                    JSONObject parseObject = JSONObject.parseObject(s);
                    String text = parseObject.getString("text");
                    mSocketCallback.onSocketMessage(text);
                }
                /**
                 *断开连接调用
                 */
                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i(TAG,"socket连接断开....");
                    openSocket = false;
                    mSocketCallback.onSocketClose();
                    Log.i(TAG, "关闭连接:::" + "i = " + i + ":::s = " + s +":::b = " + b);
                }
                /**
                 *连接报错调用
                 */
                @Override
                public void onError(Exception e) {
                    Log.i(TAG,"socket连接异常...."+ e.getMessage());
                    openSocket = false;
                    Log.e(TAG, "报错了,socket连接异常:::" + e.getMessage());
                    mSocketCallback.onSocketError(e.getMessage());
                }
            };
            //请求与服务端建立连接
            client.connect();
            //判断连接状态，0为请求中  1为已建立  其它值都是建立失败
            while(client.getReadyState().ordinal() == 0){
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                }catch (Exception e){
                    Log.w(TAG, "延迟操作出现问题，但并不影响功能");
                }
                if (!client.isOpen()) {
                    Log.i(TAG, "连接中。。。");
                }
            }

            //连接状态不再是0请求中，判断建立结果是不是1已建立
            if (client.getReadyState().ordinal() == 1){
                return client;
            }
        }catch (URISyntaxException e){
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public boolean isOpenSocket(){
        Log.i(TAG,"获取socket状态："+ openSocket);
        return openSocket;
    }

    public void sendByteMsg(byte[] data){
        if (webSocketClient != null && openSocket) {
            webSocketClient.send(data);
        }
    }

    public void sendStringMsg(String data){
        Log.i(TAG,"消息发送前-连接状态："+ openSocket);
        if (openSocket) {
            webSocketClient.send(data);
            Log.i(TAG,"发送成功");
        }else {
            Log.i(TAG,"发送失败");
        }
    }

    public void closeSocket(){
        if (webSocketClient != null && openSocket) {
            webSocketClient.close();
        }
        webSocketClient = null;
    }

    public void heartbeatMaintenance(final String uri){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    long millis = System.currentTimeMillis();
                    // 未收到服务端心跳
                    if ((millis - heartbeatLast) > 30000) {
                        WebsocketUtil websocketUtil = new WebsocketUtil(new BaseModule());
                        websocketUtil.connWebsocket(uri);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(30);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, "heartbeatMaintenance-1");
        thread.start();
    }
}
