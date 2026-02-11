package com.example.babysitme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    EditText etPhone, etBio, etPrice, etExperience;
    RadioGroup rgServiceType;
    Button btnSaveProfile, btnSelectImage, btnLogout;
    ImageView ivProfilePicture;

    FirebaseFirestore db;
    FirebaseStorage storage;
    private Uri imageUri;
    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // אתחול Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // חיבור רכיבים מה-XML
        etPhone = findViewById(R.id.etPhone);
        etBio = findViewById(R.id.etBio);
        etPrice = findViewById(R.id.etPrice);
        etExperience = findViewById(R.id.etExperience);
        rgServiceType = findViewById(R.id.rgServiceType);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnLogout = findViewById(R.id.btnLogout);

        // הגדרת בחירת תמונה מהגלריה
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        ivProfilePicture.setImageURI(uri);
                    }
                });

        btnSelectImage.setOnClickListener(v -> mGetContent.launch("image/*"));

        // לוגיקת התנתקות
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("BabysitMePrefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // לחיצה על שמירה
        btnSaveProfile.setOnClickListener(v -> validateAndUpload());
    }

    private void validateAndUpload() {
        String phone = etPhone.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String experience = etExperience.getText().toString().trim();

        // בדיקת תקינות שדות
        if (phone.isEmpty() || bio.isEmpty() || price.isEmpty() || experience.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "חובה לבחור תמונת פרופיל", Toast.LENGTH_SHORT).show();
            return;
        }

        // העלאת התמונה ל-Firebase Storage
        String fileName = "profile_pics/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = storage.getReference().child(fileName);

        Toast.makeText(this, "מעלה נתונים...", Toast.LENGTH_SHORT).show();

        ref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                // לאחר שהתמונה עלתה, נשמור את כל השאר ב-Firestore
                saveToFirestore(phone, bio, price, experience, uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "שגיאה בהעלאת תמונה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveToFirestore(String phone, String bio, String price, String experience, String photoUrl) {
        SharedPreferences prefs = getSharedPreferences("BabysitMePrefs", MODE_PRIVATE);
        String userId = prefs.getString("userDocId", null);

        if (userId == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }

        // קביעת סוג השירות
        int selectedServiceId = rgServiceType.getCheckedRadioButtonId();
        String selectedService = (selectedServiceId == R.id.rbDogwalker) ? "dogwalker" : "babysitter";

        // הכנת הנתונים לעדכון
        Map<String, Object> updates = new HashMap<>();
        updates.put("phoneNumber", phone);
        updates.put("description", bio);
        updates.put("price", price);
        updates.put("experience", experience);
        updates.put("imageUrl", photoUrl); // ה-URL של התמונה החדשה
        updates.put("type", selectedService);
        updates.put("status", "pending"); // החזרת הסטטוס לממתין כדי שהמנהל יאשר את הפרופיל החדש

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "הפרופיל נשלח לאישור מנהל!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfileActivity.this, PendingApprovalActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בשמירת נתונים: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}