package rocks.voss.beatthemeat.database.settings;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
        indices = {
                @Index(value = {"id"}),
                @Index(value = {"fk_catalog"})
        }
)
public class Meat {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public int fk_catalog;
    @NonNull
    public String name;
}
