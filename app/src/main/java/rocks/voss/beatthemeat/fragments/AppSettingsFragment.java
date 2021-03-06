package rocks.voss.beatthemeat.fragments;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;

import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.services.TemperatureCollectionService;
import rocks.voss.beatthemeat.services.ThermometerSettingsCollectionService;
import rocks.voss.beatthemeat.ui.NumberPickerPreference;
import rocks.voss.beatthemeat.ui.StringArrayPreference;

/**
 * Created by voss on 26.03.18.
 */

public class AppSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwitchPreference temperatureWebserviceEnable = new SwitchPreference(this.getContext());
        temperatureWebserviceEnable.setTitle(R.string.setting_general_temperature_webservice_enable);
        temperatureWebserviceEnable.setKey(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_ENABLED);
        temperatureWebserviceEnable.setDefaultValue(true);
        temperatureWebserviceEnable.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    if ((boolean) newValue) {
                        TemperatureCollectionService.schedule(getContext());
                    } else {
                        TemperatureCollectionService.cancelJob(getContext());
                    }
                    return true;
                }
        );

        StringArrayPreference temperatureWebserviceUrl = new StringArrayPreference(this.getContext());
        temperatureWebserviceUrl.setTitle(R.string.setting_general_temperature_webservice_url);
        temperatureWebserviceUrl.setKey(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_URL);
        temperatureWebserviceUrl.setNumberOptions(3);

        NumberPickerPreference temperatureWebserviceInterval = new NumberPickerPreference(this.getContext());
        temperatureWebserviceInterval.setKey(Constants.SETTING_GENERAL_TEMPERATURE_WEBSERVICE_INTERVAL);
        temperatureWebserviceInterval.setTitle(R.string.setting_general_temperature_webservice_interval);
        temperatureWebserviceInterval.setDefaultValue(5);
        temperatureWebserviceInterval.setMaxValue(20);
        temperatureWebserviceInterval.setMinValue(3);

        ListPreference thermometerType = new ListPreference(this.getContext());
        thermometerType.setKey(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_TYPE);
        thermometerType.setTitle(R.string.setting_general_thermometer_settings_type);
        thermometerType.setEntries(R.array.setting_general_thermometer_settings_types);
        thermometerType.setEntryValues(R.array.setting_general_thermometer_settings_types);

        EditTextPreference thermometerSettingsWebserviceUrl = new EditTextPreference(this.getContext());
        thermometerSettingsWebserviceUrl.setTitle(R.string.setting_general_thermometer_settings_webservice_url);
        thermometerSettingsWebserviceUrl.setKey(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL);
        thermometerSettingsWebserviceUrl.setDefaultValue(Constants.SETTING_GENERAL_THERMOMETER_SETTINGS_WEBSERVICE_URL_DEFAULT);
        thermometerSettingsWebserviceUrl.setOnPreferenceChangeListener((preference, o) -> {
                    ThermometerSettingsCollectionService.schedule(getContext());
                    return true;
                }
        );

        RingtonePreference alarmSound = new RingtonePreference(this.getContext());
        alarmSound.setKey(Constants.SETTING_GENERAL_ALARM);
        alarmSound.setTitle(R.string.setting_general_alarm);

        addPreferencesFromResource(R.xml.pref_appsettings);
        getPreferenceScreen().addPreference(temperatureWebserviceEnable);
        getPreferenceScreen().addPreference(temperatureWebserviceUrl);
        getPreferenceScreen().addPreference(temperatureWebserviceInterval);
        getPreferenceScreen().addPreference(thermometerSettingsWebserviceUrl);
        getPreferenceScreen().addPreference(thermometerType);
        getPreferenceScreen().addPreference(alarmSound);
    }
}
