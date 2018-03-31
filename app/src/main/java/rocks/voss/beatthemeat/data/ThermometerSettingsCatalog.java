package rocks.voss.beatthemeat.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by voss on 31.03.18.
 */

public class ThermometerSettingsCatalog {
    @Setter
    @Getter
    private String name;

    @Getter
    private List<ThermometerSettingsCategory> categories = new ArrayList<>();
}
