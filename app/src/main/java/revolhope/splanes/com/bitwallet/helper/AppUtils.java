package revolhope.splanes.com.bitwallet.helper;

import android.support.annotation.NonNull;
import android.util.Base64;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
