package rocks.voss.beatthemeat.threads;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

import lombok.Setter;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.database.TemperatureDatabase;
import rocks.voss.beatthemeat.enums.HistoryScaleEnum;
import rocks.voss.beatthemeat.ui.HistoryTemperatureCanvas;

public class HistoryTemperatureThread extends Thread {

    @Setter
    private HistoryTemperatureCanvas canvas;

    @Override
    public void run() {
        if (canvas == null) {
            return;
        }
        OffsetDateTime time = HistoryScaleEnum.getTime(canvas.getScale());
        TemperatureDatabase temperatureDatabase = MainActivity.getTemperatureDatabase();
        if (temperatureDatabase == null) {
            return;
        }
        TemperatureDao temperatureDao = temperatureDatabase.temperatureDao();
        if (temperatureDao == null) {
            return;
        }
        List<Temperature> temperatures = temperatureDao.getAll(canvas.getId(), time);
        canvas.setTemperatures(temperatures);
        canvas.invalidate();
    }
}
