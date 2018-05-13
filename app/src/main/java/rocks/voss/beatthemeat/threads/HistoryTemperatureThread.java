package rocks.voss.beatthemeat.threads;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

import lombok.Setter;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.database.TemperatureDao;
import rocks.voss.beatthemeat.enums.HistoryScaleEnum;
import rocks.voss.beatthemeat.ui.HistoryTemperatureCanvas;
import rocks.voss.beatthemeat.utils.DatabaseUtil;

public class HistoryTemperatureThread extends Thread {

    @Setter
    private HistoryTemperatureCanvas canvas;

    @Override
    public void run() {
        if (canvas == null) {
            return;
        }
        OffsetDateTime time = HistoryScaleEnum.getTime(canvas.getScale());

        TemperatureDao temperatureDao = DatabaseUtil.getTemperatureDao();
        if (temperatureDao == null) {
            return;
        }
        List<Temperature> temperatures = temperatureDao.getAll(canvas.getId(), time);
        canvas.setTemperatures(temperatures);
        canvas.invalidate();
    }
}
