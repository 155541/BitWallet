package revolhope.splanes.com.bitwallet.helper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public abstract class RandomGenerator {

    public static final int MODE_SIMPLE = 110;
    public static final int MODE_COMPLEX = 146;

    public static final int SIZE_8 = 607;
    public static final int SIZE_16 = 325;
    public static final int SIZE_24 = 78;
    public static final int SIZE_32 = 970;
    public static final int SIZE_64 = 233;

    private static final String simpleUniverse =
            "aTidBxL6N49z0pqWXuJ3vklAKw7EFGrPbHjReQo8CcS1htOfIsYZMm5ygDnUV2";

    private static final String complexUniverse = simpleUniverse +
            "$#%}|=!¡·)'-_?/&.,:[;¿](@{";

    @Nullable
    @Contract(pure = true)
    public static String create(int mode, int size) {

        if (mode != MODE_SIMPLE && mode != MODE_COMPLEX) return null;
        String u = mode == MODE_SIMPLE ? simpleUniverse : complexUniverse;
        int s;
        switch (size) {
            case SIZE_8:    s = 8; break;
            case SIZE_16:   s = 16; break;
            case SIZE_24:   s = 24; break;
            case SIZE_32:   s = 32; break;
            case SIZE_64:   s = 64; break;
            case -1:        return null;
            default:        s = size;
        }
        StringBuilder sb = new StringBuilder(s);
        int universeSize = u.length();

        for (int i = 0 ; i < s ; i++) {
            double index = Math.floor(Math.random() * universeSize) + 1;
            sb.append(u.charAt((int)index));
        }

        return sb.toString();
    }

}
