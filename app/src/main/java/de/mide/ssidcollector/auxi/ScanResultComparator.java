package de.mide.ssidcollector.auxi;

import java.util.Comparator;
import android.net.wifi.ScanResult;


/**
 * Comparator-Klasse, mit der Listen von gefunden WiFi-Netzen nach
 * dem Namen (SSID) sortiert werden können.
 */
public class ScanResultComparator implements Comparator<ScanResult> {

    /**
     * Vergleich zwei {@link ScanResult}-Objekte.
     *
     * @param sr1  ScanResult-Objekt
     *
     * @param sr2  ScanResult-Objekt
     *
     * @return  Ergebnis des Vergleich des SSID.
     */
    @Override
    public int compare(ScanResult sr1, ScanResult sr2) {

        return sr1.SSID.compareTo(sr2.SSID);
    }

}
