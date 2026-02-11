package com.example.babysitme;

import java.io.Serializable;

public class ServiceProvider implements Serializable {
    private String id;
    private String name;
    private String price;
    private String type;
    private String imageUrl;
    private String description;
    private String phoneNumber;
    private String rating;
    private String experience;
    private String facebookLink;

    // קונסטרקטור ריק חובה עבור Firebase
    public ServiceProvider() {
    }

    // קונסטרקטור מעודכן וגמיש
    public ServiceProvider(String name, String price, String type, String imageUrl,
                           String description, String phoneNumber, Object rating, Object experience, String facebookLink) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.imageUrl = imageUrl;
        this.description = description;
        this.phoneNumber = phoneNumber;
        // המרה אוטומטית ל-String כדי למנוע שגיאות קומפילציה וקריסות
        this.rating = String.valueOf(rating);
        this.experience = String.valueOf(experience);
        this.facebookLink = facebookLink;
    }

    // --- Getters & Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price != null ? price : "0"; }
    public void setPrice(String price) { this.price = price; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRating() { return rating != null ? rating : "0.0"; }
    public void setRating(String rating) { this.rating = rating; }

    public String getExperience() { return experience != null ? experience : "0"; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getFacebookLink() { return facebookLink; }
    public void setFacebookLink(String facebookLink) { this.facebookLink = facebookLink; }
}