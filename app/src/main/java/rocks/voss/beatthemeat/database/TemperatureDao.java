package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import org.threeten.bp.OffsetDateTime;

import java.util.List;

/**
 * Created by voss on 08.04.18.
 */

@Dao
public interface TemperatureDao {
    @Query("SELECT * FROM temperature WHERE thermometerId=:thermometerId AND datetime(time)>datetime(:time) ORDER BY datetime(time)")
    List<Temperature> getAll(int thermometerId, OffsetDateTime time);

    @Query("SELECT * FROM temperature WHERE thermometerId=:thermometerId ORDER BY datetime(time) DESC LIMIT 1")
    Temperature getLast(int thermometerId);

    @Insert
    void insertAll(Temperature ... temperatures);

    @Query("DELETE FROM temperature WHERE datetime(time)<datetime(:time)")
    void deleteOld(OffsetDateTime time);
}
