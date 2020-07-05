package de.mide.ssidcollector.auxi;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import de.mide.ssidcollector.R;
import de.mide.ssidcollector.db.MeineDatenbank;
import de.mide.ssidcollector.db.SsidDao;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


/**
 * Adapter-Objekt, um in DB gespeicherte Objekte vom Typ {@code CollectedSsid} für {@code ListView}-Element
 * bereitzustellen.
 * <br><br>
 *
 * Siehe
 * <a href="https://coderwall.com/p/fmavhg/android-cursoradapter-with-custom-layout-and-how-to-use-it">hier</a>
 * für ein Beispiel zur Verwendung der Klasse {@link CursorAdapter}.
 */
public class MeinCursorAdapter extends CursorAdapter {

    /** Wird benötigt, um Layout "aufzublasen". */
    private LayoutInflater _inflater = null;

    /** Spalten-Index für Feld SSID in Cursor. */
    private int _colIndexSsid = -1;


    /**
     * Konstruktor, übergibt Argumente an Super-Konstruktor und holt {@link LayoutInflater}.
     *
     * @param context  Referenz auf Activity
     *
     * @param cursor  Cursor, der darzustellende Datensätze repräsentiert
     *
     * @param flags  Attribute, um Verhalten von Adapter zu steuern
     */
    public MeinCursorAdapter(Context context, Cursor cursor, int flags) {

        super(context, cursor, flags);

        _inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }


    /**
     * Erzeugt View für eine Zeile durch "Aufblasen" des entsprechenden Layouts.
     *
     * @param context  Wird nicht benötigt.
     *
     * @param cursor  DB-Cursor, wird nicht benötigt.
     *
     * @param parent  Wird nicht benötigt.
     *
     * @return  Neues View für einen Listen-Eintrag ("Zeile").
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return _inflater.inflate(R.layout.listview_eintrag, parent, false); // attachToRoot=false
    }


    /**
     * Trägt Werte aus aktuellem Datensatz von {@code cursor} in {@code view} ein.
     *
     * @param view  View-Element für Listen-"Zeile", in das der aktuelle Wert von {@code cursor}
     *              einzutragen ist.
     *
     * @param context  Wird nicht benötigt.
     *
     * @param cursor  Cursor, aus dem ein Datensatz im UI-Element dargestellt werden soll.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textView = view.findViewById(R.id.wifiNameTextview);

        String ssid = cursor.getString( _colIndexSsid );

        textView.setText(ssid);
    }

}
