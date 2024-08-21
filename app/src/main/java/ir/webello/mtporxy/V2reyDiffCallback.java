package ir.webello.mtporxy;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class V2reyDiffCallback extends DiffUtil.Callback {
    private final List<V2rey> oldList;
    private final List<V2rey> newList;

    public V2reyDiffCallback(List<V2rey> oldList, List<V2rey> newList) {
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
        V2rey oldV2rey = oldList.get(oldItemPosition);
        V2rey newV2rey = newList.get(newItemPosition);
        return oldV2rey.getPing() == newV2rey.getPing();
    }
}
