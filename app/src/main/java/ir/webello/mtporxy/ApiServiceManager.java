package ir.webello.mtporxy;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class ApiServiceManager {

    private static final String BASE_URL = "######";
    private ApiService apiService;

    private List<V2rey> v2reyList;
    private List<Proxy> proxyList;

    public ApiServiceManager() {
        // ایجاد OkHttpClient با تایم اوت 15 ثانیه
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS) // تایم اوت برای اتصال
                .readTimeout(15, TimeUnit.SECONDS) // تایم اوت برای خواندن داده‌ها
                .writeTimeout(15, TimeUnit.SECONDS) // تایم اوت برای نوشتن داده‌ها
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // تنظیم OkHttpClient در Retrofit
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void fetchData(final DataCallback callback) {
        // اجرا در یک Thread جدید
        new Thread(new Runnable() {
            @Override
            public void run() {
                apiService.getData(0).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            v2reyList = response.body().getV2rey();
                            proxyList = response.body().getProxy();
                            callback.onDataLoaded(v2reyList, proxyList);
                        } else {
                            callback.onFailure(new Throwable("Failed to load data"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        callback.onFailure(t);
                    }
                });
            }
        }).start();
    }

    public interface DataCallback {
        void onDataLoaded(List<V2rey> v2reyList, List<Proxy> proxyList);
        void onFailure(Throwable t);
    }
}
