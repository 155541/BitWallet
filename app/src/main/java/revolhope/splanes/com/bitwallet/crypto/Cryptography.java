package revolhope.splanes.com.bitwallet.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.security.KeyStore;
import java.security.MessageDigest;

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
            //SecretKey k = (SecretKey) ks.getKey(alias, null);
            if (c == null) {
                c = Cipher.getInstance( KeyProperties.KEY_ALGORITHM_AES + "/" +
                                        KeyProperties.BLOCK_MODE_GCM + "/" +
                                        KeyProperties.ENCRYPTION_PADDING_NONE);
            }

            c.init(Cipher.ENCRYPT_MODE, KeyStoreHelper.getKey(alias));
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
            //SecretKey k = (SecretKey) ks.getKey(alias, null);
            if (c == null) {
                c = Cipher.getInstance( KeyProperties.KEY_ALGORITHM_AES + "/" +
                        KeyProperties.BLOCK_MODE_GCM + "/" +
                        KeyProperties.ENCRYPTION_PADDING_NONE);
            }
            c.init(Cipher.DECRYPT_MODE, KeyStoreHelper.getKey(alias), _k.getSpec());
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
        c.init(Cipher.ENCRYPT_MODE, KeyStoreHelper.getKey(alias));
        return c;
    }

    public boolean existsAlias(@NonNull String alias) throws Exception {
        return ks.containsAlias(alias);
    }

    private void init() throws Exception {
        ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
    }

    public String sha256(@NonNull String strData) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] raw = md.digest(AppUtils.toBase64(strData.getBytes()));
        return AppUtils.toStringBase64(raw);
    }


}
