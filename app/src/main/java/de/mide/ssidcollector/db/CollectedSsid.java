package de.mide.ssidcollector.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


/**
 * Entity-Klasse für ein gefundenes WLAN.
 */
@Entity
public class CollectedSsid {

    /** MAC address is per definition unique for each access point. */
    @PrimaryKey
    @NonNull
    public String macAddress;

    /** The app's purpose is it to collect the SSID of access points. */
    public String ssid;

    /**
     * TypeConverter is needed to convert date into a data type
     * that is supported by SQLite, see class {@link MeineTypeConverter}.
     */
    public Date dateTimeOfFirstDetection;

}
