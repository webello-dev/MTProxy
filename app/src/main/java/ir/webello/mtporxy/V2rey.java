package ir.webello.mtporxy;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class V2rey {
    private static final String TAG = "Ping";
    private static final int SOCKET_TIMEOUT = 3000; // Timeout for socket connections in milliseconds

    private String link;
    private String country;
    private String ip;
    private boolean isVip;
    private String port;
    private String protocol;

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    private int ping;        // زمان پینگ پروکسی


    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private OkHttpClient client;


    private static final Map<String, Integer> countryFlagMap = new HashMap<>();

    static {
        countryFlagMap.put("United States", R.drawable.unitedstates);
        countryFlagMap.put("Germany", R.drawable.germany);
        countryFlagMap.put("Canada", R.drawable.canada);
        countryFlagMap.put("The Netherlands", R.drawable.netherlands);
        countryFlagMap.put("France", R.drawable.france);
        countryFlagMap.put("Hong Kong", R.drawable.hongkong);
        countryFlagMap.put("Romania", R.drawable.romania);
        countryFlagMap.put("Iran", R.drawable.iran);
        countryFlagMap.put("Spain", R.drawable.spain);
        countryFlagMap.put("Sweden", R.drawable.sweden);
        countryFlagMap.put("Belgium", R.drawable.belgium);
        countryFlagMap.put("Taiwan", R.drawable.taiwan);
        countryFlagMap.put("Russia", R.drawable.russia);
        countryFlagMap.put("United Kingdom", R.drawable.unitedkingdom);
        countryFlagMap.put("Ireland", R.drawable.ireland);
        countryFlagMap.put("Vietnam", R.drawable.vietnam);
        countryFlagMap.put("Finland", R.drawable.finland);
        countryFlagMap.put("China", R.drawable.china);
        countryFlagMap.put("Peru", R.drawable.peru);
        countryFlagMap.put("Singapore", R.drawable.singapore);
        countryFlagMap.put("Japan", R.drawable.japan);
        countryFlagMap.put("India", R.drawable.india);
        countryFlagMap.put("New Zealand", R.drawable.newz);
        countryFlagMap.put("Türkiye", R.drawable.turkey);
        countryFlagMap.put("Czechia", R.drawable.czechrepublic);
        countryFlagMap.put("Costa Rica", R.drawable.costarica);
        countryFlagMap.put("Poland", R.drawable.poland);
        // می‌توانید کشورهای دیگر را نیز اضافه کنید
    }

    public V2rey() {
        client = new OkHttpClient(); // Initialize OkHttpClient once in the constructor
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public String getLink() {
        return link.replace("&amp;", "&");
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void getPingAsync(PingCallback callback) {
        executorService.execute(() -> {
            long ping = testPing();
            new Handler(Looper.getMainLooper()).post(() -> callback.onPingResult((int) ping));
        });
    }

    public interface PingCallback {
        void onPingResult(int ping);
    }

    public boolean isVip(int ping) {
        return ping >= 0 && ping < 50; // VIP is true if ping is positive and less than 100ms
    }

    public int getImageResId() {
        return countryFlagMap.getOrDefault(this.country, R.drawable.unknown);
    }
    public int getImageping(int ping) {
        if (ping >= 0 && ping < 100) {
            return R.drawable.materialsymbolssignalcellularaltrounded;
        } else if (ping >= 100 && ping < 500) {
            return R.drawable.aicroundsignalcellularaltbar;
        } else if (ping == 9999) {
            return R.drawable.baseline_network_check_24;
        } else {
            return R.drawable.icroundsignalcellularaltbar;
        }
    }

    public long testPing() {
        try {
            switch (protocol.toLowerCase()) {
                case "vless":
                case "trojan":
                    return httpPing();
                case "ss":
                    return shadowsocksPing();
                default:
                    return httpPing();
            }
        } catch (IOException e) {
            Log.e(TAG, "Ping test failed", e);
            return -1;
        }
    }

    private long httpPing() throws IOException {
        long startTime = System.currentTimeMillis();
        String url = buildUrl();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return System.currentTimeMillis() - startTime;
            } else {
                return -1;
            }
        }
    }

    private long shadowsocksPing() {
        long startTime = System.currentTimeMillis();
        try (Socket socket = new Socket()) {
            InetSocketAddress socketAddress = new InetSocketAddress(ip, Integer.parseInt(port));
            socket.connect(socketAddress, SOCKET_TIMEOUT);
            return System.currentTimeMillis() - startTime;
        } catch (IOException e) {
            Log.e(TAG, "Shadowsocks ping failed", e);
            return -1;
        }
    }

    private String buildUrl() {
        String baseUrl = protocol.equalsIgnoreCase("vless") || protocol.equalsIgnoreCase("trojan") ? "https://" : "http://";
        return baseUrl + ip + ":" + port;
    }
}
