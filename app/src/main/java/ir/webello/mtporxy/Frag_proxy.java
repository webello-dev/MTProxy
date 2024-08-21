package ir.webello.mtporxy;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;

public class Frag_proxy extends Fragment {
    private static final String TAG = "API";
    private RecyclerView recyclerView;
    private Adapter_proxy adapterProxy;
    private ProxyViewModel proxyViewModel;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proxy, container, false);

        recyclerView = view.findViewById(R.id.rv_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));

        proxyViewModel = new ViewModelProvider(requireActivity()).get(ProxyViewModel.class);

        if (proxyViewModel.getProxyListLiveData().getValue() != null && !proxyViewModel.getProxyListLiveData().getValue().isEmpty()) {
            Log.d(TAG, "adapterProxy ok" + proxyViewModel.getProxyListLiveData().getValue());
            adapterProxy = new Adapter_proxy(proxyViewModel.getProxyListLiveData().getValue());
        } else {
            Log.d(TAG, "adapterProxy no");
            adapterProxy = new Adapter_proxy(new ArrayList<>());
        }

        recyclerView.setAdapter(adapterProxy);

        startUpdateTask();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopUpdateTask();
    }

    private void startUpdateTask() {
        updateTask = new Runnable() {
            @Override
            public void run() {
                List<Proxy> proxyList = proxyViewModel.getProxyListLiveData().getValue();

                if (proxyList != null && !proxyList.isEmpty()) {
                    final int[] completedPings = {0};
                    final int totalProxies = proxyList.size();

                    List<Proxy> updatedProxies = new ArrayList<>(proxyList);

                    for (Proxy proxy : updatedProxies) {
                        proxy.getPingAsync(ping -> {
                            proxy.setPing(ping);
                            completedPings[0]++;

                            if (completedPings[0] == totalProxies) {
                                // مرتب‌سازی لیست بر اساس پینگ با یک Comparator واضح
                                Collections.sort(updatedProxies, new Comparator<Proxy>() {
                                    @Override
                                    public int compare(Proxy p1, Proxy p2) {
                                        if (p1.getPing() == -1 && p2.getPing() == -1) return 0;
                                        if (p1.getPing() == -1) return 1;
                                        if (p2.getPing() == -1) return -1;
                                        return Integer.compare(p1.getPing(), p2.getPing());
                                    }
                                });

                                adapterProxy.updateData(updatedProxies);
                                proxyViewModel.updateProxies(updatedProxies);
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "No proxies available");
                }

                handler.postDelayed(this, 10000);
            }
        };

        handler.post(updateTask);
    }

    private void stopUpdateTask() {
        if (handler != null && updateTask != null) {
            handler.removeCallbacks(updateTask);
        }
    }
}