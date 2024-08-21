package ir.webello.mtporxy;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Proxy {
    private String link;     // لینک پروکسی
    private String country;  // نام کشور پروکسی
    private String ip;       // آدرس IP پروکسی
    private String port;     // پورت پروکسی
    private String protocol; // پروتکل پروکسی (مثلاً HTTP یا SOCKS)
    private int ping;        // زمان پینگ پروکسی
    private boolean isVip;   // آیا پروکسی VIP است یا خیر

    private static final String TAG = "Ping";

    // سرویس اکسکیوتور برای اجرای کارها به صورت غیرهمزمان
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    // نقشه برای نگهداری Resource ID پرچم‌های کشورها
    private static final Map<String, Integer> countryFlagMap = new HashMap<>();

    // بلوک استاتیک برای مقداردهی اولیه کشورها و پرچم‌هایشان
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

    public String getLink() {
        return link != null ? link.replace("&amp;", "&") : "";
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

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean isVip) {
        this.isVip = isVip;
    }

    public int getPing() {
        return ping;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public void getPingAsync(PingCallback callback) {
        executorService.execute(() -> {
            Log.d(TAG, "در حال پینگ: " + ip);
            try (Socket socket = new Socket()) {
                SocketAddress socketAddress = new InetSocketAddress(ip, Integer.parseInt(port));
                long startTime = System.currentTimeMillis();
                socket.connect(socketAddress, 4000); // زمان اتصال 3500 میلی‌ثانیه
                long endTime = System.currentTimeMillis();

                int ping = (int) (endTime - startTime);
                Log.d(TAG, "پینگ به " + ip + ": " + ping + " میلی‌ثانیه");

                // ارسال نتیجه پینگ به روی نخ اصلی
                new Handler(Looper.getMainLooper()).post(() -> callback.onPingResult(ping));

            } catch (IOException e) {
                Log.e(TAG, "خطا در پینگ " + ip, e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onPingResult(-1));
            }
        });
    }

    public interface PingCallback {
        void onPingResult(int ping);
    }

    // بررسی VIP بودن پروکسی بر اساس زمان پینگ
    public boolean isVip(int ping) {
        return ping >= 0 && ping < 50;
    }

    // دریافت Resource ID پرچم کشور بر اساس نام کشور
    public int getImageResId() {
        return countryFlagMap.getOrDefault(this.country, R.drawable.unknown);
    }

    // دریافت Resource ID تصویر وضعیت سیگنال بر اساس زمان پینگ
    public int getImagePing(int ping) {
        if (ping >= 0 && ping < 100) {
            return R.drawable.materialsymbolssignalcellularaltrounded; // سیگنال خوب
        } else if (ping >= 100 && ping < 500) {
            return R.drawable.aicroundsignalcellularaltbar; // سیگنال متوسط
        } else if (ping == 9999) {
            return R.drawable.baseline_network_check_24; // خطا در شبکه
        } else {
            return R.drawable.icroundsignalcellularaltbar; // سیگنال ضعیف
        }
    }

    // متد برای مدیریت منابع
    public static void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
