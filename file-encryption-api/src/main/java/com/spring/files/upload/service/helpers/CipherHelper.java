package com.spring.files.upload.service.helpers;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CipherHelper {

    private final String SECRET_KEY = "fytSNnTVVg2jESgP6pFoRJxLkp1V2jzogPlGBd51cXHIZnYPwz9UZuCCJzIn2VZU1dbQp95tW1rgOyfcdUvtY6pe0uGmShFbopF6AsyCNS1DAuXTb1B8ZXRGCEEM6dmB";
    private final String SALT = "dfx4J5ei3OVzn0OM4w";
    private static byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public String encrypt(String strToEncrypt, String secretKeyOfUser) {
        try {
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = createKeySpec(secretKeyOfUser);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt, String secretKeyOfUser) {
        try {
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = createKeySpec(secretKeyOfUser);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    private KeySpec createKeySpec(String secretKeyOfUser) {
        return secretKeyOfUser != null ? new PBEKeySpec(secretKeyOfUser.toCharArray(), SALT.getBytes(), 65536, 256) :
                new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
    }
}
