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
import rocks.voss.beatthemeat.data.ThermometerSettingsCatalog;
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

    private ListPreference temperatureCatalog;
    private ListPreference temperatureCategory;
    private ListPreference temperatureStyle;
    private SwitchPreference isRange;
    private NumberPickerPreference temperatureMin;
    private NumberPickerPreference temperatureMax;

    private enum SelectionType {Catalog, Category, Style}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_thermometersettings);

        temperatureCatalog = new ListPreference(this.getContext());
        temperatureCatalog.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CATALOG, id));
        temperatureCatalog.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_catalog, (String) o);
                    updateTemperatureCategoryLists(temperatureCategory, o.toString(), null, SelectionType.Category);
                    invalidateCategory();
                    temperatureCategory.setEnabled(true);
                    return true;
                }
        );
        updateTemperatureCategoryLists(temperatureCatalog, null, null, SelectionType.Catalog);
        getPreferenceScreen().addPreference(temperatureCatalog);
        setPreferenceTitle(temperatureCatalog, R.string.setting_temperature_catalog, temperatureCatalog.getValue());

        temperatureCategory = new ListPreference(this.getContext());
        temperatureCategory.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_CATEGORY, id));
        temperatureCategory.setEnabled(!(temperatureCatalog.getValue() == null || temperatureCatalog.getValue().equals("")));
        temperatureCategory.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_category, (String) o);
                    updateTemperatureCategoryLists(temperatureStyle, temperatureCatalog.getValue(), o.toString(), SelectionType.Style);
                    invalidateStyle();
                    temperatureStyle.setEnabled(true);
                    return true;
                }
        );
        updateTemperatureCategoryLists(temperatureCategory, temperatureCatalog.getValue(), null, SelectionType.Category);
        getPreferenceScreen().addPreference(temperatureCategory);
        setPreferenceTitle(temperatureCategory, R.string.setting_temperature_category, temperatureCategory.getValue());


        temperatureStyle = new ListPreference(this.getContext());
        temperatureStyle.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_STYLE, id));
        temperatureStyle.setEnabled(!(temperatureCategory.getValue() == null || temperatureCategory.getValue().equals("")));
        temperatureStyle.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_style, (String) o);
                    fillThermometerSettings(temperatureCatalog.getValue(), temperatureCategory.getValue(), (String) o);
                    return true;
                }
        );
        updateTemperatureCategoryLists(temperatureStyle, temperatureCatalog.getValue(), temperatureCategory.getValue(), SelectionType.Style);
        getPreferenceScreen().addPreference(temperatureStyle);
        setPreferenceTitle(temperatureStyle, R.string.setting_temperature_style, temperatureStyle.getValue());


        isRange = new SwitchPreference(this.getContext());
        isRange.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_IS_RANGE, id));
        isRange.setTitle(R.string.setting_temperature_isrange);
        isRange.setDefaultValue(true);
        isRange.setOnPreferenceChangeListener((preference, newValue) -> {
                    temperatureMax.setEnabled((boolean) newValue);
                    invalidateCategory();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(isRange);


        temperatureMin = new NumberPickerPreference(this.getContext());
        temperatureMin.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MIN, id));
        temperatureMin.setDefaultValue(50);
        temperatureMin.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_min, String.valueOf(o));
                    invalidateCategory();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(temperatureMin);
        setPreferenceTitle(temperatureMin, R.string.setting_temperature_min, String.valueOf(temperatureMin.getValue()));


        temperatureMax = new NumberPickerPreference(this.getContext());
        temperatureMax.setKey(KeyUtil.createKey(Constants.SETTING_TEMPERATURE_MAX, id));
        temperatureMax.setDefaultValue(100);
        temperatureMax.setEnabled(isRange.isChecked());
        temperatureMax.setOnPreferenceChangeListener((preference, o) -> {
                    setPreferenceTitle(preference, R.string.setting_temperature_max, String.valueOf(o));
                    invalidateCategory();
                    return true;
                }
        );
        getPreferenceScreen().addPreference(temperatureMax);
        setPreferenceTitle(temperatureMax, R.string.setting_temperature_max, String.valueOf(temperatureMax.getValue()));
    }

    private void updateTemperatureCategoryLists(ListPreference listPreference, String catalog, String category, SelectionType selectionType) {
        List<CharSequence> list;
        switch (selectionType) {
            case Catalog:
                list = fillThermometerCatalog();
                break;
            case Category:
                list = fillThermometerCategories(catalog);
                break;
            case Style:
                list = fillThermometerStyle(catalog, category);
                break;
            default:
                return;
        }
        CharSequence[] array = list.toArray(new CharSequence[list.size()]);
        listPreference.setEntries(array);
        listPreference.setEntryValues(array);
    }

    private List<CharSequence> fillThermometerCatalog() {
        List<CharSequence> entries = new ArrayList<>();

        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
        for (ThermometerSettingsCatalog catalog : thermometerSettings.getCatalogs()) {
            entries.add(catalog.getName());
        }
        return entries;
    }

    private List<CharSequence> fillThermometerCategories(String catalogName) {
        List<CharSequence> entries = new ArrayList<>();

        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
        for (ThermometerSettingsCatalog catalog : thermometerSettings.getCatalogs()) {
            if (catalog.getName().equals(catalogName)) {
                for (ThermometerSettingsCategory category : catalog.getCategories()) {
                    entries.add(category.getName());
                }
                return entries;
            }
        }
        return entries;
    }

    private List<CharSequence> fillThermometerStyle(String catalogName, String categoryName) {
        List<CharSequence> entries = new ArrayList<>();

        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
        for (ThermometerSettingsCatalog catalog : thermometerSettings.getCatalogs()) {
            if (catalog.getName().equals(catalogName)) {
                for (ThermometerSettingsCategory category : catalog.getCategories()) {
                    if (category.getName().equals(categoryName)) {
                        for (ThermometerSettingsStyle style : category.getStyles()) {
                            entries.add(style.getName());
                        }
                        return entries;
                    }
                }
            }
        }
        return entries;
    }

    private void fillThermometerSettings(String catalogName, String categoryName, String styleName) {
        ThermometerSettings thermometerSettings = ThermometerSettings.getInstance();
        for (ThermometerSettingsCatalog catalog : thermometerSettings.getCatalogs()) {
            if (catalog.getName().equals(catalogName)) {
                for (ThermometerSettingsCategory category : catalog.getCategories()) {
                    if (category.getName().equals(categoryName)) {
                        for (ThermometerSettingsStyle style : category.getStyles()) {
                            if (style.getName().equals(styleName)) {
                                temperatureMin.setValue(style.getTemperatureMin());
                                temperatureMax.setValue(style.getTemperatureMax());
                                isRange.setChecked(style.isRange());

                                temperatureMax.setEnabled(style.isRange());
                                setPreferenceTitle(temperatureMin, R.string.setting_temperature_min, String.valueOf(temperatureMin.getValue()));
                                setPreferenceTitle(temperatureMax, R.string.setting_temperature_max, String.valueOf(temperatureMax.getValue()));

                                return;
                            }
                        }
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
