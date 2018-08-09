package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import rocks.voss.androidutils.utils.DatabaseUtil;

/**
 * Created by voss on 08.04.18.
 */

@Database(entities = {Temperature.class, Thermometer.class}, version = 3)
@TypeConverters({TimeConverter.class})
public abstract class TemperatureDatabase extends RoomDatabase implements DatabaseUtil.Database {
    public abstract TemperatureDao getTemperatureDao();

    public abstract ThermometerDao getThermometerDao();

    public <DaoObject> DaoObject getDao(Class daoElement) {
        if (daoElement.isAssignableFrom(Temperature.class)) {
            return (DaoObject) getTemperatureDao();
        } else if (daoElement.isAssignableFrom(Thermometer.class)) {
            return (DaoObject) getThermometerDao();
        }
        return null;
    }
}
