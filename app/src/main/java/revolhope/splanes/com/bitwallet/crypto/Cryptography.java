package revolhope.splanes.com.bitwallet.crypto;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class Cryptography {

    private static Crypto crypto;
    private static SecureRandom secureRandom;
    
    @NonNull
    @Contract(" -> new")
    public static Crypto buildInstance()
    {
        return new Crypto(genKey(), genIv());
    }
    
    @NonNull
    @org.jetbrains.annotations.Contract("_, _ -> new")
    public static Crypto buildInstance(@NonNull SecretKey key, @NonNull byte[] iv)
    {
        return new Crypto(key, iv);
    }
    
    @NonNull
    @Contract(" -> new")
    public static SecretKey genKey() {
        secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }
    
    public static byte[] genIv() {
        secureRandom = new SecureRandom();
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
        return iv;
    }
    
    public static class Crypto
    {
        private byte[] iv;
        private SecretKey key;
        private GCMParameterSpec parameterSpec;
        private Cipher cipher;
        private boolean isInit;
        
        private Crypto(SecretKey key, byte[] iv) {
            this.key = key;
            this.iv = iv;
            isInit = false;
        }
        
        public boolean init(int cipherMode) {

            if (key != null) {
                try {
                    cipher = Cipher.getInstance("AES_256/GCM/NoPadding");
                    parameterSpec = new GCMParameterSpec(128, iv);

                    cipher.init(cipherMode, key, parameterSpec);
                    isInit = true;
                    return true;
                }
                catch (NoSuchPaddingException | InvalidAlgorithmParameterException |
                        NoSuchAlgorithmException | InvalidKeyException e) {
                    e.printStackTrace();
                    isInit = false;
                    return false;
                }
            }
            else return false;
        }
        
        public byte[] make(byte[] data) {
            if (isInit) {
                try {
                    return cipher.doFinal(data);
                }
                catch (IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else return null;
        }
    }
}
