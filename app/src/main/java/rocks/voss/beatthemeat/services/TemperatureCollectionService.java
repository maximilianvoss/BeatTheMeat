package rocks.voss.beatthemeat.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.enums.NotificationEnum;
import rocks.voss.beatthemeat.thermometer.ThermometerData;
import rocks.voss.beatthemeat.thermometer.ThermometerDataWrapper;
import rocks.voss.beatthemeat.threads.JsonDownloadThread;
import rocks.voss.beatthemeat.utils.AlarmUtil;
import rocks.voss.beatthemeat.utils.NotificationUtil;
import rocks.voss.beatthemeat.utils.TemperatureUtil;

/**
 * Created by voss on 24.03.18.
 */
public class TemperatureCollectionService extends JobService {
    private static final int COUNT = 5;

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, TemperatureCollectionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(Constants.SERVICE_TEMPERATURE_COLLECTION_SERVICE_ID, component);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int webserviceUrlCalls = sharedPref.getInt(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_INTERVAL, COUNT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(webserviceUrlCalls * 1000);
        } else {
            builder.setPeriodic(webserviceUrlCalls * 1000);
        }

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(Constants.SERVICE_TEMPERATURE_COLLECTION_SERVICE_ID);
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> webserviceUrls = sharedPref.getStringSet(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_URL, new LinkedHashSet<>());

        try {
            JsonDownloadThread service = new JsonDownloadThread(this, new JsonDownloadThread.JsonDownloadThreadCallback() {
                @Override
                public void onDownloadComplete(InputStream stream) throws IOException {
                    ThermometerDataWrapper thermometerDataWrapper = ThermometerDataWrapper.createByStream(stream);
                    List<Integer> temperatureList = new ArrayList<>();
                    for (ThermometerData thermometer : thermometerDataWrapper.getThermometers()) {
                        if (thermometer.isActive()) {
                            temperatureList.add((int) thermometer.getTemperature());
                        } else {
                            temperatureList.add(null);
                        }
                    }
                    TemperatureUtil.saveTemperature(temperatureList);
                    MainActivity.refreshThermometers();

                    boolean isAlarm = AlarmUtil.isAlarm(getBaseContext());
                    if (isAlarm) {
                        activateNotification();
                    } else {
                        deactivateNotification();
                    }
                }

                @Override
                public void onConnectionFailure(Context context) {
                    NotificationUtil.createNotification(context, NotificationEnum.WebserviceAlarm);
                    TemperatureUtil.saveTemperature(new ArrayList<>());
                    MainActivity.refreshThermometers();
                }
            });

            for (String webserviceUrl : webserviceUrls) {
                service.addUrl(webserviceUrl);
            }
            service.start();
        } catch (MalformedURLException e) {
            Log.e(this.getClass().toString(), "MalformedURLException", e);
        }
        jobFinished(params, false);
    }

    private void activateNotification() {
        if (AlarmUtil.isEnabled()) {
            NotificationUtil.createNotification(this, NotificationEnum.TemperatureAlarm);
        }
    }

    private void deactivateNotification() {
        if (AlarmUtil.isEnabled()) {
            NotificationUtil.stopNotification(this);
        }
    }
}
