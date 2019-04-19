package rocks.voss.beatthemeat.database.settings;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MeatDao {
    @Query("SELECT * FROM Meat ORDER BY id")
    List<Meat> getAll();

    @Query("SELECT * FROM Meat WHERE fk_catalog=:fk_catalog ORDER BY id")
    List<Meat> getAll(int fk_catalog);

    @Query("SELECT * FROM Meat WHERE name=:name and fk_catalog=:fk_catalog")
    Meat getByName(int fk_catalog, String name);

    @Query("select seq from sqlite_sequence where name=\"Meat\";")
    int getLatest();

    @Insert
    void insert(Meat meat);

    @Query("DELETE FROM Meat WHERE id=:id")
    void delete(int id);

    @Query("DELETE FROM Meat")
    void deleteAll();
}
