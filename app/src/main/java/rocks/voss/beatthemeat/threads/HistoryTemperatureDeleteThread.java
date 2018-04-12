package rocks.voss.beatthemeat.threads;

import rocks.voss.beatthemeat.activities.MainActivity;

public class HistoryTemperatureDeleteThread extends Thread {
    @Override
    public void run() {
        MainActivity.getTemperatureDatabase().temperatureDao().deleteAll();
    }
}
