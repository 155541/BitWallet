package revolhope.splanes.com.bitwallet.helper;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public abstract class RandomGenerator {
    public static final int MODE_SIMPLE = 110;
    public static final int MODE_COMPLEX = 146;

    public static final int SIZE_12 = 607;
    public static final int SIZE_16 = 325;
    public static final int SIZE_24 = 78;
    public static final int SIZE_32 = 970;
    public static final int SIZE_64 = 233;
    public static final int SIZE_128 = 717;
    public static final int SIZE_OTHER = 351;

    private static final String simple = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789<>-_.:;=/&%$!";
    private static final String symbols = "\"\'¡º¿?)(ñ·\\,ç+{}[]*€@#";

    @NonNull
    private static String generateUniverse(boolean isComplex) {
        String base = simple;
        if (isComplex) {
            base += symbols;
        }
        int universeSize = base.length();
        StringBuilder sb = new StringBuilder(universeSize);
        long seed;
        Random rand;
        for (int i = 0 ; i < universeSize ; i++) {
            seed = (long) (System.currentTimeMillis() * Math.random());
            rand = new Random(seed);
            sb.append(base.charAt(rand.nextInt(universeSize-1)));
        }
        return sb.toString();
    }

    @Nullable
    @Contract(pure = true)
    public static String create(int mode, int size) {
        int s;
        switch (size) {
            case SIZE_12:    s = 12; break;
            case SIZE_16:    s = 16; break;
            case SIZE_24:    s = 24; break;
            case SIZE_32:    s = 32; break;
            case SIZE_64:    s = 64; break;
            case SIZE_128:   s = 128; break;
            case -1:         return null;
            default:         s = size;
        }
        String universe = generateUniverse(mode == MODE_COMPLEX);
        StringBuilder sb = new StringBuilder(s);
        int universeSize = universe.length();
        long seed;
        Random rand;
        for (int i = 0 ; i < s ; i++) {
            seed = (long) (System.currentTimeMillis() * Math.random());
            rand = new Random(seed);
            sb.append(universe.charAt(rand.nextInt(universeSize-1)));
        }
        return sb.toString();
    }

    @Contract(pure = true)
    public static String createToken() {
        return create(MODE_SIMPLE, SIZE_12);
    }
}
