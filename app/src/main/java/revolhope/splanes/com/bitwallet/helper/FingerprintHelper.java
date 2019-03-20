package revolhope.splanes.com.bitwallet.helper;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.core.app.ActivityCompat;
import android.widget.Toast;

import javax.crypto.Cipher;

import revolhope.splanes.com.bitwallet.crypto.Cryptography;

public abstract class FingerprintHelper {

    private static final char[] fingerprintIndex =
            new char[] {'f','i','n','g','e','r','p','r','i','n','t','.','k','e','y'};

    public static Cipher getFingerprintCipher() throws Exception {
        Cryptography crypto = new Cryptography();
        if (!crypto.existsAlias(AppUtils.toString(fingerprintIndex))) {
            crypto.newKey(AppUtils.toString(fingerprintIndex));
        }
        return crypto.getFingerprint(AppUtils.toString(fingerprintIndex));
    }


    @TargetApi(Build.VERSION_CODES.M)
    public static class AuthCallback extends FingerprintManager.AuthenticationCallback
    {
        private CancellationSignal cancellationSignal;
        private final Context context;
        private OnAuthListener onAuthListener;

        public AuthCallback(Context mContext, OnAuthListener mOnAuthListener) {
            context = mContext;
            onAuthListener = mOnAuthListener;
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
            Toast.makeText(context, "Authentication error:\n" +
                    errString, Toast.LENGTH_LONG).show();
            onAuthListener.onAuthenticate(false, errString.toString());
        }

        // onAuthenticationFailed is called when the fingerprint does not match
        // with any of the fingerprints registered on the device
        @Override
        public void onAuthenticationFailed() {
            Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
            onAuthListener.onAuthenticate(false);
        }

        // onAuthenticationHelp is called when a non-fatal error has occurred.
        // This method provides additional information about the error,
        // so to provide the user with as much feedback as possible
        // I’m incorporating this information into my toast
        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG)
                 .show();
            onAuthListener.onAuthenticate(false);
        }

        // onAuthenticationSucceeded is called when a fingerprint has been successfully matched
        // to one of the fingerprints stored on the user’s device
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

            onAuthListener.onAuthenticate(true);
        }

        public interface OnAuthListener {
            void onAuthenticate(boolean isSucceed, String... errorCodes);
        }
    }


}
