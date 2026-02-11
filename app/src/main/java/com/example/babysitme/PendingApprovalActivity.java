package com.example.babysitme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class PendingApprovalActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_approval);

        db = FirebaseFirestore.getInstance();

        // שליפת ה-ID של המשתמש מהזיכרון המקומי
        SharedPreferences prefs = getSharedPreferences("BabysitMePrefs", MODE_PRIVATE);
        String userId = prefs.getString("userDocId", null);

        if (userId != null) {
            // האזנה לשינויים בזמן אמת ב-Firebase (בתיקיית users ובשדה status)
            db.collection("users").document(userId)
                    .addSnapshotListener((snapshot, e) -> {
                        if (e != null) return;
                        if (snapshot != null && snapshot.exists()) {
                            String status = snapshot.getString("status");
                            // אם הסטטוס הפך ל-approved על ידי המנהל
                            if ("approved".equals(status)) {
                                Intent intent = new Intent(this, ServicesActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }

        // שלב 2: הגדרת כפתורי היציאה והחזרה
        Button btnExit = findViewById(R.id.btnExit);
        Button btnBackToRegister = findViewById(R.id.btnBackToRegister);

        // סגירת האפליקציה לגמרי
        btnExit.setOnClickListener(v -> finishAffinity());

        // כפתור Logout שמאפשר לחזור למסך ההרשמה ולהקליד admin
        btnBackToRegister.setOnClickListener(v -> {
            // ניקוי הזיכרון המקומי
            getSharedPreferences("BabysitMePrefs", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // חזרה למסך הראשי
            Intent intent = new Intent(PendingApprovalActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}