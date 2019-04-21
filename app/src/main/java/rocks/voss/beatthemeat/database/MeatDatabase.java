package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.beatthemeat.database.probe.Thermometer;
import rocks.voss.beatthemeat.database.probe.ThermometerDao;
import rocks.voss.beatthemeat.database.settings.Catalog;
import rocks.voss.beatthemeat.database.settings.CatalogDao;
import rocks.voss.beatthemeat.database.settings.Cooking;
import rocks.voss.beatthemeat.database.settings.CookingDao;
import rocks.voss.beatthemeat.database.settings.Cut;
import rocks.voss.beatthemeat.database.settings.CutDao;
import rocks.voss.beatthemeat.database.settings.Meat;
import rocks.voss.beatthemeat.database.settings.MeatDao;
import rocks.voss.beatthemeat.database.temperatures.Temperature;
import rocks.voss.beatthemeat.database.temperatures.TemperatureDao;
import rocks.voss.beatthemeat.database.temperatures.TimeConverter;

/**
 * Created by voss on 08.04.18.
 */

@Database(entities = {Temperature.class, Thermometer.class, Catalog.class, Meat.class, Cut.class, Cooking.class}, version = 9, exportSchema = false)
@TypeConverters({TimeConverter.class})
public abstract class MeatDatabase extends RoomDatabase implements DatabaseUtil.Database {
    public abstract TemperatureDao getTemperatureDao();
    public abstract ThermometerDao getThermometerDao();

    public abstract CatalogDao getCatalogDao();

    public abstract MeatDao getMeatDao();

    public abstract CutDao getCutDao();

    public abstract CookingDao getCookingDao();

    public <DaoObject> DaoObject getDao(Class daoElement) {
        if (daoElement.isAssignableFrom(Temperature.class)) {
            return (DaoObject) getTemperatureDao();
        } else if (daoElement.isAssignableFrom(Thermometer.class)) {
            return (DaoObject) getThermometerDao();
        } else if (daoElement.isAssignableFrom(Catalog.class)) {
            return (DaoObject) getCatalogDao();
        } else if (daoElement.isAssignableFrom(Meat.class)) {
            return (DaoObject) getMeatDao();
        } else if (daoElement.isAssignableFrom(Cut.class)) {
            return (DaoObject) getCutDao();
        } else if (daoElement.isAssignableFrom(Cooking.class)) {
            return (DaoObject) getCookingDao();
        }
        return null;
    }
}
