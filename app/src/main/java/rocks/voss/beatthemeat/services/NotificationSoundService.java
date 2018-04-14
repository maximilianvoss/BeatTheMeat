package rocks.voss.beatthemeat.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import rocks.voss.beatthemeat.Constants;

/**
 * Created by voss on 27.03.18.
 */

public class NotificationSoundService extends Service {
    private static MediaPlayer mp;
    private static boolean active = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ( active) {
            return super.onStartCommand(intent, flags, startId);
        }
        active = true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String alarm = sharedPref.getString(Constants.SETTING_GENERAL_ALARM, "");
        Uri notificationUri;
        if (alarm.equals("")) {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            notificationUri = Uri.parse(alarm);
        }
        mp = MediaPlayer.create(this, notificationUri);
        mp.setLooping(true);
        mp.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        active = false;
        if ( mp != null ) {
            mp.stop();
        }
    }
}
