package rocks.voss.beatthemeat.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.database.TemperatureDatabase;
import rocks.voss.beatthemeat.services.TemperatureCollectionService;
import rocks.voss.beatthemeat.services.ThermometerSettingsCollectionService;
import rocks.voss.beatthemeat.threads.DatabaseDeleteThread;

public class SplashActivity extends Activity {

    private DatabaseDeleteThread databaseDeleteThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        scheduleSplashScreen();

        DatabaseUtil databaseUtil = new DatabaseUtil();
        databaseUtil.openDatabase(getApplicationContext(), TemperatureDatabase.class, Constants.DATABASE_NAME);

        databaseDeleteThread = new DatabaseDeleteThread();
        databaseDeleteThread.start();

        ThermometerSettingsCollectionService.schedule(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_ENABLED, true)) {
            TemperatureCollectionService.schedule(this);
        }
    }

    private void scheduleSplashScreen() {
        int spashScreenDuration = 1000;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                databaseDeleteThread.join();
            } catch (InterruptedException e) {
                Log.e("SplashActivity", "InterruptedException", e);
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }, spashScreenDuration);
    }
}
