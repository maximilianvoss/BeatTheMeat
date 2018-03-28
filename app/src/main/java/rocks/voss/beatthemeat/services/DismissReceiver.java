package rocks.voss.beatthemeat.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.utils.TemperatureUtil;

/**
 * Created by voss on 28.03.18.
 */

public class DismissReceiver extends BroadcastReceiver {

    public DismissReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TemperatureUtil.setEnabled(false);
        Intent notificationSoundServiceIntent = new Intent(context, NotficationSoundService.class);
        context.stopService(notificationSoundServiceIntent);
        MainActivity.getSwitchAlarm().setChecked(false);
    }
}
