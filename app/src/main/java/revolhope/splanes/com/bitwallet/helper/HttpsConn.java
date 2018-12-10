package revolhope.splanes.com.bitwallet.helper;

import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpsConn {

    private static final String SEP = "hdheKK0dd";
    private URL URL_SERVER;
    private HttpsURLConnection conn;

    public HttpsConn() throws Exception {
        URL_SERVER = new URL("https", "bitlife.sytes.net", 3000, "/");
    }

    public boolean post(@NonNull String token, @NonNull String key) throws IOException {

        if (conn != null) {
            conn.disconnect();
        }
        conn = (HttpsURLConnection) URL_SERVER.openConnection();
        try
        {
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(token.length() + key.length());
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            out.write(AppUtils.toBase64((token + SEP + key).getBytes()));
            out.flush();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            byte[] resp = new byte[255];
            int lengthRead = in.read(resp);
            if (lengthRead == -1) {
                System.out.println(" :......: RESPONSE END REACHED :......: ");
            }
            String responseStr = new String(AppUtils.fromBase64(resp));

            return responseStr.contains("200");
        }
        finally {
            conn.disconnect();
        }
    }
}
