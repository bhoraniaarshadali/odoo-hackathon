package com.example.skillswap;

import org.json.JSONObject;

public class UserModel {
    private String id, name, email, offeredSkills, wantedSkills, location, availability;
    private int rating;
    private boolean isPublic;

    public UserModel(String name, String offered, String wanted, int rating) {
        this.name = name;
        this.offeredSkills = offered;
        this.wantedSkills = wanted;
        this.rating = rating;
    }
    
    // Constructor for API data
    public UserModel(String id, String name, String email, String offeredSkills, 
                    String wantedSkills, String location, String availability, 
                    int rating, boolean isPublic) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.offeredSkills = offeredSkills;
        this.wantedSkills = wantedSkills;
        this.location = location;
        this.availability = availability;
        this.rating = rating;
        this.isPublic = isPublic;
    }
    
    // Parse from JSON
    public static UserModel fromJson(JSONObject json) {
        try {
            return new UserModel(
                json.optString("id", ""),
                json.optString("name", ""),
                json.optString("email", ""),
                json.optString("offered_skills", ""),
                json.optString("wanted_skills", ""),
                json.optString("location", ""),
                json.optString("availability", ""),
                json.optInt("rating", 0),
                json.optBoolean("is_public", true)
            );
        } catch (Exception e) {
            return null;
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getOfferedSkills() { return offeredSkills; }
    public String getWantedSkills() { return wantedSkills; }
    public String getLocation() { return location; }
    public String getAvailability() { return availability; }
    public int getRating() { return rating; }
    public boolean isPublic() { return isPublic; }
    
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setOfferedSkills(String offeredSkills) { this.offeredSkills = offeredSkills; }
    public void setWantedSkills(String wantedSkills) { this.wantedSkills = wantedSkills; }
    public void setLocation(String location) { this.location = location; }
    public void setAvailability(String availability) { this.availability = availability; }
    public void setRating(int rating) { this.rating = rating; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
}
