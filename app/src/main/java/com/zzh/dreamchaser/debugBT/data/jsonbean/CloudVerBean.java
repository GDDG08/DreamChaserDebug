package com.zzh.dreamchaser.debugBT.data.jsonbean;

import org.xutils.http.annotation.HttpResponse;

//@HttpResponse(parser = JsonResponseParser.class)
public class CloudVerBean {
    private int checkUpdate;

    private int verCode;

    private String verName;

    private String chagelog;

    private boolean forceUpdate;

    private int type;

    private String directlink;

    private String weblink;

    private String bitlink;

    public void setCheckUpdate(int checkUpdate){
        this.checkUpdate = checkUpdate;
    }
    public int getCheckUpdate(){
        return this.checkUpdate;
    }
    public void setVerCode(int verCode){
        this.verCode = verCode;
    }
    public int getVerCode(){
        return this.verCode;
    }
    public void setVerName(String verName){
        this.verName = verName;
    }
    public String getVerName(){
        return this.verName;
    }
    public void setChagelog(String chagelog){
        this.chagelog = chagelog;
    }
    public String getChagelog(){
        return this.chagelog;
    }
    public void setForceUpdate(boolean forceUpdate){
        this.forceUpdate = forceUpdate;
    }
    public boolean getForceUpdate(){
        return this.forceUpdate;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public void setDirectlink(String directlink){
        this.directlink = directlink;
    }
    public String getDirectlink(){
        return this.directlink;
    }
    public void setWeblink(String weblink){
        this.weblink = weblink;
    }
    public String getWeblink(){
        return this.weblink;
    }
    public void setBitlink(String bitlink){
        this.bitlink = bitlink;
    }
    public String getBitlink(){
        return this.bitlink;
    }
}


