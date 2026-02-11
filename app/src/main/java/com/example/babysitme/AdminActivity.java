package com.example.babysitme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminAdapter adapter;
    private List<ServiceProvider> pendingList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // כפתור חזרה (סוגר את המסך)
        Button btnBack = findViewById(R.id.btnBackToRegister);
        btnBack.setOnClickListener(v -> finish());

        // כפתור התנתקות (מוחק זיכרון וחוזר להתחלה)
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logoutAdmin());

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.rvAdminPending);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pendingList = new ArrayList<>();
        loadPendingUsers();
    }

    private void logoutAdmin() {
        // מחיקת ה-ID השמור מה-SharedPreferences
        SharedPreferences prefs = getSharedPreferences("BabysitMePrefs", MODE_PRIVATE);
        prefs.edit().clear().apply(); // מנקה את כל הנתונים השמורים במכשיר

        Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();

        // מעבר למסך ההרשמה/התחברות וניקוי ה-Stack של המסכים
        Intent intent = new Intent(AdminActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadPendingUsers() {
        db.collection("users")
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pendingList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ServiceProvider user = document.toObject(ServiceProvider.class);
                            user.setId(document.getId());
                            pendingList.add(user);
                        }
                        adapter = new AdminAdapter(pendingList, this::approveUser);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    private void approveUser(String userId) {
        db.collection("users").document(userId)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminActivity.this, "המשתמש אושר!", Toast.LENGTH_SHORT).show();
                    loadPendingUsers();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminActivity.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}