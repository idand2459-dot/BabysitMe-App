package com.example.babysitme;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {

    private List<ServiceProvider> providerList;

    public ProviderAdapter(List<ServiceProvider> providerList) {
        this.providerList = providerList;
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_provider, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        ServiceProvider provider = providerList.get(position);

        // עדכון נתונים לפי ה-IDs ב-XML החדש
        holder.tvName.setText(provider.getName());
        holder.tvPrice.setText("₪" + provider.getPrice() + " לשעה");
        holder.tvRating.setText("⭐ " + provider.getRating());

        // טעינת תמונה לפי ה-ID החדש: imgProvider
        Glide.with(holder.itemView.getContext())
                .load(provider.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(holder.imgProvider);

        // לחיצה למעבר למסך פרטים
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ProviderDetailsActivity.class);
            intent.putExtra("provider", provider);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }

    public static class ProviderViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvRating;
        ImageView imgProvider; // השם עודכן ל-imgProvider לפי ה-XML

        public ProviderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvProviderName);
            tvPrice = itemView.findViewById(R.id.tvProviderPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
            imgProvider = itemView.findViewById(R.id.imgProvider);
        }
    }
}