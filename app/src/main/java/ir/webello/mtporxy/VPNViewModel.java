package ir.webello.mtporxy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

// ProxyViewModel.java
public class VPNViewModel extends ViewModel {
    private MutableLiveData<List<V2rey>> proxyListLiveData;

    public VPNViewModel() {
        proxyListLiveData = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<List<V2rey>> getProxyListLiveData() {
        return proxyListLiveData;
    }

    public void updateProxies(List<V2rey> newProxies) {
        proxyListLiveData.setValue(newProxies);
    }
}
