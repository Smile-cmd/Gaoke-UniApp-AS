package com.gksc.base;

import com.alibaba.fastjson.JSONObject;

/**
 * @author wh
 * @date 2024/12/10 18:33
 **/
public class Response {
    private  static final int success = 0;
    private  static final int fail = -1;

    public static JSONObject getFail() {
        JSONObject data = new JSONObject();
        data.put("code", fail);
        data.put("msg", "操作失败");
        return data;
    }
    public static JSONObject getFail(String msg) {
        JSONObject data = new JSONObject();
        data.put("code", fail);
        data.put("msg", msg);
        return data;
    }

    public static JSONObject getSuccess(Object res) {
        JSONObject data = new JSONObject();
        data.put("code", success);
        data.put("msg", "操作成功");
        data.put("data", res);
        return data;
    }
}
