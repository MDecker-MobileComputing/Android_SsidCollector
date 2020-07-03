package de.mide.ssidcollector.fragmente;

import de.mide.ssidcollector.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * Fragment für Seite zum eigentlichen Sammeln von 
 * SSIDs (Namen von WiFi-Netzen).
 */
public class SsidSammelFragment extends Fragment implements View.OnClickListener  {

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
     * Referenzen auf einzelne UI-Elemente holen und in Member-Variablen
     * speichern. Es wird auch die Fragment-Instanz als Event-Listener
     * für die Buttons registriert.
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
    }

    /**
     * Event-Handler für Button.
     *
     * @param view  Button, der Event ausgelöst hat.
     */
    public void onClick(View view) {


    }

}