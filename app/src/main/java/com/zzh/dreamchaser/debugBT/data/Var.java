package com.zzh.dreamchaser.debugBT.data;

import com.zzh.dreamchaser.debugBT.view.SimpleScopeView;

import java.util.ArrayList;
import java.util.List;

import static com.zzh.dreamchaser.debugBT.tool.byteCov.*;

public class Var {
//    type:
//    BYTE = 0u,
//    uInt8 = 1,
//    uInt16 = 2,
//    uInt32 = 3,
//    Float = 4,
//    Char = 5

    final public static String typeLi[] = {"BYTE", "uInt8", "uInt16", "uInt32", "Float", "Char"};
    public int type;
    public String tag;
    public byte[] data = i82Byte(0);
    public List<Float> history = new ArrayList<>();

    public Var(int ty, String ta) {
        this.type = ty;
        this.tag = ta;
    }

    public Var(int ty, String ta, byte[] da) {
        this.type = ty;
        this.tag = ta;
        this.data = da;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setData(byte[] data) {
        this.data = data;
        history.add(getFloat4All(type,data));
        if(history.size()> SimpleScopeView.MAX_REX)
            history.remove(0);
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
        try {
            switch (type) {
                case 0:
                    return byte2Hex(data);
                case 1:
                    return byte2i8(data) + "";
                case 2:
                    return byte2i16(data) + "";
                case 3:
                    return byte2i32(data) + "";
                case 4:
                    return byte2Fl(data) + "";
                case 5:
                    return new String(data);
            }
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
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
