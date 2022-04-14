package com.zzh.dreamchaser.debugBT.data.jsonbean;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StdAPIParser implements ResponseParser<String> {

    @Override
    public void beforeRequest(UriRequest request) throws Throwable {
        // custom check params?
        LogUtil.d(request.getParams().toString());
    }

    @Override
    public void afterRequest(UriRequest request) throws Throwable {
        // custom check response Headers?
        LogUtil.d("response code:" + request.getResponseCode());
    }

    /**
     * 转换result为resultType类型的对象
     *
     * @param resultType  返回值类型(可能带有泛型信息)
     * @param resultClass 返回值类型
     * @param result      网络返回数据(支持String, byte[], JSONObject, JSONArray, InputStream)
     * @return 请求结果, 类型为resultType
     */
    @Override
    public Object parse(Type resultType, Class<?> resultClass, String result) throws Throwable {
        // TODO: json to java bean
        if (resultClass == StdAPI.class) {
            JSONObject jSONObject = new JSONObject(result);
            int code = jSONObject.getInt("code");
            String data = jSONObject.getJSONObject("data").toString();
            boolean succeed = jSONObject.getBoolean("succeed");
            StdAPI stdAPI = new StdAPI(code,data,succeed);
            return stdAPI;
        }
        return null;
    }
}