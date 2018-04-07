package com.example.rajesh.firesignin;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by RajeshRaman on 12-Mar-18.
 */

public class FireSignIn extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
    }
}
