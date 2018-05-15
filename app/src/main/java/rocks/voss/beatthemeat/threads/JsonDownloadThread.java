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
import java.util.ArrayList;
import java.util.List;

import rocks.voss.beatthemeat.enums.NotificationEnum;
import rocks.voss.beatthemeat.utils.NotificationUtil;

/**
 * Created by voss on 24.03.18.
 */
public class JsonDownloadThread extends Thread {
    private List<URL> urls = new ArrayList<>();
    private final Context context;
    private JsonDownloadThreadCallback jsonDownloadCallback;

    public JsonDownloadThread(Context context, JsonDownloadThreadCallback jsonDownloadCallback) {
        this.context = context;
        this.jsonDownloadCallback = jsonDownloadCallback;
    }

    public void addUrl(String url) throws MalformedURLException {
        if (url != null && !url.equals("")) {
            addUrl(new URL(url));
        }
    }

    public void addUrl(URL url) {
        urls.add(url);
    }

    public void addUrls(URL... urls) {
        for (URL url : urls) {
            addUrl(url);
        }
    }

    public void addUrls(String... urls) throws MalformedURLException {
        for (String url : urls) {
            addUrl(url);
        }
    }

    @Override
    public void run() {
        for (URL url : urls) {
            HttpURLConnection urlConnection = establishConnection(url);
            if (urlConnection == null) {
                continue;
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                NotificationUtil.stopNotification(context, NotificationEnum.WebserviceAlarm);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);

                jsonDownloadCallback.onDownloadComplete(sharedPref, new JSONObject(sb.toString()));
                break;
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "IOException", e);
                jsonDownloadCallback.onConnectionFailure(context);
            } catch (JSONException e) {
                Log.e(this.getClass().toString(), "JSONException", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }
    }

    private HttpURLConnection establishConnection(URL url) {
        try {
            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(2000);
            urlConnection.setReadTimeout(2000);
            urlConnection.connect();
            return urlConnection;
        } catch (IOException e) {
            Log.e(this.getClass().toString(), "IOException", e);
            return null;
        }
    }

    public interface JsonDownloadThreadCallback {
        void onDownloadComplete(SharedPreferences sharedPref, JSONObject jsonObject);

        void onConnectionFailure(Context context);
    }
}
