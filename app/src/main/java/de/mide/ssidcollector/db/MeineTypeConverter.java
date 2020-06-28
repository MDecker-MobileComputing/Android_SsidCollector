package de.mide.ssidcollector.db;

import androidx.room.TypeConverter;
import java.util.Date;

/**
 * Die Methoden in dieser Klasse wandeln in den Entity-Klassen verwendete Datentypen, die
 * nicht von SQLite gespeichert werden können, in Datentypen um, die SQLite unterstützt.
 */
public class MeineTypeConverter {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
