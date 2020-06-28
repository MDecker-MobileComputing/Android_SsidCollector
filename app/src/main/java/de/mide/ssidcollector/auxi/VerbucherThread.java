package de.mide.ssidcollector.auxi;

import static de.mide.ssidcollector.MainActivity.TAG4LOGGING;

import android.net.wifi.ScanResult;
import android.content.Context;
import android.util.Log;
import android.webkit.ConsoleMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mide.ssidcollector.db.CollectedSsid;
import de.mide.ssidcollector.db.MeineDatenbank;


/**
 * Thread, der in einem Hintergrund-Thread (Worker-Thread) die bei einem Scan-Lauf
 * gefundenen WiFi-Netze in die Datenbank zu schreiben.
 */
public class VerbucherThread extends Thread {

    /** Liste der zu verbuchenden Ergebnisse. */
    private List<ScanResult> _scanResultList = null;

    /** Application Context. */
    private Context _context = null;


    /**
     * Thread erzeugen und gleich starten.
     *
     * @param scanResultList  Liste mit bei einem Suchlauf gefundenen WiFi-Netzen.
     *
     * @param context   Application Context
     */
    public VerbucherThread( List<ScanResult> scanResultList,
                            Context context
                          ) {

        _scanResultList = scanResultList;
        _context        = context;

        start();
    }


    /**
     * Der Inhalt dieser Methode wird im Hintergrund-Thread ausgeführt
     * und führt die eigentlichen DB-Insert-Operation durch.
     */
    @Override
    public void run() {

        Date jetztDate = new Date();


        // Ergebnisdatensätze in CollectedSsid-Objekte umkopieren

        List<CollectedSsid> collectedSsidList = new ArrayList<>( _scanResultList.size() );

        for (ScanResult scanResult: _scanResultList) {

            CollectedSsid collectedSsid = new CollectedSsid();
            collectedSsid.macAddress               = scanResult.BSSID;
            collectedSsid.ssid                     = scanResult.SSID;
            collectedSsid.dateTimeOfFirstDetection = jetztDate;

            collectedSsidList.add(collectedSsid);
        }


        MeineDatenbank meineDatenbank = MeineDatenbank.getSingletonInstance(_context);

        meineDatenbank.ssidDao().insertSsid( collectedSsidList );

        Log.i(TAG4LOGGING, "Datensätze via DAO in DB eingefügt.");
    }

}