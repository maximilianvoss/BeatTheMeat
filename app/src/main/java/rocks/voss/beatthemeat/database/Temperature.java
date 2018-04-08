package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;


/**
 * Created by voss on 08.04.18.
 */

@Entity(
        primaryKeys = {"thermometerId", "time"},
        indices = {
                @Index(value = {"thermometerId", "time"})
        }
)
public class Temperature {
    @NonNull
    public org.threeten.bp.OffsetDateTime time;

    @NonNull
    public int thermometerId;

    public int temperature;
}
