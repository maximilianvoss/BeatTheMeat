package rocks.voss.beatthemeat.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.enums.DismissTypeEnum;
import rocks.voss.beatthemeat.enums.NotificationEnum;
import rocks.voss.beatthemeat.receiver.DismissReceiver;
import rocks.voss.beatthemeat.services.NotificationSoundService;

public class NotificationUtil {

    private static boolean notificationActive = false;

    public static void createNotification(Context context, NotificationEnum notificationType) {
        if ( notificationActive ) {
            return;
        }
        notificationActive = true;
        createNotificationChannel(context);

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, notificationType.name());
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeReceiverIntent = new Intent(context, DismissReceiver.class);
        snoozeReceiverIntent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, notificationType.name());
        snoozeReceiverIntent.putExtra(Constants.NOTIFICATION_DISMISS_TYPE, DismissTypeEnum.Snooze.name());
        PendingIntent snoozeReceiverPendingIntent = PendingIntent.getBroadcast(context, 1, snoozeReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent dismissReceiverIntent = new Intent(context, DismissReceiver.class);
        dismissReceiverIntent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, notificationType.name());
        dismissReceiverIntent.putExtra(Constants.NOTIFICATION_DISMISS_TYPE, DismissTypeEnum.Dismiss.name());
        PendingIntent dismissReceiverPendingIntent = PendingIntent.getBroadcast(context, 2, dismissReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.NOTIFICIATION_CHANNEL_ID)
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

        if ( notificationType == NotificationEnum.TemperatureAlarm ) {
            Intent soundNotification = new Intent(context, NotificationSoundService.class);
            context.startService(soundNotification);
        }
    }

    public static void stopNotification(Context context) {
        if ( notificationActive) {
            Intent soundNotification = new Intent(context, NotificationSoundService.class);
            context.stopService(soundNotification);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Constants.NOTIFICIATION_ID);
        }
        notificationActive = false;
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        CharSequence name = context.getString(R.string.channel_name);
        String description = context.getString(R.string.channel_description);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(Constants.NOTIFICIATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }
}
