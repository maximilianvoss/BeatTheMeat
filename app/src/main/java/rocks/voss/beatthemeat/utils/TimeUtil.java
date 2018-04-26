package rocks.voss.beatthemeat.utils;

import android.util.Log;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.zone.ZoneRulesException;

import java.util.TimeZone;

public class TimeUtil {
    public static OffsetDateTime getNow() {
        OffsetDateTime time;
        try {
            time = OffsetDateTime.now();
        } catch (ZoneRulesException e) {
            Log.e("TemperatureCollectionSe", "ZoneRulesException", e);
            TimeZone.setDefault(TimeZone.getTimeZone("Z"));
            time = OffsetDateTime.now();
        }
        return time;
    }
}
