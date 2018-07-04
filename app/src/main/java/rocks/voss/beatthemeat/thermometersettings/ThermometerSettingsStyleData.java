package rocks.voss.beatthemeat.thermometersettings;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by voss on 30.03.18.
 */
@Data
public class ThermometerSettingsStyleData implements Serializable {
    private String name;
    private int temperatureMin;
    private int temperatureMax;
    private boolean temperatureIsRange;
}
