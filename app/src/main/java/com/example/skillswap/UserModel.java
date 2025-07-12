package com.example.skillswap;
public class UserModel {
    private String name, offeredSkills, wantedSkills;
    private int rating;

    public UserModel(String name, String offered, String wanted, int rating) {
        this.name = name;
        this.offeredSkills = offered;
        this.wantedSkills = wanted;
        this.rating = rating;
    }

    public String getName() { return name; }
    public String getOfferedSkills() { return offeredSkills; }
    public String getWantedSkills() { return wantedSkills; }
    public int getRating() { return rating; }
}
