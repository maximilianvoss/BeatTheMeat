package rocks.voss.beatthemeat.activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.database.TemperatureDatabase;
import rocks.voss.beatthemeat.services.TemperatureCollectionService;
import rocks.voss.beatthemeat.services.ThermometerSettingsCollectionService;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        scheduleSplashScreen();
        MainActivity.setTemperatureDatabase(Room.databaseBuilder(getApplicationContext(), TemperatureDatabase.class, "temperatures").build());
//        MainActivity.getTemperatureDatabase().temperatureDao().deleteAll();

        ThermometerSettingsCollectionService.schedule(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_ENABLED, true)) {
            TemperatureCollectionService.schedule(this);
        }
    }

    private void scheduleSplashScreen() {
        int spashScreenDuration = 3000;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, spashScreenDuration);
    }
}
