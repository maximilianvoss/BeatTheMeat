package rocks.voss.beatthemeat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import lombok.Getter;
import lombok.Setter;
import rocks.voss.beatthemeat.activities.MainActivity;

/**
 * Created by voss on 28.03.18.
 */

public class TemperatureUtil {

    @Setter
    @Getter
    private static boolean enabled;

    public static boolean isAlarm(Context context) {
        for (int i = 0; i < MainActivity.getThermometers().size(); i++) {
            if ( isAlarm(context, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAlarm(Context context, int id) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isRange = sharedPref.getBoolean(KeyUtil.createKey("isRange", id), true);
        int temperatureCurrent = sharedPref.getInt(KeyUtil.createKey("temperatureCurrent", id), 100);
        int temperatureMin = sharedPref.getInt(KeyUtil.createKey("temperatureMin", id), 50);
        int temperatureMax = sharedPref.getInt(KeyUtil.createKey("temperatureMax", id), 100);

        if (isRange) {
            if (temperatureCurrent < temperatureMin || temperatureCurrent > temperatureMax) {
                return true;
            }
        } else {
            if (temperatureCurrent >= temperatureMin) {
                return true;
            }
        }
        return false;
    }
}
