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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_vpn extends RecyclerView.Adapter<Adapter_vpn.FreeViewHolder> {

    private List<V2rey> vpnList;
    private Map<V2rey, Integer> pingMap = new HashMap<>();

    public Adapter_vpn(List<V2rey> vpnList) {
        this.vpnList = vpnList;
    }
    private void initializePingMap(List<V2rey> proxies) {
        for (V2rey v2rey : proxies) {
            pingMap.put(v2rey, -1); // Initialize with -1 (unknown ping)
        }
    }
    @NonNull
    @Override
    public FreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_vpn_free, parent, false);
        return new FreeViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull Adapter_vpn.FreeViewHolder holder, int position) {
        V2rey v2rey = vpnList.get(position);
        holder.bind(v2rey);

        // اضافه کردن مارژین 80 پیکسل به آخرین آیتم
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();

        if (position == vpnList.size() - 1) {
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
        return vpnList.size();
    }

    public void updateData(List<V2rey> newvpnList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new V2reyDiffCallback(this.vpnList, newvpnList));
        this.vpnList.clear();
        this.vpnList.addAll(newvpnList);
        diffResult.dispatchUpdatesTo(this); // به‌روزرسانی فقط آیتم‌های تغییر یافته
    }
    public void addData(List<V2rey> newList) {
        this.vpnList.addAll(newList);
        notifyDataSetChanged();
    }
    public static class FreeViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_itme_vpn_free;
        private ImageView imgPing_itme_vpn_free;
        private ImageView taj_item_vpn;
        private TextView country_itme_vpn_free;
        private TextView ping_itme_vpn_free;
        private TextView tayp_itme_vpn;
        private ConstraintLayout con_item_vpn;

        public FreeViewHolder(@NonNull View itemView) {
            super(itemView);
            img_itme_vpn_free = itemView.findViewById(R.id.img_itme_vpn_free);
            imgPing_itme_vpn_free = itemView.findViewById(R.id.imgPing_itme_vpn_free);
            taj_item_vpn = itemView.findViewById(R.id.taj_item_vpn);
            country_itme_vpn_free = itemView.findViewById(R.id.country_itme_vpn_free);
            ping_itme_vpn_free = itemView.findViewById(R.id.ping_itme_vpn_free);
            tayp_itme_vpn = itemView.findViewById(R.id.tayp_itme_vpn);
            con_item_vpn = itemView.findViewById(R.id.con_item_vpn);
        }

        public void bind(V2rey vpn) {
            taj_item_vpn.setVisibility(View.GONE);
            ping_itme_vpn_free.setText(String.valueOf(vpn.getPing()));
            imgPing_itme_vpn_free.setImageResource(vpn.getImageping(vpn.getPing()));
            Picasso.get()
                    .load(vpn.getImageResId())
                    .placeholder(R.drawable.unknown)
                    .error(R.drawable.unknown)
                    .resize(70, 70)
                    .centerCrop()
                    .into(img_itme_vpn_free);

            Picasso.get()
                    .load(vpn.getImageping(9999)) // مقدار پیش‌فرض
                    .placeholder(R.drawable.baseline_network_check_24)
                    .error(R.drawable.baseline_network_check_24)
                    .resize(30, 30)
                    .centerCrop()
                    .into(imgPing_itme_vpn_free);

            country_itme_vpn_free.setText(vpn.getCountry());

                    boolean vipStatus = vpn.isVip(vpn.getPing());
                    vpn.setVip(vipStatus);

                    // تغییر ویوهای مربوطه در همان ViewHolder
                    if (vipStatus) {
                        tayp_itme_vpn.setBackgroundResource(R.drawable.backgrand_item_p_v_vip_type);
                        taj_item_vpn.setVisibility(View.VISIBLE);
                    } else {
                        tayp_itme_vpn.setBackgroundResource(R.drawable.backgrand_item_p_v_free_type);
                        taj_item_vpn.setVisibility(View.GONE);
                    }

                    // تنظیم رویدادها برای دکمه‌ها
                    setButtonActions(vpn, vipStatus);
        }

        private void setButtonActions(V2rey vpn, boolean isVip) {
            if (isVip) {
                con_item_vpn.setOnClickListener(view -> {
                    Intent i = new Intent(view.getContext(), Premium.class);
                    view.getContext().startActivity(i);
                });

            } else {
                con_item_vpn.setOnClickListener(view -> {
                    String link = vpn.getLink();
                    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("vpn link", link);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(view.getContext(), "Link copied", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}
