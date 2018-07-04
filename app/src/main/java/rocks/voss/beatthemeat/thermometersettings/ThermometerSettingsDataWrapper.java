package rocks.voss.beatthemeat.thermometersettings;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import lombok.Data;
import lombok.Getter;

/**
 * Created by voss on 30.03.18.
 */

@Data
public class ThermometerSettingsDataWrapper implements Serializable {
    @Getter
    private static ThermometerSettingsDataWrapper instance;
    private ThermometerSettingsCatalogData[] catalogs;

    public static ThermometerSettingsDataWrapper createByStream(InputStream stream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        instance = mapper.readValue(stream, ThermometerSettingsDataWrapper.class);
        return instance;
    }
}
