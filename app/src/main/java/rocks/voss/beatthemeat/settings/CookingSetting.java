package rocks.voss.beatthemeat.settings;

import java.io.Serializable;

import lombok.Data;
import rocks.voss.androidutils.utils.DatabaseUtil;
import rocks.voss.beatthemeat.database.settings.Cooking;
import rocks.voss.beatthemeat.database.settings.Cut;

/**
 * Created by voss on 30.03.18.
 */
@Data
public class CookingSetting implements Serializable {
    private int id;
    private int fk_catalog;
    private int fk_meat;
    private int fk_cut;
    private String name;
    private int temperatureMin;
    private int temperatureMax;
    private boolean temperatureIsRange;

    public static CookingSetting createBy(Cooking cooking) {
        CookingSetting cookingSetting = new CookingSetting();
        cookingSetting.id = cooking.id;
        cookingSetting.fk_catalog = cooking.fk_catalog;
        cookingSetting.fk_meat = cooking.fk_meat;
        cookingSetting.fk_cut = cooking.fk_cut;
        cookingSetting.name = cooking.name;
        cookingSetting.temperatureMin = cooking.temperatureMin;
        cookingSetting.temperatureMax = cooking.temperatureMax;
        cookingSetting.temperatureIsRange = cooking.temperatureIsRange;
        return cookingSetting;
    }

    public void persist(Cut cut) {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        Cooking cooking = new Cooking();
        cooking.name = name;
        cooking.temperatureMin = temperatureMin;
        cooking.temperatureMax = temperatureMax;
        cooking.temperatureIsRange = temperatureIsRange;
        cooking.fk_catalog = cut.fk_catalog;
        cooking.fk_meat = cut.fk_meat;
        cooking.fk_cut = cut.id;

        databaseUtil.insert(Cooking.class, cooking);
    }
}
