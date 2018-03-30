package rocks.voss.beatthemeat.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import lombok.Setter;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.utils.TemperatureUtil;

/**
 * Created by voss on 24.03.18.
 */
public class DataCollectionService extends JobService {

    private static final int SEC = 1000;
    private static final int COUNT = 5;

    @Setter
    private static boolean notificationActive = false;

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, DataCollectionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(Constants.SERVICE_DATA_COLLECTION_SERVICE_ID, component);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int webserviceUrlCalls = sharedPref.getInt(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_INTERVAL, COUNT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(webserviceUrlCalls * SEC);
        } else {
            builder.setPeriodic(webserviceUrlCalls * SEC);
        }

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(Constants.SERVICE_DATA_COLLECTION_SERVICE_ID);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        execute(params);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            schedule(this);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void execute(JobParameters params) {
        DataCollectionServiceThread service = new DataCollectionServiceThread(getBaseContext());
        service.start();
        try {
            service.join();
            MainActivity.refreshThermometers();
            boolean isAlarm = TemperatureUtil.isAlarm(this);
            if ( isAlarm ) {
                activateNotification();
            } else {
                deactivateNotification();
            }
        } catch (InterruptedException e) {
            Log.e(this.getClass().toString() ,"InterruptedException", e);
        }
        jobFinished(params, false);
    }

    private void activateNotification() {
        if ( TemperatureUtil.isEnabled() && !notificationActive) {
            notificationActive = true;
            Intent intent = new Intent(this, NotificationSoundService.class);
            startService(intent);
        }
    }

    private void deactivateNotification() {
        if ( TemperatureUtil.isEnabled() && notificationActive) {
            notificationActive = false;
            Intent intent = new Intent(this, NotificationSoundService.class);
            stopService(intent);
        }
    }
}
