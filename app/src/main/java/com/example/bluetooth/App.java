/**
 * The App class provides a global context and instance access throughout the application's lifecycle.
 * This class ensures the app context is available for various components, such as activities, services and broadcast receivers.
 */
package com.example.bluetooth;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static App app;

    public static App getInstance() {
        return app;
    }
    public static Context getAppContext() {
        return app.getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
