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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.OffsetDateTime;

import java.net.MalformedURLException;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.enums.NotificationEnum;
import rocks.voss.beatthemeat.threads.JsonDownloadThread;
import rocks.voss.beatthemeat.threads.JsonDownloadThreadCallback;
import rocks.voss.beatthemeat.utils.KeyUtil;
import rocks.voss.beatthemeat.utils.TemperatureUtil;

/**
 * Created by voss on 24.03.18.
 */
public class TemperatureCollectionService extends JobService {

    private static final int SEC = 1000;
    private static final int COUNT = 5;

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, TemperatureCollectionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(Constants.SERVICE_TEMPERATURE_COLLECTION_SERVICE_ID, component);

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
        String webserviceUrl = sharedPref.getString(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_URL, "");

        try {
            JsonDownloadThread service = new JsonDownloadThread(this, webserviceUrl, new JsonDownloadThreadCallback() {
                @Override
                public void onDownloadComplete(SharedPreferences sharedPref, JSONObject jsonObject) {
                    try {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        JSONArray temperatures = jsonObject.getJSONArray(Constants.JSON_TEMPERATURES_OBJECT);
                        for (int i = 0; i < temperatures.length(); i++) {
                            int temperature = temperatures.getInt(i);
                            editor.putInt(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CURRENT, i), temperature);
                            insertTemperatureIntoDatabase(i , temperature);
                        }
                        editor.apply();
                        MainActivity.refreshThermometers();

                        boolean isAlarm = TemperatureUtil.isAlarm(getBaseContext());
                        if (isAlarm) {
                            activateNotification();
                        } else {
                            deactivateNotification();
                        }
                    } catch (JSONException e) {
                        Log.e(getClass().toString(), "JSONException", e);
                    }
                }

                @Override
                public void onConnectionFailure(Context context) {
                    Intent intent = new Intent(context, NotificationSoundService.class);
                    intent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, NotificationEnum.WebserviceAlarm.name());
                    context.startService(intent);
                }
            });

            service.start();
        } catch (MalformedURLException e) {
            Log.e(this.getClass().toString(), "MalformedURLException", e);
        }
        jobFinished(params, false);
    }

    private void activateNotification() {
        if (TemperatureUtil.isEnabled() ) {
            Intent intent = new Intent(this, NotificationSoundService.class);
            intent.putExtra(Constants.NOTIFICATION_ALERT_TYPE, NotificationEnum.TemperatureAlarm.name());
            startService(intent);
        }
    }

    private void deactivateNotification() {
        if (TemperatureUtil.isEnabled()) {
            Intent intent = new Intent(this, NotificationSoundService.class);
            stopService(intent);
        }
    }

    private void insertTemperatureIntoDatabase(int id, int temperatureValue) {
        Temperature temperature = new Temperature();
        temperature.thermometerId = id;
        temperature.temperature = temperatureValue;
        temperature.time = OffsetDateTime.now();
        MainActivity.getTemperatureDatabase().temperatureDao().insertAll(temperature);
    }
}
