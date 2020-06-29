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

    /** MAC-Adresse ist per Definition eindeutig für einen WiFi-Access-Point. */
    @PrimaryKey
    @NonNull
    public String macAddress;

    /** Service Set Identifier (WiFi-Name). */
    public String ssid;

    /**
     * Da Date kein von SQLite unterstützter Datentyp ist werden
     * entsprechende Konverter-Methoden benötigt, siehe Klasse
     * {@code MeineTypeConverter}.
     */
    public Date dateTimeOfFirstDetection;

}
