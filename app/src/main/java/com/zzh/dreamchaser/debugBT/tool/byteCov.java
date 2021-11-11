package com.zzh.dreamchaser.debugBT.tool;

import java.util.Formatter;

public class byteCov {
//    public static String byte2Hex(byte[] bytes) {
//        String strHex = "";
//        StringBuilder sb = new StringBuilder("");
//        for (int n = 0; n < bytes.length; n++) {
//            strHex = Integer.toHexString(bytes[n] & 0xFF);
//            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
//        }
//        return sb.toString().trim();
//    }

    /**
     * 字节转换为 16 进制字符串
     *
     * @param b 字节
     * @return Hex 字符串
     */
    public static String byte2Hex(byte b) {
        StringBuilder hex = new StringBuilder(Integer.toHexString(b));
        if (hex.length() > 2) {
            hex = new StringBuilder(hex.substring(hex.length() - 2));
        }
        while (hex.length() < 2) {
            hex.insert(0, "0");
        }
        return hex.toString();
    }


    /**
     * 字节数组转换为 16 进制字符串
     *
     * @param bytes 字节数组
     * @return Hex 字符串
     */
    public static String byte2Hex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hash = formatter.toString();
        formatter.close();
        return hash;
    }

    public static byte[] i82Byte(int i) {
        byte[] result = new byte[1];
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    public static int byte2i8(byte[] bytes) {
        int value = bytes[0] & 0xFF;
        return value;
    }

    public static byte[] i162Byte(int i) {
        byte[] result = new byte[2];
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    public static int byte2i16(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = i * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    public static byte[] i322Byte(int i) {
        byte[] result = new byte[4];
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    public static int byte2i32(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = i * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    public static byte[] fl2Byte(float data) {
        int intBits = Float.floatToIntBits(data);
        return i322Byte(intBits);
    }

    public static float byte2Fl(byte[] bytes) {
        return Float.intBitsToFloat(byte2i32(bytes));
    }

    public static int findFlag(int begin_pos, byte[] data, byte[] flag, int dataSize, int flagSize) {
        int cnt = 0;
        for (int i = begin_pos; i < dataSize; i++) {
            if (data[i] == flag[cnt]) {
                cnt++;
                if (cnt == flagSize)
                    return i + 1 - flagSize;
            } else {
                cnt = 0;
            }
        }
        return -1;
    }

}
