package ir.webello.mtporxy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ViewModel‌ها برای مدیریت داده‌های پروکسی و VPN
    private ProxyViewModel proxyViewModel;
    private VPNViewModel vpnViewModel;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ایجاد و مقداردهی اولیه ViewModel‌ها
        proxyViewModel = new ViewModelProvider(this).get(ProxyViewModel.class);
        vpnViewModel = new ViewModelProvider(this).get(VPNViewModel.class);

        // بارگذاری داده‌ها هنگام ایجاد اکتیویتی
        fetchData();

        // تنظیم FloatingActionButton برای تازه‌سازی داده‌ها
        FloatingActionButton floatingActionButton = findViewById(R.id.reflesh_bottom);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();  // بارگذاری مجدد داده‌ها
            }
        });

        // پیکربندی BottomAppBar با گوشه‌های گرد
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        MaterialShapeDrawable bottomBarBackground = (MaterialShapeDrawable) bottomAppBar.getBackground();
        bottomBarBackground.setShapeAppearanceModel(
                bottomBarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopRightCorner(com.google.android.material.shape.CornerFamily.ROUNDED, 60f)
                        .setTopLeftCorner(com.google.android.material.shape.CornerFamily.ROUNDED, 60f)
                        .build());

        // تنظیم BottomNavigationView برای تغییر بین Frag_proxy و Frag_vpn
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar_main);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.proxy:
                    // جایگزینی Fragment با Frag_proxy
                    replaceFragment(new Frag_proxy());
                    break;
                case R.id.vpn:
                    // جایگزینی Fragment با Frag_vpn
                    replaceFragment(new Frag_vpn());
                    break;
            }
            return true;
        });

        // انتخاب پیش‌فرض برای BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.proxy);

        // هیچ عملی در هنگام انتخاب دوباره آیتم‌ها انجام نمی‌شود
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
            // هیچ کاری انجام نمی‌شود
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        AppCompatButton button = findViewById(R.id.appCompatButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.openDrawer(navigationView);
                } else {
                    drawerLayout.closeDrawer(navigationView);
                }
            }
        });

        // دسترسی به آیتم‌های سایدبار
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(MainActivity.this, "آیتم 1 انتخاب شد", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "آیتم 2 انتخاب شد", Toast.LENGTH_SHORT).show();
                        break;
                    // اضافه کردن موارد بیشتر بر اساس نیاز
                }
                drawerLayout.closeDrawer(navigationView); // بستن سایدبار پس از انتخاب آیتم
                return true;
            }
        });
    }

    // متد برای بارگذاری داده‌ها از API
    private void fetchData() {
        Toast.makeText(MainActivity.this, "جستجو...", Toast.LENGTH_SHORT).show();
        ApiServiceManager apiServiceManager = new ApiServiceManager();
        final int maxRetries = 5;  // تعداد حداکثر تلاش‌ها برای بارگذاری داده‌ها
        final long retryDelayMillis = 2000;  // تاخیر بین تلاش‌ها به میلی‌ثانیه

        // شروع بارگذاری داده‌ها با مکانیزم تلاش مجدد
        fetchDataWithRetry(apiServiceManager, 0, maxRetries, retryDelayMillis);
    }

    // متد برای بارگذاری داده‌ها با منطق تلاش مجدد
    private void fetchDataWithRetry(ApiServiceManager apiServiceManager, int attempt, int maxRetries, long retryDelayMillis) {
        String TAG = "API";
        apiServiceManager.fetchData(new ApiServiceManager.DataCallback() {
            @Override
            public void onDataLoaded(List<V2rey> newV2reyList, List<Proxy> newProxyList) {
                Log.d(TAG, "پروکسی جدید: " + newProxyList);
                Log.d(TAG, "VPN جدید: " + newV2reyList);

                if (newProxyList != null && !newProxyList.isEmpty()) {
                    // به‌روزرسانی داده‌های پروکسی در ViewModel
                    proxyViewModel.updateProxies(newProxyList);
                    Toast.makeText(MainActivity.this, "پروکسی تنظیم شد", Toast.LENGTH_SHORT).show();
                } else {
                    // مدیریت داده‌های خالی یا نال پروکسی
                    Toast.makeText(MainActivity.this, "جستجو ناموفق", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "داده‌های جدید برای پروکسی وجود ندارد.");
                    if (attempt < maxRetries) {
                        Toast.makeText(MainActivity.this, "در حال تلاش مجدد... پروکسی " + (attempt + 1), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "تلاش مجدد... پروکسی " + (attempt + 1));
                        retryFetchData(apiServiceManager, attempt + 1, maxRetries, retryDelayMillis);
                    } else {
                        Toast.makeText(MainActivity.this, "تعداد تلاش‌های حداکثر برای پروکسی رسید", Toast.LENGTH_SHORT).show();
                    }
                }

                if (newV2reyList != null && !newV2reyList.isEmpty()) {
                    // به‌روزرسانی داده‌های VPN در ViewModel
                    Toast.makeText(MainActivity.this, "VPN تنظیم شد", Toast.LENGTH_SHORT).show();
                    vpnViewModel.updateProxies(newV2reyList);
                } else {
                    // مدیریت داده‌های خالی یا نال VPN
                    Toast.makeText(MainActivity.this, "جستجو ناموفق", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "داده‌های جدید برای VPN وجود ندارد.");
                    if (attempt < maxRetries) {
                        Toast.makeText(MainActivity.this, "در حال تلاش مجدد... VPN " + (attempt + 1), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "تلاش مجدد... VPN " + (attempt + 1));
                        retryFetchData(apiServiceManager, attempt + 1, maxRetries, retryDelayMillis);
                    } else {
                        Toast.makeText(MainActivity.this, "تعداد تلاش‌های حداکثر برای VPN رسید", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "بارگذاری داده‌ها با شکست مواجه شد", t);
                // تلاش مجدد در صورت شکست
                if (attempt < maxRetries) {
                    Log.d(TAG, "تلاش مجدد... Attempt " + (attempt + 1));
                    retryFetchData(apiServiceManager, attempt + 1, maxRetries, retryDelayMillis);
                }
            }
        });
    }

    // متد برای مدیریت منطق تلاش مجدد با تاخیر
    private void retryFetchData(ApiServiceManager apiServiceManager, int nextAttempt, int maxRetries, long retryDelayMillis) {
        new Handler().postDelayed(() -> fetchDataWithRetry(apiServiceManager, nextAttempt, maxRetries, retryDelayMillis), retryDelayMillis);
    }

    // متد کمکی برای جایگزینی Fragment‌ها
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
