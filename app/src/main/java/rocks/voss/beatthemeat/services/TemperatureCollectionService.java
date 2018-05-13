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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.enums.NotificationEnum;
import rocks.voss.beatthemeat.threads.JsonDownloadThread;
import rocks.voss.beatthemeat.threads.JsonDownloadThreadCallback;
import rocks.voss.beatthemeat.utils.AlarmUtil;
import rocks.voss.beatthemeat.utils.NotificationUtil;
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
        String alternativeWebserviceUrl = sharedPref.getString(Constants.SETTING_GENERAL_TEMPERATURE_ALTERNATIVE_WEBSERVICE_URL, "");

        try {
            JsonDownloadThread service = new JsonDownloadThread(this, new JsonDownloadThreadCallback() {
                @Override
                public void onDownloadComplete(SharedPreferences sharedPref, JSONObject jsonObject) {
                    try {
                        JSONArray temperatures = jsonObject.getJSONArray(Constants.JSON_TEMPERATURES_OBJECT);
                        List<Integer> temperatureList = new ArrayList<>();
                        for (int i = 0; i < temperatures.length() && i < MainActivity.getThermometers().size(); i++) {
                            temperatureList.add(temperatures.getInt(i));
                        }
                        TemperatureUtil.saveTemperature(temperatureList);
                        MainActivity.refreshThermometers();

                        boolean isAlarm = AlarmUtil.isAlarm(getBaseContext());
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
                    NotificationUtil.createNotification(context, NotificationEnum.WebserviceAlarm);
                    TemperatureUtil.saveTemperature(new ArrayList<Integer>());
                    MainActivity.refreshThermometers();
                }
            });
            service.addUrls(webserviceUrl, alternativeWebserviceUrl);
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
