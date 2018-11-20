package revolhope.splanes.com.bitwallet.view;

import android.support.v7.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    
    
    private ImageView[] ivPattern = new ImageView[9];
    private int[] patternImageViewIds = new int[]{
        imageView_pattern11,imageView_pattern12,imageView_pattern13
        imageView_pattern21,imageView_pattern22,imageView_pattern23
        imageView_pattern31,imageView_pattern32,imageView_pattern33
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        bindImageViewPattern();
        
        KeyguardManager keyguardManager = 
            (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = 
            (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        
        if (checkFingerprintRequirement(keyguardManager, fingerprintManager)) {
            try  {
                FingerprintHelper.Util fingerUtil = new FingerprintHelper.Util();
                fingerUtil.generateKey();
                if (fingerUtil.initCipher())
                {
                    CryptoObject cryptoObj = new FingerprintHelper.CryptoObject(fingerUtil.getCipher());
                    FingerprintHelper.AuthCallback callback = new FingerprintHelper.AuthCallback(this);
                    callback.startAuth(fingerprintManager, cryptoObject);
                }
            }
            catch(FingerprintHelper.FingerException exc) {
                exc.printStackTrace();
            }
        }
    }
    
    private void bindImageViewPattern() {
        for (int i = 0 ; i < 9 ; i++) {
            ivPattern[i] = findViewById(patternImageViewIds[i]);
        }
    }

    private boolean checkFingerprintRequirement(KeyguardManager keyguardManager,
                                                FingerprintManager fingerprintManager) {
        //Check whether the device has a fingerprint sensor//
        if (!fingerprintManager.isHardwareDetected()) {
            // SNACKBAR WITH: ("Your device doesn't support fingerprint authentication");
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != 
            PackageManager.PERMISSION_GRANTED) {
            // If your app doesn't have this permission, then display the following text//
            // SNACKBAR WITH: ("Please enable the fingerprint permission");
            // TODO: throw permission dialog¿?
            return false;
        }
        if (!fingerprintManager.hasEnrolledFingerprints()) {
          // If the user hasn’t configured any fingerprints, then display the following message//
          // SNACKBAR WITH: ("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
            return false;
        }
        if (!keyguardManager.isKeyguardSecure()) {
          // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
          // SNACKBAR WITH: ("Please enable lockscreen security in your device's Settings");
            return false;
        }
        return true;
    }
}
