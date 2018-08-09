package rocks.voss.beatthemeat.threads;

import org.threeten.bp.OffsetDateTime;

import lombok.Setter;
import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.androidutils.utils.TimeUtil;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.database.TemperatureDao;

public class DatabaseDeleteThread extends Thread {

    @Setter
    private int thermometerId = -1;

    @Override
    public void run() {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        TemperatureDao temperatureDao = databaseUtil.getDao(Temperature.class);
        if (temperatureDao == null) {
            return;
        }

        if (thermometerId == -1) {
            OffsetDateTime time = TimeUtil.getNow();
            time = time.minusDays(1);
            temperatureDao.deleteAll(time);
        } else {
            temperatureDao.delete(thermometerId);
        }
    }
}
