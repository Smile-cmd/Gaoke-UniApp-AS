package com.gksc.base.network.instance;

import com.gksc.base.network.NetworkService;
import com.gksc.base.network.gksc.GaoKeNetworkServiceImpl;

/**
 * @author wh
 * @since 2025/9/15 11:14
 **/
public class NetworkInstance {
    private static NetworkService networkServiceInstance;

    public static NetworkService getInstance(String flag){
        if ("gaoke".equalsIgnoreCase(flag)) {
            networkServiceInstance = new GaoKeNetworkServiceImpl();
        }
        return networkServiceInstance;
    }
}
