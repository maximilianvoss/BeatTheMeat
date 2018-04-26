package rocks.voss.beatthemeat.threads;

import org.threeten.bp.OffsetDateTime;

import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.database.TemperatureDatabase;
import rocks.voss.beatthemeat.utils.TimeUtil;

public class HistoryTemperatureDeleteThread extends Thread {
    @Override
    public void run() {
        TemperatureDatabase temperatureDatabase = MainActivity.getTemperatureDatabase();
        if (temperatureDatabase == null) {
            return;
        }
        TemperatureDao temperatureDao = temperatureDatabase.temperatureDao();
        if (temperatureDao == null) {
            return;
        }

        OffsetDateTime time = TimeUtil.getNow();
        time.minusDays(1);
        temperatureDao.deleteOld(time);
    }
}
