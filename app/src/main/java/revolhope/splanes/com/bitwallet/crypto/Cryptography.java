
public abstract class Cryptography {

    private static Crypto crypto;
    private static SecureRandom secureRandom;
    
    public static Crypto buildInstance()
    {
        return new Crypto(genKey(), genIv());
    }
    
    public static Crypto buildInstance(@NonNull byte[] iv, @NonNull byte[] key)
    {
        return new Crypto(key, iv);
    }
    
    public static byte[] genKey() {
        secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        return secretKey.getEncoded();
    }
    
    public static byte[] genIv() {
        secureRandom = new SecureRandom();
        byte[] iv = new byte[12];
        secureRandom.nextBytes(iv);
    }
    
    public static class Crypto
    {
        private byte[] iv;
        private byte[] key;
        private GCMParameterSpec parameterSpec;
        private Cipher cipher;
        private boolean isInit;
        
        private Crypto(byte[] key, byte[] iv) {
            this.key = key;
            this.iv = iv;
            isInit = false;
        }
        
        public boolean init(int cipherMode) {
            if (key != null) {                
                cipher = Cipher.getInstance("AES_256/GCM/NoPadding");
                parameterSpec = new GCMParameterSpec(128, iv);
                cipher.init(cipherMode, key, parameterSpec);
                isInit = true;
                return true;
            }
            else return false;
        }
        
        public byte[] do(byte[] plainData) {
            if (isInit) return cipher.doFinal(plainData);
            else null;
        }
    }
}
