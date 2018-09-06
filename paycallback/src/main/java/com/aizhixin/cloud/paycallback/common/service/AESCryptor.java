package com.aizhixin.cloud.paycallback.common.service;

import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Java和Android通用的AES加解密工具
 */
@Component
public class AESCryptor {
    private final static Logger LOG = LoggerFactory.getLogger(AESCryptor.class);
    private final static String UTF8_ENCODING = "UTF-8";
    private final static String AES_ENCODING = "AES";
    private final static String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
//    private final static byte[] IV = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

    @Value("${security.key}")
    @Setter private String key;

    /**
     * 加密过程
     * @param tobeEncrypted  明文
     * @return               密文
     */
    public String encrypt(String tobeEncrypted) {
        if (!StringUtils.isEmpty(tobeEncrypted)) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES_ENCODING);
                Cipher encryptCipher = Cipher.getInstance(CIPHER_ALGORITHM);
                IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, 16);
                encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                return new Base64().encodeToString(encryptCipher.doFinal(tobeEncrypted.getBytes(UTF8_ENCODING)));
            } catch (Exception e) {
                LOG.warn("EnCoding ({}) Exception:{}", tobeEncrypted, e);
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解密过程
     * @param encrypted 密文
     * @return          明文
     */
    public String decrypt(String encrypted){
        if (!StringUtils.isEmpty(encrypted)) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES_ENCODING);
                Cipher decryptCipher = Cipher.getInstance(CIPHER_ALGORITHM);
                IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, 16);
                decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
                return new String(decryptCipher.doFinal(Base64.decodeBase64(encrypted)), UTF8_ENCODING);
            } catch (Exception e) {
                LOG.warn("Decode ({}) Exception:{}", encrypted, e);
                e.printStackTrace();
            }
        }
        return null;
    }
}
