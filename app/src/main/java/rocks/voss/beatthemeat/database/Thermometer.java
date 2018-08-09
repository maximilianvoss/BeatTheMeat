package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

@Entity(
        primaryKeys = {"id"},
        indices = {
                @Index(value = {"id"})
        }
)
public class Thermometer {
    @NonNull
    public int id;
    @NonNull
    public boolean isRange;
    @NonNull
    public int temperatureMin;
    @NonNull
    public int temperatureMax;
    @NonNull
    public int order;
}
