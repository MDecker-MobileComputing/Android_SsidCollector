package de.mide.ssidcollector.auxi;

import java.util.Comparator;
import android.net.wifi.ScanResult;


/**
 * Comparator-Klasse, mit der Listen von gefunden WiFi-Netzen nach
 * dem Namen (SSID) sortiert werden können.
 */
public class ScanErgebnisComparator implements Comparator<ScanResult> {

    /**
     * Vergleich von zwei {@link ScanResult}-Objekten.
     *
     * @param sr1  ScanResult-Objekt 1 (linke Seite des Vergleichs)
     *
     * @param sr2  ScanResult-Objekt 2 (rechte Seite des Vergleichs)
     *
     * @return  Ergebnis des Vergleich des SSID.
     */
    @Override
    public int compare(ScanResult sr1, ScanResult sr2) {

        return sr1.SSID.compareTo(sr2.SSID);
    }

}
