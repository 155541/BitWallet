package revolhope.splanes.com.bitwallet.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import revolhope.splanes.com.bitwallet.crypto.Cryptography;


public class HttpConn {

    private static final String SEP = "hdheKK0dd";
    private URL URL_SERVER;
    private HttpURLConnection conn;
    private WifiManager wifiManager;

    public HttpConn(@NonNull Context context) throws IOException {
        URL_SERVER = new URL("http", "bitlife.sytes.net", 3000, "/");
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) throw new IOException("Error getting device mac address...");
    }

    public boolean post(@NonNull String token, @NonNull String key) throws IOException {

        if (conn != null) {
            conn.disconnect();
        }
        conn = (HttpURLConnection) URL_SERVER.openConnection();
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            JSONObject requestBody = new JSONObject();
            JSONObject headers = new JSONObject();

            headers.put("Timestamp", Calendar.getInstance().getTimeInMillis());

            try {
                Cryptography cryptography = new Cryptography();
                @SuppressLint("HardwareIds")
                String auth = cryptography.sha256(wifiManager.getConnectionInfo().getMacAddress());
                headers.put("Auth", auth);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            headers.put("Request-Ip", getIp());
//            TODO: TO CONTINUE
            requestBody.put("headers", headers);
            requestBody.put("token", token);
            requestBody.put("content", key);

            out.writeBytes(requestBody.toString());
            out.flush();

            InputStream in = new BufferedInputStream(conn.getInputStream());
            byte[] resp = new byte[255];
            int r = in.read(resp);
            if (r > 0) {
                String responseStr = new String(AppUtils.fromBase64(resp));
                return responseStr.contains("200");
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } finally {
            conn.disconnect();
        }
    }

    // TODO: COPY-PASTE METHOD, CHECK IT!
    private String getIp() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (isIPv4) return sAddr;
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }
}
