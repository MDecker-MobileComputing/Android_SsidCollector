package de.mide.ssidcollector;

import static android.content.pm.PackageManager.FEATURE_WIFI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


/**
 *
 * Beispiele für Scannen nach WLAN-Netzen:
 * <ul>
 *  <li><a href="https://developer.android.com/guide/topics/connectivity/wifi-scan">Beispiel in offizieller Doku</a></li> 
 *  <li><a href="https://stackoverflow.com/a/17167318/1364368">Antwort auf SO</a></li> 
 * </ul>
 */
public class MainActivity extends Activity {

    private TextView _ergebnisTextView = null;

    private Button _suchButton = null;

    private WifiManager _wifiManager = null;

    private ScanErgebnisBroadcastReceiver _scanErgebnisReceiver = null;

    private static final IntentFilter SCAN_RESULTS_AVAILABLE_ACTION_INTENT_FILTER =
                                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    /**
     * Lifecycle-Methode: Layout-Datei laden, UI-Elemente holen, Überprüfung
     * ob Gerät WLAN kann; wenn ja, dann WiFiManager holen und BroadcastReceiver
     * registrieren.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _ergebnisTextView = findViewById(R.id.ergebnisTextView );
        _suchButton       = findViewById(R.id.starteSucheButton);

        Context context = getApplicationContext();
                        
        if ( context.getPackageManager().hasSystemFeature(FEATURE_WIFI) == false ) {
            
            zeigeDialog("Fehler", "Gerät hat kein WLAN-Modul." );
            _suchButton.setEnabled(false);
            
        } else {

          _wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);            
            
          _scanErgebnisReceiver = new ScanErgebnisBroadcastReceiver();
          registerReceiver(_scanErgebnisReceiver, SCAN_RESULTS_AVAILABLE_ACTION_INTENT_FILTER);
        }
    }


    /**
     * Button-Event-Handler zum Triggern eines neues Scan-laufs.
     * Der Scan-Lauf ist asynchron, seine Beendigung wird über einen
     * Broadcast-Intent mitgeteilt.
     * <br><br>
     *
     * Siehe <a href="https://stackoverflow.com/questions/49178307/">diese Antwort auf SO</a>
     * für Einschränkungen Scans nach WiFi-Netzen.
     *
     * @param view  Button, der Event ausgelöst hat.
     */
    public void onSucheButton(View view) {
        
        if ( _wifiManager == null ) {

            zeigeDialog("Interner Fehler", "WiFiManager nicht gefunden." );
            return;
        }

        if ( _wifiManager.isWifiEnabled() == false ) {

            zeigeDialog("Fehler", "WLAN ist nicht eingeschaltet." );
            return;
        }


        boolean scanGestartet = _wifiManager.startScan();
        if (scanGestartet == true) {

            _suchButton.setEnabled(false);
            _ergebnisTextView.setText("");

        } else {

            zeigeDialog("Fehler", "Scan konnte nicht gestartet werden." );
        }
    }


    /* *************************** */
    /* *** Start innere Klasse *** */
    /* *************************** */
    class ScanErgebnisBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            List<ScanResult> scanResultList = _wifiManager.getScanResults();

            StringBuffer sb = new StringBuffer();
            for (ScanResult scanResult : scanResultList) {

                sb.append( scanResult.SSID );
                sb.append(" (MAC: ").append( scanResult.BSSID ).append(")\n"); // https://stackoverflow.com/a/61221077/1364368
            }
            _ergebnisTextView.setText( sb.toString() );

            zeigeDialog("Scan beendet", "Anzahl WiFi-Netze gefunden: " + scanResultList.size());

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
