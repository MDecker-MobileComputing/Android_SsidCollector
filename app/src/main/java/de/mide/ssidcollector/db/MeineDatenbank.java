package de.mide.ssidcollector.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


/**
 * Die Datenbank-Klasse enthält eine Getter-Methode für die DAOs.
 * Alle Entity- und TypeConverter-Klassen müssen dieser Klasse mit der Annotation
 * <code>TypeConverters</code>hinzugefügt werden.
 * <br><br>
 *
 * DAO-Methoden müssen in einem Worker-Thread ausgeführt werden.
 */
@Database(entities = {CollectedSsidEntity.class}, version=1)
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
     * @return  Data Access Object für Entity-Klasse {@link CollectedSsidEntity}.
     */
    public abstract SsidDao ssidDao();

    //public abstract NochEinDao nochEinDao();


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
                    .allowMainThreadQueries() // siehe https://github.com/MDecker-MobileComputing/Android_SsidCollector/issues/1
                    .build();
        }

        return SINGLETON_INSTANCE;
    }

}
