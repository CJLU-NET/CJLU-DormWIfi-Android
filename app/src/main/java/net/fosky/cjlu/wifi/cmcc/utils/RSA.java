package net.fosky.cjlu.wifi.cmcc.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSA {

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static final String ALGORITHM_NAME = "RSA";
    private static final String MD5_RSA = "MD5withRSA";

    public static String encrypt(String text) {
        String pub_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIXmz5YIIaNPgYDfckrl2XsgB6wqf+2M+nvnnmJD21MtaNGXyX7KqrBchGfQbKj2bijb1vqRXpchF7h8ZYhrhKwdfEym+Qt/1HqSbAvx6kBvcKxu9IQqkFkGP47+h5yXz66z0agToNb70KfrVAlwQj9R+el9MMwUKQmcdKGm2NRwIDAQAB";

        return encrypt_data(text, getPublicKey(pub_key));
    }

    /**
     * 获取公钥
     *
     * @param publicKey base64加密的公钥字符串
     */
    private static PublicKey getPublicKey(String publicKey) {
        byte[] decodedKey = Base64.decode(publicKey.getBytes(), Base64.NO_WRAP);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * RSA加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     */
    private static String encrypt_data(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int inputLen = data.getBytes().length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offset > 0) {
                if (inputLen - offset > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data.getBytes(), offset, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data.getBytes(), offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            // 获取加密内容使用base64进行编码,并以UTF-8为标准转化成字符串
            // 加密后的字符串
            return new String(Base64.encode(encryptedData, Base64.NO_WRAP));
        } catch (Exception e) {
            return null;
        }

    }

}
