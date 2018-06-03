package rocks.voss.beatthemeat.threads;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.utils.DatabaseUtil;

public class HistoryDatabaseThread extends Thread {
    private final int thermometerId;
    private final HistoryDatabaseThreadCallback callback;
    private final OffsetDateTime time;

    public HistoryDatabaseThread(int thermometerId, HistoryDatabaseThreadCallback callback) {
        this(thermometerId, null, callback);
    }

    public HistoryDatabaseThread(int thermometerId, OffsetDateTime time, HistoryDatabaseThreadCallback callback) {
        this.thermometerId = thermometerId;
        this.time = time;
        this.callback = callback;
    }

    @Override
    public void run() {
        TemperatureDao temperatureDao = DatabaseUtil.getTemperatureDao();
        if (temperatureDao == null) {
            return;
        }

        List<Temperature> temperatures;
        if (time == null) {
            temperatures = temperatureDao.getAll(thermometerId);
        } else {
            temperatures = temperatureDao.getAll(thermometerId, time);
        }
        callback.onDataReady(temperatures);
    }

    public interface HistoryDatabaseThreadCallback {
        void onDataReady(List<Temperature> temperatures);
    }
}
