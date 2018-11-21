package revolhope.splanes.com.bitwallet.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import revolhope.splanes.com.bitwallet.helper.AppUtils;

public final class KeyStoreHelper {

    private static final char[] keyStoreFile =
            new char[] {'b','i','t','w','a','l','l','e','t','.','k','e','y','s','t','o','r','e'};

    private static final char[] keyStorePwd =
            new char[] {'E','2','v','.','q','W','-','0','4','p','$','s','L','t','R','H','4','l',
                        '1', '_','<','G','h','c','5','u','=','H','j','K','i','P','X','8','0','.',
                        'e','p'};

    private static KeyStore keyStore;
    private static KeyStore.ProtectionParameter protectionParam;

    public static void init()
    {
        try
        {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            boolean first = !Files.exists(new File(AppUtils.toString(keyStoreFile)).toPath(),
                             LinkOption.NOFOLLOW_LINKS);
            if (first) {
                keyStore.load(null);
            }
            else {
                keyStore.load(new FileInputStream(AppUtils.toString(keyStoreFile)),
                        keyStorePwd);
            }
        }
        catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean existsAlias(String alias)
    {
        if (keyStore != null) {
            try
            {
                return keyStore.containsAlias(alias);
            }
            catch (KeyStoreException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else return false;
    }

    @Nullable
    public static Key getKey(String alias, @Nullable char[] password)
    {
        if (existsAlias(alias)) {
            try
            {
                if (password != null) {
                    protectionParam = new KeyStore.PasswordProtection(password);
                }
                KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry)
                        keyStore.getEntry(alias, password == null ? null : protectionParam);
                return entry.getSecretKey();
            }
            catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else return null;
    }

    public static boolean generateKey(@NonNull String alias, @NonNull SecretKey key,
                                      @Nullable char[] password)
    {
        try {

            KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(key);

            if (password == null) {
                keyStore.setEntry(alias, entry, null);
            }
            else {
                protectionParam = new KeyStore.PasswordProtection(password);
                keyStore.setEntry(alias, entry, protectionParam);
            }
            store();
            return true;

        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void store()
    {
        try (FileOutputStream fos = new FileOutputStream(AppUtils.toString(keyStoreFile)))
        {
            keyStore.store(fos, keyStorePwd);
        }
        catch (IOException | KeyStoreException |
               NoSuchAlgorithmException | CertificateException e)
        {
            e.printStackTrace();
        }
    }


}
