package com.mycompany.dishcover.Session;

public class AppSession {

    private static AppSession instance;

    private final String username = "john-example";
    private final String fName = "John";
    private final String lName = "Example";
    private boolean brightMode = true;

    private AppSession() {}

    public static AppSession getInstance() {
        if (instance == null) {
            instance = new AppSession();
        }
        return instance;
    }

    // ===== Getter methods for final fields =====
    public String getUsername() {
        return this.username;
    }

    public String getFName() {
        return this.fName;
    }

    public String getLName() {
        return this.lName;
    }

    // ===== Getter and Setter for brightMode =====
    public boolean isBrightMode() {
        return this.brightMode;
    }

    public void setBrightMode(boolean brightMode) {
        this.brightMode = brightMode;
    }
}

    

