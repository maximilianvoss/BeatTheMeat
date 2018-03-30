package rocks.voss.beatthemeat.data;

import lombok.Data;

/**
 * Created by voss on 30.03.18.
 */
@Data
public class ThermometerSettingsStyle {
    private String name;
    private int temperatureMin;
    private int temperatureMax;
    private boolean isRange;
}
