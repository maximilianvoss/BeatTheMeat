package rocks.voss.beatthemeat.utils;

import android.content.Context;

import lombok.Getter;
import lombok.Setter;
import rocks.voss.beatthemeat.database.probe.Thermometer;
import rocks.voss.beatthemeat.database.probe.ThermometerCache;
import rocks.voss.beatthemeat.database.temperatures.Temperature;
import rocks.voss.beatthemeat.database.temperatures.TemperatureCache;

/**
 * Created by voss on 28.03.18.
 */

public class AlarmUtil {

    @Setter
    @Getter
    private static boolean enabled;

    public static boolean isAlarm(Context context) {
        for (Thermometer thermometer : ThermometerCache.getThermometers()) {
            if (isAlarm(context, thermometer)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAlarm(Context context, Thermometer thermometer) {
        Temperature temperature = TemperatureCache.getLatestTemperature(thermometer.id);

        if (temperature == null) {
            return false;
        }

        if (thermometer.isRange) {
            return temperature.temperature < thermometer.temperatureMin || temperature.temperature > thermometer.temperatureMax;
        } else {
            return temperature.temperature >= thermometer.temperatureMin;
        }
    }
}
