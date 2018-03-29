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

import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.activities.MainActivity;

/**
 * Created by voss on 27.03.18.
 */

public class NotificationSoundService extends Service {
    private MediaPlayer mp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String alarm = sharedPref.getString("alarm", "");
        Uri notificationUri;
        if (alarm.equals("")) {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            notificationUri = Uri.parse(alarm);
        }
        mp = MediaPlayer.create(this, notificationUri);
        mp.setLooping(true);

        createNotificationChannel();

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        Intent dismissReceiverIntent = new Intent(this, DismissReceiver.class);
        PendingIntent dismissReceiverPendingIntent = PendingIntent.getBroadcast(this, 0, dismissReceiverIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICIATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.beatthemeat)
                .setContentTitle("Beat The Meat Alarm")
                .setContentText("Check the meat!")
                .setContentIntent(mainActivityPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .addAction(R.mipmap.beatthemeat, "Dismiss", dismissReceiverPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIFICIATION_TEMPERATURE_ID, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constants.NOTIFICIATION_TEMPERATURE_ID);
        mp.stop();
        DataCollectionService.setNotificationActive(false);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        mp.start();
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
