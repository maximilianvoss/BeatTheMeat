package rocks.voss.beatthemeat.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import lombok.Setter;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.ui.NumberPickerPreference;
import rocks.voss.beatthemeat.utils.KeyUtil;

/**
 * Created by voss on 26.03.18.
 */

public class ThermometerSettingsFragment extends PreferenceFragment {
    @Setter
    private int id;

    private NumberPickerPreference temperatureMax;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_thermometersettings);

        SwitchPreference isRange = new SwitchPreference(this.getContext());
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
        getPreferenceScreen().addPreference(isRange);

        // TODO: to be continued
//        ListPreference typePreference = new ListPreference(this.getContext());
//        typePreference.setTitle("Name");

        NumberPickerPreference temperatureMin = new NumberPickerPreference(this.getContext());
        temperatureMin.setKey(KeyUtil.createKey("temperatureMin", id));
        temperatureMin.setTitle("Temperature Min");
        temperatureMin.setDefaultValue(50);
        getPreferenceScreen().addPreference(temperatureMin);

        temperatureMax = new NumberPickerPreference(this.getContext());
        temperatureMax.setKey(KeyUtil.createKey("temperatureMax", id));
        temperatureMax.setTitle("Temperature Max");
        temperatureMax.setDefaultValue(100);
        temperatureMax.setEnabled(isRange.isChecked());
        getPreferenceScreen().addPreference(temperatureMax);
    }
}
