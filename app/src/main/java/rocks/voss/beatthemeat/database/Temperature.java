package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import rocks.voss.beatthemeat.thermometer.ThermometerData;
import rocks.voss.beatthemeat.utils.TimeUtil;


/**
 * Created by voss on 08.04.18.
 */

@Entity(
        primaryKeys = {"thermometerId", "time"},
        indices = {
                @Index(value = {"thermometerId", "time"})
        }
)
public class Temperature {
    @NonNull
    public org.threeten.bp.OffsetDateTime time;

    @NonNull
    public int thermometerId;

    public int temperature;

    public static Temperature createByThermometerData(ThermometerData thermometerData) {
        if (!thermometerData.isActive()) {
            return null;
        }
        Temperature temperature = new Temperature();
        temperature.temperature = (int) thermometerData.getTemperature();
        temperature.thermometerId = thermometerData.getId();
        temperature.time = TimeUtil.getNow();
        return temperature;
    }

    public boolean equals(Temperature temperature) {
        if (temperature == null) {
            return false;
        }
        if (this.temperature == temperature.temperature) {
            return true;
        }
        return false;
    }
}
