package rocks.voss.beatthemeat.threads;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.utils.DatabaseUtil;

// TODO: merge with HistoryTemperatureCanvasThread
public class HistoryDatabaseThread extends Thread {

    @Setter
    private int thermometerId;

    @Getter
    private List<Temperature> temperatures;

    @Override
    public void run() {
        TemperatureDao temperatureDao = DatabaseUtil.getTemperatureDao();
        if (temperatureDao == null) {
            return;
        }
        temperatures = temperatureDao.getAll(thermometerId);
    }
}
