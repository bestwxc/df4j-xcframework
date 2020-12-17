package com.df4j.xcframework.base.util;

import com.df4j.xcframework.base.exception.XcException;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5工具类
 */
public class Md5Utils {
    public static String md5(String s) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("md5");
            return Hex.encodeHexString(md5.digest(s.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new XcException("计算MD5错误", e);
        }
    }
}
