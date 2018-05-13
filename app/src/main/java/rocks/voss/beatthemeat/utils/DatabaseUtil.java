package rocks.voss.beatthemeat.utils;

import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.database.TemperatureDatabase;

public class DatabaseUtil {
    public static TemperatureDao getTemperatureDao() {
        TemperatureDatabase temperatureDatabase = MainActivity.getTemperatureDatabase();
        if (temperatureDatabase == null) {
            return null;
        }
        TemperatureDao temperatureDao = temperatureDatabase.temperatureDao();
        return temperatureDao;
    }
}
