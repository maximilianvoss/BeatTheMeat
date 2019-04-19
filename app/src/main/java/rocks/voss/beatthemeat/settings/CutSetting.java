package rocks.voss.beatthemeat.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.beatthemeat.database.settings.Cooking;
import rocks.voss.beatthemeat.database.settings.CookingDao;
import rocks.voss.beatthemeat.database.settings.Cut;
import rocks.voss.beatthemeat.database.settings.CutDao;
import rocks.voss.beatthemeat.database.settings.Meat;

/**
 * Created by voss on 30.03.18.
 */
@Data
public class CutSetting implements Serializable {
    private int id;
    private int fk_catalog;
    private int fk_meat;
    private String name;
    private CookingSetting[] cookings;

    public static CutSetting createBy(Cut cut) {
        CutSetting cutSetting = new CutSetting();
        cutSetting.id = cut.id;
        cutSetting.fk_catalog = cut.fk_catalog;
        cutSetting.fk_meat = cut.fk_meat;
        cutSetting.name = cut.name;

        DatabaseUtil databaseUtil = new DatabaseUtil();
        CookingDao cookingDao = databaseUtil.getDao(Cooking.class);
        List<CookingSetting> cookings = new ArrayList<>();
        for (Cooking cooking : cookingDao.getAll(cut.fk_catalog, cut.fk_meat, cut.id)) {
            cookings.add(CookingSetting.createBy(cooking));
        }
        cutSetting.setCookings(cookings.toArray(new CookingSetting[]{}));
        return cutSetting;
    }

    public CookingSetting findCookingByName(String name) {
        if (name == null) {
            return null;
        }
        for (CookingSetting coocking : cookings) {
            if (name.equals(coocking.getName())) {
                return coocking;
            }
        }
        return null;
    }

    public void persist(Meat meat) {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        Cut cut = new Cut();
        cut.name = name;
        cut.fk_catalog = meat.fk_catalog;
        cut.fk_meat = meat.id;

        databaseUtil.insert(Cut.class, cut, true);
        CutDao cutDao = databaseUtil.getDao(Cut.class);
        cut.id = cutDao.getLatest();

        for (CookingSetting cooking : cookings) {
            cooking.persist(cut);
        }
    }
}
