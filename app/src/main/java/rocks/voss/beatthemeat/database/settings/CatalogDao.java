package rocks.voss.beatthemeat.database.settings;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CatalogDao {
    @Query("SELECT * FROM catalog ORDER BY id")
    List<Catalog> getAll();

    @Query("SELECT * FROM catalog WHERE name=:name")
    Catalog getByName(String name);

    @Query("select seq from sqlite_sequence where name=\"Catalog\";")
    int getLatest();

    @Insert
    void insert(Catalog catalog);

    @Query("DELETE FROM catalog WHERE id=:id")
    void delete(int id);

    @Query("DELETE FROM catalog")
    void deleteAll();
}
