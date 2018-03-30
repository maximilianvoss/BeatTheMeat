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

        SwitchPreference enableWebserviceCalls = new SwitchPreference(this.getContext());
        enableWebserviceCalls.setTitle(R.string.setting_general_enable_webservice_calls);
        enableWebserviceCalls.setDefaultValue(true);
        enableWebserviceCalls.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
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

        EditTextPreference temperatureWebserviceUrl = new EditTextPreference(this.getContext());
        temperatureWebserviceUrl.setTitle(R.string.setting_general_temperature_webservice_url);
        temperatureWebserviceUrl.setKey(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_URL);

        NumberPickerPreference temperatureWebserviceInterval = new NumberPickerPreference(this.getContext());
        temperatureWebserviceInterval.setKey(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_INTERVAL);
        temperatureWebserviceInterval.setTitle(R.string.setting_general_temperature_webservice_interval);
        temperatureWebserviceInterval.setDefaultValue(5);
        temperatureWebserviceInterval.setMaxValue(20);
        temperatureWebserviceInterval.setMinValue(3);

        RingtonePreference alarmSound = new RingtonePreference(this.getContext());
        alarmSound.setKey(Constants.SETTING_GENERAL_ALARM);
        alarmSound.setTitle(R.string.setting_general_alarm);

        addPreferencesFromResource(R.xml.pref_appsettings);
        getPreferenceScreen().addPreference(enableWebserviceCalls);
        getPreferenceScreen().addPreference(temperatureWebserviceUrl);
        getPreferenceScreen().addPreference(temperatureWebserviceInterval);
        getPreferenceScreen().addPreference(alarmSound);
    }
}
