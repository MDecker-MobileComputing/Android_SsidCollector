package de.mide.ssidcollector.fragmente;

import de.mide.ssidcollector.R;
import de.mide.ssidcollector.asyncTasks.AnzDatensaetzeAnzeigeTask;
import de.mide.ssidcollector.auxi.MeinCursorAdapter;
import de.mide.ssidcollector.db.MeineDatenbank;
import de.mide.ssidcollector.db.SsidDao;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * Fragment zur Anzeige der gespeicherten WiFi-Namen.
 */
public class DatenbankFragment extends Fragment {

    /** UI-Element zur Anzeige aktueller Anzahl der Datensätze in DB. */
    private TextView _anzDatensaetzeTextView = null;

    /** UI-Element zur Anzeige der Liste mit den SSIDs. */
    private ListView _listView = null;


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

        _anzDatensaetzeTextView = view.findViewById(R.id.anzDatensaetzeTextView);

        _listView = view.findViewById(R.id.ssidListview);

        MeineDatenbank meineDatenbank = MeineDatenbank.getSingletonInstance( getActivity() );
        SsidDao ssidDao = meineDatenbank.ssidDao();

        Cursor cursor = ssidDao.getCursorAll();

        MeinCursorAdapter adapter = new MeinCursorAdapter( getActivity(), cursor, 0); // flags=0
    }


    /**
     * Lifecycle-Methode, aktualisiert Anzeige mit Anzahl der Datensätze.
     */
    @Override
    public void onStart() {

        super.onStart();

        new AnzDatensaetzeAnzeigeTask(_anzDatensaetzeTextView);
    }

}