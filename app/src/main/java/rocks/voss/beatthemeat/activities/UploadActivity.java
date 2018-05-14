package rocks.voss.beatthemeat.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.database.Temperature;
import rocks.voss.beatthemeat.threads.HistoryDatabaseThread;
import rocks.voss.beatthemeat.utils.TimeUtil;

import static android.content.ContentValues.TAG;

public class UploadActivity extends Activity {

    protected static final int REQUEST_CODE_SIGN_IN = 0;
    protected static final int REQUEST_CODE_OPEN_ITEM = 1;

    //    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    private TaskCompletionSource<DriveId> mOpenItemTaskSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        EditText textfieldFilename = findViewById(R.id.filename);
        OffsetDateTime date = TimeUtil.getNow();
        String timestamp = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        textfieldFilename.setText(textfieldFilename.getText().toString() + "-" + timestamp + ".csv");

        Button createButton = findViewById(R.id.create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startExport();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    Log.e("UploadActivity", "Sign-in failed.");
                    finish();
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                } else {
                    Log.e("UploadActivity", "Sign-in failed.");
                    finish();
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void startExport() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Drive.SCOPE_FILE)
                    .requestScopes(Drive.SCOPE_APPFOLDER)
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
//        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        writeFile();
    }

    private void writeFile() {
        EditText textfieldFilename = findViewById(R.id.filename);
        String filename = textfieldFilename.getText().toString();
        if (filename == null || filename.equals("")) {
            return;
        }

        int thermometerId = getIntent().getIntExtra(Constants.THERMOMETER_CANVAS_ID, 0);
        HistoryDatabaseThread historyDatabaseThread = new HistoryDatabaseThread();
        historyDatabaseThread.setThermometerId(thermometerId);
        historyDatabaseThread.start();

        final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                            DriveFolder parent = rootFolderTask.getResult();
                            DriveContents contents = createContentsTask.getResult();
                            OutputStream outputStream = contents.getOutputStream();
                            try (Writer writer = new OutputStreamWriter(outputStream)) {
                                writer.write("Time,Temperature\r\n");
                                historyDatabaseThread.join();
                                for (Temperature temperature : historyDatabaseThread.getTemperatures()) {
                                    writer.write(temperature.time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                                    writer.write(",");
                                    writer.write(String.valueOf(temperature.temperature));
                                    writer.write("\r\n");
                                }
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(filename)
                                    .setMimeType("text/csv")
                                    .setStarred(true)
                                    .build();

                            return mDriveResourceClient.createFile(parent, changeSet, contents);
                        }
                )
                .addOnSuccessListener(this,
                        driveFile -> {
                            Log.e(TAG, "File created");
                            finish();
                        }
                )
                .addOnFailureListener(this, e -> {
                            Log.e(TAG, "Unable to create file", e);
                            finish();
                        }
                );
    }
}