package com.zzh.dreamchaser.debugBT.data.jsonbean;

import org.xutils.http.annotation.HttpResponse;

@HttpResponse(parser = StdAPIParser.class)
public class StdAPI {
    private int code;
    private String data;
    private boolean succeed;

    public StdAPI(int code, String data, boolean succeed) {
        this.code = code;
        this.data = data;
        this.succeed = succeed;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public boolean getSucceed() {
        return this.succeed;
    }
}
