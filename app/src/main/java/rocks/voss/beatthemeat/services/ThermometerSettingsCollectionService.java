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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.thermometersettings.ThermometerSettingsDataWrapper;
import rocks.voss.beatthemeat.threads.JsonDownloadThread;

/**
 * Created by voss on 24.03.18.
 */
public class ThermometerSettingsCollectionService extends JobService {
    private static final int MAX_WAITING_TIME = 0;

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, ThermometerSettingsCollectionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(Constants.SERVICE_THERMOMETER_SETTINGS_COLLECTION_SERVICE_ID, component);

        builder.setMinimumLatency(MAX_WAITING_TIME * 1000);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(Constants.SERVICE_THERMOMETER_SETTINGS_COLLECTION_SERVICE_ID);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        return execute(params);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private boolean execute(JobParameters params) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String webserviceUrl = sharedPref.getString(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL, Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL_DEFAULT);

        try {
            JsonDownloadThread service = new JsonDownloadThread(this, new JsonDownloadThread.JsonDownloadThreadCallback() {
                @Override
                public void onDownloadComplete(InputStream stream) throws IOException {
                    ThermometerSettingsDataWrapper.createByStream(stream);
                }

                @Override
                public void onConnectionFailure(Context context) {
                }
            });

            service.addUrl(webserviceUrl);
            service.start();
        } catch (MalformedURLException e) {
            Log.e(this.getClass().toString(), "MalformedURLException", e);
        }
        jobFinished(params, false);
        return true;
    }
}
