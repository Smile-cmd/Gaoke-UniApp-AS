package com.gksc.base.network.gksc;

import com.alibaba.fastjson.JSONObject;
import com.gksc.base.network.NetworkService;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author wh
 * @since 2025/9/15 11:11
 **/
public class GaoKeNetworkServiceImpl implements NetworkService {
    @Override
    public JSONObject localIpAddress() {
        JSONObject data = new JSONObject();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        data.put("code", 0);
                        data.put("ip", inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException ex) {
            data.put("code", -1);
            data.put("ip", null);
            data.put("msg", ex.toString());
        }
        return data;
    }
}
