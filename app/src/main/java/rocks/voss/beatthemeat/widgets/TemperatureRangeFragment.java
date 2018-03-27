package rocks.voss.beatthemeat.widgets;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import lombok.Setter;
import rocks.voss.beatthemeat.R;
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

        addPreferencesFromResource(R.xml.pref_thermometersetting);
        getPreferenceScreen().addPreference(isRange);
        getPreferenceScreen().addPreference(temperatureMin);
        getPreferenceScreen().addPreference(temperatureMax);
    }
}
