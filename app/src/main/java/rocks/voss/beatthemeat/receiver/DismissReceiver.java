package rocks.voss.beatthemeat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.enums.DismissTypeEnum;
import rocks.voss.beatthemeat.enums.NotificationEnum;
import rocks.voss.beatthemeat.services.NotificationSoundService;
import rocks.voss.beatthemeat.services.TemperatureCollectionService;
import rocks.voss.beatthemeat.utils.TemperatureUtil;

/**
 * Created by voss on 28.03.18.
 */

public class DismissReceiver extends BroadcastReceiver {

    public DismissReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DismissTypeEnum dismissType = DismissTypeEnum.valueOf(intent.getStringExtra(Constants.NOTIFICATION_DISMISS_TYPE));
        NotificationEnum notificationType = NotificationEnum.valueOf(intent.getStringExtra(Constants.NOTIFICATION_ALERT_TYPE));

        Intent notificationSoundServiceIntent = new Intent(context, NotificationSoundService.class);
        notificationSoundServiceIntent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, notificationType.name());
        context.stopService(notificationSoundServiceIntent);

        if ( dismissType.equals(DismissTypeEnum.Dismiss)) {
            switch (notificationType) {
                case TemperatureAlarm:
                    TemperatureUtil.setEnabled(false);
                    MainActivity.getSwitchAlarm().setChecked(false);
                    break;
                case WebserviceAlarm:
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_ENABLED, false);
                    editor.apply();
                    TemperatureCollectionService.cancelJob(context);
                    break;
            }
        }
    }
}
