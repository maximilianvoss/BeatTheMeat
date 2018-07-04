package rocks.voss.beatthemeat.thermometersettings;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by voss on 31.03.18.
 */
@Data
public class ThermometerSettingsCatalogData implements Serializable {
    private String name;
    private ThermometerSettingsCategoryData[] categories;
}
