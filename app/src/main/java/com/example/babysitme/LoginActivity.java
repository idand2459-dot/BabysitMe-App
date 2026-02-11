package com.example.babysitme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    EditText etName, etPassword;
    Button btnLogin, btnBack;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etLoginName);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBackToRegister);

        btnLogin.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
                return;
            }

            checkUser(name, password);
        });

        // חזרה למסך הרשמה
        btnBack.setOnClickListener(v -> finish());
    }

    private void checkUser(String name, String password) {
        db.collection("users")
                .whereEqualTo("name", name)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // משתמש נמצא!
                        String docId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        String role = queryDocumentSnapshots.getDocuments().get(0).getString("role");
                        String status = queryDocumentSnapshots.getDocuments().get(0).getString("status");

                        // שמירה ב-SharedPrefs כדי שלא יצטרך להתחבר כל פעם
                        getSharedPreferences("BabysitMePrefs", MODE_PRIVATE)
                                .edit()
                                .putString("userDocId", docId)
                                .apply();

                        navigateToMain(role, status);
                    } else {
                        Toast.makeText(this, "שם או סיסמה שגויים", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToMain(String role, String status) {
        Intent intent;
        if ("approved".equals(status)) {
            intent = "provider".equals(role) ?
                    new Intent(this, EditProfileActivity.class) :
                    new Intent(this, ServicesActivity.class);
        } else {
            intent = new Intent(this, PendingApprovalActivity.class);
        }
        startActivity(intent);
        finish();
    }
}