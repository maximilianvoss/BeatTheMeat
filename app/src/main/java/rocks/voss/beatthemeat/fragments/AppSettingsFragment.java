package rocks.voss.beatthemeat.fragments;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;

import rocks.voss.beatthemeat.R;

/**
 * Created by voss on 26.03.18.
 */

public class AppSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditTextPreference editTextPreference = new EditTextPreference(this.getContext());
        editTextPreference.setTitle("Webservice URL");
        editTextPreference.setKey("webserviceURL");

        RingtonePreference ringtonePreference = new RingtonePreference(this.getContext());
        ringtonePreference.setKey("alarm");
        ringtonePreference.setTitle("Alarm");

        addPreferencesFromResource(R.xml.pref_appsettings);
        getPreferenceScreen().addPreference(editTextPreference);
        getPreferenceScreen().addPreference(ringtonePreference);
    }
}
