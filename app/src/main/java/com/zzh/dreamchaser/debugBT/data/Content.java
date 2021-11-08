package com.zzh.dreamchaser.debugBT.data;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.zzh.dreamchaser.debugBT.tool.byteCov.*;

public class Content {
    public static String typeLi[] = {"BYTE", "uInt8", "uInt16", "uInt32", "Float", "Char"};
    public ArrayList tagList = new ArrayList();
    public ArrayList list = new ArrayList();
    public int dataLen = 0;

    public void CreatContent(byte[] data) {
        int num = (data.length - 1) / 20;
        for (int i = 0; i < num; i++) {
            int len = 19;
            for (int j = 1; j < 19; j++) {
                if (data[i * 20 + 1 + 1 + j] == (byte) 0) {
                    len = j;
                    break;
                }
            }
            byte[] temp = new byte[len];
            System.arraycopy(data, i * 20 + 1 + 1, temp, 0, len);
            String tag = new String(temp);
            int type = data[i * 20 + 1] & 0xFF;
            list.add(new Var(type, tag));
            tagList.add(tag);
            dataLen++;
        }
//        dataLen = list.size();
    }

    public void Update(byte[] data) {
        int cur_pos = 1;
        for (int i = 0; i < list.size(); i++) {
            int len = getDataLen(((Var) list.get(i)).type);
            byte[] temp = new byte[len];
            System.arraycopy(data, cur_pos, temp, 0, len);
            ((Var) list.get(i)).setData(temp);
            cur_pos += len;
            Log.d("RESULT:", ((Var) list.get(i)).getTag()+"-->"+((Var) list.get(i)).getStr());
        }
    }

    private int getDataLen(int type) {
        switch (type) {
            case 0:
            case 1:
            case 5:
                return 1;
            case 2:
                return 2;
            case 3:
            case 4:
                return 4;
        }
        return 0;
    }
}
