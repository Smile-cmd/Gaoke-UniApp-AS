package com.gksc.base;

import com.alibaba.fastjson.JSONObject;
import com.gksc.base.network.NetworkService;
import com.gksc.base.network.instance.NetworkInstance;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.common.UniModule;

/**
 * @author wh
 * @since 2025/9/15 11:02
 **/
public class BaseModule extends UniModule {
    /*
     * 获取设备ip地址
     * @return code：0成功，-1发生异常
     * */
    @UniJSMethod(uiThread = false)
    public JSONObject getIpAddress(String flag) {
        NetworkService networkService = NetworkInstance.getInstance(flag == null ? "gaoke" : flag);
        return networkService.localIpAddress();
    }
}
