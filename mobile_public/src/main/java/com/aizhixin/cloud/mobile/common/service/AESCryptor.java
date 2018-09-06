package com.aizhixin.cloud.mobile.common.service;


import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * Java和Android通用的AES加解密工具
 */
@Component
public class AESCryptor {
    private final static Logger LOG = LoggerFactory.getLogger(AESCryptor.class);
    private final static String UTF8_ENCODING = "UTF-8";
    private final static String AES_ENCODING = "AES";
    private final static String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private final static byte[] IV = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    private static SecretKeySpec secretKey;
    private static Cipher encryptCipher;
    private static Cipher decryptCipher;

    @Autowired
    public AESCryptor(@Value("${security.key}") String key) {
        try {
            secretKey = new SecretKeySpec(key.getBytes(UTF8_ENCODING), AES_ENCODING);
            encryptCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));

            decryptCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
        } catch (UnsupportedEncodingException e) {
            LOG.warn("UnsupportedEncodingException:{}", e);
            throw new RuntimeException("AES Crypto is not supported by System. " + e.getMessage());
        } catch (Exception e) {
            LOG.warn("Exception:{}", e);
            throw new RuntimeException("Encrypt using AES failed. " + e.getMessage());
        }
    }

    /**
     * 加密
     * Encrypt the data
     * @param tobeEncrypted
     * @return
     */
    public String encrypt(String tobeEncrypted) {
        if (!StringUtils.isEmpty(tobeEncrypted)) {
            try {
                return Base64.encodeBase64String(encryptCipher.doFinal(tobeEncrypted.getBytes(UTF8_ENCODING)));
            } catch (Exception e) {
                LOG.warn("EnCoding ({}) Exception:{}", tobeEncrypted, e);
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解密
     * Decrypt the data
     * @param encrypted
     * @return
     */
    public String decrypt(String encrypted){
        if (!StringUtils.isEmpty(encrypted)) {
            try {
                return new String(decryptCipher.doFinal(Base64.decodeBase64(encrypted)), UTF8_ENCODING);
            } catch (Exception e) {
                LOG.warn("Decode ({}) Exception:{}", encrypted, e);
                e.printStackTrace();
            }
        }
        return null;
    }

//    public static void main(String[] args){
//        AESCryptor cryptor =  new AESCryptor("AIZHIXIN2020OAUT");//只能16位
//
//        String encrypted = cryptor.encrypt("1234567");
//        System.out.println(encrypted);
//        System.out.println(cryptor.decrypt(encrypted));
//    }
}
