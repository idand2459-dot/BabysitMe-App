package com.example.babysitme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText etFullName, etFacebookLink, etPassword;
    RadioGroup rgUserRole;
    Button btnUploadID, btnRegister;
    FirebaseFirestore db;
    FirebaseStorage storage;
    private Uri imageUri;
    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // בדיקה אם המשתמש כבר מחובר (דילוג על הרשמה)
        SharedPreferences prefs = getSharedPreferences("BabysitMePrefs", MODE_PRIVATE);
        String docId = prefs.getString("userDocId", null);

        if (docId != null) {
            db.collection("users").document(docId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String status = documentSnapshot.getString("status");
                    String role = documentSnapshot.getString("role");

                    if ("approved".equals(status)) {
                        if ("provider".equals(role)) {
                            startActivity(new Intent(MainActivity.this, EditProfileActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, ServicesActivity.class));
                        }
                        finish();
                    } else {
                        startActivity(new Intent(MainActivity.this, PendingApprovalActivity.class));
                        finish();
                    }
                }
            });
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // חיבור השדות מה-XML
        etFullName = findViewById(R.id.etFullName);
        etFacebookLink = findViewById(R.id.etFacebookLink);
        etPassword = findViewById(R.id.etPassword);
        rgUserRole = findViewById(R.id.rgUserRole);
        btnUploadID = findViewById(R.id.btnUploadID);
        btnRegister = findViewById(R.id.btnRegister);

        // --- השורה החדשה שהוספנו: מעבר למסך התחברות ---
        findViewById(R.id.tvGoToLogin).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        Toast.makeText(this, "התמונה נבחרה בהצלחה!", Toast.LENGTH_SHORT).show();
                    }
                });

        btnUploadID.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (fullName.equalsIgnoreCase("admin")) {
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                return;
            }

            if (fullName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "נא למלא שם וסיסמה", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "הסיסמה חייבת להכיל לפחות 6 תווים", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                uploadImageAndSaveUser();
            } else {
                Toast.makeText(this, "חובה לבחור תמונה לפני ההרשמה", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void uploadImageAndSaveUser() {
        String name = etFullName.getText().toString().trim();
        String fbLink = etFacebookLink.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        int selectedId = rgUserRole.getCheckedRadioButtonId();
        String role = (selectedId == R.id.rbProvider) ? "provider" : "seeker";

        if (name.isEmpty() || fbLink.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "id_photos/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = storage.getReference().child(fileName);
        Toast.makeText(this, "מעלה נתונים...", Toast.LENGTH_SHORT).show();

        ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                saveToFirestore(name, fbLink, uri.toString(), role, password);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void saveToFirestore(String name, String fbLink, String photoUrl, String role, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("facebookLink", fbLink);
        user.put("imageUrl", photoUrl);
        user.put("status", "pending");
        user.put("role", role);
        user.put("password", password);
        user.put("price", "0");
        user.put("type", "pending");

        db.collection("users").add(user).addOnSuccessListener(documentReference -> {
            getSharedPreferences("BabysitMePrefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isRegistered", true)
                    .putString("userDocId", documentReference.getId())
                    .apply();

            Toast.makeText(MainActivity.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();

            Intent intent = "provider".equals(role) ?
                    new Intent(MainActivity.this, EditProfileActivity.class) :
                    new Intent(MainActivity.this, PendingApprovalActivity.class);

            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}