package rocks.voss.beatthemeat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import lombok.Getter;
import lombok.Setter;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.MainActivity;

/**
 * Created by voss on 28.03.18.
 */

public class AlarmUtil {

    @Setter
    @Getter
    private static boolean enabled;

    public static boolean isAlarm(Context context) {
        for (int i = 0; i < MainActivity.getThermometers().size(); i++) {
            if (isAlarm(context, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAlarm(Context context, int id) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isRange = sharedPref.getBoolean(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_IS_RANGE, id), true);
        int temperatureMin = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MIN, id), 50);
        int temperatureMax = sharedPref.getInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MAX, id), 100);
        int temperatureCurrent = TemperatureUtil.getCurrentTemperature(id);

        if (temperatureCurrent == Constants.FALLBACK_VALUE_TEMPERATURE_NOT_SET) {
            return false;
        }

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
