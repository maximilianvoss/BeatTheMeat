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
import rocks.voss.beatthemeat.data.ThermometerSettingsCatalog;
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
        String webserviceUrl = sharedPref.getString(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL, Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL_DEFAULT);

        try {
            JsonDownloadThread service = new JsonDownloadThread(this, webserviceUrl, new JsonDownloadCallbackInterface() {
                @Override
                public void onDownloadComplete(SharedPreferences sharedPref, JSONObject jsonObject) {
                    try {
                        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
                        thermometerSettings.clear();

                        JSONArray jsonCatalogs = jsonObject.getJSONArray(Constants.JSON_THERMOMETER_SETTINGS_CATALOGS);
                        for ( int i = 0; i < jsonCatalogs.length(); i++ ) {
                            JSONObject jsonCatalog = jsonCatalogs.getJSONObject(i);
                            ThermometerSettingsCatalog catalog = new ThermometerSettingsCatalog();
                            catalog.setName(jsonCatalog.getString(Constants.JSON_THERMOMETER_SETTINGS_PROPERTY_NAME));
                            thermometerSettings.getCatalogs().add(catalog);

                            JSONArray jsonCategories = jsonCatalog.getJSONArray(Constants.JSON_THERMOMETER_SETTINGS_CATEGORIES);
                            for (int j = 0; j < jsonCategories.length(); j++) {
                                JSONObject jsonCategory = jsonCategories.getJSONObject(j);

                                ThermometerSettingsCategory category = new ThermometerSettingsCategory();
                                category.setName(jsonCategory.getString(Constants.JSON_THERMOMETER_SETTINGS_PROPERTY_NAME));
                                catalog.getCategories().add(category);

                                JSONArray jsonItems = jsonCategory.getJSONArray(Constants.JSON_THERMOMETER_SETTINGS_STYLES);
                                for (int k = 0; k < jsonItems.length(); k++) {
                                    JSONObject jsonItem = jsonItems.getJSONObject(k);
                                    ThermometerSettingsStyle style = new ThermometerSettingsStyle();
                                    style.setName(jsonItem.getString(Constants.JSON_THERMOMETER_SETTINGS_PROPERTY_NAME));
                                    style.setRange(jsonItem.getBoolean(Constants.JSON_THERMOMETER_SETTINGS_PROPERTY_IS_RANGE));
                                    style.setTemperatureMin(jsonItem.getInt(Constants.JSON_THERMOMETER_SETTINGS_PROPERTY_TEMP_MIN));
                                    style.setTemperatureMax(jsonItem.getInt(Constants.JSON_THERMOMETER_SETTINGS_PROPERTY_TEMP_MAX));
                                    category.getStyles().add(style);
                                }
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
