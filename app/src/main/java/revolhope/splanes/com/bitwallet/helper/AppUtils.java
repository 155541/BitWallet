package revolhope.splanes.com.bitwallet.helper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class AppUtils {


    public static String format(String format, long time)
    {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(time);
    }

}
