package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

/**
 * Created by voss on 08.04.18.
 */

@Dao
public interface TemperatureDao {
    @Query("SELECT * FROM temperature")
    List<Temperature> getAll();

    @Query("SELECT * FROM temperature WHERE thermometerId=:thermometerId ORDER BY datetime(time)")
    List<Temperature> getAll(int thermometerId);

    @Query("SELECT * FROM temperature WHERE thermometerId=:thermometerId AND datetime(time)>datetime(:time) ORDER BY datetime(time)")
    List<Temperature> getAll(int thermometerId, OffsetDateTime time);

    @Insert
    void insertAll(Temperature ... temperatures);

    @Delete
    void delete(Temperature temperature);

    @Query("DELETE FROM temperature")
    void deleteAll();
}
