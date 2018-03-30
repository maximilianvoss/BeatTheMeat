package rocks.voss.beatthemeat.fragments;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;

import rocks.voss.beatthemeat.Constants;
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
        runningService.setTitle(R.string.setting_general_enable_webservice_calls);
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
        webseviceURL.setTitle(R.string.setting_general_webservice_url);
        webseviceURL.setKey(Constants.SETTING_GENERAL_WEBSERVICE_URL);

        NumberPickerPreference webserviceURLCalls = new NumberPickerPreference(this.getContext());
        webserviceURLCalls.setKey(Constants.SETTING_GENERAL_WEBSERVICE_URL_CALLS);
        webserviceURLCalls.setTitle(R.string.setting_general_webservice_call_interval);
        webserviceURLCalls.setDefaultValue(5);
        webserviceURLCalls.setMaxValue(20);
        webserviceURLCalls.setMinValue(3);

        RingtonePreference alarm = new RingtonePreference(this.getContext());
        alarm.setKey(Constants.SETTING_GENERAL_ALARM);
        alarm.setTitle(R.string.setting_general_alarm);

        addPreferencesFromResource(R.xml.pref_appsettings);
        getPreferenceScreen().addPreference(runningService);
        getPreferenceScreen().addPreference(webseviceURL);
        getPreferenceScreen().addPreference(webserviceURLCalls);
        getPreferenceScreen().addPreference(alarm);
    }
}
