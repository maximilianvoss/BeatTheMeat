package rocks.voss.beatthemeat.threads;

import org.threeten.bp.OffsetDateTime;

import lombok.Setter;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.utils.DatabaseUtil;
import rocks.voss.beatthemeat.utils.TimeUtil;

public class DatabaseDeleteThread extends Thread {

    @Setter
    private int thermometerId = -1;

    @Override
    public void run() {
        TemperatureDao temperatureDao = DatabaseUtil.getTemperatureDao();
        if (temperatureDao == null) {
            return;
        }

        if (thermometerId == -1) {
            OffsetDateTime time = TimeUtil.getNow();
            time = time.minusDays(1);
            temperatureDao.delete(time);
        } else {
            temperatureDao.delete(thermometerId);
        }
    }
}
