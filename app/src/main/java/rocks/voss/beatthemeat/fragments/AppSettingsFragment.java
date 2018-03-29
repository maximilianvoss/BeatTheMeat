package rocks.voss.beatthemeat.fragments;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;

import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.services.DataCollectionService;
import rocks.voss.beatthemeat.ui.NumberPickerPreference;

/**
 * Created by voss on 26.03.18.
 */

public class AppSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwitchPreference runningService = new SwitchPreference(this.getContext());
        runningService.setTitle("Enable Web Service Calls");
        runningService.setDefaultValue(true);
        runningService.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ( (boolean) newValue ) {
                    DataCollectionService.schedule(getContext());
                } else {
                    DataCollectionService.cancelJob(getContext());
                }
                return true;
            }
        });

        EditTextPreference webseviceURL = new EditTextPreference(this.getContext());
        webseviceURL.setTitle("Webservice URL");
        webseviceURL.setKey("webserviceURL");

        NumberPickerPreference webserviceURLCalls = new NumberPickerPreference(this.getContext());
        webserviceURLCalls.setKey("webserviceURLCalls");
        webserviceURLCalls.setTitle("Interval for Web Service Calls (in seconds)");
        webserviceURLCalls.setDefaultValue(5);
        webserviceURLCalls.setMaxValue(20);
        webserviceURLCalls.setMinValue(3);

        RingtonePreference alarm = new RingtonePreference(this.getContext());
        alarm.setKey("alarm");
        alarm.setTitle("Alarm");

        addPreferencesFromResource(R.xml.pref_appsettings);
        getPreferenceScreen().addPreference(runningService);
        getPreferenceScreen().addPreference(webseviceURL);
        getPreferenceScreen().addPreference(webserviceURLCalls);
        getPreferenceScreen().addPreference(alarm);
    }
}
