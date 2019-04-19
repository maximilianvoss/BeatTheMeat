package rocks.voss.beatthemeat.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.beatthemeat.database.settings.Catalog;
import rocks.voss.beatthemeat.database.settings.CatalogDao;
import rocks.voss.beatthemeat.database.settings.Meat;
import rocks.voss.beatthemeat.database.settings.MeatDao;

/**
 * Created by voss on 31.03.18.
 */
@Data
public class CatalogSetting implements Serializable {
    private int id;
    private String name;
    private MeatSetting[] meats;

    public static CatalogSetting createBy(Catalog catalog) {
        CatalogSetting catalogSetting = new CatalogSetting();
        catalogSetting.id = catalog.id;
        catalogSetting.name = catalog.name;

        DatabaseUtil databaseUtil = new DatabaseUtil();
        MeatDao meatDao = databaseUtil.getDao(Meat.class);
        List<MeatSetting> meats = new ArrayList<>();
        for (Meat meat : meatDao.getAll(catalog.id)) {
            meats.add(MeatSetting.createBy(meat));
        }
        catalogSetting.setMeats(meats.toArray(new MeatSetting[]{}));
        return catalogSetting;
    }

    public MeatSetting findMeatByName(String name) {
        if (name == null) {
            return null;
        }
        for (MeatSetting meat : meats) {
            if (name.equals(meat.getName())) {
                return meat;
            }
        }
        return null;
    }

    public void persist() {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        Catalog catalog = new Catalog();
        catalog.name = name;
        databaseUtil.insert(Catalog.class, catalog, true);
        CatalogDao catalogDao = databaseUtil.getDao(Catalog.class);
        catalog.id = catalogDao.getLatest();

        for (MeatSetting meat : meats) {
            meat.persist(catalog);
        }
    }
}
