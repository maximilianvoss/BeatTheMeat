package rocks.voss.beatthemeat.sources.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import lombok.Data;

@Data
public class ThermometerDataWrapper implements Serializable {
    private ThermometerData[] thermometers;

    public static ThermometerDataWrapper createByStream(InputStream stream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(stream, ThermometerDataWrapper.class);
    }
}
