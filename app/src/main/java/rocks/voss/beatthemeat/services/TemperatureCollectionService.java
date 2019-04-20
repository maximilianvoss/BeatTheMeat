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
import java.util.LinkedHashSet;
import java.util.Set;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.database.temperatures.Temperature;
import rocks.voss.beatthemeat.database.temperatures.TemperatureCache;
import rocks.voss.beatthemeat.enums.NotificationEnum;
import rocks.voss.beatthemeat.sources.grilleye.GrillEyePro;
import rocks.voss.beatthemeat.sources.json.ThermometerData;
import rocks.voss.beatthemeat.sources.json.ThermometerDataWrapper;
import rocks.voss.beatthemeat.threads.GrillEyeProThread;
import rocks.voss.beatthemeat.threads.JsonDownloadThread;
import rocks.voss.beatthemeat.utils.AlarmUtil;
import rocks.voss.beatthemeat.utils.NotificationUtil;

/**
 * Created by voss on 24.03.18.
 */
public class TemperatureCollectionService extends JobService {
    private static final int COUNT = 5;
    private static int webserviceCallInterval;

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, TemperatureCollectionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(Constants.SERVICE_TEMPERATURE_COLLECTION_SERVICE_ID, component);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        webserviceCallInterval = sharedPref.getInt(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_INTERVAL, COUNT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(webserviceCallInterval * 1000);
        } else {
            builder.setPeriodic(webserviceCallInterval * 1000);
        }

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(Constants.SERVICE_TEMPERATURE_COLLECTION_SERVICE_ID);
        GrillEyePro.stop();
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
        String webserviceType = sharedPref.getString(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_TYPE, Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_TYPE_JSON);

        if (webserviceType != null) {
            if (webserviceType.equals(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_TYPE_JSON)) {
                jsonWebservice(webserviceUrls);
            } else if (webserviceType.equals(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_TYPE_GRILLEYE_PRO)) {
                grilleyeProWebservice(webserviceUrls);
            }
        }
        jobFinished(params, false);
    }

    private void grilleyeProWebservice(Set<String> webserviceUrls) {
        try {
            GrillEyeProThread service = new GrillEyeProThread(this, webserviceCallInterval, new GrillEyeProThread.GrilleyeProDownloadThreadCallback() {
                @Override
                public void onDownloadComplete(int temperatures[]) {
                    ThermometerData[] thermometerData = new ThermometerData[temperatures.length];
                    for (int i = 0; i < temperatures.length; i++) {
                        thermometerData[i] = new ThermometerData();
                        thermometerData[i].setId(i);
                        thermometerData[i].setActive(temperatures[i] != 65486);
                        thermometerData[i].setTemperature(temperatures[i]);

                        Temperature temperature = Temperature.createByThermometerData(thermometerData[i]);
                        TemperatureCache.insertTemperature(temperature);
                    }

                    MainActivity.refreshThermometers();
                    if (AlarmUtil.isAlarm(getBaseContext())) {
                        activateNotification();
                    } else {
                        deactivateNotification();
                    }
                }

                @Override
                public void onConnectionFailure(Context context) {
                    NotificationUtil.createNotification(context, NotificationEnum.WebserviceAlarm);
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
    }

    private void jsonWebservice(Set<String> webserviceUrls) {
        try {
            JsonDownloadThread service = new JsonDownloadThread(this, new JsonDownloadThread.JsonDownloadThreadCallback() {
                @Override
                public void onDownloadComplete(InputStream stream) throws IOException {
                    ThermometerDataWrapper thermometerDataWrapper = ThermometerDataWrapper.createByStream(stream);
                    for (ThermometerData thermometerData : thermometerDataWrapper.getThermometers()) {
                        Temperature temperature = Temperature.createByThermometerData(thermometerData);
                        TemperatureCache.insertTemperature(temperature);
                    }

                    MainActivity.refreshThermometers();
                    if (AlarmUtil.isAlarm(getBaseContext())) {
                        activateNotification();
                    } else {
                        deactivateNotification();
                    }
                }

                @Override
                public void onConnectionFailure(Context context) {
                    NotificationUtil.createNotification(context, NotificationEnum.WebserviceAlarm);
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
