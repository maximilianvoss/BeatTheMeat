package rocks.voss.beatthemeat.services;

import android.content.Context;
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

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.utils.KeyUtil;

/**
 * Created by voss on 24.03.18.
 */
public class DataCollectionServiceThread extends Thread {

    private final Context context;

    public DataCollectionServiceThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String webserviceUrl = sharedPref.getString(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_URL, "");
            if (!webserviceUrl.equals("")) {
                URL url = new URL(webserviceUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                JSONObject jObject = new JSONObject(sb.toString());

                reader.close();
                urlConnection.disconnect();

                SharedPreferences.Editor editor = sharedPref.edit();
                JSONArray temperatures = jObject.getJSONArray(Constants.JSON_TEMPERATURES_OBJECT);
                for (int i = 0; i < temperatures.length(); i++) {
                    editor.putInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CURRENT, i), temperatures.getInt(i));
                }
                editor.apply();
            }
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "IOException", e);
        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "JSONException", e);
        }
    }


}
