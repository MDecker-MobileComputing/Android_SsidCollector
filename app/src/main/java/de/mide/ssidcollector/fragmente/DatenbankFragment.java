package de.mide.ssidcollector.fragmente;

import de.mide.ssidcollector.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


/**
 * Fragment zur Anzeige der gespeicherten WiFi-Namen.
 */
public class DatenbankFragment extends Fragment {

    /**
     * Layout-Datei für Fragment mit Inflater "aufblasen" und View daraus erzeugen.
     */
    @Override
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState
                             ) {

        return inflater.inflate(R.layout.datenbank_fragment , container, false);
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
    }

}