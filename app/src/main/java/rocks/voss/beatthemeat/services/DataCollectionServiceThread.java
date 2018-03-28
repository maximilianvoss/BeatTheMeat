package rocks.voss.beatthemeat.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.Setter;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.utils.KeyUtil;
import rocks.voss.beatthemeat.utils.TemperatureUtil;

/**
 * Created by voss on 24.03.18.
 */
public class DataCollectionServiceThread extends Thread {

    private Context context;

    @Setter
    private static boolean notificationActive = false;

    public DataCollectionServiceThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            String result = null;

            URL url = new URL("https://maximilian.voss.rocks/etc/test.json");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();

            JSONObject jObject = new JSONObject(result);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            JSONArray temperatures = jObject.getJSONArray("temperatures");
            for (int i = 0; i < temperatures.length(); i++) {
                editor.putInt(KeyUtil.createKey("temperatureCurrent", i), temperatures.getInt(i));
            }
            editor.commit();
            MainActivity.refreshThermometers();
            if ( TemperatureUtil.isAlarm(context)) {
                activateNotification();
            }
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "IOException", e);
        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "JSONException", e);
        }
    }

    private void activateNotification() {
        if ( TemperatureUtil.isEnabled() && !notificationActive) {
            notificationActive = true;
            Intent intent = new Intent(context, NotficationSoundService.class);
            context.startService(intent);
        }
    }
}
