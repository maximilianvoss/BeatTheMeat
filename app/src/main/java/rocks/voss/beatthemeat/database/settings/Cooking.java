package rocks.voss.beatthemeat.database.settings;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        indices = {
                @Index(value = {"id"}),
                @Index(value = {"fk_catalog"}),
                @Index(value = {"fk_meat"}),
                @Index(value = {"fk_cut"})
        }
)
public class Cooking {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public int fk_catalog;
    @NonNull
    public int fk_meat;
    @NonNull
    public int fk_cut;
    @NonNull
    public String name;
    @NonNull
    public int temperatureMin;
    @NonNull
    public int temperatureMax;
    @NonNull
    public boolean temperatureIsRange;
}


