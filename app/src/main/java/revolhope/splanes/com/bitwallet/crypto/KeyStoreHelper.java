package revolhope.splanes.com.bitwallet.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public abstract class KeyStoreHelper {

    private static KeyStore keyStore;
    private static boolean isInit;

    static {
        isInit = false;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            isInit = true;
        }
        catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SecretKey createKey(@NonNull String alias) throws Exception {

        if (!isInit) initialize();

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

    public static void deleteKey(@NonNull String alias) throws Exception {
        if (!isInit) initialize();
        keyStore.deleteEntry(alias);
    }

    public static Key getKey(@NonNull String alias) throws Exception {
        if (!isInit) initialize();
        if (keyStore.containsAlias(alias)) return keyStore.getKey(alias, null);
        else throw new Exception("Alias not found...");
    }

    private static void initialize() throws Exception {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        isInit = true;
    }
}
