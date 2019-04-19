package rocks.voss.beatthemeat.database.settings;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CookingDao {
    @Query("SELECT * FROM Cooking ORDER BY id")
    List<Cooking> getAll();

    @Query("SELECT * FROM Cooking WHERE fk_catalog=:fk_catalog and fk_meat=:fk_meat and fk_cut=:fk_cut ORDER BY id")
    List<Cooking> getAll(int fk_catalog, int fk_meat, int fk_cut);

    @Query("SELECT * FROM Cooking WHERE name=:name and fk_catalog=:fk_catalog and fk_meat=:fk_meat and fk_cut=:fk_cut")
    Cooking getByName(int fk_catalog, int fk_meat, int fk_cut, String name);

    @Query("select seq from sqlite_sequence where name=\"Cooking\";")
    int getLatest();

    @Insert
    void insert(Cooking cooking);

    @Query("DELETE FROM Cooking WHERE id=:id")
    void delete(int id);

    @Query("DELETE FROM Cooking")
    void deleteAll();
}
