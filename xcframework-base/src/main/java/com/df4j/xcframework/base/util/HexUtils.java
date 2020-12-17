package com.df4j.xcframework.base.util;

import com.df4j.xcframework.base.exception.XcException;
import org.apache.commons.codec.binary.Hex;

/**
 * HEX转换工具类
 */
public class HexUtils {

    /**
     * 将字节数组转换成HEX字符串
     *
     * @param data
     * @return
     */
    public static String toHexString(byte[] data) {
        return Hex.encodeHexString(data);
    }

    /**
     * 将HEX字符串转换成字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] toByte(String hexString) {
        try {
            return Hex.decodeHex(hexString);
        } catch (Exception e) {
            throw new XcException("将HEX字符串转换成byte[]出错", e);
        }
    }
}
