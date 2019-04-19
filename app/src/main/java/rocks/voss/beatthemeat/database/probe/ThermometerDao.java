package rocks.voss.beatthemeat.database.probe;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ThermometerDao {
    @Query("SELECT * FROM Thermometer")
    List<Thermometer> getAll();

    @Insert
    void insertAll(Thermometer... thermometers);

    @Insert
    void insert(Thermometer thermometer);

    @Query("DELETE FROM Thermometer WHERE id=:id")
    void delete(int id);

    @Update(onConflict = REPLACE)
    void update(Thermometer thermometer);
}
