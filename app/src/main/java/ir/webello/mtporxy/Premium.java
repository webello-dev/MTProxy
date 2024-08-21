package ir.webello.mtporxy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class Premium extends AppCompatActivity {
        AppCompatButton appCompatButton,recharge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        appCompatButton=findViewById(R.id.appCompatButton);
        appCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recharge=findViewById(R.id.Brecharge);
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // لینک مورد نظر را در اینجا قرار دهید
                String url = "https://webello.ir/";

                // یک Intent برای باز کردن مرورگر با URL مشخص شده
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));

                // بررسی کنید که آیا دستگاه می‌تواند این Intent را هندل کند
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}