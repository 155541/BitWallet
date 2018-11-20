package revolhope.splanes.com.bitwallet.helper;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import revolhope.splanes.com.bitwallet.view.MainActivity;

public abstract class FingerprintHelper {

    private static final String NAME = "finger-key";

    public static class Util
    {
        private KeyStore keyStore;
        private Cipher cipher;

        public void genKey() throws FingerException {

            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                keyStore.load(null);
                KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                                NAME,
                                KeyProperties.PURPOSE_ENCRYPT |
                                        KeyProperties.PURPOSE_DECRYPT);

                builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

                keyGenerator.init(builder.build());
                keyGenerator.generateKey();
            }
            catch ( KeyStoreException |
                    NoSuchAlgorithmException |
                    NoSuchProviderException |
                    IOException |
                    CertificateException |
                    InvalidAlgorithmParameterException e) {

                throw new FingerException("Error while generate key. Stacktrace:" + e.getMessage());
            }
        }

        public boolean initCipher() {

            try {
                cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" +
                                            KeyProperties.BLOCK_MODE_CBC + "/" +
                                            KeyProperties.ENCRYPTION_PADDING_PKCS7);
            }
            catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                e.printStackTrace();
                return false;
            }

            try {
                keyStore.load(null);
                SecretKey key = (SecretKey) keyStore.getKey(NAME, null);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return true;
            }
            catch (KeyStoreException |
                    CertificateException |
                    UnrecoverableKeyException |
                    IOException |
                    NoSuchAlgorithmException |
                    InvalidKeyException e) {

                e.printStackTrace();
                return false;
            }

        }

        public Cipher getCipher() { return cipher; }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static class AuthCallback extends FingerprintManager.AuthenticationCallback
    {
        private CancellationSignal cancellationSignal;
        private final Context context;

        public AuthCallback(Context mContext) {
            context = mContext;
        }

        // Implement the startAuth method,
        // which is responsible for starting the fingerprint authentication process
        public void startAuth(FingerprintManager manager,
                              FingerprintManager.CryptoObject cryptoObject) {

            cancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject,
                                 cancellationSignal,
                                0,
                                this,
                                null);
        }

        // onAuthenticationError is called when a fatal error has occurred.
        // It provides the error code and error message as its parameters
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Toast.makeText(context, "Authentication error:\n" + errString, Toast.LENGTH_LONG)
                 .show();
        }

        // onAuthenticationFailed is called when the fingerprint does not match
        // with any of the fingerprints registered on the device
        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
        }

        // onAuthenticationHelp is called when a non-fatal error has occurred.
        // This method provides additional information about the error,
        // so to provide the user with as much feedback as possible
        // I’m incorporating this information into my toast
        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG)
                 .show();
        }

        // onAuthenticationSucceeded is called when a fingerprint has been successfully matched
        // to one of the fingerprints stored on the user’s device
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
            if (context instanceof Activity)
            {
                ((Activity) context).finish();
            }
        }
    }

    public static class FingerException extends Exception {
        private String error;
        private FingerException(String error)
        {
            this.error = error;
        }
        public String getError() { return error; }
    }
}
