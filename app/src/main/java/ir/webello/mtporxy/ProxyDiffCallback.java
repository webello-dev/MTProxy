package ir.webello.mtporxy;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ProxyDiffCallback extends DiffUtil.Callback {
    private final List<Proxy> oldList;
    private final List<Proxy> newList;

    public ProxyDiffCallback(List<Proxy> oldList, List<Proxy> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // فرض می‌کنیم که IP پروکسی‌ها یکتا و منحصر به فرد است
        return oldList.get(oldItemPosition).getIp().equals(newList.get(newItemPosition).getIp());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Proxy oldProxy = oldList.get(oldItemPosition);
        Proxy newProxy = newList.get(newItemPosition);
        return oldProxy.getPing() == newProxy.getPing();
    }
}
