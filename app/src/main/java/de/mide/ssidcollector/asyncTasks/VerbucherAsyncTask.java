package de.mide.ssidcollector.asyncTasks;

import static de.mide.ssidcollector.MainActivity.TAG4LOGGING;

import android.net.wifi.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mide.ssidcollector.db.CollectedSsid;
import de.mide.ssidcollector.db.MeineDatenbank;
import de.mide.ssidcollector.db.SsidDao;


/**
 * AsyncTask, um in einem Hintergrund-/Worker-Thread die gefundenen Datensätze in
 * die Datenbank einzufügen. Nach dieser Einfüge-Operation wird die Anzahl der
 * Datensätze in der Tabelle mit einem Toast-Objekt angezeigt.
 */
public class VerbucherAsyncTask extends AsyncTask<Void,Void,Integer> {

    /** Liste der zu verbuchenden Ergebnisse. */
    private List<ScanResult> _scanResultList = null;

    /** Application Context; wird für DB-Zugriff und Erzeugung von Toast-Objekt benötigt. */
    private Context _context = null;


    /**
     * AsyncTask erzeugen und gleich starten.
     *
     * @param scanResultList  Liste mit bei einem Suchlauf gefundenen WiFi-Netzen.
     *
     * @param context   Application Context
     */
    public VerbucherAsyncTask( List<ScanResult> scanResultList,
                               Context          context
                             ) {

        _scanResultList = scanResultList;
        _context        = context;

        execute();
    }


    /**
     * Der Inhalt dieser Methode wird im Hintergrund-Thread ausgeführt
     * und führt die eigentlichen DB-Insert-Operation durch.
     *
     * @param voids  Dummy-Input-Argument
     *
     * @return  Anzahl Records, die nach Einfüger-Operation in DB ist.
     */
    @Override
    protected Integer doInBackground(Void... voids) {

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

        SsidDao ssidDao = meineDatenbank.ssidDao();

        ssidDao.insertSsid( collectedSsidList );

        Log.i(TAG4LOGGING, "Datensätze via DAO in DB eingefügt.");

        int numOfRows = ssidDao.getNumberOfRows();

        return numOfRows;
    }


    /**
     * Methode wird in MAIN/UI-Thread ausgeführt und zeigt Anzahl der Datensätze, die sich
     * nach Einfüge-Operation in der DB-Tabelle befindet, in einem Toast-Objekt an.
     *
     * @param anzDatensaetze  Anzahl Records, die jetzt in Tabelle {@code CollectedSsid} sind.
     */
    @Override
    protected void onPostExecute(Integer anzDatensaetze) {

        Toast.makeText( _context,
                   "Anzahl Datensätze in DB: " + anzDatensaetze,
                        Toast.LENGTH_LONG
                      ).show();
    }

}