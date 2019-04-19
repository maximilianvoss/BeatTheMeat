package rocks.voss.beatthemeat.settings;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.beatthemeat.database.settings.Catalog;
import rocks.voss.beatthemeat.database.settings.Cut;
import rocks.voss.beatthemeat.database.settings.CutDao;
import rocks.voss.beatthemeat.database.settings.Meat;
import rocks.voss.beatthemeat.database.settings.MeatDao;

@Data
public class MeatSetting {
    private int id;
    private int fk_catalog;
    private String name;
    private CutSetting[] cuts;

    public static MeatSetting createBy(Meat meat) {
        MeatSetting meatSetting = new MeatSetting();
        meatSetting.id = meat.id;
        meatSetting.fk_catalog = meat.fk_catalog;
        meatSetting.name = meat.name;

        DatabaseUtil databaseUtil = new DatabaseUtil();
        CutDao cutDao = databaseUtil.getDao(Cut.class);
        List<CutSetting> cuts = new ArrayList<>();
        for (Cut cut : cutDao.getAll(meat.fk_catalog, meat.id)) {
            cuts.add(CutSetting.createBy(cut));
        }
        meatSetting.setCuts(cuts.toArray(new CutSetting[]{}));
        return meatSetting;
    }

    public CutSetting findCutByName(String name) {
        if (name == null) {
            return null;
        }
        for (CutSetting cut : cuts) {
            if (name.equals(cut.getName())) {
                return cut;
            }
        }
        return null;
    }

    public void persist(Catalog catalog) {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        Meat meat = new Meat();
        meat.name = name;
        meat.fk_catalog = catalog.id;

        databaseUtil.insert(Meat.class, meat, true);
        MeatDao meatDao = databaseUtil.getDao(Meat.class);
        meat.id = meatDao.getLatest();

        for (CutSetting cut : cuts) {
            cut.persist(meat);
        }
    }
}
