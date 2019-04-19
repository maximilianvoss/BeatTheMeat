package rocks.voss.beatthemeat.database.settings;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CutDao {
    @Query("SELECT * FROM Cut ORDER BY id")
    List<Cut> getAll();

    @Query("SELECT * FROM Cut WHERE fk_catalog=:fk_catalog and fk_meat=:fk_meat ORDER BY id")
    List<Cut> getAll(int fk_catalog, int fk_meat);

    @Query("SELECT * FROM Cut WHERE name=:name and fk_catalog=:fk_catalog and fk_meat=:fk_meat")
    Cut getByName(int fk_catalog, int fk_meat, String name);

    @Query("select seq from sqlite_sequence where name=\"Cut\";")
    int getLatest();

    @Insert
    void insert(Cut cut);

    @Query("DELETE FROM Cut WHERE id=:id")
    void delete(int id);

    @Query("DELETE FROM Cut")
    void deleteAll();
}
