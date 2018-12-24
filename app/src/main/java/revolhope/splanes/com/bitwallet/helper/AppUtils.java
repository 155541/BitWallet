package revolhope.splanes.com.bitwallet.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import revolhope.splanes.com.bitwallet.R;

public final class AppUtils {


// =================================================================================================
//                                          DATE UTILS
// =================================================================================================

    public static String format(String format, long time)
    {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(time);
    }

    public static long timestamp() {
        return Calendar.getInstance().getTimeInMillis();
    }

    @Nullable
    public static Long toMillis(String format, String date) {

        try {
            return new SimpleDateFormat(format, Locale.ENGLISH).parse(date).getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

// =================================================================================================
//                                         STRING UTILS
// =================================================================================================

    @NonNull
    public static String toString(@NonNull char[] array)
    {
        StringBuilder sb = new StringBuilder(array.length);
        for (char c : array) {
            sb.append(c);
        }
        return sb.toString();
    }

    @Contract(pure = true)
    public static int stringToConstant(String internalConstant) {
        switch (internalConstant)
        {
            case "12 chars": return RandomGenerator.SIZE_12;
            case "16 chars": return RandomGenerator.SIZE_16;
            case "24 chars": return RandomGenerator.SIZE_24;
            case "32 chars": return RandomGenerator.SIZE_32;
            case "64 chars": return RandomGenerator.SIZE_64;
            case "128 chars": return RandomGenerator.SIZE_128;
            case "Other..." : return RandomGenerator.SIZE_OTHER;
            default: return -1;
        }
    }

// =================================================================================================
//                                         BASE64 UTILS
// =================================================================================================

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
