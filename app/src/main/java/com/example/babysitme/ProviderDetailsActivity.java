package com.example.babysitme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class ProviderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_details);

        // הוספת חץ חזור למעלה
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("פרטי נותן השירות");
        }

        // חיבור הרכיבים מה-XML (לפי ה-IDs החדשים)
        ImageView imgProfile = findViewById(R.id.ivDetailProfileImage);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPrice = findViewById(R.id.tvDetailPrice);
        TextView tvExperience = findViewById(R.id.tvDetailExperience);
        TextView tvBio = findViewById(R.id.tvDetailBio);
        Button btnWhatsApp = findViewById(R.id.btnContactWhatsApp);

        // קבלת האובייקט המלא מה-Intent
        ServiceProvider provider = (ServiceProvider) getIntent().getSerializableExtra("provider");

        if (provider != null) {
            // הצגת הנתונים
            tvName.setText(provider.getName());
            tvPrice.setText("₪" + provider.getPrice() + " לשעה");
            tvExperience.setText(provider.getExperience() + " שנות ניסיון");
            tvBio.setText(provider.getDescription()); // כאן יוצג התיאור בעברית שהעובד רשם

            // טעינת תמונה
            Glide.with(this)
                    .load(provider.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(imgProfile);

            // לוגיקה לכפתור WhatsApp
            btnWhatsApp.setOnClickListener(v -> {
                openWhatsApp(provider.getPhoneNumber(), provider.getName());
            });
        }
    }

    private void openWhatsApp(String phone, String name) {
        try {
            // יצירת הודעה אוטומטית בעברית
            String message = "שלום " + name + ", הגעתי אלייך דרך אפליקציית BabysitMe!";
            String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + Uri.encode(message);

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp לא מותקן במכשיר", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}