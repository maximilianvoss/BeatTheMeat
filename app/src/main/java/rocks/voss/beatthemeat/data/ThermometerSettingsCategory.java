package rocks.voss.beatthemeat.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by voss on 30.03.18.
 */

public class ThermometerSettingsCategory {
    @Setter
    @Getter
    private String name;

    @Getter
    private List<ThermometerSettingsStyle> styles = new ArrayList<>();
}
