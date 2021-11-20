package com.zzh.dreamchaser.debugBT.data;

import java.util.List;

import static com.zzh.dreamchaser.debugBT.data.Content.typeLi;
import static com.zzh.dreamchaser.debugBT.tool.byteCov.*;

public class Var {
//    type:
//    BYTE = 0u,
//    uInt8 = 1,
//    uInt16 = 2,
//    uInt32 = 3,
//    Float = 4,
//    Char = 5


    public int type;
    public String tag;
    public byte[] data;

    public Var(int ty, String ta) {
        this.type = ty;
        this.tag = ta;
    }

    public void setType(int type) {
        this.type = type;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public String getType() {
        return typeLi[type];
    }
    public String getTag() {
        return tag;
    }
    public byte[] getData() {
        return data;
    }
    public String getStr() {
        switch(type){
            case 0:
                return byte2Hex(data);
            case 1:
                return byte2i8(data)+"";
            case 2:
                return byte2i16(data)+"";
            case 3:
                return byte2i32(data)+"";
            case 4:
                return byte2Fl(data)+"";
            case 5:
                return new String(data);
        }
        return null;
    }
//    public byte getValue() {
//        return data[0];
//    }
//    public int getValue() {
//        return byte2i32(data);
//    }
//    public float getValue() {
//        return ;
//    }
//    public char getValue() {
//        return ;
//    }
}
