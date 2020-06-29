package de.mide.ssidcollector;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.FEATURE_WIFI;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import de.mide.ssidcollector.auxi.ScanResultComparator;
import de.mide.ssidcollector.auxi.VerbucherAsyncTask;


/**
 * App zum Sammeln von Namen von WiFi-Netzen (SSIDs).
 * <br><br>
 *
 * Code-Beispiele für Scannen nach WLAN-Netzen:
 * <ul>
 *  <li><a href="https://developer.android.com/guide/topics/connectivity/wifi-scan">Beispiel in offizieller Doku</a></li> 
 *  <li><a href="https://stackoverflow.com/a/17167318/1364368">Antwort auf SO</a></li> 
 * </ul>
 * <br><br>
 *
 * Sammeln von WiFi-Namen wenn App im Hintergrund ist (mit Service-Klasse) nicht sinnvoll:
 * <i>"The startScan() method performs a full scan for background apps only a few times each hour. 
 * If a background app calls the method again soon afterward, the WifiManager class provides cached 
 * results from the previous scan."</i>
 * <a href="https://developer.android.com/about/versions/oreo/background-location-limits">(Quelle)</a>
 * <br><br>
 *
 * This project is licensed under the terms of the BSD 3-Clause License.
 */
public class MainActivity extends Activity {

    public static final String TAG4LOGGING = "SsidCollector";

    /** Request Code für Erkennung Callback-Aufruf für Runtime Permissions. */
    private static int REQUEST_CODE = 123;

    /** Button zum Start eines Scan-Laufs. */
    private Button _suchButton = null;

    /** UI-Element zur Anzeige der gefundenen WLAN-Netze. */
    private TextView _ergebnisTextView = null;

    /** Fortschrittsanzeige (drehender Kreis), wird während Scan-Vorgang auf sichtbar geschaltet. */
    private ProgressBar _progressBar = null;

    /**
     * Checkbox mit Aufschrift "Kumulativ"; wenn angewählt, dann wird zu Beginn eines neuen
     * Suchvorgangs das TextView-Element mit den Ergebnissen nicht gelöscht.
     */
    private CheckBox _loescheVorSucheCheckbox = null;

    /** WiFiManager-Objekt, wird benötigt, um Scan-Vorgang zu starten. */
    private WifiManager _wifiManager = null;

    /** BroadcastReceiver-Objekt das aufgerufen wird, wenn ein Scan-Vorgang beendet ist. */
    private ScanErgebnisBroadcastReceiver _scanErgebnisReceiver = null;

    /** Intent-Filter für BroadcastReceiver zum Empfang von WLAN-Scan-Ergebnissen. */
    private static IntentFilter SCAN_RESULTS_AVAILABLE_INTENT_FILTER = null;

    /** Comparator-Objekt zum Sortieren der Liste der gefundenen Wifi-Netze. */
    private static ScanResultComparator SCAN_RESULT_COMPARATOR = new ScanResultComparator();


    /**
     * Lifecycle-Methode: Layout-Datei laden, UI-Elemente holen, Überprüfung
     * ob Gerät WLAN kann; wenn ja, dann WiFiManager holen und BroadcastReceiver
     * registrieren.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _ergebnisTextView        = findViewById(R.id.ergebnisTextView        );
        _suchButton              = findViewById(R.id.starteSucheButton       );
        _progressBar             = findViewById(R.id.progressbar             );
        _loescheVorSucheCheckbox = findViewById(R.id.loeschenVorSucheCheckbox);

        // ProgressBar auf unendliche Animation umschalten
        _progressBar.setIndeterminate(true);

        SCAN_RESULTS_AVAILABLE_INTENT_FILTER = new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION);

        Context context = getApplicationContext();
                        
        if ( context.getPackageManager().hasSystemFeature(FEATURE_WIFI) == false ) {
            
            zeigeDialog("Fehler", "Gerät hat kein WLAN-Modul." );
            _suchButton.setEnabled(false);
            
        } else {

          _wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }

        boolean hatRuntimePermissions = checkRuntimePermissions();
        if (hatRuntimePermissions == false) {

            _suchButton.setEnabled(false);
        }

    }


    /**
     * Überprüft, ob App aktuell über die für den WLAN-Scan benötigten Permissions verfügt.
     * Wenn dies nicht der Fall ist, dann werden die Permissions auch gleich vom Nutzer
     * angefordert.
     *
     * @return  {@code true} wenn die App schon die benötigten Runtime Permissions hat;
     *          wenn {@code false}, dann werden die Berechtigungen beim Nutzer angefragt
     *          und nach Antwort durch den Nutzer die Callback-Methode
     *          {@code onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean checkRuntimePermissions() {

        int apiLevel = android.os.Build.VERSION.SDK_INT;
        if (apiLevel >= 23) {

            Log.i(TAG4LOGGING, "API-Level von Gerät >= 23, also müssen Runtime-Permissions überprüft werden.");

            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {

                String[] permissionArray = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissionArray, REQUEST_CODE);

                return false;

            } else {

                return true;
            }

        } else {

            // apiLevel < 23, also gibt es noch keine Runtime Permissions
            return true;
        }
    }


    /**
     * Callback-Methode für Anfrage Runtime Permissions beim Nutzer.
     *
     * @param requestCode  Erkennungs-Code
     *
     * @param permissions  Array der angefragten Berechtigungen.
     *
     * @param grantResults  Ergebnisse (genehmigt oder abgelehnt) für einzelne Berechtigungen.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_CODE) {

            if (grantResults[0] == PERMISSION_GRANTED) {

                _suchButton.setEnabled(true);

            } else {

                zeigeDialog("Berechtigung verweigert",
                        "Sie haben der App die benötigten Berechtigungen verweigert, sie kann deshalb nicht genutzt werden.");
                _suchButton.setEnabled(false);
            }
        }
    }


    /**
     * BroadcastReceiver-Objekt zum Empfang der Scan-Ergebnisse registrieren.
     * Diese Methode muss unmittelbar vor Start eines Scan-Vorgangs aufgerufen
     * werden. Schaltet auch
     */
    private void registerBroadcastReceiver() {

        _scanErgebnisReceiver = new ScanErgebnisBroadcastReceiver();

        registerReceiver(_scanErgebnisReceiver, SCAN_RESULTS_AVAILABLE_INTENT_FILTER);

        _progressBar.setVisibility(View.VISIBLE);
    }


    /**
     * Registrierung eines BroadcastReceiver-Objekts zum Empfang der Scan-Ergebnisse
     * aufheben.
     */
    private void unregisterBroadcastReceiver() {

        if (_scanErgebnisReceiver != null) {

            unregisterReceiver(_scanErgebnisReceiver);

        } else {

            Log.w(TAG4LOGGING, "Keine BroadcastReceiver-Objekt, das deregistriert werden kann.");
        }

        _progressBar.setVisibility(View.INVISIBLE);
    }


    /**
     * Button-Event-Handler zum Triggern eines neues Scan-laufs.
     * Der Scan-Lauf ist asynchron, seine Beendigung wird über einen
     * Broadcast-Intent mitgeteilt. Der entsprechende BroadcastReceiver
     * wird unmittelbar vor Beginn des Scan-Vorgangs registriert.
     * <br><br>
     *
     * Siehe <a href="https://stackoverflow.com/questions/49178307/">diese Antwort auf SO</a>
     * für Einschränkungen Scans nach WiFi-Netzen.
     *
     * @param view  Button, der Event ausgelöst hat.
     */
    public void onSucheButton(View view) {

        boolean hatRuntimePermission = checkRuntimePermissions();
        if (hatRuntimePermission == false) {

            _suchButton.setEnabled(false);
            return;
        }
        
        if ( _wifiManager == null ) {

            zeigeDialog("Interner Fehler", "WiFiManager nicht gefunden." );
            return;
        }

        if ( _wifiManager.isWifiEnabled() == false ) {

            zeigeDialog("Fehler", "WLAN ist nicht eingeschaltet." );
            return;
        }

        registerBroadcastReceiver();

        if (_loescheVorSucheCheckbox.isChecked() == false) {

            _ergebnisTextView.setText("");
        }

        boolean scanGestartet = _wifiManager.startScan();
        if (scanGestartet == true) {

            _suchButton.setEnabled(false);

        } else {

            zeigeDialog("Fehler", "Scan konnte nicht gestartet werden." );
        }
    }


    /* *************************** */
    /* *** Start innere Klasse *** */
    /* *************************** */
    private class ScanErgebnisBroadcastReceiver extends BroadcastReceiver {

        /**
         * Event-Handler-Methode für beendeten Scan-Vorgang.
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            unregisterBroadcastReceiver();

            List<ScanResult> scanResultList = _wifiManager.getScanResults();

            Collections.sort(scanResultList, SCAN_RESULT_COMPARATOR);

            int anzResult = scanResultList.size();

            StringBuffer sb = new StringBuffer();
            sb.append("Anzahl WiFi-Netze gefunden: " + anzResult + "\n\n");

            for (ScanResult scanResult : scanResultList) {

                sb.append( scanResult.SSID );
                sb.append(" (MAC: ").append( scanResult.BSSID ).append(")\n"); // https://stackoverflow.com/a/61221077/1364368

                //_meineDatenbank.ssidDao().insertSsid();
            }

            sb.append("\n\n");

            _ergebnisTextView.append( sb.toString() );

            if (anzResult > 0) {

                new VerbucherAsyncTask( scanResultList, getApplicationContext() );
            }

            _suchButton.setEnabled(true);
        }
    }
    /* ************************** */
    /* *** Ende innere Klasse *** */
    /* ************************** */


    /**
     * Hilfsmethode zur Anzeige eines Dialogs für Fehlermeldungen u.ä.
     *
     * @param title  Dialog-Titel
     *
     * @param nachricht  Eigentlicher Text des Dialogs.
     */
    private void zeigeDialog(String title, String nachricht) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(nachricht);

        dialogBuilder.setPositiveButton( "Weiter", null );
        dialogBuilder.setCancelable(false);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}
