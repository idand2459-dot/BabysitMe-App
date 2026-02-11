package com.example.babysitme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ServicesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        // חיבור הכרטיסים מה-XML
        CardView cardBabysitter = findViewById(R.id.cardBabysitter);
        CardView cardDogWalker = findViewById(R.id.cardDogWalker);

        // --- החלק החדש: חיבור כפתור ההתנתקות ---
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // 1. ניקוי הזיכרון המקומי (SharedPreferences)
            SharedPreferences prefs = getSharedPreferences("BabysitMePrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            // 2. הודעה למשתמש
            Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();

            // 3. חזרה למסך הראשי וניקוי היסטוריית המסכים כדי שלא יוכלו לחזור ב"אחורה"
            Intent intent = new Intent(ServicesActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        // --------------------------------------

        // לחיצה על בייביסיטר
        cardBabysitter.setOnClickListener(v -> {
            openProviderList("babysitter");
        });

        // לחיצה על דוגווקר
        cardDogWalker.setOnClickListener(v -> {
            openProviderList("dogwalker");
        });
    }

    private void openProviderList(String type) {
        Intent intent = new Intent(ServicesActivity.this, ProviderListActivity.class);
        intent.putExtra("SERVICE_TYPE", type); // שליחת סוג השירות לסינון
        startActivity(intent);
    }
}