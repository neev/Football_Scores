package barqsoft.footballscores;

import android.content.Context;

/**
 * Created by neeraja on 10/23/2015.
 */
public class MyApp extends android.app.Application {
    private static MyApp instance;
    public MyApp() {
        instance = this;
    }
    public static Context getContext() {
        return instance;
    }
}