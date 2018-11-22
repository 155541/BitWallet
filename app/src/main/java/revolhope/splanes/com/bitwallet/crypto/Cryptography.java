package revolhope.splanes.com.bitwallet.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import revolhope.splanes.com.bitwallet.helper.AppUtils;
import revolhope.splanes.com.bitwallet.model.K;

public final class Cryptography {

    private KeyStore ks;
    private Cipher c;

    public Cryptography() throws Exception {
        init();
    }

    public SecretKey newKey(@NonNull String alias) throws Exception {

        KeyGenerator kg = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                                                  "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder =  new KeyGenParameterSpec.Builder(
                alias,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);

        builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setUserAuthenticationRequired(false)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);

        kg.init(builder.build());
        return kg.generateKey();
    }

    public void delete(@NonNull String alias) throws Exception {
        ks.deleteEntry(alias);
    }

    @Nullable
    public K encrypt(@NonNull byte[] data, @NonNull String alias) {

        try {
            SecretKey k = (SecretKey) ks.getKey(alias, null);
            if (c == null) {
                c = Cipher.getInstance( KeyProperties.KEY_ALGORITHM_AES + "/" +
                                        KeyProperties.BLOCK_MODE_GCM + "/" +
                                        KeyProperties.ENCRYPTION_PADDING_NONE);
            }

            c.init(Cipher.ENCRYPT_MODE, k);
            byte[] bytes = c.doFinal(data);
            GCMParameterSpec spec = c.getParameters().getParameterSpec(GCMParameterSpec.class);

            String pwd64 = AppUtils.toStringBase64(bytes);
            K _k = new K();
            _k.setAccId(alias);
            _k.setPwdBase64(pwd64);
            _k.setSpec(spec);

            return _k;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public byte[] decrypt(@NonNull byte[] data, @NonNull K _k, @NonNull String alias) {

        try {
            SecretKey k = (SecretKey) ks.getKey(alias, null);
            if (c == null) {
                c = Cipher.getInstance( KeyProperties.KEY_ALGORITHM_AES + "/" +
                        KeyProperties.BLOCK_MODE_GCM + "/" +
                        KeyProperties.ENCRYPTION_PADDING_NONE);
            }
            c.init(Cipher.DECRYPT_MODE, k, _k.getSpec());
            return c.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cipher getFingerprint(String alias) throws Exception {

        if (c == null) {
            c = Cipher.getInstance( KeyProperties.KEY_ALGORITHM_AES + "/" +
                    KeyProperties.BLOCK_MODE_GCM + "/" +
                    KeyProperties.ENCRYPTION_PADDING_NONE);
        }
        c.init(Cipher.ENCRYPT_MODE, ks.getKey(alias, null));
        return c;
    }

    public boolean existsAlias(@NonNull String alias) throws Exception {
        return ks.containsAlias(alias);
    }

    private void init() throws Exception {
        ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
    }




// =================================================================================================
// =================================================================================================
// =================================================================================================
// =================================================================================================
// =================================================================================================
// =================================================================================================
    /*
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
    */
}
