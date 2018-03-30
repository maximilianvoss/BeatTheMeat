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
    private List<ThermometerSettingsCategory> categories = new ArrayList<>();

    public static ThermometerSettings getInstance() {
        if (instance == null) {
            instance = new ThermometerSettings();
        }
        return instance;
    }

    public void clear() {
        for ( int i = 0; i < categories.size(); i++ ) {
            categories.get(i).getStyles().clear();
        }
        categories.clear();
    }
}
