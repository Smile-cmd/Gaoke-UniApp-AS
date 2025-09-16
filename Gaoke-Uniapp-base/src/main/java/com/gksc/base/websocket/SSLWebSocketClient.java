package com.gksc.base.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.Socket;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;



/**
 * @author wh
 * @date 2024/4/17 10:27
 **/
public class SSLWebSocketClient extends WebSocketClient {
    public SSLWebSocketClient(URI serverURI, String message) {
        super(serverURI);
        if(serverURI.toString().contains("wss://")){
            trustAllHosts(this);
            this.send(message);
        }
    }

    public SSLWebSocketClient(URI serverURI,Draft draft) {
        super(serverURI,draft);
        if(serverURI.toString().contains("wss://")) {
            trustAllHosts(this);
        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    /**
     *忽略证书
     * client 客户端实例
     */
    private void trustAllHosts(SSLWebSocketClient client) {
        TrustManager[] trustAllCerts = new TrustManager[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            trustAllCerts = new TrustManager[]{new X509ExtendedTrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {

                }

                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
    //                return new java.security.cert.X509Certificate[]{};
    //                System.out.println("getAcceptedIssuers");
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    System.out.println("checkClientTrusted");
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    System.out.println("checkServerTrusted");
                }
            }};
        }

        try {

            SSLContext ssl = SSLContext.getInstance("SSL");
            ssl.init(null, trustAllCerts, new java.security.SecureRandom());

            SSLSocketFactory socketFactory = ssl.getSocketFactory();
            this.setSocketFactory(socketFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
