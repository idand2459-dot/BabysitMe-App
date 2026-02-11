package com.example.babysitme;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    private List<ServiceProvider> pendingUsers;
    private OnApproveClickListener listener;

    public interface OnApproveClickListener {
        void onApproveClick(String userId);
    }

    public AdminAdapter(List<ServiceProvider> pendingUsers, OnApproveClickListener listener) {
        this.pendingUsers = pendingUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_provider, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        ServiceProvider user = pendingUsers.get(position);

        // שימוש ב-Getters המוגנים שלנו
        holder.tvName.setText(user.getName());
        holder.tvPrice.setText("מחיר מבוקש: " + user.getPrice() + " ש\"ח");
        holder.tvExperience.setText(user.getExperience() + " שנות ניסיון");
        holder.tvRating.setText("★ " + user.getRating());

        if (user.getDescription() != null && !user.getDescription().isEmpty()) {
            holder.tvDescription.setText(user.getDescription());
        } else {
            holder.tvDescription.setText("אין תיאור זמין");
        }

        // טעינת תמונה עם Glide
        Glide.with(holder.itemView.getContext())
                .load(user.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imgID);

        // כפתור פייסבוק
        holder.btnCheckFB.setOnClickListener(v -> {
            String fbLink = user.getFacebookLink();
            if (fbLink != null && !fbLink.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbLink));
                    v.getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "לינק לא תקין", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(v.getContext(), "לא הוזן קישור", Toast.LENGTH_SHORT).show();
            }
        });

        // כפתור אישור
        holder.btnApprove.setVisibility(View.VISIBLE);
        holder.btnApprove.setOnClickListener(v -> {
            if (user.getId() != null) {
                listener.onApproveClick(user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingUsers.size();
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvExperience, tvRating, tvDescription;
        ImageView imgID;
        Button btnCheckFB, btnApprove;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvProviderName);
            tvPrice = itemView.findViewById(R.id.tvProviderPrice);
            tvExperience = itemView.findViewById(R.id.tvExperience);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDescription = itemView.findViewById(R.id.tvProviderDescription);
            imgID = itemView.findViewById(R.id.imgProvider);
            btnCheckFB = itemView.findViewById(R.id.btnWhatsApp);
            btnApprove = itemView.findViewById(R.id.btnApproveUser);
        }
    }
}
