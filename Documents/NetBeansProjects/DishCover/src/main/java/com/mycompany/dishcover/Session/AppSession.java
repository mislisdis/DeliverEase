
package com.mycompany.dishcover.Session;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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

}

    

