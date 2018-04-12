package rocks.voss.beatthemeat.threads;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by voss on 24.03.18.
 */
public class JsonDownloadThread extends Thread {
    private URL url = null;
    private final Context context;
    private JsonDownloadThreadCallback jsonDownloadCallback;

    public JsonDownloadThread(Context context, URL url, JsonDownloadThreadCallback jsonDownloadCallback) {
        this.context = context;
        this.url = url;
        this.jsonDownloadCallback = jsonDownloadCallback;
    }

    public JsonDownloadThread(Context context, String url, JsonDownloadThreadCallback jsonDownloadCallback) throws MalformedURLException {
        this(context, new URL(url), jsonDownloadCallback);
    }

    @Override
    public void run() {
        try {
            if ( url != null ) {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(2000);
                urlConnection.setReadTimeout(2000);
                urlConnection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                urlConnection.disconnect();

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
                jsonDownloadCallback.onDownloadComplete(sharedPref, new JSONObject(sb.toString()));
            }
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "IOException", e);
            jsonDownloadCallback.onConnectionFailure(context);
        } catch (JSONException e) {
            Log.e(this.getClass().toString(), "JSONException", e);
        }
    }
}
