package rocks.voss.beatthemeat.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.enums.DismissTypeEnum;
import rocks.voss.beatthemeat.enums.NotificationEnum;
import rocks.voss.beatthemeat.receiver.DismissReceiver;

/**
 * Created by voss on 27.03.18.
 */

public class NotificationSoundService extends Service {
    private MediaPlayer mp;
    private NotificationEnum notificationType;
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

        createNotificationChannel();

        notificationType = NotificationEnum.valueOf(intent.getStringExtra(Constants.NOTIFICATION_ALERT_TYPE));

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, notificationType.name());
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeReceiverIntent = new Intent(this, DismissReceiver.class);
        snoozeReceiverIntent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, notificationType.name());
        snoozeReceiverIntent.putExtra(Constants.NOTIFICATION_DISMISS_TYPE, DismissTypeEnum.Snooze.name());
        PendingIntent snoozeReceiverPendingIntent = PendingIntent.getBroadcast(this, 1, snoozeReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismissReceiverIntent = new Intent(this, DismissReceiver.class);
        dismissReceiverIntent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, notificationType.name());
        dismissReceiverIntent.putExtra(Constants.NOTIFICATION_DISMISS_TYPE, DismissTypeEnum.Dismiss.name());
        PendingIntent dismissReceiverPendingIntent = PendingIntent.getBroadcast(this, 2, dismissReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICIATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.beatthemeat)
                .setContentIntent(mainActivityPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.mipmap.beatthemeat, Constants.NOTIFICATION_SNOOZE, snoozeReceiverPendingIntent)
                .addAction(R.mipmap.beatthemeat, Constants.NOTIFICATION_DISMISS, dismissReceiverPendingIntent);

        switch (notificationType) {
            case TemperatureAlarm:
                mBuilder.setContentTitle(Constants.NOTIFICATION_TEMPERATURE_TITLE);
                mBuilder.setContentText(Constants.NOTIFICATION_TEMPERATURE_TEXT);
                break;
            case WebserviceAlarm:
                mBuilder.setContentTitle(Constants.NOTIFICATION_WEBSERVICE_TITLE);
                mBuilder.setContentText(Constants.NOTIFICATION_WEBSERVICE_TEXT);
                break;
            default:
        }

        notificationManager.notify(Constants.NOTIFICIATION_ID, mBuilder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mp.stop();
        notificationManager.cancel(Constants.NOTIFICIATION_ID);
        active = false;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(Constants.NOTIFICIATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }
}
