package revolhope.splanes.com.bitwallet.helper;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class AppUtils {


    public static String format(String format, long time)
    {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(time);
    }

    @NonNull
    public static String toString(@NonNull char[] array)
    {
        StringBuilder sb = new StringBuilder(array.length);
        for (char c : array) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static byte[] toBase64(@NonNull byte[] bytes)
    {
        return Base64.encode(bytes, Base64.DEFAULT);
    }

    public static String toStringBase64(@NonNull byte[] bytes)
    {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] fromBase64(@NonNull byte[] bytes)
    {
        return Base64.decode(bytes, Base64.DEFAULT);
    }

    public static byte[] fromStringBase64(@NonNull String strBase64)
    {
        return Base64.decode(strBase64, Base64.DEFAULT);
    }
}
