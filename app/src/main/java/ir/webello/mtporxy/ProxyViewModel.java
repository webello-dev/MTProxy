package ir.webello.mtporxy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

// ProxyViewModel.java
public class ProxyViewModel extends ViewModel {
    private MutableLiveData<List<Proxy>> proxyListLiveData;

    public ProxyViewModel() {
        proxyListLiveData = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<Proxy>> getProxyListLiveData() {
        return proxyListLiveData;
    }

    public void updateProxies(List<Proxy> newProxies) {
        proxyListLiveData.setValue(newProxies);
    }
}
