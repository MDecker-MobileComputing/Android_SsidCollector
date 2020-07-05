/**
 * Persistenz mit Room-API (Object-relationaler Mapper).
 * <br><br>
 *
 * Einstiegs-Doku:
 * <ul>
 *   <li><a href="https://developer.android.com/training/data-storage/room">Einstiegs-Doku</a></li>
 *   <li><a href="https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9">Tutorial auf medium.com</a></li>
 * </ul>
 * <br><br>
 *
 * Room-Libraries in Datei {@code app/build.gradle} eintragen:
 * <pre>
 * implementation 'android.arch.persistence.room:runtime:2.2.5'
 * annotationProcessor 'android.arch.persistence.room:compiler:2.2.5'
 * </pre>
 * <br><br>
 *
 * <b>Verwendung:</b>
 * <ul>
 *     <li>Singleton-Instanz von {@code MeineDatenbank} holen.</li>
 *     <li>Relevantes DAO mit entsprechender Getter-Methode von {@code MeineDatenbank}-Instanz.</li>
 *     <li>Methode für gewünschte DB-Operation an DAO-Objekt aufrufen.</li>
 * </ul>
 * <br><br>
 *
 * Datenbank-Datei interaktiv in Emulator-Instanz öffnen:
 * <pre>
 *     adb shell
 *     shell> cd /data/data/de.mide.ssidcollector/databases
 *     shell> sqlite ssids.db
 * </pre>
 *
 * Konfiguration in Datei {@code app/build.gradle}, damit während Compilierung keine Warnung kommt, weil
 * Schema nicht ausgegeben werden kann
 * (siehe auch <a href="https://stackoverflow.com/a/44424908/1364368">diese Antwort</a>):
 * <pre>
 * javaCompileOptions {
 *     annotationProcessorOptions {
 *         arguments = ["room.schemaLocation": "$projectDir/schemas".toString()]
 *     }
 * }
 * </pre>
 * Schema wird in Form einer JSON-Datei in Ordner {@code app/schemas} geschrieben.
 * <br><br>
 *
 * Datenbank-Schema interaktiv mit SQLite abfragen:
 * <pre>
 * sqlite> .schema
 * sqlite> CREATE TABLE android_metadata (locale TEXT);
 * sqlite> CREATE TABLE room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT);
 * sqlite> CREATE TABLE `CollectedSsid` (`macAddress` TEXT NOT NULL, `ssid` TEXT, `dateTimeOfFirstDetection` INTEGER, PRIMARY KEY(`macAddress`));
 * </pre>
 */
package de.mide.ssidcollector.db;