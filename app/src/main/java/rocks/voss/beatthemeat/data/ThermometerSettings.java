package rocks.voss.beatthemeat.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by voss on 30.03.18.
 */

public class ThermometerSettings {
    private static ThermometerSettings instance = null;

    @Getter
    private List<ThermometerSettingsCatalog> catalogs = new ArrayList<>();

    public static ThermometerSettings getInstance() {
        if (instance == null) {
            instance = new ThermometerSettings();
        }
        return instance;
    }

    public void clear() {
        for ( ThermometerSettingsCatalog catalog : catalogs ) {
            for ( ThermometerSettingsCategory category : catalog.getCategories() ) {
                category.getStyles().clear();
            }
            catalog.getCategories().clear();
        }
        catalogs.clear();
    }
}
