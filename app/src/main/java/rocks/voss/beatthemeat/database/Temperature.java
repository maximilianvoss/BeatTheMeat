package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import rocks.voss.androidutils.database.ExportDataSet;
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
public class Temperature implements ExportDataSet {
    @NonNull
    public int thermometerId;
    @NonNull
    public org.threeten.bp.OffsetDateTime time;
    public boolean isActive;
    public int temperature;

    public static Temperature createByThermometerData(ThermometerData thermometerData) {
        Temperature temperature = new Temperature();
        temperature.temperature = (int) thermometerData.getTemperature();
        temperature.thermometerId = thermometerData.getId();
        temperature.time = TimeUtil.getNow();
        temperature.isActive = thermometerData.isActive();
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

    @Override
    public List<String> getValues() {
        List<String> list = new ArrayList<>(2);
        list.add(time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        list.add(String.valueOf(temperature));
        return list;
    }
}
