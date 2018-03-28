package rocks.voss.beatthemeat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;

import lombok.Setter;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.activities.MainActivity;
import rocks.voss.beatthemeat.ui.NumberPickerPreference;
import rocks.voss.beatthemeat.utils.KeyUtil;

/**
 * Created by voss on 26.03.18.
 */

public class TemperatureRangeFragment extends PreferenceFragment {
    @Setter
    private int id;

    private SwitchPreference isRange;
    private NumberPickerPreference temperatureMin;
    private NumberPickerPreference temperatureMax;
    private RingtonePreference alarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isRange = new SwitchPreference(this.getContext());
        isRange.setKey(KeyUtil.createKey("isRange", id));
        isRange.setTitle("Temperature is Range");
        isRange.setDefaultValue(true);
        isRange.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                temperatureMax.setEnabled((boolean) newValue);
                return true;
            }
        });

        temperatureMin = new NumberPickerPreference(this.getContext());
        temperatureMin.setKey(KeyUtil.createKey("temperatureMin", id));
        temperatureMin.setTitle("Temperature Min");
        temperatureMin.setDefaultValue(50);

        temperatureMax = new NumberPickerPreference(this.getContext());
        temperatureMax.setKey(KeyUtil.createKey("temperatureMax", id));
        temperatureMax.setTitle("Temperature Max");
        temperatureMax.setDefaultValue(100);

        alarm = new RingtonePreference(this.getContext());
        alarm.setKey(KeyUtil.createKey("alarm", id));
        alarm.setTitle("Alarm");

        Preference removeButton = new Preference(this.getContext());
        removeButton.setTitle("Remove last Thermometer");
        removeButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MainActivity.removeThermometer(id);
                Intent intent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(intent);
                return true;
            }
        });

        addPreferencesFromResource(R.xml.pref_thermometersetting);
        getPreferenceScreen().addPreference(isRange);
        getPreferenceScreen().addPreference(temperatureMin);
        getPreferenceScreen().addPreference(temperatureMax);
        getPreferenceScreen().addPreference(alarm);
        getPreferenceScreen().addPreference(removeButton);
    }
}
