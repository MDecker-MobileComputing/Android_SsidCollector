package de.mide.ssidcollector.fragmente;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.List;

import de.mide.ssidcollector.R;
import de.mide.ssidcollector.auxi.DialogHelper;
import de.mide.ssidcollector.auxi.ScanErgebnisComparator;
import de.mide.ssidcollector.asyncTasks.VerbucherAsyncTask;

import static android.content.pm.PackageManager.FEATURE_WIFI;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static de.mide.ssidcollector.MainActivity.TAG4LOGGING;


/**
 * Fragment/Seite zum eigentlichen Sammeln von SSIDs (Namen von WiFi-Netzen).
 */
public class SsidSammelFragment extends Fragment implements View.OnClickListener  {

    /** Intent-Filter für BroadcastReceiver zum Empfang von WLAN-Scan-Ergebnissen. */
    private static IntentFilter SCAN_RESULTS_AVAILABLE_INTENT_FILTER =
                                        new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION);

    /** Eine Millisekunde hat 1 Mio Nanosekunden, siehe auch diesen
     * <a href="https://de.wikipedia.org/w/index.php?title=Sekunde&oldid=198467543#Mit_der_Sekunde_zusammenh%C3%A4ngende_Einheiten">Artikel auf dt. Wikipedia.</a>
     */
    private static long NANOSEKUNDEN_PRO_MILLISEKUNDEN = 1_000_000;

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

    /** Comparator-Objekt zum Sortieren der Liste der gefundenen Wifi-Netze. */
    private static ScanErgebnisComparator SCAN_RESULT_COMPARATOR = new ScanErgebnisComparator();

    /** BroadcastReceiver-Objekt das aufgerufen wird, wenn ein Scan-Vorgang beendet ist. */
    private ScanErgebnisBroadcastReceiver _scanErgebnisReceiver = null;

    /**
     * Startzeitpunkt für Messung Dauer von Scan-Vorgang, wird mit Wert von Methode
     * {@link System#nanoTime()} gefüllt.
     */
    private long _zeitmessungStart = 0;

    /**
     * Endzeitpunkt für Messung Dauer von Scan-Vorgang,
     * wird mit Wert von {@link System#nanoTime()} gefüllt.
     */
    private long _zeitmessungEnde = 0;


    /**
     * Layout-Datei für Fragment mit Inflater "aufblasen" und View daraus erzeugen.
     */
    @Override
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState
                             ) {

        return inflater.inflate(R.layout.sammel_fragment, container, false);
    }


    /**
     * Referenzen auf einzelne UI-Elemente holen und in Member-Variablen speichern.
     * Es wird auch die Fragment-Instanz als Event-Listener für die Buttons registriert.
     *
     * @param view  Referenz auf View-Objekt, das von Methode <i>onCreateView()</i>
     *              mit Inflater erstellt und mit return zurückgegeben wurde.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        _ergebnisTextView        = view.findViewById( R.id.ergebnisTextView         );
        _suchButton              = view.findViewById( R.id.starteSucheButton        );
        _progressBar             = view.findViewById( R.id.progressbar              );
        _loescheVorSucheCheckbox = view.findViewById( R.id.loeschenVorSucheCheckbox );

        _suchButton.setOnClickListener(this);

        // ProgressBar auf unendliche Animation umschalten
        _progressBar.setIndeterminate(true);

        Context context = getActivity().getApplicationContext();

        if ( context.getPackageManager().hasSystemFeature(FEATURE_WIFI) == false ) {

            DialogHelper.zeigeDialog(context, "Fehler", "Gerät hat kein WLAN-Modul." );
            _suchButton.setEnabled(false);

        } else {

            _wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
    }


    /**
     * Event-Handler für Button, startet den Scan-Vorgang.
     *
     * @param view  Button, der Event ausgelöst hat.
     */
    public void onClick(View view) {

        if ( _wifiManager == null ) {

            DialogHelper.zeigeDialog(getActivity(), "Interner Fehler", "WiFiManager nicht gefunden." );
            return;
        }

        if ( _wifiManager.isWifiEnabled() == false ) {

            DialogHelper.zeigeDialog(getActivity(), "Fehler", "WLAN ist nicht eingeschaltet." );
            return;
        }

        _zeitmessungStart = System.nanoTime();
        registerBroadcastReceiver();

        if (_loescheVorSucheCheckbox.isChecked() == false) {

            _ergebnisTextView.setText("");
        }

        boolean scanGestartet = _wifiManager.startScan();
        if (scanGestartet == true) {

            _suchButton.setEnabled(false);

        } else {

            DialogHelper.zeigeDialog(getActivity(),"Fehler", "Scan konnte nicht gestartet werden." );
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

            _zeitmessungEnde = System.nanoTime();

            List<ScanResult> scanResultList = _wifiManager.getScanResults();

            Collections.sort(scanResultList, SCAN_RESULT_COMPARATOR);

            int anzResult = scanResultList.size();

            long zeitDiffMillisekunden = ( _zeitmessungEnde - _zeitmessungStart ) / NANOSEKUNDEN_PRO_MILLISEKUNDEN;

            StringBuffer sb = new StringBuffer();
            sb.append("Anzahl WiFi-Netze gefunden (");
            sb.append(zeitDiffMillisekunden).append(" ms): ");
            sb.append( anzResult);
            sb.append("\n\n");

            for (ScanResult scanResult : scanResultList) {

                sb.append( scanResult.SSID );
                sb.append(" (MAC: ").append( scanResult.BSSID ).append(")\n"); // https://stackoverflow.com/a/61221077/1364368
            }

            sb.append("\n\n");

            _ergebnisTextView.append( sb.toString() );

            if (anzResult > 0) {

                new VerbucherAsyncTask( scanResultList, getActivity().getApplicationContext() );
            }

            _suchButton.setEnabled(true);
        }
    }
    /* ************************** */
    /* *** Ende innere Klasse *** */
    /* ************************** */


    /**
     * BroadcastReceiver-Objekt zum Empfang der Scan-Ergebnisse registrieren.
     * Diese Methode muss unmittelbar vor Start eines Scan-Vorgangs aufgerufen
     * werden. Schaltet auch
     */
    private void registerBroadcastReceiver() {

        _scanErgebnisReceiver = new ScanErgebnisBroadcastReceiver();

        getActivity().registerReceiver(_scanErgebnisReceiver, SCAN_RESULTS_AVAILABLE_INTENT_FILTER);

        _progressBar.setVisibility(View.VISIBLE);
    }


    /**
     * Registrierung eines BroadcastReceiver-Objekts zum Empfang der Scan-Ergebnisse
     * aufheben.
     */
    private void unregisterBroadcastReceiver() {

        if (_scanErgebnisReceiver != null) {

            getActivity().unregisterReceiver(_scanErgebnisReceiver);

        } else {

            Log.w(TAG4LOGGING, "Keine BroadcastReceiver-Objekt, das deregistriert werden kann.");
        }

        _progressBar.setVisibility(View.INVISIBLE);
    }

}