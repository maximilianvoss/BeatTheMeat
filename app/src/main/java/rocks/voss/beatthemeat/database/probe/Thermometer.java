package rocks.voss.beatthemeat.database.probe;

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

    public int fk_catalog;
    public int fk_meat;
    public int fk_cut;
    public int fk_cooking;
}
