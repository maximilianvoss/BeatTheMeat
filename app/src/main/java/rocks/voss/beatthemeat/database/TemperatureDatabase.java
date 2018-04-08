package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

/**
 * Created by voss on 08.04.18.
 */

@Database(entities = {Temperature.class}, version = 1)
@TypeConverters({TimeConverter.class})
public abstract class TemperatureDatabase extends RoomDatabase {
    public abstract TemperatureDao temperatureDao();
}
