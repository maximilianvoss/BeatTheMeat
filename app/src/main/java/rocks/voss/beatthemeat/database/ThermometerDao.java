package rocks.voss.beatthemeat.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ThermometerDao {
    @Query("SELECT * FROM Thermometer ORDER BY `order`")
    List<Thermometer> getAll();

    @Insert
    void insertAll(Thermometer... thermometers);

    @Query("DELETE FROM Thermometer WHERE id=:id")
    void delete(int id);
}
