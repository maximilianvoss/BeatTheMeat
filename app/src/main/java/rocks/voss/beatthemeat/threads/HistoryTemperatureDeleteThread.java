package rocks.voss.beatthemeat.threads;

import org.threeten.bp.OffsetDateTime;

import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.utils.DatabaseUtil;
import rocks.voss.beatthemeat.utils.TimeUtil;

public class HistoryTemperatureDeleteThread extends Thread {
    @Override
    public void run() {
        TemperatureDao temperatureDao = DatabaseUtil.getTemperatureDao();
        if (temperatureDao == null) {
            return;
        }

        OffsetDateTime time = TimeUtil.getNow();
        time.minusDays(1);
        temperatureDao.deleteOld(time);
    }
}
