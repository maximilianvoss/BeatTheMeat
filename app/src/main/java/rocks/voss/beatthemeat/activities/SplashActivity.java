package rocks.voss.beatthemeat.activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.database.TemperatureDatabase;
import rocks.voss.beatthemeat.services.TemperatureCollectionService;
import rocks.voss.beatthemeat.services.ThermometerSettingsCollectionService;
import rocks.voss.beatthemeat.threads.HistoryTemperatureDeleteThread;

public class SplashActivity extends AppCompatActivity {

    private HistoryTemperatureDeleteThread historyTemperatureDeleteThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        scheduleSplashScreen();
        MainActivity.setTemperatureDatabase(Room.databaseBuilder(getApplicationContext(), TemperatureDatabase.class, "temperatures").build());

        historyTemperatureDeleteThread = new HistoryTemperatureDeleteThread();
        historyTemperatureDeleteThread.start();

        ThermometerSettingsCollectionService.schedule(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_ENABLED, true)) {
            TemperatureCollectionService.schedule(this);
        }
    }

    private void scheduleSplashScreen() {
        int spashScreenDuration = 1000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    historyTemperatureDeleteThread.join();
                } catch (InterruptedException e) {
                    Log.e("SplashActivity", "InterruptedException", e);
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, spashScreenDuration);
    }
}
