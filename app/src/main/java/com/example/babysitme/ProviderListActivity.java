package com.example.babysitme; // מוודא שהחבילה קיימת

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProviderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProviderAdapter adapter;
    private List<ServiceProvider> providerList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_list);

        db = FirebaseFirestore.getInstance();

        // הגדרת Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String serviceType = getIntent().getStringExtra("SERVICE_TYPE");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (serviceType != null && serviceType.equals("babysitter")) {
                getSupportActionBar().setTitle("בייביסיטרים זמינים");
            } else {
                getSupportActionBar().setTitle("דוג-ווקרים זמינים");
            }
        }

        // הגדרת RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProviders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        providerList = new ArrayList<>();
        adapter = new ProviderAdapter(providerList);
        recyclerView.setAdapter(adapter);

        // טעינת נתונים
        loadProvidersData(serviceType);
    }

    private void loadProvidersData(String type) {
        db.collection("users")
                .whereEqualTo("type", type)
                .whereEqualTo("status", "approved")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    providerList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ServiceProvider provider = doc.toObject(ServiceProvider.class);
                        // אם אין לך פונקציית setId במחלקה ServiceProvider, השורה הזו תמחק
                        providerList.add(provider);
                    }
                    addMockData(type);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בטעינה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    addMockData(type);
                    adapter.notifyDataSetChanged();
                });
    }

    private void addMockData(String type) {
        if (type != null && type.equals("babysitter")) {
            providerList.add(new ServiceProvider("נועה לוי", "55", "babysitter", "https://i.pravatar.cc/150?u=1",
                    "בייביסיטר מנוסה, סבלנית מאוד ואוהבת לשחק עם ילדים.", "0501234567", 4.9, 5, ""));
        } else {
            providerList.add(new ServiceProvider("רועי ישראלי", "40", "dogwalker", "https://i.pravatar.cc/150?u=10",
                    "אוהב כלבים מושבע, מטייל גם עם כלבים גדולים ואנרגטיים.", "0509998877", 5.0, 4, ""));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}