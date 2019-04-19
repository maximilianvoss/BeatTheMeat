package rocks.voss.beatthemeat.settings;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.beatthemeat.database.settings.Catalog;
import rocks.voss.beatthemeat.database.settings.CatalogDao;
import rocks.voss.beatthemeat.database.settings.Cooking;
import rocks.voss.beatthemeat.database.settings.CookingDao;
import rocks.voss.beatthemeat.database.settings.Cut;
import rocks.voss.beatthemeat.database.settings.CutDao;
import rocks.voss.beatthemeat.database.settings.Meat;
import rocks.voss.beatthemeat.database.settings.MeatDao;

/**
 * Created by voss on 30.03.18.
 */

@Data
public class WrapperSetting implements Serializable {
    @Getter
    private static WrapperSetting instance;
    private CatalogSetting[] catalogs;

    public static WrapperSetting load() {
        if (instance == null) {
            instance = new WrapperSetting();
            DatabaseUtil databaseUtil = new DatabaseUtil();
            final List<CatalogSetting> catalogSettings = new ArrayList<>();

            databaseUtil.getAll(Catalog.class, null, elements -> {
                for (Catalog catalog : (List<Catalog>) (List<?>) elements) {
                    catalogSettings.add(CatalogSetting.createBy(catalog));
                }
            });
            instance.setCatalogs(catalogSettings.toArray(new CatalogSetting[]{}));
        }
        return instance;
    }

    public static WrapperSetting createByStream(InputStream stream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        instance = mapper.readValue(stream, WrapperSetting.class);
        return instance;
    }

    public CatalogSetting findCatalogByName(String name) {
        if (name == null) {
            return null;
        }
        for (CatalogSetting catalog : catalogs) {
            if (name.equals(catalog.getName())) {
                return catalog;
            }
        }
        return null;
    }

    public void perist() {
        if (catalogs == null || catalogs.length < 1) {
            return;
        }
        swipeDatabase();
        for (CatalogSetting catalog : catalogs) {
            catalog.persist();
        }
    }

    private void swipeDatabase() {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        CatalogDao catalogDao = databaseUtil.getDao(Catalog.class);
        MeatDao meatDao = databaseUtil.getDao(Meat.class);
        CutDao cutDao = databaseUtil.getDao(Cut.class);
        CookingDao cookingDao = databaseUtil.getDao(Cooking.class);

        if (catalogDao != null) {
            catalogDao.deleteAll();
        }
        if (meatDao != null) {
            meatDao.deleteAll();
        }
        if (cutDao != null) {
            cutDao.deleteAll();
        }
        if (cookingDao != null) {
            cookingDao.deleteAll();
        }
    }
}
