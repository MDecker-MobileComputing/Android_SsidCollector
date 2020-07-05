package de.mide.ssidcollector.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


/**
 * Die Datenbank-Klasse enthält eine Getter-Methode für die DAOs.
 * Alle Entity- und TypeConverter-Klasse müssen dieser Klasse mit den entsprechenden Annotationen
 * hinzugefügt werden.
 * <br><br>
 *
 * DAO-Methoden müssen in einem Worker-Thread ausgeführt werden.
 */
@Database(entities = {CollectedSsid.class}, version=1)
@TypeConverters({MeineTypeConverter.class})
public abstract class MeineDatenbank extends RoomDatabase {

    /**
     * Name der Datenbank-Datei, die von SQLite3 angelegt wird.
     * DB-Datei wird im Ordner {@code /data/data/de.mide.ssidcollector/databases}
     * gespeichert.
     */
    private static final String DB_DATEI_NAME = "ssids.db";

    /** Referenz auf Singleton-Instanz, wird lazy erzeugt. */
    private static MeineDatenbank SINGLETON_INSTANCE = null;

    /**
     * Getter für {@code SsidDao}.
     *
     * @return  Data Access Object für Entity-Klasse {@link CollectedSsid}.
     */
    public abstract SsidDao ssidDao();


    /**
     * Getter für Singleton-Instanz der vorliegenden Klasse; bei Bedarf wird diese Instanz
     * erzeugt.
     *
     * @param context  Application Context, wird für evtl. Erzeugung der Singleton-Instanz
     *                 benötigt.
     *
     * @return  Singleton-Instanz
     */
    public static MeineDatenbank getSingletonInstance(Context context) {

        if (SINGLETON_INSTANCE == null) {

            SINGLETON_INSTANCE =
                    Room.databaseBuilder(
                            context.getApplicationContext(),
                            MeineDatenbank.class,
                            DB_DATEI_NAME
                    )
                    .allowMainThreadQueries() // nicht empfohlen
                    .build();
        }

        return SINGLETON_INSTANCE;
    }

}
