package rocks.voss.beatthemeat.thermometer;

import java.io.Serializable;

import lombok.Data;

@Data
public class ThermometerData implements Serializable {
    private int id;
    private float temperature;
    private boolean active;
}
