package rocks.voss.beatthemeat.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;
import rocks.voss.beatthemeat.Constants;
import rocks.voss.beatthemeat.R;
import rocks.voss.beatthemeat.data.ThermometerSettings;
import rocks.voss.beatthemeat.data.ThermometerSettingsCategory;
import rocks.voss.beatthemeat.data.ThermometerSettingsStyle;
import rocks.voss.beatthemeat.ui.NumberPickerPreference;
import rocks.voss.beatthemeat.utils.KeyUtil;

/**
 * Created by voss on 26.03.18.
 */

public class ThermometerSettingsFragment extends PreferenceFragment {
    @Setter
    private int id;

    private SwitchPreference isRange;
    private NumberPickerPreference temperatureMin;
    private NumberPickerPreference temperatureMax;
    private ListPreference temperatureStyle;
    private ListPreference temperatureCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_thermometersettings);

        isRange = new SwitchPreference(this.getContext());
        isRange.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_IS_RANGE, id));
        isRange.setTitle(R.string.setting_temperature_isrange);
        isRange.setDefaultValue(true);
        isRange.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                temperatureMax.setEnabled((boolean) newValue);
                invalidateCategory();
                return true;
            }
        });
        getPreferenceScreen().addPreference(isRange);


        temperatureCategory = new ListPreference(this.getContext());
        temperatureCategory.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CATEGORY, id));
        temperatureCategory.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                setPreferenceTitle(preference, R.string.setting_temperature_category, (String) o);
                updateTemperatureCategoryLists(temperatureStyle, o.toString());
                invalidateStyle();
                temperatureStyle.setEnabled(true);
                return true;
            }
        });
        updateTemperatureCategoryLists(temperatureCategory, null);
        getPreferenceScreen().addPreference(temperatureCategory);
        setPreferenceTitle(temperatureCategory, R.string.setting_temperature_category, temperatureCategory.getValue());


        temperatureStyle = new ListPreference(this.getContext());
        temperatureStyle.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_STYLE, id));
        temperatureStyle.setEnabled(temperatureCategory.getValue() == null || temperatureCategory.getValue().equals("") ? false : true);
        temperatureStyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                setPreferenceTitle(preference, R.string.setting_temperature_style, (String) o);
                fillThermometerSettings(temperatureCategory.getValue(), (String) o);
                return true;
            }
        });
        updateTemperatureCategoryLists(temperatureStyle, temperatureCategory.getValue());
        getPreferenceScreen().addPreference(temperatureStyle);
        setPreferenceTitle(temperatureStyle, R.string.setting_temperature_style, temperatureStyle.getValue());


        temperatureMin = new NumberPickerPreference(this.getContext());
        temperatureMin.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MIN, id));
        temperatureMin.setDefaultValue(50);
        temperatureMin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                setPreferenceTitle(preference, R.string.setting_temperature_min, String.valueOf(o));
                invalidateCategory();
                return true;
            }
        });
        getPreferenceScreen().addPreference(temperatureMin);
        setPreferenceTitle(temperatureMin, R.string.setting_temperature_min, String.valueOf(temperatureMin.getValue()));


        temperatureMax = new NumberPickerPreference(this.getContext());
        temperatureMax.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MAX, id));
        temperatureMax.setDefaultValue(100);
        temperatureMax.setEnabled(isRange.isChecked());
        temperatureMax.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                setPreferenceTitle(preference, R.string.setting_temperature_max, String.valueOf(o));
                invalidateCategory();
                return true;
            }
        });
        getPreferenceScreen().addPreference(temperatureMax);
        setPreferenceTitle(temperatureMax, R.string.setting_temperature_max, String.valueOf(temperatureMax.getValue()));
    }

    private void updateTemperatureCategoryLists(ListPreference listPreference, String category) {
        List<CharSequence> list;
        if (category == null) {
            list = fillThermometerCategories();
        } else {
            list = fillThermometerStyle(category);
        }
        CharSequence[] array = list.toArray(new CharSequence[list.size()]);
        listPreference.setEntries(array);
        listPreference.setEntryValues(array);
    }

    private List<CharSequence> fillThermometerCategories() {
        List<CharSequence> entries = new ArrayList<>();

        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
        for (int i = 0; i < thermometerSettings.getCategories().size(); i++) {
            entries.add(thermometerSettings.getCategories().get(i).getName());
        }
        return entries;
    }

    private List<CharSequence> fillThermometerStyle(String category) {
        List<CharSequence> entries = new ArrayList<>();

        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
        for (ThermometerSettingsCategory thermometerSettingsCategory : thermometerSettings.getCategories()) {
            if (thermometerSettingsCategory.getName().equals(category)) {
                for (int i = 0; i < thermometerSettingsCategory.getStyles().size(); i++) {
                    entries.add(thermometerSettingsCategory.getStyles().get(i).getName());
                }
            }
        }
        return entries;
    }

    private void fillThermometerSettings(String category, String style) {
        List<CharSequence> entries = new ArrayList<>();

        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
        for (ThermometerSettingsCategory thermometerSettingsCategory : thermometerSettings.getCategories()) {
            if (thermometerSettingsCategory.getName().equals(category)) {
                for (ThermometerSettingsStyle thermometerSettingsStyle : thermometerSettingsCategory.getStyles() ) {
                    if ( thermometerSettingsStyle.getName().equals(style)) {
                        temperatureMin.setValue(thermometerSettingsStyle.getTemperatureMin());
                        temperatureMax.setValue(thermometerSettingsStyle.getTemperatureMax());
                        isRange.setChecked(thermometerSettingsStyle.isRange());

                        setPreferenceTitle(temperatureMin, R.string.setting_temperature_min, String.valueOf(temperatureMin.getValue()));
                        setPreferenceTitle(temperatureMax, R.string.setting_temperature_max, String.valueOf(temperatureMax.getValue()));
                        temperatureMax.setEnabled(thermometerSettingsStyle.isRange());

                        return;
                    }
                }
            }
        }
    }

    private void setPreferenceTitle(Preference preference, int titleResId, String addition) {
        preference.setTitle(titleResId);
        if (addition != null && !addition.equals("")) {
            preference.setTitle(preference.getTitle() + " " + addition);
        }
    }

    private void invalidateCategory() {
        temperatureCategory.setValue("");
        setPreferenceTitle(temperatureCategory, R.string.setting_temperature_category, null);
        temperatureStyle.setEnabled(false);
        invalidateStyle();
    }

    private void invalidateStyle() {
        temperatureStyle.setValue("");
        setPreferenceTitle(temperatureStyle, R.string.setting_temperature_style, null);
    }
}
