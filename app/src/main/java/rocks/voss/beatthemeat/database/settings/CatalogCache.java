package rocks.voss.beatthemeat.database.settings;

import java.util.ArrayList;
import java.util.List;

import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.beatthemeat.settings.CatalogSetting;

public class CatalogCache {
    private static List<CatalogSetting> catalogSettings = null;
    private static List<Catalog> catalogs = null;
    private static DatabaseUtil databaseUtil = new DatabaseUtil();

    public static List<CatalogSetting> getCatalogs() {
        if (catalogs == null) {
            catalogs = new ArrayList<>();
            catalogSettings = new ArrayList<>();

            databaseUtil.getAll(Catalog.class, null, elements -> {
                catalogs.addAll((List<Catalog>) (List<?>) elements);
            });

            for (Catalog catalog : catalogs) {
                CatalogSetting catalogSetting = CatalogSetting.createBy(catalog);
                catalogSettings.add(catalogSetting);
            }
        }
        return catalogSettings;
    }
}
