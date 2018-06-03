package rocks.voss.beatthemeat.utils;

import lombok.Getter;
import lombok.Setter;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.database.TemperatureDatabase;

public class DatabaseUtil {

    @Setter
    @Getter
    private static TemperatureDatabase temperatureDatabase;

    public static TemperatureDao getTemperatureDao() {
        if (temperatureDatabase == null) {
            return null;
        }
        return temperatureDatabase.temperatureDao();
    }
}
