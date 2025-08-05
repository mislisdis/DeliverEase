package com.mycompany.dishcover.Util;

public class Session {
    private static int userId;
    private static String currentUsername;

    public static void setSession(int id, String username) {
        userId = id;
        currentUsername = username;
    }

    public static int getUserId() {
        return userId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void clear() {
        userId = 0;
        currentUsername = null;
    }
}
