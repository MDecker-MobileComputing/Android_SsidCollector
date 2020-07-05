package de.mide.ssidcollector.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import androidx.room.PrimaryKey;

import java.util.Date;


/**
 * Entity-Klasse für ein gefundenes WLAN.
 */
@Entity
public class CollectedSsidEntity {

    /** MAC-Adresse ist per Definition eindeutig für einen WiFi-Access-Point. */
    @PrimaryKey
    @NonNull
    public String macAddress;

    /** Name von WiFi-Netzt (SSID: Service Set Identifier). */
    public String ssid;

    /**
     * Spalte "_id" wird von CursorAdapter benötigt, wird mit verhashtem String von
     * Mac-Adresse gefüllt. Spalten-Name wird mit {@code ColumnInfo} definiert,
     * damit man diese Member-Variable nicht abweichend von den anderen Member-
     * Variablen in dieser Klasse mit einem vorangestellten Unterstrich benennen
     * muss.
     */
    @ColumnInfo(name = "_id")
    public long id;

    /**
     * Da Date kein von SQLite unterstützter Datentyp ist werden
     * entsprechende Konverter-Methoden benötigt, siehe Klasse
     * {@code MeineTypeConverter}.
     */
    public Date dateTimeOfFirstDetection;

}
