package rocks.voss.beatthemeat.activities;

import android.app.Activity;
import android.os.Bundle;

import rocks.voss.beatthemeat.fragments.AppSettingsFragment;

public class AppSettingsActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppSettingsFragment appSettingsFragment = new AppSettingsFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, appSettingsFragment).commit();
    }
}
