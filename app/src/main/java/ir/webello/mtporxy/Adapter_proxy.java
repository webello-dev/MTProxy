package ir.webello.mtporxy;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_proxy extends RecyclerView.Adapter<Adapter_proxy.FreeViewHolder> {

    private List<Proxy> proxyList;
    private Map<Proxy, Integer> pingMap = new HashMap<>();

    public Adapter_proxy(List<Proxy> proxyList) {
        this.proxyList = proxyList;
        initializePingMap(proxyList);
    }

    private void initializePingMap(List<Proxy> proxies) {
        for (Proxy proxy : proxies) {
            pingMap.put(proxy, -1); // Initialize with -1 (unknown ping)
        }
    }

    @NonNull
    @Override
    public FreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_proxy_free, parent, false);
        return new FreeViewHolder(view, this); // Pass 'this' to the ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull FreeViewHolder holder, int position) {
        Proxy proxy = proxyList.get(position);
        holder.bind(proxy);

        // اضافه کردن مارژین 80 پیکسل به آخرین آیتم
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();

        if (position == proxyList.size() - 1) {
            // اگر آخرین آیتم است، مارژین پایین را 80 پیکسل تنظیم کنید
            layoutParams.bottomMargin = (int) (80 * holder.itemView.getContext().getResources().getDisplayMetrics().density);
        } else {
            // اگر آخرین آیتم نیست، مارژین پایین را صفر تنظیم کنید
            layoutParams.bottomMargin = 13;
        }

        holder.itemView.setLayoutParams(layoutParams);
    }


    @Override
    public int getItemCount() {
        return proxyList.size();
    }

    public void updateData(List<Proxy> newProxyList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ProxyDiffCallback(this.proxyList, newProxyList));
        this.proxyList.clear();
        this.proxyList.addAll(newProxyList);
        diffResult.dispatchUpdatesTo(this); // به‌روزرسانی فقط آیتم‌های تغییر یافته
    }


    public void addData(List<Proxy> newList) {
        this.proxyList.addAll(newList);
        for (Proxy proxy : newList) {
            pingMap.put(proxy, -1); // Initialize new proxies with -1
        }
        notifyDataSetChanged();
    }

    public static class FreeViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_itme_proxy_free;
        private ImageView imgPing_itme_proxy_free;
        private ImageView taj_item_proxr;
        private TextView country_itme_proxy_free;
        private TextView ping_itme_proxy_free;
        private TextView tayp_itme_proxy;
        private AppCompatButton send_itme_proxy_free;
        private AppCompatButton copy_itme_proxy_free;

        private Adapter_proxy adapter; // Reference to the adapter

        public FreeViewHolder(@NonNull View itemView, Adapter_proxy adapter) {
            super(itemView);
            this.adapter = adapter; // Set adapter reference
            img_itme_proxy_free = itemView.findViewById(R.id.img_itme_proxy_free);
            imgPing_itme_proxy_free = itemView.findViewById(R.id.imgPing_itme_proxy_free);
            taj_item_proxr = itemView.findViewById(R.id.taj_item_proxr);
            country_itme_proxy_free = itemView.findViewById(R.id.country_itme_proxy_free);
            ping_itme_proxy_free = itemView.findViewById(R.id.ping_itme_proxy_free);
            tayp_itme_proxy = itemView.findViewById(R.id.tayp_itme_vpn);
            send_itme_proxy_free = itemView.findViewById(R.id.send_itme_proxy_free);
            copy_itme_proxy_free = itemView.findViewById(R.id.copy_itme_proxy_free);
        }

        public void bind(Proxy proxy) {
            taj_item_proxr.setVisibility(View.GONE);
            ping_itme_proxy_free.setText(String.valueOf(proxy.getPing()));
            imgPing_itme_proxy_free.setImageResource(proxy.getImagePing(proxy.getPing()));

            Picasso.get()
                    .load(proxy.getImageResId())
                    .placeholder(R.drawable.unknown)
                    .error(R.drawable.unknown)
                    .resize(70, 70)
                    .centerCrop()
                    .into(img_itme_proxy_free);

            Picasso.get()
                    .load(proxy.getImagePing(proxy.getPing())) // مقدار پیش‌فرض
                    .placeholder(R.drawable.baseline_network_check_24)
                    .error(R.drawable.baseline_network_check_24)
                    .resize(70, 70)
                    .centerCrop()
                    .into(imgPing_itme_proxy_free);

            country_itme_proxy_free.setText(proxy.getCountry());


                    boolean vipStatus = proxy.isVip(proxy.getPing());
                    proxy.setVip(vipStatus);

                    // تغییر ویوهای مربوطه در همان ViewHolder
                    if (vipStatus) {
                        tayp_itme_proxy.setBackgroundResource(R.drawable.backgrand_item_p_v_vip_type);
                        taj_item_proxr.setVisibility(View.VISIBLE);
                    } else {
                        tayp_itme_proxy.setBackgroundResource(R.drawable.backgrand_item_p_v_free_type);
                        taj_item_proxr.setVisibility(View.GONE);
                    }
                    setButtonActions(proxy, vipStatus);

        }

        private void setButtonActions(Proxy proxy, boolean isVip) {
            if (isVip) {
                send_itme_proxy_free.setOnClickListener(view -> {
                    Intent i = new Intent(view.getContext(), Premium.class);
                    view.getContext().startActivity(i);
                });

                copy_itme_proxy_free.setOnClickListener(view -> {
                    Intent i = new Intent(view.getContext(), Premium.class);
                    view.getContext().startActivity(i);
                });
            } else {
                send_itme_proxy_free.setOnClickListener(view -> {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(proxy.getLink()));
                    view.getContext().startActivity(i);
                });

                copy_itme_proxy_free.setOnClickListener(view -> {
                    String link = proxy.getLink();
                    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("proxy link", link);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(view.getContext(), "Link copied", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}
