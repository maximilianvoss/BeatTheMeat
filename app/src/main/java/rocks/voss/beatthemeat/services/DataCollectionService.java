package rocks.voss.beatthemeat.services;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

/**
 * Created by voss on 24.03.18.
 */
public class DataCollectionService extends JobService {

    private static final int SEC = 1000;

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, DataCollectionService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JobIds.DATA_COLLECTION_SERVICE_ID, component);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(3 * SEC);
        } else {
            builder.setPeriodic(3 * SEC);
        }

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static void cancelJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JobIds.DATA_COLLECTION_SERVICE_ID);
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
        } catch (InterruptedException e) {
            Log.e(this.getClass().toString() ,"InterruptedException", e);
        }
        jobFinished(params, false);
    }
}
