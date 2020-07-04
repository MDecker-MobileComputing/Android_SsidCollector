package de.mide.ssidcollector.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import de.mide.ssidcollector.db.MeineDatenbank;
import de.mide.ssidcollector.db.SsidDao;


/**
 *  AsyncTask, um TextView mit aktueller Anzahl Datensätze in DB zu füllen.
 */
public class AnzDatensaetzeAnzeigeTask extends AsyncTask<Void,Void,Integer> {

    MeineDatenbank _meineDatenbank = null;

    private TextView _textView = null;

    /**
     * Konstruktor zur Übergabe von TextView-Elemente und um AsyncTask zu starten.
     *
     * @param textView  UI-Elemente zur Anzeige der Anzahl der Records
     */
    public AnzDatensaetzeAnzeigeTask(TextView textView) {

        _textView = textView;

        Context context = _textView.getRootView().getContext();

        _meineDatenbank = MeineDatenbank.getSingletonInstance( context );

        execute();
    }


    /**
     * DB-Query wird in Hintergrund-Thread ausgeführt.
     *
     * @param voids  Keine Argumente
     *
     * @return  Anzahl der Datensätze in der DB.
     */
    @Override
    protected Integer doInBackground(Void... voids) {

        SsidDao ssidDao = _meineDatenbank.ssidDao();

        return ssidDao.getAnzahlDatensaetze();
    }


    /**
     * Methode wird in MAIN/UI-Thread ausgeführt und zeigt Anzahl der Datensätze, die sich
     * nach Einfüge-Operation in der DB-Tabelle befindet, in einem Toast-Objekt an.
     *
     * @param anzDatensaetze  Anzahl Records, die jetzt in Tabelle {@code CollectedSsid} sind.
     */
    @Override
    protected void onPostExecute(Integer anzDatensaetze) {

        _textView.setText("Anzahl Datensätze in DB: " + anzDatensaetze);
    }

}
