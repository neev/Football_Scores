package barqsoft.footballscores.sync;

import android.content.Intent;
import android.os.IBinder;
import android.app.Service;

/**
 * Created by neeraja on 10/22/2015.
 */
public class FootballAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private FootballAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new FootballAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
