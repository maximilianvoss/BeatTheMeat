package rocks.voss.beatthemeat.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.data.ThermometerSettings;
import rocks.voss.beatthemeat.data.ThermometerSettingsCategory;
import rocks.voss.beatthemeat.data.ThermometerSettingsStyle;

/**
 * Created by voss on 24.03.18.
 */
public class ThermometerSettingsCollectionService extends JobService {
    private static final int SEC = 1000;
    private static final int MAX_WAITING_TIME = 0;

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, ThermometerSettingsCollectionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(Constants.SERVICE_THERMOMETER_SETTINGS_COLLECTION_SERVICE_ID, component);

        builder.setMinimumLatency(MAX_WAITING_TIME * SEC);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(Constants.SERVICE_THERMOMETER_SETTINGS_COLLECTION_SERVICE_ID);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void execute(JobParameters params) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String webserviceUrl = sharedPref.getString(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL, "");

        try {
            JsonDownloadThread service = new JsonDownloadThread(this, webserviceUrl, new JsonDownloadCallbackInterface() {
                @Override
                public void onDownloadComplete(SharedPreferences sharedPref, JSONObject jsonObject) {
                    try {
                        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
                        thermometerSettings.clear();

                        JSONArray jsonCategories = jsonObject.getJSONArray("categories");
                        for ( int i = 0; i < jsonCategories.length(); i++ ) {
                            JSONObject jsonCategory = jsonCategories.getJSONObject(i);

                            ThermometerSettingsCategory category = new ThermometerSettingsCategory();
                            category.setName(jsonCategory.getString("name"));
                            thermometerSettings.getCategories().add(category);

                            JSONArray jsonItems = jsonCategory.getJSONArray("styles");
                            for ( int j = 0; j < jsonItems.length(); j++ ) {
                                JSONObject jsonItem = jsonItems.getJSONObject(j);
                                ThermometerSettingsStyle style = new ThermometerSettingsStyle();
                                style.setName(jsonItem.getString("name"));
                                style.setRange(jsonItem.getBoolean("temperatureIsRange"));
                                style.setTemperatureMin(jsonItem.getInt("temperatureMin"));
                                style.setTemperatureMax(jsonItem.getInt("temperatureMax"));
                                category.getStyles().add(style);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(getClass().toString(), "JSONException", e);
                    }
                }
            });

            service.start();
            service.join();
        } catch (InterruptedException e) {
            Log.e(this.getClass().toString(), "InterruptedException", e);
        } catch (MalformedURLException e) {
            Log.e(this.getClass().toString(), "MalformedURLException", e);
        }
        jobFinished(params, false);
    }
}
