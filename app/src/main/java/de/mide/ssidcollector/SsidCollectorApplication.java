package de.mide.ssidcollector;

import android.app.Application;


/**
 * Laut Artikel in ct 12/2020 soll man das Datenbank-Objekt für Rooms in der {@code create()}-Methode
 * einer Unterklasse von {@code Application} erzeugen.
 */
public class SsidCollectorApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        // TODO: RoomsDB erzeugen, siehe auch https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9
    }

}