package rocks.voss.beatthemeat.threads;

import org.threeten.bp.OffsetDateTime;

import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.androidutils.utils.TimeUtil;
import rocks.voss.beatthemeat.database.temperatures.Temperature;
import rocks.voss.beatthemeat.database.temperatures.TemperatureDao;

public class DatabaseDeleteThread extends Thread {

    @Override
    public void run() {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        TemperatureDao temperatureDao = databaseUtil.getDao(Temperature.class);
        if (temperatureDao == null) {
            return;
        }

        OffsetDateTime time = TimeUtil.getNow();
        time = time.minusDays(2);
        temperatureDao.deleteAll(time);
    }
}
