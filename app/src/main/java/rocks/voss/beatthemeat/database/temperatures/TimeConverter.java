package rocks.voss.beatthemeat.database.temperatures;


import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by voss on 08.04.18.
 */

public class TimeConverter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @TypeConverter
    public static OffsetDateTime toOffsetDateTime(String value) {
        return formatter.parse(value, OffsetDateTime.FROM);
    }

    @TypeConverter
    public static String fromOffsetDateTime(OffsetDateTime date) {
        return date.format(formatter);
    }
}
