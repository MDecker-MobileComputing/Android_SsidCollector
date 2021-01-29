package de.mide.ssidcollector.db;

import androidx.room.TypeConverter;
import java.util.Date;


/**
 * Die Methoden in dieser Klasse wandeln in den Entity-Klassen verwendete Datentypen, die
 * nicht von SQLite gespeichert werden können, in Datentypen um, die SQLite unterstützt.
 * <br><br>
 *
 * Alle Konverter-Klasse müssen in der von <code>RoomDatabase</code> abgeleiteten Klasse
 * (in diesem Projekt: {@link MeineDatenbank} mit der Annotation <code>TypeConverters</code>
 * deklariert werden.
 */
public class MeineTypeConverter {

    /**
     * Konvertiert einen Zeitstempel-Wert im Format {@code long} in Format {@link Date}.
     *
     * @param value  Zeitstempel als {@code long}-Wert in Datenbank gespeichert.
     *
     * @return  Objekt mit Datum und Uhrzeit
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {

        return value == null ? null : new Date(value);
    }

    /**
     * Wandelt {@code Date}-Objekt in {@code long}-Wert um, der in Datenbank gespeichert
     * wird.
     *
     * @param date  Datum- und Uhrzeit, die in Datenbank gespeichert werden sollen.
     *
     * @return  Zeitstempel-Wert zum Speichern in Datenbank.
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {

        return date == null ? null : date.getTime();
    }

}
