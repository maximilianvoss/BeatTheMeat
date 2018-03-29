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

import rocks.voss.beatthemeat.utils.KeyUtil;

/**
 * Created by voss on 24.03.18.
 */
public class DataCollectionServiceThread extends Thread {

    private Context context;

    public DataCollectionServiceThread(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String webserviceUrl = sharedPref.getString("webserviceURL", "");
            if (webserviceUrl != null && !webserviceUrl.equals("")) {
                URL url = new URL(webserviceUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                JSONObject jObject = new JSONObject(sb.toString());

                reader.close();
                urlConnection.disconnect();

                SharedPreferences.Editor editor = sharedPref.edit();
                JSONArray temperatures = jObject.getJSONArray("temperatures");
                for (int i = 0; i < temperatures.length(); i++) {
                    editor.putInt(KeyUtil.createKey("temperatureCurrent", i), temperatures.getInt(i));
                }
                editor.commit();
            }
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "IOException", e);
        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "JSONException", e);
        }
    }


}
