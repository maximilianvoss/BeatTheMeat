package rocks.voss.beatthemeat.services;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

/**
 * Created by voss on 30.03.18.
 */

public interface JsonDownloadCallbackInterface {
    void onDownloadComplete(SharedPreferences sharedPref, JSONObject jsonObject);
    void onConnectionFailure(Context context);
}
