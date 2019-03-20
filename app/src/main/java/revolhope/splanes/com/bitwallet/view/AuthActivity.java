package revolhope.splanes.com.bitwallet.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import revolhope.splanes.com.bitwallet.R;
import revolhope.splanes.com.bitwallet.helper.FingerprintHelper;

public class AuthActivity extends AppCompatActivity {
    
    private FingerprintManager fingerprintManager;
    private boolean hasRequirements = false;

    private ImageView[] ivPattern = new ImageView[9];
    private int[] patternImageViewIds = new int[]{
            R.id.imageView_pattern11, R.id.imageView_pattern12, R.id.imageView_pattern13,
            R.id.imageView_pattern21, R.id.imageView_pattern22, R.id.imageView_pattern23,
            R.id.imageView_pattern31, R.id.imageView_pattern32, R.id.imageView_pattern33
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        bindImageViewPattern();

        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (fingerprintManager != null)
            hasRequirements = checkFingerprintRequirement(fingerprintManager);
    }

    @Override
    protected void onResume() {
        if (fingerprintManager != null && hasRequirements) {
            try  {
                FingerprintManager.CryptoObject cryptoObj =
                        new FingerprintManager.CryptoObject(FingerprintHelper.getFingerprintCipher());

                FingerprintHelper.AuthCallback callback =
                        new FingerprintHelper.AuthCallback(this,
                                new FingerprintHelper.AuthCallback.OnAuthListener() {
                            @Override
                            public void onAuthenticate(boolean isSucceed, String... errorCodes) {
                                if (isSucceed) {
                                    Intent i = new Intent(getApplicationContext(),
                                            MainActivity.class);
                                    startActivity(i);
                                    Vibrator vibrator =
                                            (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    if (vibrator != null) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(200,
                                                VibrationEffect.DEFAULT_AMPLITUDE));
                                    }
                                }
                            }
                        });

                callback.startAuth(fingerprintManager, cryptoObj);
            }
            catch(Exception exc) {
                exc.printStackTrace();
            }
        }
        super.onResume();
    }

    private void bindImageViewPattern() {
        for (int i = 0 ; i < 9 ; i++) {
            ivPattern[i] = findViewById(patternImageViewIds[i]);
        }
    }

    private boolean checkFingerprintRequirement(@NonNull FingerprintManager fingerprintManager) {

        View view = findViewById(R.id.auth_layout);

        if (ActivityCompat.checkSelfPermission(this,
                                                Manifest.permission.USE_FINGERPRINT) !=
            PackageManager.PERMISSION_GRANTED) {

            Snackbar.make(view,
                    "Please enable the fingerprint permission",
                    Snackbar.LENGTH_INDEFINITE).show();
            // TODO: throw permission dialog or set in action button on snack bar¿?
            return false;
        }
        if (!fingerprintManager.hasEnrolledFingerprints()) {

            Snackbar.make(view,
                    "No fingerprint configured. Please register at least one" +
                            " fingerprint in your device's Settings",
                    Snackbar.LENGTH_INDEFINITE).show();

            return false;
        }
        return true;
    }
}
