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

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.activities.HistoryActivity;
import rocks.voss.beatthemeat.threads.HistoryTemperatureThread;

/**
 * Created by voss on 24.03.18.
 */
public class HistoryTemperatureService extends JobService {

    private static final int SEC = 1000;
    private static final int COUNT = 5;

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, HistoryTemperatureService.class);
        JobInfo.Builder builder = new JobInfo.Builder(Constants.SERVICE_HISTORY_TEMPERATURE_SERVICE_ID, component);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int webserviceUrlCalls = sharedPref.getInt(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_INTERVAL, COUNT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(webserviceUrlCalls * SEC);
        } else {
            builder.setPeriodic(webserviceUrlCalls * SEC);
        }

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
        execute();
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(Constants.SERVICE_HISTORY_TEMPERATURE_SERVICE_ID);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        execute(params);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            schedule(this);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static void execute() {
        HistoryTemperatureThread thread = new HistoryTemperatureThread();
        thread.setCanvas(HistoryActivity.getCanvas());
        thread.start();
    }

    private void execute(JobParameters params) {
        execute();
        jobFinished(params, false);
    }
}
