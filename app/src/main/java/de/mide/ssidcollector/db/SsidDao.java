package de.mide.ssidcollector.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import java.util.List;

/**
 * Interface aus das DAO erzeugt wird (DAO: Data Access Object), siehe auch die Doku
 * <a href="https://developer.android.com/training/data-storage/room/accessing-data>hier</a>.
 */
@Dao
public interface SsidDao {

    /**
     * Datensatz mit gefundenem WiFi-Netz einfügen; wenn MAC-Adresse (Primär-Schlüssel) schon
     * in Tabelle vorhanden, dann wird die Einfüge-Operation einfach ignoriert.
     *
     * @param ssidList Einzufügende Datensätze.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertSsid(List<CollectedSsid> ssidList);


    /*
    @Delete
    public void deleteSsid(CollectedSsid... ssids);

    @Query("SELECT COUNT(*) FROM collectedSsid")
    public int getNumberOfRows();

    @Query("SELECT * FROM collectedSsid ORDER BY dateTimeOfFirstDetection desc")
    public CollectedSsid[] fetchAllSortedByAge();

    @Query("SELECT * FROM collectedSsid ORDER BY ssid")
    public CollectedSsid[] fetchAllSortedByName();

    @Query("SELECT * FROM collectedSsid WHERE dateTimeOfFirstDetection < :thresholdDate")
    public User[] fetchAllNotOlderThan(date thresholdDate);
    */

}
