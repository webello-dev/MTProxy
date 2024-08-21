package ir.webello.mtporxy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Frag_vpn extends Fragment {
    private static final String TAG = "API";
    private RecyclerView recyclerView;
    private Adapter_vpn adaptervpn;
    private VPNViewModel vpnViewModel;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vpn, container, false);

        recyclerView = view.findViewById(R.id.rv_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));

        vpnViewModel = new ViewModelProvider(requireActivity()).get(VPNViewModel.class);

        // بررسی داده‌ها در ViewModel و استفاده از آن‌ها
        if (vpnViewModel.getProxyListLiveData().getValue() != null && !vpnViewModel.getProxyListLiveData().getValue().isEmpty()) {
            Log.d(TAG, "adaptervpn ok" + vpnViewModel.getProxyListLiveData().getValue());
            adaptervpn = new Adapter_vpn(vpnViewModel.getProxyListLiveData().getValue());
        } else {
            Log.d(TAG, "adaptervpn no");
            adaptervpn = new Adapter_vpn(new ArrayList<>());
        }
        recyclerView.setAdapter(adaptervpn);

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
                List<V2rey> vpnList = vpnViewModel.getProxyListLiveData().getValue();

                if (vpnList != null && !vpnList.isEmpty()) {
                    final int[] completedPings = {0};
                    final int totalProxies = vpnList.size();

                    List<V2rey> updatedProxies = new ArrayList<>(vpnList);

                    for (V2rey proxy : updatedProxies) {
                        proxy.getPingAsync(ping -> {
                            proxy.setPing(ping);
                            completedPings[0]++;

                            if (completedPings[0] == totalProxies) {
                                // مرتب‌سازی لیست بر اساس پینگ با یک Comparator واضح
                                Collections.sort(updatedProxies, new Comparator<V2rey>() {
                                    @Override
                                    public int compare(V2rey p1, V2rey p2) {
                                        if (p1.getPing() == -1 && p2.getPing() == -1) return 0;
                                        if (p1.getPing() == -1) return 1;
                                        if (p2.getPing() == -1) return -1;
                                        return Integer.compare(p1.getPing(), p2.getPing());
                                    }
                                });

                                adaptervpn.updateData(updatedProxies);
                                vpnViewModel.updateProxies(updatedProxies);
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
