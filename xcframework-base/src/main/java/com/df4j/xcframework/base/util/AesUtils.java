package com.df4j.xcframework.base.util;

import com.df4j.xcframework.base.exception.XcException;
import org.apache.commons.codec.Charsets;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES工具类
 */
public class AesUtils {

    public static final int AES128 = 128;
    public static final int AES192 = 192;
    public static final int AES256 = 256;

    /**
     * 生成AESKEY
     * @param bits
     * @param password
     * @return
     */
    public static byte[] generateKeyByte(Integer bits, String password){
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");// 创建AES的Key生产者
            kgen.init(bits, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            return enCodeFormat;
        }catch (Exception e){
            throw new XcException("生成AESKey出错",e);
        }
    }

    /**
     * 生成AESKEY
     * @param bits
     * @param password
     * @return
     */
    public static String generateKeyString(Integer bits, String password){
        return HexUtils.toHexString(generateKeyByte(bits,password));
    }

    /**
     * 生成AESKEY
     * @param keyString HEX编码
     * @return
     */
    public static byte[] generateKeyByte(String keyString){
        return HexUtils.toByte(keyString);
    }

    /**
     * 生成AESKEY
     * @param keyString HEX编码
     * @return
     */
    public static SecretKeySpec generateKey(String keyString){
        return generateKey(generateKeyByte(keyString));
    }

    /**
     * 生成AESKEY
     * @param encodeKey
     * @return
     */
    public static SecretKeySpec generateKey(byte[] encodeKey){
        return new SecretKeySpec(encodeKey, "AES");
    }

    /**
     * 生成AESKEY
     * @param bits
     * @param password
     * @return
     */
    public static SecretKeySpec generateKey(Integer bits, String password){
        return generateKey(generateKeyByte(bits,password));
    }

    /**
     * AES加/解密
     * @param content
     * @param key
     * @return
     */
    public static byte[] encode(byte[] content, SecretKeySpec key, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            cipher.init(mode, key);// 初始化为解密模式的密码器
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e){
            throw new XcException("AES加/解密错误",e);
        }
    }

    /**
     * AES加密
     * @param content
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] content, SecretKeySpec key){
        return encode(content, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * AES解密
     * @param content
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] content, SecretKeySpec key){
        return encode(content, key, Cipher.DECRYPT_MODE);
    }

    /**
     * 加密
     * @param content
     * @param key
     * @return
     */
    public static byte[] encrypt(String content, SecretKeySpec key) {
        try{
            return encrypt(content.getBytes(Charsets.UTF_8),key);
        }catch (Exception e){
            throw new XcException("加密错误",e);
        }
    }

    /**
     * 加密
     * @param content
     * @param key
     * @return
     */
    public static String encryptHex(String content, SecretKeySpec key){
        return HexUtils.toHexString(encrypt(content, key));
    }

    /**
     * 加密
     * @param content
     * @param key
     * @return
     */
    public static String encryptHex(byte[] content, SecretKeySpec key){
        return HexUtils.toHexString(encrypt(content,key));
    }


    /**
     * AES解密
     * @param hexContent
     * @param key
     * @return
     */
    public static byte[] decrypt(String hexContent, SecretKeySpec key){
        return decrypt(HexUtils.toByte(hexContent),key);
    }

    /**
     * AES解密
     * @param content
     * @param key
     * @return
     */
    public static String decryptHex(byte[] content, SecretKeySpec key) {
        return new String(decrypt(content,key), Charsets.UTF_8);
    }

    /**
     * AES解密
     * @param hexContent
     * @param key
     * @return
     */
    public static String decryptHex(String hexContent, SecretKeySpec key){
        return decryptHex(HexUtils.toByte(hexContent),key);
    }
}
