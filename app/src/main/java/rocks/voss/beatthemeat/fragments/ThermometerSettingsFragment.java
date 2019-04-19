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
import rocks.voss.beatthemeat.settings.CatalogSetting;
import rocks.voss.beatthemeat.settings.CookingSetting;
import rocks.voss.beatthemeat.settings.CutSetting;
import rocks.voss.beatthemeat.settings.MeatSetting;
import rocks.voss.beatthemeat.settings.WrapperSetting;
import rocks.voss.beatthemeat.ui.NumberPickerPreference;
import rocks.voss.beatthemeat.utils.KeyUtil;

/**
 * Created by voss on 26.03.18.
 */

public class ThermometerSettingsFragment extends PreferenceFragment {
    @Setter
    private int id;

    private ListPreference temperatureCatalog;
    private ListPreference temperatureMeat;
    private ListPreference temperatureCut;
    private ListPreference temperatureCooking;
    private SwitchPreference isRange;
    private NumberPickerPreference temperatureMin;
    private NumberPickerPreference temperatureMax;

    private WrapperSetting settingsWrapper = WrapperSetting.getInstance();
    private CatalogSetting settingsCatalog = null;
    private MeatSetting settingsMeat = null;
    private CutSetting settingsCut = null;
    private CookingSetting settingsCooking = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_thermometersettings);

        temperatureCatalog = new ListPreference(this.getContext());
        temperatureCatalog.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CATALOG, id));
        temperatureCatalog.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_catalog, (String) o);
                    settingsCatalog = settingsWrapper.findCatalogByName((String) o);
                    settingsMeat = null;
                    settingsCut = null;
                    settingsCooking = null;
                    updateUI();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(temperatureCatalog);
        setPreferenceTitle(temperatureCatalog, R.string.setting_temperature_catalog, temperatureCatalog.getValue());

        temperatureMeat = new ListPreference(this.getContext());
        temperatureMeat.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MEAT, id));
        temperatureMeat.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_meat, (String) o);
                    settingsMeat = settingsCatalog.findMeatByName((String) o);
                    settingsCut = null;
                    settingsCooking = null;
                    updateUI();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(temperatureMeat);
        setPreferenceTitle(temperatureMeat, R.string.setting_temperature_meat, temperatureMeat.getValue());

        temperatureCut = new ListPreference(this.getContext());
        temperatureCut.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CUT, id));
        temperatureCut.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_cut, (String) o);
                    settingsCut = settingsMeat.findCutByName((String) o);
                    settingsCooking = null;
                    updateUI();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(temperatureCut);
        setPreferenceTitle(temperatureCut, R.string.setting_temperature_cut, temperatureCut.getValue());

        temperatureCooking = new ListPreference(this.getContext());
        temperatureCooking.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_COOKING, id));
        temperatureCooking.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_cooking, (String) o);
                    settingsCooking = settingsCut.findCookingByName((String) o);
                    updateUI();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(temperatureCooking);
        setPreferenceTitle(temperatureCooking, R.string.setting_temperature_cooking, temperatureCooking.getValue());

        isRange = new SwitchPreference(this.getContext());
        isRange.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_IS_RANGE, id));
        isRange.setTitle(R.string.setting_temperature_isrange);
        isRange.setDefaultValue(true);
        isRange.setOnPreferenceChangeListener((preference, newValue) -> {
                    temperatureMin.setEnabled((boolean) newValue);
                    settingsCooking = null;
                    updateUI();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(isRange);

        temperatureMin = new NumberPickerPreference(this.getContext());
        temperatureMin.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MIN, id));
        temperatureMin.setDefaultValue(50);
        temperatureMin.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_min, String.valueOf(o));
                    settingsCooking = null;
                    updateUI();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(temperatureMin);
        setPreferenceTitle(temperatureMin, R.string.setting_temperature_min, String.valueOf(temperatureMin.getValue()));

        temperatureMax = new NumberPickerPreference(this.getContext());
        temperatureMax.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MAX, id));
        temperatureMax.setDefaultValue(100);
        temperatureMax.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_max, String.valueOf(o));
                    settingsCooking = null;
                    updateUI();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(temperatureMax);
        setPreferenceTitle(temperatureMax, R.string.setting_temperature_max, String.valueOf(temperatureMax.getValue()));

        loadInitial();
    }

    private void loadInitial() {
        settingsCatalog = settingsWrapper.findCatalogByName(temperatureCatalog.getValue());
        if (settingsCatalog != null) {
            settingsMeat = settingsCatalog.findMeatByName(temperatureMeat.getValue());
        }
        if (settingsMeat != null) {
            settingsCut = settingsMeat.findCutByName(temperatureCut.getValue());
        }
        if (settingsCut != null) {
            settingsCooking = settingsCut.findCookingByName(temperatureCooking.getValue());
        }
        updateUI();
    }

    private void updateUI() {
        if (settingsWrapper != null) {
            List<CharSequence> list = new ArrayList<>();
            for (CatalogSetting catalog : settingsWrapper.getCatalogs()) {
                list.add(catalog.getName());
            }
            CharSequence[] array = list.toArray(new CharSequence[list.size()]);
            temperatureCatalog.setEntries(array);
            temperatureCatalog.setEntryValues(array);
            temperatureCatalog.setEnabled(true);
        } else {
            temperatureCatalog.setEnabled(false);
            temperatureCatalog.setValue("");
            setPreferenceTitle(temperatureCatalog, R.string.setting_temperature_catalog, temperatureCatalog.getValue());
        }

        if (settingsCatalog != null) {
            List<CharSequence> list = new ArrayList<>();
            for (MeatSetting meat : settingsCatalog.getMeats()) {
                list.add(meat.getName());
            }
            CharSequence[] array = list.toArray(new CharSequence[list.size()]);
            temperatureMeat.setEntries(array);
            temperatureMeat.setEntryValues(array);
            temperatureMeat.setEnabled(true);
        } else {
            temperatureMeat.setEnabled(false);
            temperatureMeat.setValue("");
            setPreferenceTitle(temperatureMeat, R.string.setting_temperature_meat, temperatureMeat.getValue());
        }

        if (settingsMeat != null) {
            List<CharSequence> list = new ArrayList<>();
            for (CutSetting cut : settingsMeat.getCuts()) {
                list.add(cut.getName());
            }
            CharSequence[] array = list.toArray(new CharSequence[list.size()]);
            temperatureCut.setEntries(array);
            temperatureCut.setEntryValues(array);
            temperatureCut.setEnabled(true);
        } else {
            temperatureMeat.setValue("");
            setPreferenceTitle(temperatureMeat, R.string.setting_temperature_meat, temperatureMeat.getValue());

            temperatureCut.setEnabled(false);
            temperatureCut.setValue("");
            setPreferenceTitle(temperatureCut, R.string.setting_temperature_cut, temperatureCut.getValue());
        }

        if (settingsCut != null) {
            List<CharSequence> list = new ArrayList<>();
            for (CookingSetting cooking : settingsCut.getCookings()) {
                list.add(cooking.getName());
            }
            CharSequence[] array = list.toArray(new CharSequence[list.size()]);
            temperatureCooking.setEntries(array);
            temperatureCooking.setEntryValues(array);
            temperatureCooking.setEnabled(true);
        } else {
            temperatureCut.setValue("");
            setPreferenceTitle(temperatureCut, R.string.setting_temperature_cut, temperatureCut.getValue());

            temperatureCooking.setEnabled(false);
            temperatureCooking.setValue("");
            setPreferenceTitle(temperatureCooking, R.string.setting_temperature_cooking, temperatureCooking.getValue());
        }

        if (settingsCooking != null) {
            temperatureMin.setValue(settingsCooking.getTemperatureMin());
            temperatureMax.setValue(settingsCooking.getTemperatureMax());
            isRange.setChecked(settingsCooking.isTemperatureIsRange());

            temperatureMin.setEnabled(settingsCooking.isTemperatureIsRange());
            setPreferenceTitle(temperatureMin, R.string.setting_temperature_min, String.valueOf(temperatureMin.getValue()));
            setPreferenceTitle(temperatureMax, R.string.setting_temperature_max, String.valueOf(temperatureMax.getValue()));
        } else {
            temperatureCooking.setValue("");
            setPreferenceTitle(temperatureCooking, R.string.setting_temperature_cooking, temperatureCooking.getValue());
        }
    }

    private enum SelectionType {Catalog, Meat, Cut, Cooked}

    private void setPreferenceTitle(Preference preference, int titleResId, String addition) {
        preference.setTitle(titleResId);
        if (addition != null && !addition.equals("")) {
            preference.setTitle(preference.getTitle() + " " + addition);
        }
    }
}
